package com.song.timer.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by song on 2017/6/24.
 */
@Slf4j
public class ClassUtils {

	public static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public static Map<Object/*Instance*/, Set<Method>> getMethodsAnnotatedWith(Class<? extends Annotation> annotation, String... packageNames) {
		Map<Object, Set<Method>> allMethodHolder = new HashMap<>();
		Set<Class<?>> allClasses = findAllClasses(packageNames);
		allClasses.forEach(clz -> {
			Method[] methods = clz.getMethods();
			if (methods != null) {
				Set<Method> methodSet = new HashSet<>();
				for (Method method : methods) {
					if (method != null && method.isAnnotationPresent(annotation)) {
						methodSet.add(method);
					}
				}
				if (methodSet.size() > 0) {
					try {
						Object instance = clz.newInstance();
						allMethodHolder.put(instance, methodSet);
					} catch (Exception e) {
						ExceptionUtils.wrapAndThrow(e);
					}
				}
			}
		});
		return allMethodHolder;
	}

	private static Set<Class<?>> findAllClasses(String[] packageNames) {
		if (packageNames != null && packageNames.length > 0) {
			Set<Class<?>> classes = new HashSet<>();
			for (String packageName : packageNames) {
				if (StringUtils.isNoneBlank(packageName)) {
					URL resourceUrl = getResourceUrl(packageName);
					if (resourceUrl != null) {
						String protocol = resourceUrl.getProtocol();
						if (protocol.equals("file")) {
							handleRegularFile(classes, packageName, resourceUrl.getPath());
						} else if (protocol.equals("jar")) {
							try {
								handleJarFile(classes, resourceUrl);
							} catch (IOException e) {
								log.error("io error_" + e.getMessage(), e);
							}
						}
					}
				}
			}
			return classes;
		}
		return Collections.emptySet();
	}

	public static URL getResourceUrl(String path) {
		return Thread.currentThread().getContextClassLoader()
				.getResource(getResourceName(path));
	}

	public static String getResourceName(String packageName) {
		if (StringUtils.isNotBlank(packageName)) {
			String name = packageName
					.replace(".", "/")
					.replace("\\", "/");
			return name;
		}
		return null;
	}

	public static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) ||
				!Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	public static String getMethodFullKey(Class cls, Member method) {
		return cls.getName() + "." + getMethodKey(cls, method);
	}

	public static String getMethodKeyWithoutParameter(Class cls, Member member) {
		return cls.getName() + "#" + getMethodName(member);
	}

	public static String getMethodKey(Class cls, Member method) {
		return getMethodName(method) + "(" + Joiner.on(", ").join(getParameterNames(method)) + ")";
	}

	public static String getMethodName(Member method) {
		return method instanceof Method ? method.getName() :
				method instanceof Constructor ? "<init>" : null;
	}

	public static List<String> getParameterNames(final Member member) {
		List<String> result = Lists.newArrayList();

		Class<?>[] parameterTypes = member instanceof Method ? ((Method) member).getParameterTypes() :
				member instanceof Constructor ? ((Constructor) member).getParameterTypes() : null;

		if (parameterTypes != null) {
			for (Class<?> paramType : parameterTypes) {
				String name = getName(paramType);
				result.add(name);
			}
		}

		return result;
	}


	public static String getName(Class type) {
		if (type.isArray()) {
			try {
				Class cl = type;
				int dim = 0;
				while (cl.isArray()) {
					dim++;
					cl = cl.getComponentType();
				}
				return cl.getName() + TextUtils.repeat("[]", dim);
			} catch (Throwable ignore) {
			}
		}
		return type.getName();
	}

	private static void handleJarFile(Set<Class<?>> classHashSet, URL url) throws IOException {
		JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
		while (jarURLConnection != null) {
			JarFile jarFile = jarURLConnection.getJarFile();
			while (jarFile != null) {
				Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
				while (jarEntryEnumeration.hasMoreElements()) {
					JarEntry entry = jarEntryEnumeration.nextElement();
					String jarEntityName = entry.getName();
					if (jarEntityName.endsWith(".class")) {
						String className = jarEntityName.substring(0, jarEntityName.lastIndexOf(".class")).replace("/", ".");
						classHashSet.add(loadClass(className, false));
					}
				}
			}
		}
	}


	private static void handleRegularFile(Set<Class<?>> classHashSet, String packageName, String path) {
		final File[] files = new File(path).listFiles(pathname -> pathname.isDirectory() || (pathname.isFile() && pathname.getName().endsWith(".class")));
		if (files != null) {
			for (File temp : files) {
				if (temp.isDirectory()) {
					if (StringUtils.isEmpty(packageName)) {
						handleRegularFile(classHashSet, temp.getName(), path + "/" + temp.getName());
					} else {
						handleRegularFile(classHashSet, packageName + "/" + temp.getName(), path + "/" + temp.getName());
					}
				} else if (temp.isFile()) {
					String className = temp.getName().substring(0, temp.getName().indexOf(".class"));
					classHashSet.add(loadClass((packageName + "/" + className).replace("/", "."), false));
				}
			}
		}
	}

	public static Class<?> loadClass(String className, boolean isInitialized) {
		Class cls = null;
		try {
			cls = Class.forName(className, isInitialized, getClassLoader());
		} catch (ClassNotFoundException e) {
			ExceptionUtils.wrapAndThrow(e);
		}
		return cls;
	}

}

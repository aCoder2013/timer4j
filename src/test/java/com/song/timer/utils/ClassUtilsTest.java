package com.song.timer.utils;

import com.song.timer.annotation.TimerTask;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * Created by song on 2017/6/24.
 */
public class ClassUtilsTest {

	@Test
	public void getResourceUrl() throws Exception {
		Map<Object, Set<Method>> methodsMap = ClassUtils.getMethodsAnnotatedWith(TimerTask.class, "com.song");
		System.out.println(methodsMap.toString());
	}

}
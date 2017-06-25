package com.song.timer.config;

import com.song.timer.utils.ClassUtils;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by song on 2017/6/24.
 */
@Slf4j
public class TimerTaskRunner implements Runnable {

	private final Object instance;

	private final Method method;

	public TimerTaskRunner(Object instance, Method method) {
		this.instance = instance;
		this.method = method;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		try {
			ClassUtils.makeAccessible(method);
			method.invoke(instance, (Object[]) null);
		} catch (InvocationTargetException e) {
			ExceptionUtils.wrapAndThrow(e.getTargetException());
		} catch (Throwable e) {
			ExceptionUtils.wrapAndThrow(e);
		} finally {
			log.info("Job {} execute cost {} ms .", instance.getClass().getSimpleName(), (System.currentTimeMillis() - start));
		}
	}
}

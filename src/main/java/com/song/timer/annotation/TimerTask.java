package com.song.timer.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.TimeZone;

/**
 * 标记需要定时执行的方法，cron、fixedDelay、initialDelay必须指定其中一个
 *
 * Created by song on 2017/6/24.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TimerTask {

	/**
	 * cron 表达式
	 */
	String cron() default "";

	/**
	 * 市区，默认为{@link TimeZone#getDefault()}
	 */
	String timeZone() default "";

	/**
	 * 以固定的间隔执行任务，单位是毫秒
	 */
	long fixedDelay() default -1;

	/**
	 * 任务执行的初始间隔,单位是毫秒
	 */
	long initialDelay() default -1;
}

package com.song.timer.core;

import com.song.timer.annotation.TimerTask;
import com.song.timer.config.CronTask;
import com.song.timer.config.FixedDelayTask;
import com.song.timer.config.TimerTaskFuture;
import com.song.timer.config.TimerTaskRunner;
import com.song.timer.utils.ClassUtils;
import com.song.timer.utils.TextUtils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import lombok.extern.slf4j.Slf4j;

/**
 * 解析{@link com.song.timer.annotation.TimerTask} 标注的方法
 *
 * Created by song on 2017/6/24.
 */
@Slf4j
public class TimerTaskProcessor {

	/**
	 * 任务调度器
	 */
	private TaskExecutor taskExecutor = new TaskExecutor();

	/**
	 * 需要扫描的包名
	 */
	private List<String> packageNamesToScan;

	private final Map<Object/*instance*/, Set<Method>/*method from the instance*/> methodsHolder = new HashMap<>();

	private final Map<String, TimerTaskFuture> timerTaskFutureMap = new HashMap<>();

	public TimerTaskProcessor() {
	}

	public void init() {
		if (isInvalidPackages()) {
			throw new IllegalArgumentException("Package names should be null");
		}
		findTimerTaskMethods();
		processTimerTask();
	}

	private boolean isInvalidPackages() {
		return packageNamesToScan == null || packageNamesToScan.size() == 0;
	}

	private void processTimerTask() {
		methodsHolder.forEach((instance, methods) -> {
			if (methods != null && methods.size() > 0) {
				methods.forEach(method -> {
					boolean processed = false;
					TimerTaskFuture timerTaskFuture = null;
					TimerTask annotation = method.getAnnotation(TimerTask.class);
					TimerTaskRunner timerTaskRunner = new TimerTaskRunner(instance, method);
					long initialDelay = annotation.initialDelay();
					if (initialDelay < 0) {
						initialDelay = 0;
					}
					long fixedDelay = annotation.fixedDelay();
					if (fixedDelay > 0) {
						timerTaskFuture = this.taskExecutor.scheduleFixedDelayTask(new FixedDelayTask(timerTaskRunner, initialDelay, fixedDelay));
						processed = true;
					}
					String cron = annotation.cron();
					if (StringUtils.isNotBlank(cron)) {
						if (processed) {
							throw new IllegalStateException("Specify 'cron' or 'fixedDelay', not both");
						}
						TimeZone timeZone = parseTimeZone(annotation);
						timerTaskFuture = this.taskExecutor.schedule(new CronTask(timerTaskRunner, cron, timeZone));
						processed = true;
					}
					if (!processed) {
						throw new IllegalArgumentException("Process timer task '" +
								ClassUtils.getMethodKeyWithoutParameter(instance.getClass(), method) +
								"' failed,must specify one of 'cron' or 'fixedDelay'");
					}
					timerTaskFutureMap.put(ClassUtils.getMethodFullKey(instance.getClass(), method), timerTaskFuture);
				});
			}
		});
	}

	private TimeZone parseTimeZone(TimerTask annotation) {
		TimeZone timeZone = null;
		String timeZoneText = annotation.timeZone();
		if (StringUtils.isNotBlank(timeZoneText)) {
			timeZone = TextUtils.parseTimeZoneString(timeZoneText);
		} else {
			timeZone = TimeZone.getDefault();
		}
		return timeZone;
	}

	private void findTimerTaskMethods() {
		methodsHolder.putAll(ClassUtils.getMethodsAnnotatedWith(TimerTask.class, packageNamesToScan.toArray(new String[]{})));
	}

	public synchronized void stop() {
		this.timerTaskFutureMap.forEach((name, timerTaskFuture) -> {
			timerTaskFuture.cancel();
			log.info("{} has been stopped", name);
		});
	}

	public List<String> getPackageNamesToScan() {
		return packageNamesToScan;
	}

	public void setPackageNamesToScan(List<String> packageNamesToScan) {
		this.packageNamesToScan = packageNamesToScan;
	}
}

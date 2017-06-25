package com.song.timer.core;

import com.song.timer.config.CronTask;
import com.song.timer.config.FixedDelayTask;
import com.song.timer.config.TimerTaskFuture;
import com.song.timer.core.support.CronScheduledRunner;
import com.song.timer.support.CronParser;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by song on 2017/6/24.
 */
public class TaskExecutor {

	/**
	 * 用于执行定时任务的线程池,默认为处理器核数
	 */
	private final ScheduledExecutorService scheduledExecutorService;

	public TaskExecutor() {
		this.scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	}

	public TimerTaskFuture schedule(CronTask cronTask) {
		ScheduledFuture<?> schedule = new CronScheduledRunner(new CronParser(cronTask.getExpression(), cronTask.getTimeZone()), cronTask.getRunnable(), this.scheduledExecutorService).schedule();
		return new TimerTaskFuture(schedule);
	}

	public TimerTaskFuture scheduleFixedDelayTask(FixedDelayTask fixedDelayTask) {
		ScheduledFuture<?> scheduledFuture = this.scheduledExecutorService
				.scheduleAtFixedRate(fixedDelayTask.getRunnable(), fixedDelayTask.getInitDelay(), fixedDelayTask.getFixedDelay(), TimeUnit.MILLISECONDS);
		return new TimerTaskFuture(scheduledFuture);
	}

}

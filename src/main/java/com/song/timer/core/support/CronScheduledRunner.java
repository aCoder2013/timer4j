package com.song.timer.core.support;

import com.song.timer.config.TriggerContext;
import com.song.timer.core.ScheduledRunner;
import com.song.timer.core.TimerTrigger;
import com.song.timer.support.CronParser;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 执行cron类型的任务
 *
 * FIXME: 2017/6/25 修复任务并发执行时的问题
 *
 * Created by song on 2017/6/25.
 */
public class CronScheduledRunner implements ScheduledRunner,ScheduledFuture<Object>,Runnable  {

	private final Runnable taskRunner;

	private final TimerTrigger timerTrigger;

	private final TriggerContext triggerContext = new TriggerContext();

	private volatile ScheduledFuture currentScheduledFuture;

	private final ScheduledExecutorService executor;

	private Date scheduledExecutionTime;

	public CronScheduledRunner(CronParser cronParser, Runnable taskRunner, ScheduledExecutorService executor) {
		this.taskRunner = taskRunner;
		this.executor = executor;
		this.timerTrigger = new CronTimerTrigger(cronParser);
	}

	@Override
	public ScheduledFuture<?> schedule() {
		this.scheduledExecutionTime = this.timerTrigger.nextExecutionTime(triggerContext);
		long initialDelay = this.scheduledExecutionTime.getTime() - System.currentTimeMillis();
		this.currentScheduledFuture = this.executor.schedule(this, initialDelay, TimeUnit.MILLISECONDS);
		return this;
	}

	@Override
	public void run() {
		Date actualExecutionTime = new Date();
		this.taskRunner.run();
		Date completionTime = new Date();//完成时间
		this.triggerContext.update(this.scheduledExecutionTime, actualExecutionTime, completionTime);
		if (!this.currentScheduledFuture.isCancelled()) {
			schedule();
		}
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return this.currentScheduledFuture.getDelay(unit);
	}

	@Override
	public int compareTo(Delayed other) {
		if (this == other) {
			return 0;
		}
		long diff = getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
		return (diff == 0 ? 0 : ((diff < 0) ? -1 : 1));
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return this.currentScheduledFuture.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return this.currentScheduledFuture.isCancelled();
	}

	@Override
	public boolean isDone() {
		return this.currentScheduledFuture.isDone();
	}

	@Override
	public Object get() throws InterruptedException, ExecutionException {
		return this.currentScheduledFuture.get();
	}

	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return this.currentScheduledFuture.get(timeout, unit);
	}

}

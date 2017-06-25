package com.song.timer.config;

import java.util.Date;

import lombok.Getter;

/**
 * Created by song on 2017/6/25.
 */
@Getter
public class TriggerContext {

	private volatile Date lastScheduledExecutionTime;

	private volatile Date lastActualExecutionTime;

	private volatile Date lastCompletionTime;

	public TriggerContext() {
	}

	public TriggerContext(Date lastScheduledExecutionTime, Date lastActualExecutionTime, Date lastCompletionTime) {
		this.lastScheduledExecutionTime = lastScheduledExecutionTime;
		this.lastActualExecutionTime = lastActualExecutionTime;
		this.lastCompletionTime = lastCompletionTime;
	}

	public void update(Date lastScheduledExecutionTime, Date lastActualExecutionTime, Date lastCompletionTime) {
		this.lastScheduledExecutionTime = lastScheduledExecutionTime;
		this.lastActualExecutionTime = lastActualExecutionTime;
		this.lastCompletionTime = lastCompletionTime;
	}
}

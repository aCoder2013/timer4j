package com.song.timer.core;

import com.song.timer.config.TriggerContext;

import java.util.Date;

/**
 * Created by song on 2017/6/25.
 */
public interface TimerTrigger {

	/**
	 * 计算下一次执行任务的时间
	 *
	 * @param triggerContext 任务调度的上下文
	 */
	Date nextExecutionTime(TriggerContext triggerContext);
}

package com.song.timer.core.support;

import com.song.timer.config.TriggerContext;
import com.song.timer.core.TimerTrigger;
import com.song.timer.support.CronParser;

import java.util.Date;

/**
 * Created by song on 2017/6/25.
 */
public class CronTimerTrigger implements TimerTrigger {

	private final CronParser cronParser;

	public CronTimerTrigger(CronParser cronParser) {
		this.cronParser = cronParser;
	}

	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		Date time = triggerContext.getLastCompletionTime();
		if (time != null) {
			Date lastScheduledExecutionTime = triggerContext.getLastScheduledExecutionTime();
			if (lastScheduledExecutionTime != null && lastScheduledExecutionTime.before(time)) {
				//如果在上提前执行了，那么直接用lastScheduledExecutionTime
				time = lastScheduledExecutionTime;
			}
		} else {
			//首次执行直接用当前时间
			time = new Date();
		}
		return cronParser.next(time);
	}
}

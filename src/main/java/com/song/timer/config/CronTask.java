package com.song.timer.config;

import java.util.TimeZone;

import lombok.Getter;

/**
 * Created by song on 2017/6/25.
 */
@Getter
public class CronTask extends Task {

	private final String expression;

	private final TimeZone timeZone;

	public CronTask(Runnable runnable, String expression, TimeZone timeZone) {
		super(runnable);
		this.expression = expression;
		this.timeZone = timeZone;
	}

}

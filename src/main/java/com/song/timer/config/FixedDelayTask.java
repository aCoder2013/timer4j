package com.song.timer.config;

import lombok.Getter;

/**
 * Created by song on 2017/6/24.
 */
@Getter
public class FixedDelayTask extends Task {

	private final long initDelay;

	private final long fixedDelay;

	public FixedDelayTask(Runnable runnable, long initDelay, long fixedDelay) {
		super(runnable);
		this.initDelay = initDelay;
		this.fixedDelay = fixedDelay;
	}
}

package com.song.timer.config;

import lombok.Getter;

/**
 * Created by song on 2017/6/24.
 */
@Getter
public abstract class Task {

	private final Runnable runnable;

	public Task(Runnable runnable) {
		this.runnable = runnable;
	}
}

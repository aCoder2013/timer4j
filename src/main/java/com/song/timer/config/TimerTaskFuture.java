package com.song.timer.config;

import java.util.concurrent.ScheduledFuture;

/**
 * Created by song on 2017/6/24.
 */
public class TimerTaskFuture {

	private final ScheduledFuture future;

	public TimerTaskFuture(ScheduledFuture future) {
		this.future = future;
	}

	public void cancel(){
		if(this.future != null){
			future.cancel(true);
		}
	}
}

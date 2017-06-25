package com.song.timer.core;

import com.song.timer.annotation.TimerTask;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by song on 2017/6/24.
 */
public class TimerPrinter {

	private AtomicInteger count = new AtomicInteger(0);

	@TimerTask(initialDelay = 0, fixedDelay = 200)
	public void print() {
		System.out.println("Test :" + System.currentTimeMillis());
	}


	//	@TimerTask
	public void invalidPrint() {
		System.out.println("This is wrong");
	}

	@TimerTask(cron = "*/1 * * * * *")
	public void cronTask() throws Exception {
		System.out.println("This is a cron :" + count.incrementAndGet());
	}
}

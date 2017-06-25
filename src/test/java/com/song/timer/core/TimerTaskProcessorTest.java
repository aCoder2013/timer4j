package com.song.timer.core;

import org.junit.Test;

import java.util.Collections;

/**
 * Created by song on 2017/6/24.
 */
public class TimerTaskProcessorTest {

	@Test
	public void scheduledPrint() throws Exception {
		TimerTaskProcessor processor = new TimerTaskProcessor();
		processor.setPackageNamesToScan(Collections.singletonList("com.song"));
		processor.init();
		Thread.sleep(5000);
		processor.stop();
		Thread.sleep(100000);
	}

}
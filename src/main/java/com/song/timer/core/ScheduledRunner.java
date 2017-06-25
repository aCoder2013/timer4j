package com.song.timer.core;

import java.util.concurrent.ScheduledFuture;

/**
 * Created by song on 2017/6/25.
 */
public interface ScheduledRunner{

	ScheduledFuture<?> schedule();

}

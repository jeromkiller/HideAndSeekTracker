package com.github.jeromkiller.HideAndSeekTracker;

import java.time.LocalTime;

public class TimeUtil {
    static public LocalTime tickToTime(int ticks) {
        int seconds = (int) (ticks / 0.6);
        return LocalTime.ofSecondOfDay(seconds);
    }

    static public int timeToTick(LocalTime time) {
        int seconds = (time.toSecondOfDay());
        return (int) (seconds * 0.6);
    }
}

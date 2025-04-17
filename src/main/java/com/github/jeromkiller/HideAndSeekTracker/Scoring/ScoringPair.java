package com.github.jeromkiller.HideAndSeekTracker.Scoring;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScoringPair<T> {
    private T setting;
    private int points;
}

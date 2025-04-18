package com.github.jeromkiller.HideAndSeekTracker.Scoring;

import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekPlayer;
import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekRound;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class PointSystem <T> {
    final ScoreType scoreType;
    List<ScoringPair<T>> scorePairs;
    int fallThroughScore;

    public enum ScoreType {
        POSITION("Position"),
        HINTS("Hints"),
        TIME("Time"),
        NAME("Players");

        final String name;

        ScoreType(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public static ScoreType fromString(String str) {
            switch(str) {
                case "Position":
                    return POSITION;
                case "Hints":
                    return HINTS;
                case "Time":
                    return TIME;
                case "Players":
                    return NAME;
            }
            return null;
        }
    };

    PointSystem(ScoreType type) {
        this.scoreType = type;
        scorePairs = new ArrayList<>();
        this.fallThroughScore = 0;
    }

    public abstract int scorePlayer(HideAndSeekPlayer player, HideAndSeekRound round);
    public abstract void addSetting();
    public abstract void updateSetting(int index, T value);

    public void addScorePair(T setting, int points) {
        scorePairs.add(new ScoringPair<>(setting, points));
    }

    public void deleteSetting(int index) {
        if(index >= scorePairs.size()) {
            return;
        }
        scorePairs.remove(index);
    }
    public void updatePoints(int index, int value) {
        if(index >= scorePairs.size()) {
            return;
        }
        scorePairs.get(index).setPoints(value);
    }
}

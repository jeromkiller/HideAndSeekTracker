package com.github.jeromkiller.HideAndSeekTracker.Scoring;

import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekPlayer;
import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekRound;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Data
public abstract class PointSystem <T> {
    final ScoreType scoreType;
    final boolean canBeCalculatedOnce;
    List<ScoringPair<T>> scorePairs;
    int fallThroughScore;
    boolean calcEveryRound;

    public enum ScoreType {
        POSITION("Position"),
        HINTS("Hints"),
        TIME("Time"),
        NAME("Players"),
        PERCENTILE("Percentile");

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
                case "Percentile":
                    return PERCENTILE;
            }
            return null;
        }
    };

    PointSystem(ScoreType type, boolean canCalcOnce) {
        this.scoreType = type;
        this.canBeCalculatedOnce = canCalcOnce;
        scorePairs = new ArrayList<>();
        this.fallThroughScore = 0;
        this.calcEveryRound = true;
    }

    public abstract int scorePlayer(HideAndSeekPlayer player, @Nullable HideAndSeekRound round);
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

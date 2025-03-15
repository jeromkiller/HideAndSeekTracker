package com.github.jeromkiller.HideAndSeekTracker.Scoring;

import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekPlayer;
import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekRound;

import java.util.List;

public interface PointSystem {
    enum ScoreType {
        POSITION("Position"),
        HINTS("Hints");

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
            }
            return null;
        }
    };

    ScoreType getScoreType();

    List<ScoringPair> getScoringPairs();
    int getFallThroughScore();
    void setFallThroughScore(int score);

    int scorePlayer(HideAndSeekPlayer player, HideAndSeekRound round);

    void addSetting();
    void deleteSetting(int index);

    void updateSetting(int index, int value);
    void updatePoints(int index, int value);
    void updateFallFallthroughPoints(int value);
}

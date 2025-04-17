package com.github.jeromkiller.HideAndSeekTracker.Scoring;

import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekPlayer;
import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekRound;

public class HintScoring extends NumberScoring {

    HintScoring() {
        super(ScoreType.HINTS);
    }

    @Override
    public int scorePlayer(HideAndSeekPlayer player, HideAndSeekRound round) {
        final int hints = player.getHints();
        for(final ScoringPair<Integer> pair: scorePairs) {
            if(hints <= pair.getSetting()) {
                return pair.getPoints();
            }
        }
        return fallThroughScore;
    }

}
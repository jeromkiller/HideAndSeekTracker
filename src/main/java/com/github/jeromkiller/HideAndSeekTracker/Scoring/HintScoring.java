package com.github.jeromkiller.HideAndSeekTracker.Scoring;

import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekPlayer;
import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekRound;

public class HintScoring extends NumberScoring {

    HintScoring() {
        super(ScoreType.HINTS, false);
    }

    @Override
    public int scorePlayer(HideAndSeekPlayer player, HideAndSeekRound round) {
        final int hints = player.getHints();
        for(final ScoringPair<Integer> pair: scoreTiers) {
            if(hints <= pair.getSetting()) {
                return pair.getPoints();
            }
        }
        return fallThroughScore;
    }

}
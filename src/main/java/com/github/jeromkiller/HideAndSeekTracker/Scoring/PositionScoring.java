package com.github.jeromkiller.HideAndSeekTracker.Scoring;

import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekPlayer;
import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekRound;

public class PositionScoring extends NumberScoring {
    public PositionScoring() {
        super(ScoreType.POSITION);
    }

    @Override
    public int scorePlayer(HideAndSeekPlayer player, HideAndSeekRound round) {
        final int position = player.getPlacementValue();
        if(position == 0) {
            return 0;
        }
        for(final ScoringPair<Integer> pair: scorePairs) {
            if(position <= pair.getSetting()) {
                return pair.getPoints();
            }
        }
        return fallThroughScore;
    }
}

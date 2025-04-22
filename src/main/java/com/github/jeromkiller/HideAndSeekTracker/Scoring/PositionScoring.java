package com.github.jeromkiller.HideAndSeekTracker.Scoring;

import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekPlayer;
import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekRound;

public class PositionScoring extends NumberScoring {
    public PositionScoring() {
        super(ScoreType.POSITION, false);
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

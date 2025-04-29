package com.github.jeromkiller.HideAndSeekTracker.Scoring;

import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekPlayer;
import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekRound;

import javax.annotation.Nullable;

public class PercentileScoring extends PointSystem<Integer> {
    PercentileScoring() {
        super(ScoreType.PERCENTILE, false);
    }

    @Override
    public int scorePlayer(HideAndSeekPlayer player, @Nullable HideAndSeekRound round) {
        if(round == null) {
            return 0;
        }

        final double totalPlacedPlayers = round.getSharedPlacementSpot();
        double percentilePlacement = (double) (player.getPlacementValue() - 1) / totalPlacedPlayers;
        percentilePlacement = (1 - percentilePlacement) * 100;
        for(final ScoringPair<Integer> pair : scoreTiers) {
            if(percentilePlacement >= pair.getSetting()) {
                return pair.getPoints();
            }
        }
        return fallThroughScore;
    }

    @Override
    public void addSetting() {
        if(scoreTiers.isEmpty()) {
            scoreTiers.add(new ScoringPair<>(99, 0));
            return;
        }
        ScoringPair<Integer> lastPair = scoreTiers.get(scoreTiers.size() -1);
        ScoringPair<Integer> newLast = new ScoringPair<>(lastPair.getSetting() - 1, fallThroughScore);
        scoreTiers.add(newLast);
    }

    @Override
    public void updateSetting(int index, Integer value) {
        if(index >= scoreTiers.size()) {
            return;
        }
        scoreTiers.get(index).setSetting(value);

        // if you change the setting to something lower than the ones after this,
        // they should all go up, since they have to follow a set order
        if(scoreTiers.size() == index + 1) {
            return; // don't have to update
        }

        if(scoreTiers.get(index + 1).getSetting() < value) {
            return;
        }

        int newSetting = value - 1;
        for(int i = index + 1; i < scoreTiers.size(); i++) {
            scoreTiers.get(i).setSetting(newSetting);
            newSetting--;
        }
    }
}

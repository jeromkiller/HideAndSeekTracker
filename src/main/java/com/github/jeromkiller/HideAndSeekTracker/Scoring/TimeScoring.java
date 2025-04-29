package com.github.jeromkiller.HideAndSeekTracker.Scoring;

import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekPlayer;
import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekRound;
import com.github.jeromkiller.HideAndSeekTracker.Util.TimeUtil;

import java.time.*;

public class TimeScoring extends PointSystem<LocalTime> {
    TimeScoring() {
        super(ScoreType.TIME, false);
    }

    @Override
    public int scorePlayer(HideAndSeekPlayer player, HideAndSeekRound round) {
        final int ticks = player.getTickCount();
        for(final ScoringPair<LocalTime> pair: scoreTiers) {
            final long time_ticks = TimeUtil.timeToTick(pair.getSetting());
            if(ticks < time_ticks) {
                return pair.getPoints();
            }
        }
        return fallThroughScore;
    }

    @Override
    public void addSetting() {
        if(scoreTiers.isEmpty()) {
            scoreTiers.add(new ScoringPair<>(LocalTime.ofSecondOfDay(1), fallThroughScore));
            return;
        }
        ScoringPair<LocalTime> lastPair = scoreTiers.get(scoreTiers.size() - 1);
        ScoringPair<LocalTime> newLast = new ScoringPair<>(lastPair.getSetting().plusSeconds(1), fallThroughScore);
        scoreTiers.add(newLast);
    }

    @Override
    public void updateSetting(int index, LocalTime value) {
        if(index >= scoreTiers.size()) {
            return;
        }
        scoreTiers.get(index).setSetting(value);

        // if you change the setting to something higher than the ones after this,
        // they should all go up, since they have to follow a set order
        if(scoreTiers.size() == index + 1) {
            return; // don't have to update
        }

        final LocalTime nextSetting = scoreTiers.get(index + 1).getSetting();
        if(nextSetting.compareTo(value) > 0) {
            return; // next setting is still smaller higher than current value
        }
    }
}

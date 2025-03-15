package com.github.jeromkiller.HideAndSeekTracker.Scoring;

import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekPlayer;
import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekRound;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HintScoring implements PointSystem {
    final ScoreType scoreType = ScoreType.HINTS;
    List<ScoringPair> scoreTiers = new ArrayList<>();
    int fallThroughScore = 0;

    @Override
    public ScoreType getScoreType() {
        return scoreType;
    }

    @Override
    public int scorePlayer(HideAndSeekPlayer player, HideAndSeekRound round) {
        final int hints = player.getHints();
        for(final ScoringPair pair: scoreTiers) {
            if(hints <= pair.getSetting()) {
                return pair.getPoints();
            }
        }
        return fallThroughScore;
    }

    @Override
    public List<ScoringPair> getScoringPairs() {
        return scoreTiers;
    }

    @Override
    public void addSetting() {
        if(scoreTiers.isEmpty()) {
            scoreTiers.add(new ScoringPair(1, 1));
            return;
        }
        ScoringPair lastPair = scoreTiers.get(scoreTiers.size() -1);
        ScoringPair newLast = new ScoringPair(lastPair.getSetting() + 1, fallThroughScore);
        scoreTiers.add(newLast);
    }

    @Override
    public void deleteSetting(int index) {
        scoreTiers.remove(index);
    }

    @Override
    public void updateSetting(int index, int value) {
        if(index >= scoreTiers.size()) {
            return;
        }
        scoreTiers.get(index).setSetting(value);

        // if you change the setting to something higher than the ones after this,
        // they should all go up, since they have to follow a set order
        if(scoreTiers.size() == index + 1) {
            return; // don't have to update
        }

        if(scoreTiers.get(index + 1).getSetting() > value) {
            return; // next setting is still smaller higher than current value
        }

        int newSetting = value + 1;
        for(int i = index + 1; i < scoreTiers.size(); i++) {
            scoreTiers.get(i).setSetting(newSetting);
            newSetting++;
        }
    }

    @Override
    public void updatePoints(int index, int value) {
        scoreTiers.get(index).setPoints(value);
    }

    @Override
    public void updateFallFallthroughPoints(int value) {
        fallThroughScore = value;
    }

}
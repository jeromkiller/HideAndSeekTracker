package com.github.jeromkiller.HideAndSeekTracker.Scoring;

public abstract class NumberScoring extends PointSystem<Integer> {

    public NumberScoring(ScoreType type, boolean canCalcOnce) {
        super(type, canCalcOnce);
    }

    @Override
    public void addSetting() {
        if(scoreTiers.isEmpty()) {
            scoreTiers.add(new ScoringPair<>(1, 0));
            return;
        }
        ScoringPair<Integer> lastPair = scoreTiers.get(scoreTiers.size() -1);
        ScoringPair<Integer> newLast = new ScoringPair<>(lastPair.getSetting() + 1, fallThroughScore);
        scoreTiers.add(newLast);
    }

    @Override
    public void updateSetting(int index, Integer value) {
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
}

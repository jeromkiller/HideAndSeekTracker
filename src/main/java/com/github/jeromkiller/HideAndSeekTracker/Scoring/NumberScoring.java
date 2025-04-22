package com.github.jeromkiller.HideAndSeekTracker.Scoring;

public abstract class NumberScoring extends PointSystem<Integer> {

    public NumberScoring(ScoreType type, boolean canCalcOnce) {
        super(type, canCalcOnce);
    }

    @Override
    public void addSetting() {
        if(scorePairs.isEmpty()) {
            scorePairs.add(new ScoringPair<>(1, 0));
            return;
        }
        ScoringPair<Integer> lastPair = scorePairs.get(scorePairs.size() -1);
        ScoringPair<Integer> newLast = new ScoringPair<>(lastPair.getSetting() + 1, fallThroughScore);
        scorePairs.add(newLast);
    }

    @Override
    public void updateSetting(int index, Integer value) {
        if(index >= scorePairs.size()) {
            return;
        }
        scorePairs.get(index).setSetting(value);

        // if you change the setting to something higher than the ones after this,
        // they should all go up, since they have to follow a set order
        if(scorePairs.size() == index + 1) {
            return; // don't have to update
        }

        if(scorePairs.get(index + 1).getSetting() > value) {
            return; // next setting is still smaller higher than current value
        }

        int newSetting = value + 1;
        for(int i = index + 1; i < scorePairs.size(); i++) {
            scorePairs.get(i).setSetting(newSetting);
            newSetting++;
        }
    }
}

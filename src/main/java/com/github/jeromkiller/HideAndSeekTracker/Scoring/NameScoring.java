package com.github.jeromkiller.HideAndSeekTracker.Scoring;

import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekPlayer;
import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekRound;

import java.util.Locale;
import java.util.Optional;

public class NameScoring extends PointSystem<String> {
    NameScoring() {
        super(ScoreType.NAME, true);
    }

    @Override
    public int scorePlayer(HideAndSeekPlayer player, HideAndSeekRound round) {
        Optional<ScoringPair<String>> foundPair = scorePairs.stream().filter(
                s -> {return s.getSetting().equals(player.getName());}).findFirst();

        return foundPair.map(ScoringPair::getPoints).orElse(0);
    }

    @Override
    public void addSetting() {
        scorePairs.add(new ScoringPair<>("new_name", 1));
    }

    @Override
    public void updateSetting(int index, String value) {
        if(index >= scorePairs.size()) {
            return;
        }

        String name = value.toLowerCase(Locale.ROOT);
        if(scorePairs.stream().anyMatch(s -> {return s.getSetting().equals(name);})) {
            return; // don't save duplicates
        }

        scorePairs.get(index).setSetting(name);
    }
}
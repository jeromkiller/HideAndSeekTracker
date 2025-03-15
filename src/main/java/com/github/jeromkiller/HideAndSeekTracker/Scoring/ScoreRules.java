package com.github.jeromkiller.HideAndSeekTracker.Scoring;

import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekPlayer;
import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekRound;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ScoreRules {
    private final List<PointSystem> pointSystems = new ArrayList<>();

    public int scorePlayer(HideAndSeekPlayer player, HideAndSeekRound round) {
        final int sum = pointSystems.stream().mapToInt(system -> system.scorePlayer(player, round)).sum();
        return Integer.max(sum, 0);
    }

    public void addSystem() {
        pointSystems.add(new PositionScoring());
    }

    public void deleteSystem(PointSystem system) {
        pointSystems.remove(system);
    }

    public void load(ScoreRules rules) {
        pointSystems.clear();
        pointSystems.addAll(rules.getPointSystems());
    }

    public void changePointSystemCatagory(PointSystem system, PointSystem.ScoreType catagory) {
        PointSystem newSystem = null;
        switch (catagory) {
            case POSITION:
                newSystem = new PositionScoring(system.getScoringPairs(), system.getFallThroughScore());
                break;
            case HINTS:
                newSystem = new HintScoring(system.getScoringPairs(), system.getFallThroughScore());
                break;
        }
        final int index = pointSystems.indexOf(system);
        pointSystems.set(index, newSystem);
    }

    public static ScoreRules getDefaultRules() {
        ScoreRules scoreRules = new ScoreRules();

        PositionScoring posScore = new PositionScoring();
        posScore.getScoringPairs().add(new ScoringPair(1, 6));
        posScore.getScoringPairs().add(new ScoringPair(2, 5));
        posScore.getScoringPairs().add(new ScoringPair(3, 4));
        posScore.setFallThroughScore(3);
        scoreRules.getPointSystems().add(posScore);

        HintScoring hintScore = new HintScoring();
        hintScore.getScoringPairs().add(new ScoringPair(1, 0));
        hintScore.getScoringPairs().add(new ScoringPair(2, -1));
        hintScore.setFallThroughScore(-2);
        scoreRules.getPointSystems().add(hintScore);

        return scoreRules;
    }
}

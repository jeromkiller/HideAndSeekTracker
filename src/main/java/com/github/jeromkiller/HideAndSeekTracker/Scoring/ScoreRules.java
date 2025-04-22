package com.github.jeromkiller.HideAndSeekTracker.Scoring;

import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekPlayer;
import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekRound;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ScoreRules {
    private final List<PointSystem<?>> pointSystems = new ArrayList<>();
    public int scorePlayer(HideAndSeekPlayer player, HideAndSeekRound round) {
        if(!player.hasPlaced()) {
            return 0;
        }
        final int sum = pointSystems.stream().filter(PointSystem::isCalcEveryRound)
                .mapToInt(system -> system.scorePlayer(player, round)).sum();
        return Integer.max(sum, 0);
    }

    public int scorePlayerOnce(HideAndSeekPlayer player) {
        final int sum = pointSystems.stream().filter(system -> {return !system.isCalcEveryRound();})
                .mapToInt(system -> system.scorePlayer(player, null)).sum();
        return sum;
    }

    public void addSystem(PointSystem.ScoreType type) {
        switch (type) {
            case POSITION:
                pointSystems.add(new PositionScoring());
                return;
            case HINTS:
                pointSystems.add(new HintScoring());
                return;
            case NAME:
                pointSystems.add(new NameScoring());
                return;
            case TIME:
                pointSystems.add(new TimeScoring());
                return;
            case PERCENTILE:
                pointSystems.add(new PercentileScoring());
                return;
            default:
                return;
        }
    }

    public void deleteSystem(PointSystem<?> system) {
        pointSystems.remove(system);
    }

    public void load(ScoreRules rules) {
        pointSystems.clear();
        pointSystems.addAll(rules.getPointSystems());
    }

    public void changePointSystemCatagory(PointSystem<?> system, PointSystem.ScoreType catagory) {
        PointSystem<?> newSystem = null;
        switch (catagory) {
            case POSITION:
                newSystem = new PositionScoring();
                break;
            case HINTS:
                newSystem = new HintScoring();
                break;
            case NAME:
                newSystem = new NameScoring();
        }
        final int index = pointSystems.indexOf(system);
        pointSystems.set(index, newSystem);
    }

    public static ScoreRules getDefaultRules() {
        ScoreRules scoreRules = new ScoreRules();

        PositionScoring posScore = new PositionScoring();
        posScore.addScorePair(1, 6);
        posScore.addScorePair(2, 5);
        posScore.addScorePair(3, 4);
        posScore.setFallThroughScore(3);
        scoreRules.getPointSystems().add(posScore);

        HintScoring hintScore = new HintScoring();
        hintScore.addScorePair(1, 0);
        hintScore.addScorePair(2, -1);
        hintScore.setFallThroughScore(-2);
        scoreRules.getPointSystems().add(hintScore);

        return scoreRules;
    }
}

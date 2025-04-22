package com.github.jeromkiller.HideAndSeekTracker.game;

import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekTrackerPlugin;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class HideAndSeekGame {
    private static final Logger log = LoggerFactory.getLogger(HideAndSeekGame.class);

    private HideAndSeekRound activeRound;
    private ArrayList<HideAndSeekRound> pastRounds;
    private final HideAndSeekTrackerPlugin plugin;
    private final HashMap<String, HideAndSeekPlayer> scoreTotals;

    public HideAndSeekGame(HideAndSeekTrackerPlugin plugin)
    {
        this.plugin = plugin;
        this.activeRound = new HideAndSeekRound(plugin, 1);
        this.pastRounds = new ArrayList<>();
        this.scoreTotals = new HashMap<>();
    }

    public int newRound()
    {
        activeRound.endRound();
        pastRounds.add(activeRound);
        ArrayList<String> previousPlayers = new ArrayList<>(activeRound.getParticipants().keySet());
        activeRound = new HideAndSeekRound(plugin, activeRound.getRoundNumber() + 1);
        activeRound.setPlayers(previousPlayers);
        final int savedRoundIndex = pastRounds.size() -1;
        return savedRoundIndex;
    }

    public void startRound() {
        activeRound.startRound();
    }

    public LinkedHashSet<String> setPlayers(List<String> playerNames)
    {
        final LinkedHashSet<String> parsedNames = activeRound.setPlayers(playerNames);
        plugin.getPanel().getGamePanel().updatePlacements();
        return parsedNames;
    }

    public void addPlayerName(String playerNames)
    {
        activeRound.addPlayer(playerNames);
        plugin.getPanel().getGamePanel().updatePlacements();
    }

    public void tick()
    {
        activeRound.tick();
    }

    public void playerFound(String playerName)
    {
        activeRound.playerFound(playerName);
    }

    public int calculatePlayerScore(String playerName) {
        int total = 0;
        HideAndSeekPlayer player = null;
        for(final HideAndSeekRound round : pastRounds) {
            if(!round.getParticipants().containsKey(playerName)) {
                continue;
            }
            player = round.getParticipants().get(playerName);
            total += player.getScore();
        }

        if(activeRound.getParticipants().containsKey(playerName)) {
            player = activeRound.getParticipants().get(playerName);
            total += player.getScore();
        }

        if(player != null) {
            total += plugin.getScoreRules().scorePlayerOnce(player);
        }

        return total;
    }

    public void recalculateAllScores() {
        for(HideAndSeekRound round : pastRounds) {
            round.recalculateScores();
        }
        activeRound.recalculateScores();

        recalculateTotalScores();
    }

    public void recalculateTotalScores() {
        HashSet<String> allPlayers = new HashSet<>(activeRound.getParticipants().keySet());
        for(final HideAndSeekRound round : pastRounds) {
            allPlayers.addAll(round.getParticipants().keySet());
        }

        List<HideAndSeekPlayer> playerScores = new ArrayList<>();
        for(final String playerName : allPlayers) {
            HideAndSeekPlayer playerScore = new HideAndSeekPlayer(playerName);
            playerScore.setScore(calculatePlayerScore(playerName));
            playerScores.add(playerScore);
        }
        playerScores.sort(Comparator.comparingInt(HideAndSeekPlayer::getScore).reversed());

        int internalPlacement = 1;
        int position = 1;
        int prevScore = Integer.MAX_VALUE;
        if(!playerScores.isEmpty()) {
            prevScore = playerScores.get(0).getScore();
        }

        for(final HideAndSeekPlayer player : playerScores) {
            final int score = player.getScore();
            if(score < prevScore) {
                position++;
                prevScore = score;
            }
            final int prevHints = player.getHints();
            final int prevTicks = player.getTickCount();
            player.setStats(internalPlacement, position, prevHints, prevTicks);
            internalPlacement++;
            scoreTotals.put(player.getName(), player);
        }

        // remove inactive players from the total list
        Set<String> activeNames = playerScores.stream().map(p -> p.getName()).collect(Collectors.toSet());
        Set<String> removeNames = new HashSet<>(scoreTotals.keySet());
        removeNames.removeAll(activeNames);
        for(final String removeName : removeNames) {
            scoreTotals.remove(removeName);
        }
    }

    public String totalScoreExport() {
        List<HideAndSeekPlayer> playerList = new ArrayList<>(scoreTotals.values());
        playerList.sort(Comparator.comparingInt(HideAndSeekPlayer::getInternalPlacement));

        StringBuilder exportString = new StringBuilder();
        exportString.append("Point Total:");

        for(final HideAndSeekPlayer player: playerList) {
            StringBuilder exportLine = new StringBuilder();
            final String placementText = player.getPlacementText();
            exportLine.append(placementText);
            if(!Objects.equals(placementText, "DNF")) {
                exportLine.append(" Place");
            }
            int spacing = 12 - exportLine.length();
            if(spacing > 0) {
                exportLine.append(" ".repeat(spacing));
            } else {
                exportLine.append(" ");
            }
            exportLine.append(" - ");
            exportLine.append(player.getName());

            spacing = 32 - exportLine.length();
            if(spacing > 0) {
                exportLine.append(" ".repeat(spacing));
            } else {
                exportLine.append(" ");
            }
            exportLine.append(player.getScore());
            exportLine.append(" points");

            exportString.append("\n");
            exportString.append(exportLine);
        }
        return exportString.toString();
    }

    public void deleteRound(int index) {
        if(index >= pastRounds.size()) {
            return;
        }
        pastRounds.remove(index);

        // renumber the other rounds
        int roundNumber = 1;
        for(HideAndSeekRound round : pastRounds) {
            round.setRoundNumber(roundNumber);
            roundNumber++;
        }
        activeRound.setRoundNumber(roundNumber);
    }
}

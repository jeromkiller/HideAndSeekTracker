package com.github.jeromkiller.HideAndSeekTracker.game;

import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekTrackerPlugin;
import lombok.Data;

import java.util.*;

@Data
public class HideAndSeekRound {
    private int hintsGiven;
    private int placementIndex;
    private int leniencyCounter;
    private int sharedPlacementSpot;
    private int roundNumber;
    private boolean roundStarted;
    private int gameTime;
    private final HashMap<String, HideAndSeekPlayer> participants;

    private final HideAndSeekTrackerPlugin plugin;

    HideAndSeekRound(HideAndSeekTrackerPlugin plugin, int roundNumber) {
        this.hintsGiven = 1;
        this.placementIndex = 0;
        this.leniencyCounter = 0;
        this.sharedPlacementSpot = 0;
        this.roundNumber = roundNumber;
        this.participants = new HashMap<>();
        this.plugin = plugin;
        this.roundStarted = false;
        this.gameTime = 0;
    }

    public String addPlayer(String playerName)
    {
        // list of lowercase names to return without duplicates
        playerName = playerName.toLowerCase();
        if(!participants.containsKey(playerName)) {
            participants.put(playerName, new HideAndSeekPlayer(playerName));
        }
        return playerName;
    }

    public LinkedHashSet<String> setPlayers(List<String> playerNames)
    {
        // add missing players to the list
        LinkedHashSet<String> parsedNames = new LinkedHashSet<>();
        for(String playerName : playerNames) {
            String parsedName = addPlayer(playerName);
            parsedNames.add(parsedName);
        }

        // remove players who's names have been erased
        Set<String> removeNames = new HashSet<>(participants.keySet());
        removeNames.removeAll(parsedNames);
        for(final String removedPlayer : removeNames) {
            participants.remove(removedPlayer);
        }

        return parsedNames;
    }

    public void tick()
    {
        if(leniencyCounter > 0)
        {
            leniencyCounter -= 1;
        }
        if(roundStarted) {
            gameTime++;
        }
        plugin.getPanel().getGamePanel().updateTimer(gameTime);
    }

    public void playerFound(String playerName)
    {
        if(!roundStarted) {
            return;
        }
        playerName = playerName.toLowerCase();

        HideAndSeekPlayer player = participants.get(playerName);
        if(player == null) {
            return;
        }

        if(player.hasPlaced()) {
            return;
        }

        if(leniencyCounter == 0)
        {
            sharedPlacementSpot += 1;
            leniencyCounter = plugin.getSettings().getTickLenience();
        }

        participants.get(playerName).setStats(placementIndex, sharedPlacementSpot, hintsGiven, gameTime);
        final int points = plugin.getScoreRules().scorePlayer(player, this);
        participants.get(playerName).setScore(points);
        placementIndex += 1;
        plugin.getPanel().getGamePanel().updatePlacements();
    }

    public void startRound() {
        roundStarted = true;
    }

    public void endRound() {
        roundStarted = false;
    }

    public String plainTextExport() {
        List<HideAndSeekPlayer> playerList = new ArrayList<>(participants.values());
        playerList.sort(Comparator.comparingInt(HideAndSeekPlayer::getInternalPlacement));

        StringBuilder exportString = new StringBuilder();
        exportString.append("Round ").append(getRoundNumber()).append(": ");

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

    public String devExport(boolean discordExport)
    {
        List<String> exportLines = new ArrayList<>();
        final String seperator = discordExport ? ", " : "\t";

        // get export order from the user list
        LinkedHashSet<String> participantNames = plugin.getPanel().getSetupPanel().getPlayerNameList();

        for(final String playerName :  participantNames)
        {
            HideAndSeekPlayer player = participants.getOrDefault(playerName, new HideAndSeekPlayer(playerName));
            String hints = player.getHints() > 0 ? Integer.toString(player.getHints()) : "DNF";
            String placement = player.getPlacementExportString();
            String newLine = hints + seperator + placement;
            exportLines.add(newLine);
        }
        String exportString = String.join("\n", exportLines);
        if(discordExport)
        {
            exportString = "```\n" + exportString + "\n```";
        }
        return exportString;
    }

    public int getNumPlaced()
    {
        int num = 0;
        for(final HideAndSeekPlayer player : participants.values())
        {
            num += player.getInternalPlacement() < Integer.MAX_VALUE ? 1 : 0;
        }
        return num;
    }

    public int getNumParticipants()
    {
        return participants.size();
    }

    public void recalculateScores()
    {
        for(final HideAndSeekPlayer player : participants.values()) {
            final int score = plugin.getScoreRules().scorePlayer(player, this);
            player.setScore(score);
        }
    }
}

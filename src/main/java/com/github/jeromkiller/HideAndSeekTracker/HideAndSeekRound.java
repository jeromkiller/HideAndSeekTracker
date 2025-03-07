package com.github.jeromkiller.HideAndSeekTracker;

import lombok.Data;

import java.util.*;

@Data
public class HideAndSeekRound {
    private int hintsGiven;
    private int placementIndex;
    private int leniencyCounter;
    private int sharedPlacementSpot;
    private final HashMap<String, HideAndSeekPlayer> participants;

    private final HideAndSeekTrackerPlugin plugin;

    HideAndSeekRound(HideAndSeekTrackerPlugin plugin) {
        this.hintsGiven = 1;
        this.placementIndex = 0;
        this.leniencyCounter = 0;
        this.sharedPlacementSpot = 0;

        this.participants = new HashMap<>();
        this.plugin = plugin;
    }

    public LinkedHashSet<String> addPlayers(List<String> playerNames)
    {
        // list of lowercase names to return without duplicates
        LinkedHashSet<String> parsedNames = new LinkedHashSet<>();
        for(String playerName : playerNames) {
            playerName = playerName.toLowerCase();
            parsedNames.add(playerName);
            if(!participants.containsKey(playerName)) {
                participants.put(playerName, new HideAndSeekPlayer(playerName));
            }
        }
        return parsedNames;
    }

    public LinkedHashSet<String> setPlayers(List<String> playerNames)
    {
        // add missing players to the list
         LinkedHashSet<String> parsedNames = addPlayers(playerNames);

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
    }

    public void playerFound(String playerName)
    {
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

        participants.get(playerName).setStats(placementIndex, sharedPlacementSpot, hintsGiven);
        placementIndex += 1;
        plugin.getPanel().getGamePanel().updatePlacements();
    }

    public String plainTextExport() {
        List<HideAndSeekPlayer> playerList = new ArrayList<>(participants.values());
        playerList.sort(Comparator.comparingInt(HideAndSeekPlayer::getInternalPlacement));

        StringBuilder exportString = new StringBuilder();
        for(final HideAndSeekPlayer player: playerList) {
            if(exportString.length() != 0) {
                exportString.append("\n");
            }
            exportString.append(getPlacementText(player.getPlacementValue()));
            exportString.append(" - ");
            exportString.append(player.getName());
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

    private String getPlacementText(int placement) {
        // speclial cases
        switch(placement) {
            case 0:
                return "DNF\t\t\t";
            case 11:
            case 12:
            case 13:
                return placement + "th Place\t";
        }

        // follow the pattern
        int lastDigit = placement % 10;
        switch (lastDigit) {
            case 1:
                return placement + "st Place\t";
            case 2:
                return placement + "nd Place\t";
            case 3:
                return placement + "rd Place\t";
        }
        return placement + "th Place\t";
    }
}

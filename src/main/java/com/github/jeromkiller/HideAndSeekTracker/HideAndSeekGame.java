package com.github.jeromkiller.HideAndSeekTracker;

import java.util.*;

public class HideAndSeekGame {
    LinkedHashMap<String, HideAndSeekPlayer> participants;
    int hintsGiven;
    int placementIndex;
    HideAndSeekTable table;
    int leniencyTicks;
    int sharedPlacementSpot;

    private final HideAndSeekTrackerConfig config;

    HideAndSeekGame(HideAndSeekTrackerConfig config)
    {
        this.config = config;

        participants = new LinkedHashMap<>();
        hintsGiven = 0;
        placementIndex = 0;
        table = null;
        leniencyTicks = 0;
        sharedPlacementSpot = 0;
    }

    public void newRound()
    {
        hintsGiven = 1;
        placementIndex = 0;
        sharedPlacementSpot = 0;

        for(HideAndSeekPlayer player : participants.values())
        {
            player.reset();
        }
        table.update();
    }

    public void setHintsGiven(int hint)
    {
        hintsGiven = hint;
    }

    public String setPlayers(String[] playerNames)
    {
        //participants.clear();
        for(String playerName : playerNames)
        {
            addPlayer(playerName);
        }
        table.update();
        return generateSyncString();
    }

    public void addPlayer(String playerName)
    {
        playerName = playerName.toLowerCase();

        if(!participants.containsKey(playerName)) {
            participants.put(playerName, new HideAndSeekPlayer(playerName));
            table.update();
        }
    }

    public void setTable(HideAndSeekTable table)
    {
        this.table = table;
    }

    public void tick()
    {
        if(leniencyTicks > 0)
        {
            leniencyTicks -= 1;
        }
    }

    public void playerFound(String playerName)
    {
        playerName = playerName.toLowerCase();

        HideAndSeekPlayer player = participants.get(playerName);
        if(player == null) {
            return;
        }

        if(player.hasPlaced())
            return;

        if(leniencyTicks == 0)
        {
            sharedPlacementSpot += 1;
            leniencyTicks = config.tickLeniency();
        }

        placementIndex += 1;
        participants.get(playerName).setStats(placementIndex, sharedPlacementSpot, hintsGiven);
        table.update();
    }

    private String generateSyncString()
    {
        int hashCode = Arrays.hashCode(participants.keySet().toArray());
        return Integer.toHexString(hashCode);
    }

    public String export(boolean discordExport)
    {
        List<String> exportLines = new ArrayList<>();
        final String seperator = discordExport ? ", " : "\t";


        for(HideAndSeekPlayer player :  participants.values())
        {
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
}

package com.github.jeromkiller.HideAndSeekTracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HideAndSeekGame {
    private static final Logger log = LoggerFactory.getLogger(HideAndSeekGame.class);
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

        this.participants = new LinkedHashMap<>();
        this.hintsGiven = 0;
        this.placementIndex = 0;
        this.table = null;
        this.leniencyTicks = 0;
        this.sharedPlacementSpot = 0;
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
        LinkedHashMap<String, HideAndSeekPlayer> newParticipants = new LinkedHashMap<>();
        for(String playerName : playerNames)
        {
            playerName = playerName.toLowerCase();

            if(participants.containsKey(playerName)) {
                // copy already existing players over
                newParticipants.put(playerName, participants.get(playerName));
            }
            else {
                // create new players if they don't exist yet
                newParticipants.put(playerName, new HideAndSeekPlayer(playerName));
            }
        }
        participants.clear();
        participants.putAll(newParticipants);
        table.update();
        return generateSyncString();
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

        if(player.hasPlaced()) {
            return;
        }

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

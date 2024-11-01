package com.github.jeromkiller.HideAndSeekTracker;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

public class HideAndSeekGame {
    private static final Logger log = LoggerFactory.getLogger(HideAndSeekGame.class);
    private final LinkedHashMap<String, HideAndSeekPlayer> participants;

    private int hintsGiven;
    private int placementIndex;
    private int leniencyCounter;
    private int sharedPlacementSpot;

    private final HideAndSeekTrackerPlugin plugin;

    HideAndSeekGame(HideAndSeekTrackerPlugin plugin)
    {
        this.plugin = plugin;
        this.participants = plugin.getParticipants();
        this.hintsGiven = 0;
        this.placementIndex = 0;
        this.leniencyCounter = 0;
        this.sharedPlacementSpot = 0;
    }

    public void newRound()
    {
        hintsGiven = 1;
        placementIndex = 0;
        leniencyCounter = 0;
        sharedPlacementSpot = 0;

        for(HideAndSeekPlayer player : participants.values())
        {
            player.reset();
        }
        plugin.getPanel().getGamePanel().updatePlacements();
    }

    public void setHintsGiven(int hint)
    {
        hintsGiven = hint;
    }

    public String setPlayers(List<String> playerNames)
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
        plugin.getPanel().getGamePanel().updatePlacements();
        return generateSyncString();
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

        placementIndex += 1;
        participants.get(playerName).setStats(placementIndex, sharedPlacementSpot, hintsGiven);
        plugin.getPanel().getGamePanel().updatePlacements();
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

    public int getNumPlaced()
    {
        int num = 0;
        for(HideAndSeekPlayer player : participants.values())
        {
            num += player.getInternalPlacement() > 0 ? 1 : 0;
        }
        return num;
    }

    public int getNumParticipants()
    {
        return participants.size();
    }
}

package com.github.jeromkiller.HideAndSeekTracker;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Data
public class HideAndSeekGame {
    private static final Logger log = LoggerFactory.getLogger(HideAndSeekGame.class);

    private HideAndSeekRound activeRound;
    private ArrayList<HideAndSeekRound> pastRounds;
    private final HideAndSeekTrackerPlugin plugin;

    HideAndSeekGame(HideAndSeekTrackerPlugin plugin)
    {
        this.plugin = plugin;
        this.activeRound = new HideAndSeekRound(plugin, 1);
        this.pastRounds = new ArrayList<>();
    }

    public int newRound()
    {
        pastRounds.add(activeRound);
        ArrayList<String> previousPlayers = new ArrayList<>(activeRound.getParticipants().keySet());
        activeRound = new HideAndSeekRound(plugin, activeRound.getRoundNumber() + 1);
        activeRound.setPlayers(previousPlayers);
        final int savedRoundIndex = pastRounds.size() -1;
        return savedRoundIndex;
    }

    public LinkedHashSet<String> setPlayers(List<String> playerNames)
    {
        final LinkedHashSet<String> parsedNames = activeRound.setPlayers(playerNames);
        plugin.getPanel().getGamePanel().updatePlacements();
        return parsedNames;
    }

    public LinkedHashSet<String> addPlayerNames(List<String> playerNames)
    {
        final LinkedHashSet<String> parsedNames = activeRound.addPlayers(playerNames);
        plugin.getPanel().getGamePanel().updatePlacements();
        return parsedNames;
    }

    public void tick()
    {
        activeRound.tick();
    }

    public void playerFound(String playerName)
    {
        activeRound.playerFound(playerName);
    }
}

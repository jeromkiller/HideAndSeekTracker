package com.github.jeromkiller.HideAndSeekTracker.Util;

import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekTrackerPlugin;
import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekPlayer;
import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekRound;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
public class roundExport {
    private long id;
    private int hintsGiven;
    private String host;
    private int roundNumber;
    private int gameTime;
    private final List<playerExport> participants;

    public roundExport(HideAndSeekRound round) {
        this.id = round.getId();
        this.hintsGiven = round.getHintsGiven();
        this.host = round.getHost();
        this.roundNumber = round.getRoundNumber();
        this.participants = new ArrayList<>();
        for(HideAndSeekPlayer player : round.getParticipants().values()) {
            this.participants.add(new playerExport(player));
        }
        this.gameTime = round.getGameTime();
    }

    public HideAndSeekRound toHideAndSeekRound(HideAndSeekTrackerPlugin plugin) {
        HashMap<String, HideAndSeekPlayer> players = new HashMap<>();
        for(playerExport player : participants) {
            players.put(player.getName(), player.toHideAndSeekPlayer());
        }

        HideAndSeekRound round = new HideAndSeekRound(id, hintsGiven, 0, 0,
            0, host, roundNumber, true, gameTime, players, plugin);

        round.recalculatePlacements();
        return round;
    }
}

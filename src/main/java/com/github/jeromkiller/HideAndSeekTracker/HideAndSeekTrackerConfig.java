package com.github.jeromkiller.HideAndSeekTracker;

import net.runelite.client.config.*;
import java.awt.*;

@ConfigGroup("hideandseektracker")
public interface HideAndSeekTrackerConfig extends Config{
    String HIDE_UNFINISHED_KEY = "HaS_HideUnfinishedPlayers";
    String DEV_MODE_KEY = "HaS_DevMode";

    @ConfigItem(
            keyName = HIDE_UNFINISHED_KEY,
            name = "Hide Unfinished",
            description = "Hide players who haven't finished from the scoreboard"
    )
    default boolean hideUnfinishedPlayers()
    {
        return false;
    }

    @ConfigItem(
            keyName = DEV_MODE_KEY,
            name = "Developer Mode",
            description = "Enable some development features"
    )
    default boolean DevMode()
    {
        return false;
    }
}

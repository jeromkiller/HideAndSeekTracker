package com.github.jeromkiller.HideAndSeekTracker;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

public class HideAndSeekTrackerPanel extends PluginPanel {
    private final HideAndSeekTrackerPlugin plugin;

    private final JTabbedPane tabPane;
    @Getter
    private final CaptureAreaManagementPanel areaPanel;
    @Getter
    private final GameSetupPanel setupPanel;
    @Getter
    private final GamePanel gamePanel;

    public HideAndSeekTrackerPanel(HideAndSeekTrackerPlugin plugin)
    {
        this.plugin = plugin;

        tabPane = new JTabbedPane();

        setupPanel = new GameSetupPanel(plugin);
        tabPane.add("Setup", setupPanel);

        areaPanel = new CaptureAreaManagementPanel(plugin);
        tabPane.add("Areas", areaPanel);

        gamePanel = new GamePanel(plugin);
        tabPane.add("Game", gamePanel);

        add(tabPane);
    }
}

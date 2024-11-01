package com.github.jeromkiller.HideAndSeekTracker;

import lombok.Getter;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;

@Getter
public class HideAndSeekTrackerPanel extends PluginPanel {

    private final CaptureAreaManagementPanel areaPanel;
    private final GameSetupPanel setupPanel;
    private final GamePanel gamePanel;

    public HideAndSeekTrackerPanel(HideAndSeekTrackerPlugin plugin)
    {
        JTabbedPane tabPane = new JTabbedPane();

        areaPanel = new CaptureAreaManagementPanel(plugin);
        tabPane.add("Areas", areaPanel);

        setupPanel = new GameSetupPanel(plugin);
        tabPane.add("Setup", setupPanel);

        gamePanel = new GamePanel(plugin);
        tabPane.add("Game", gamePanel);

        add(tabPane);
    }
}

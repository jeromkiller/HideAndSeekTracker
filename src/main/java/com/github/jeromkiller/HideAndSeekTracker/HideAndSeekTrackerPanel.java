package com.github.jeromkiller.HideAndSeekTracker;

import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Getter
public class HideAndSeekTrackerPanel extends PluginPanel {

    private final CaptureAreaManagementPanel areaPanel;
    private final GameSetupPanel setupPanel;
    private final GamePanel gamePanel;

    public HideAndSeekTrackerPanel(HideAndSeekTrackerPlugin plugin)
    {
        super(false);

        final int borderWidth = PluginPanel.BORDER_OFFSET;
        setBorder(new EmptyBorder(0, borderWidth, borderWidth, borderWidth));
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JTabbedPane tabPane = new JTabbedPane();

        areaPanel = new CaptureAreaManagementPanel(plugin);
        tabPane.addTab("Areas", new JScrollPane(areaPanel));


        setupPanel = new GameSetupPanel(plugin);
        tabPane.addTab("Setup", new JScrollPane(setupPanel));

        gamePanel = new GamePanel(plugin);
        tabPane.addTab("Game", gamePanel);

        add(tabPane);
    }
}

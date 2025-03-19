package com.github.jeromkiller.HideAndSeekTracker;

import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

@Getter
public class HideAndSeekTrackerPanel extends PluginPanel {

    private final CaptureAreaManagementPanel areaPanel;
    private final GameSetupPanel setupPanel;
    private final GamePanel gamePanel;
    private final ScoringPanel scorePanel;
    private final SettingsPanel settingsPanel;

    private static final ImageIcon COG_ICON;

    static {
        BufferedImage cogIcon = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "config_edit_icon.png");
        COG_ICON = new ImageIcon(cogIcon);
    }

    public HideAndSeekTrackerPanel(HideAndSeekTrackerPlugin plugin)
    {
        super(false);

        final int borderWidth = PluginPanel.BORDER_OFFSET;
        setBorder(new EmptyBorder(0, borderWidth, borderWidth, borderWidth));
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JTabbedPane tabPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

        // maybe replace the text with icons
        areaPanel = new CaptureAreaManagementPanel(plugin);
        tabPane.addTab("Areas", new JScrollPane(areaPanel));

        scorePanel = new ScoringPanel(plugin);
        tabPane.add("Points", new JScrollPane(scorePanel));

        setupPanel = new GameSetupPanel(plugin);
        tabPane.addTab("Players", new JScrollPane(setupPanel));

        gamePanel = new GamePanel(plugin);
        tabPane.addTab("Game", gamePanel);

        settingsPanel = new SettingsPanel(plugin);
        tabPane.addTab("Settings", COG_ICON, settingsPanel, "Change Plugin Settings");
        tabPane.setTabComponentAt(4, new JLabel(COG_ICON));

        add(tabPane);
    }
}

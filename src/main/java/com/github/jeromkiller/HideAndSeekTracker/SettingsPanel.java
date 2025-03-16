package com.github.jeromkiller.HideAndSeekTracker;

import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SettingsPanel extends JPanel {
    private final JSpinner tickLeniency = new JSpinner(new SpinnerNumberModel(2, 0, 100, 1));
    private final JToggleButton showRenderDist = new JToggleButton(OFF_SWITCHER);
    private final JToggleButton hideUnfinishedPlayers = new JToggleButton(OFF_SWITCHER);
    private final JToggleButton useDevMode = new JToggleButton(OFF_SWITCHER);

    private final HideAndSeekTrackerPlugin plugin;
    private final HideAndSeekSettings settings;

    private static final ImageIcon ON_SWITCHER;
    private static final ImageIcon OFF_SWITCHER;

    static
    {
        BufferedImage onSwitcher = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "switcher_on.png");
        ON_SWITCHER = new ImageIcon(onSwitcher);
        OFF_SWITCHER = new ImageIcon(ImageUtil.flipImage(
                ImageUtil.luminanceScale(
                        ImageUtil.grayscaleImage(onSwitcher),
                        0.61f
                ),
                true,
                false
        ));
    }

    SettingsPanel(HideAndSeekTrackerPlugin plugin)
    {
        this.plugin = plugin;
        this.settings = plugin.getSettings();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JPanel contents = new JPanel();
        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 2, 5, 2);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;

        tickLeniency.addChangeListener(e -> settings.setTickLenience((int) tickLeniency.getValue()));
        addSettingRow("Placement Leniency Ticks", tickLeniency, contents, constraints);

        showRenderDist.setSelected(settings.getShowRenderDist());
        showRenderDist.setSelectedIcon(ON_SWITCHER);
        showRenderDist.addItemListener(e -> settings.setShowRenderDist(showRenderDist.isSelected()));
        SwingUtil.removeButtonDecorations(showRenderDist);
        addSettingRow("Show Render Distance", showRenderDist, contents, constraints);

        hideUnfinishedPlayers.setSelected(settings.getHideUnfinished());
        hideUnfinishedPlayers.setSelectedIcon(ON_SWITCHER);
        hideUnfinishedPlayers.addItemListener(e -> settings.setHideUnfinished(hideUnfinishedPlayers.isSelected()));
        SwingUtil.removeButtonDecorations(hideUnfinishedPlayers);
        addSettingRow("Hide Unfinished Players", hideUnfinishedPlayers, contents, constraints);

        useDevMode.setSelected(settings.getDevMode());
        useDevMode.setSelectedIcon(ON_SWITCHER);
        useDevMode.addItemListener(e -> settings.setDevMode(useDevMode.isSelected()));
        SwingUtil.removeButtonDecorations(useDevMode);
        addSettingRow("Use Dev Mode", useDevMode, contents, constraints);


        add(contents, BorderLayout.NORTH);
        loadSettings();
    }

    private void addSettingRow(String text, JComponent component, JPanel container, GridBagConstraints constraints) {
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        container.add(new JLabel(text), constraints);
        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        container.add(component, constraints);
        constraints.gridx = 0;
        constraints.gridy++;
    }

    public void loadSettings() {
        final int tickLeniencySetting = settings.getTickLenience();
        tickLeniency.setValue(tickLeniencySetting);

        final boolean showRenderDistSetting = settings.getShowRenderDist();
        showRenderDist.setSelected(showRenderDistSetting);

        final boolean hideUnfinishedPlayersSetting = settings.getHideUnfinished();
        hideUnfinishedPlayers.setSelected(hideUnfinishedPlayersSetting);

        final boolean useDevModeSetting = settings.getDevMode();
        useDevMode.setSelected(useDevModeSetting);
    }
}

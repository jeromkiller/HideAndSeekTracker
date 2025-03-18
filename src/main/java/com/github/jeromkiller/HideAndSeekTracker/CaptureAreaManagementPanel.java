package com.github.jeromkiller.HideAndSeekTracker;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.PluginErrorPanel;

import javax.swing.*;
import java.awt.*;

public class CaptureAreaManagementPanel extends BasePanel {

    private final PluginErrorPanel noAreasPanel = new PluginErrorPanel();
    private final JPanel areaView = new JPanel(new GridBagLayout());
    private final JButton newArea = new JButton("New Area");
    private final JButton importArea = new JButton("Import from clipboard");
    private final JButton exportVisibleAreas = new JButton("Export visible to clipboard");
    private final JLabel copyStatusLabel = new JLabel("Copied!");

    private final HideAndSeekTrackerPlugin plugin;

    CaptureAreaManagementPanel(HideAndSeekTrackerPlugin plugin)
    {
        this.plugin = plugin;

        setLayout(new BorderLayout());

        areaView.setBackground(ColorScheme.DARK_GRAY_COLOR);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        noAreasPanel.setContent("No Capture areas set", "Add a capture area for people to finnish in");
        noAreasPanel.setVisible(false);

        newArea.addActionListener(e -> plugin.startCaptureAreaCreation());
        importArea.addActionListener(e -> plugin.importCaptureAreaFromClip());
        exportVisibleAreas.addActionListener(e -> exportVisibleAreas());

        areaView.add(noAreasPanel, constraints);
        constraints.gridy++;

        //centerPanel.add(areaView, BorderLayout.CENTER);
        add(areaView, BorderLayout.NORTH);
    }

    public void rebuild()
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        areaView.removeAll();

        for(final CaptureArea captureArea : plugin.getCaptureAreas())
        {
            areaView.add(new ExistingCaptureAreaPanel(plugin, captureArea), constraints);
            constraints.gridy++;

            addSpacer(constraints);
        }

        final boolean empty = constraints.gridy == 0;
        noAreasPanel.setVisible(empty);

        areaView.add(noAreasPanel, constraints);
        constraints.gridy++;

        if(plugin.getCaptureCreationOptions().isCurrentlyCreating())
        {
            areaView.add(new CaptureCreationPanel(plugin), constraints);
        }
        else
        {
            areaView.add(newArea, constraints);
        }
        constraints.gridy++;
        addSpacer(constraints);

        areaView.add(importArea, constraints);
        constraints.gridy++;

        addSpacer(constraints);

        areaView.add(exportVisibleAreas, constraints);
        constraints.gridy++;

        copyStatusLabel.setVisible(false);
        areaView.add(copyStatusLabel, constraints);
        constraints.gridy++;

        repaint();
        revalidate();
    }

    private void addSpacer(GridBagConstraints constraints)
    {
        areaView.add(Box.createRigidArea(new Dimension(0, 5)), constraints);
        constraints.gridy++;
    }

    private void exportVisibleAreas()
    {
        plugin.copyVisibleCaptureAreasToClip();
        copyStatusLabel.setVisible(true);
        Timer hideStatusTimer = new Timer(1000, e -> copyStatusLabel.setVisible(false));
        hideStatusTimer.setRepeats(false);
        hideStatusTimer.start();
    }
}

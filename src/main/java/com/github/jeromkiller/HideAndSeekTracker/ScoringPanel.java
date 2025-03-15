package com.github.jeromkiller.HideAndSeekTracker;

import com.github.jeromkiller.HideAndSeekTracker.Scoring.PointSystem;
import com.github.jeromkiller.HideAndSeekTracker.Scoring.ScoreRules;

import javax.swing.*;
import java.awt.*;

public class ScoringPanel extends JPanel {

    public final HideAndSeekTrackerPlugin plugin;
    public final ScoreRules scoreRules;
    public final JPanel rulesView = new JPanel(new GridBagLayout());

    public ScoringPanel(HideAndSeekTrackerPlugin plugin) {
        this.plugin = plugin;
        this.scoreRules = plugin.getScoreRules();

        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        content.add(rulesView, constraints);
        constraints.gridy++;

        JButton newRulesButton = new JButton("New points/penalty");
        newRulesButton.addActionListener(e -> addNewRule());
        content.add(newRulesButton, constraints);
        newRulesButton.setVisible(false); // Hiding until more rule types are implemented
        constraints.gridy++;

        add(content, BorderLayout.NORTH);
    }

    public void rebuild() {
        rulesView.removeAll();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        for(PointSystem system : scoreRules.getPointSystems()) {
            ScoringSettingPanel panel = new ScoringSettingPanel(plugin, system);
            rulesView.add(panel, constraints);
            constraints.gridy++;
        }

        repaint();
        revalidate();
    }

    private void addNewRule() {
        scoreRules.addSystem();
        rebuild();
        plugin.updateScoreRules();
    }
}

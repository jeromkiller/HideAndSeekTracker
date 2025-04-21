package com.github.jeromkiller.HideAndSeekTracker.Panels.PointsPanel;

import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekTrackerPlugin;
import com.github.jeromkiller.HideAndSeekTracker.Scoring.PointSystem;
import com.github.jeromkiller.HideAndSeekTracker.Scoring.ScoreRules;

import javax.swing.*;
import java.awt.*;

public class ScoringPanel extends JPanel {

    public final HideAndSeekTrackerPlugin plugin;
    public final ScoreRules scoreRules;
    public final JPanel rulesView = new JPanel(new GridBagLayout());
    public boolean creatingSetting;

    public ScoringPanel(HideAndSeekTrackerPlugin plugin) {
        this.plugin = plugin;
        this.scoreRules = plugin.getScoreRules();
        this.creatingSetting = false;

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

        add(content, BorderLayout.NORTH);
    }

    public void rebuild() {
        rulesView.removeAll();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        for(PointSystem<?> system : scoreRules.getPointSystems()) {
            ScoringSettingPanel panel = new ScoringSettingPanel(plugin, system);
            rulesView.add(panel, constraints);
            constraints.gridy++;
        }

        JButton newRulesButton = new JButton("New points/penalty");
        newRulesButton.addActionListener(e -> startNewRuleCreation());
        rulesView.add(newRulesButton, constraints);
        constraints.gridy++;

        NewScoreSettingPanel creationPanel = new NewScoreSettingPanel(plugin, this);
        rulesView.add(creationPanel, constraints);
        constraints.gridy++;

        newRulesButton.setVisible(!creatingSetting);
        creationPanel.setVisible(creatingSetting);

        repaint();
        revalidate();
    }

    private void startNewRuleCreation() {
        creatingSetting = true;
        rebuild();
    }

    public void cancelNewRuleCreation() {
        creatingSetting = false;
        rebuild();
    }

    public void addNewRule(PointSystem.ScoreType scoreType) {
        creatingSetting = false;
        scoreRules.addSystem(scoreType);
        rebuild();
        plugin.updateScoreRules();
    }
}

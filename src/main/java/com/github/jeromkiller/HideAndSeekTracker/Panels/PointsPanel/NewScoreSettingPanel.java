package com.github.jeromkiller.HideAndSeekTracker.Panels.PointsPanel;

import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekTrackerPlugin;
import com.github.jeromkiller.HideAndSeekTracker.Panels.BasePanel;
import com.github.jeromkiller.HideAndSeekTracker.Scoring.PointSystem;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class NewScoreSettingPanel extends BasePanel {
    HideAndSeekTrackerPlugin plugin;
    ScoringPanel parent;

    private final JComboBox<String> scoreTypes;

    NewScoreSettingPanel(HideAndSeekTrackerPlugin plugin, ScoringPanel parent) {
        this.plugin = plugin;
        this.parent = parent;

        setLayout(new BorderLayout());
        setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2),
                new LineBorder(ColorScheme.DARKER_GRAY_COLOR, 1)));

        List<String> scoreTypeNames = new ArrayList<>();
        for(PointSystem.ScoreType st : PointSystem.ScoreType.values()) {
            scoreTypeNames.add(st.toString());
        }

        JPanel content = new JPanel(new GridBagLayout());
        this.add(content);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 1;
        constraints.gridwidth = 2;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);

        scoreTypes = new JComboBox<>(scoreTypeNames.toArray(new String[0]));
        scoreTypes.getModel().setSelectedItem("Select Rule");
        scoreTypes.addActionListener(this::comboBoxChanged);
        content.add(scoreTypes, constraints);

        JLabel deleteSystem = new JLabel();
        setupImageIcon(deleteSystem, "Delete Rule", DELETE_ICON, DELETE_HOVER_ICON, parent::cancelNewRuleCreation);
        constraints.gridx = 2;
        constraints.gridwidth = 1;
        content.add(deleteSystem, constraints);
        constraints.gridy++;

        constraints.gridx = 0;
        content.add(new JLabel("Setting", SwingConstants.CENTER), constraints);
        constraints.gridx = 1;
        content.add(new JLabel("Points", SwingConstants.CENTER), constraints);

        constraints.gridx = 0;
        constraints.gridy++;

        JTextField settingsField = new JTextField("1+");
        settingsField.setEnabled(false);
        content.add(settingsField, constraints);
        constraints.gridx = 1;

        JTextField pointsField = new JTextField("0");
        pointsField.setEnabled(false);
        content.add(pointsField, constraints);
        constraints.gridx = 0;
        constraints.gridy++;
    }

    private void comboBoxChanged(ActionEvent e) {
        final String pickedOption = (String)scoreTypes.getSelectedItem();
        PointSystem.ScoreType scoreType = PointSystem.ScoreType.fromString(pickedOption);
        parent.addNewRule(scoreType);
    }
}

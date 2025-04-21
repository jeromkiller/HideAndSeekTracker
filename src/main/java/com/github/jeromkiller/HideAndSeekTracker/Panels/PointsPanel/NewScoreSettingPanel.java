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

        scoreTypes = new JComboBox<>(scoreTypeNames.toArray(new String[0]));
        scoreTypes.getModel().setSelectedItem("Select Rule");
        scoreTypes.addActionListener(this::comboBoxChanged);
        this.add(scoreTypes);
    }

    private void comboBoxChanged(ActionEvent e) {
        final String pickedOption = (String)scoreTypes.getSelectedItem();
        PointSystem.ScoreType scoreType = PointSystem.ScoreType.fromString(pickedOption);
        parent.addNewRule(scoreType);
    }
}

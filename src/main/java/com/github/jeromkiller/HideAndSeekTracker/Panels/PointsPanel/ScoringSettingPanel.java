package com.github.jeromkiller.HideAndSeekTracker.Panels.PointsPanel;

import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekTrackerPlugin;
import com.github.jeromkiller.HideAndSeekTracker.Panels.BasePanel;
import com.github.jeromkiller.HideAndSeekTracker.Panels.Widgets.PercentileScoreTextEntry;
import com.github.jeromkiller.HideAndSeekTracker.Scoring.PointSystem;
import com.github.jeromkiller.HideAndSeekTracker.Scoring.ScoringPair;
import com.github.jeromkiller.HideAndSeekTracker.Panels.Widgets.NameScoreTextEntry;
import com.github.jeromkiller.HideAndSeekTracker.Panels.Widgets.NumberScoreTextEntry;
import com.github.jeromkiller.HideAndSeekTracker.Panels.Widgets.TimeScoreTextEntry;
import lombok.Data;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class ScoringSettingPanel<T> extends BasePanel {

    private static final NumberFormatter pointsFormatter;

    static {
        pointsFormatter = new NumberFormatter(NumberFormat.getInstance());
        pointsFormatter.setValueClass(Integer.class);
        pointsFormatter.setMinimum(-100);
        pointsFormatter.setMaximum(100);
        pointsFormatter.setAllowsInvalid(true);
        pointsFormatter.setCommitsOnValidEdit(true);
    }

    private static void selectText(FocusEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ((JFormattedTextField)e.getSource()).selectAll();
            }
        });
    }

    // maybe consider moving this class to a separate file
    // if I'm in the mood to refactor this
    // but not right now, figuring out the formatters for this gave me a headache for 2 days straight
    @Data
    public class SettingsRow<T> {
        private final HideAndSeekTrackerPlugin plugin;
        private final int index;
        private final ScoringPair<T> scoringPair;
        private final JFormattedTextField settingBox;
        private final JFormattedTextField pointsBox;
        private final JLabel deleteLabel = new JLabel();

        SettingsRow(HideAndSeekTrackerPlugin plugin, PointSystem<T> pointSystem, ScoringPair<T> scoringPair, int index, T prevValue, JPanel container, GridBagConstraints constraints) {
            this.plugin = plugin;
            this.index = index;
            this.scoringPair = scoringPair;

            switch(pointSystem.getScoreType()) {
                case POSITION:
                case HINTS:
                    settingBox = new NumberScoreTextEntry((int) scoringPair.getSetting(), (int)prevValue + 1);
                    break;
                case PERCENTILE:
                    settingBox = new PercentileScoreTextEntry((int) scoringPair.getSetting(), (int)prevValue - 1);
                    break;
                case TIME:
                    settingBox = new TimeScoreTextEntry((LocalTime) scoringPair.getSetting(), ((LocalTime) prevValue).plusSeconds(1));
                    break;
                case NAME:
                    settingBox = new NameScoreTextEntry((String) scoringPair.getSetting());
                    break;
                default:
                    settingBox = new JFormattedTextField();
            }

            settingBox.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    pointSystem.updateSetting(index, (T) settingBox.getValue());
                    rebuild();
                    plugin.updateScoreRules();
                }
            });

            pointsBox = new JFormattedTextField(pointsFormatter);
            pointsBox.setValue(scoringPair.getPoints());
            pointsBox.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    selectText(e);
                }
                @Override
                public void focusLost(FocusEvent e) {
                    scoringPair.setPoints((Integer) pointsBox.getValue());
                    rebuild();
                    plugin.updateScoreRules();
                }
            });

            setupImageIcon(deleteLabel, "Remove Line", MINUS_ICON, MINUS_HOVER_ICON, () -> {
                pointSystem.deleteSetting(index);
                rebuild();
                plugin.updateScoreRules();
            });

            constraints.gridx = 0;
            container.add(settingBox, constraints);
            constraints.gridx = 1;
            container.add(pointsBox, constraints);
            constraints.gridx = 2;
            container.add(deleteLabel, constraints);
        }
    }

    private final JComboBox<String> scoreTypes;

    private final HideAndSeekTrackerPlugin plugin;
    private final PointSystem<T> pointSystem;
    private final JPanel content = new JPanel(new GridBagLayout());
    private final List<JTextField> settingsRows = new ArrayList<>();

    public ScoringSettingPanel(HideAndSeekTrackerPlugin plugin, PointSystem<T> pointSystem) {
        this.plugin = plugin;
        this.pointSystem = pointSystem;

        setLayout(new BorderLayout());
        setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2),
                new LineBorder(ColorScheme.DARKER_GRAY_COLOR, 1)));

        scoreTypes = new JComboBox<>();
        scoreTypes.getModel().setSelectedItem(pointSystem.getScoreType().toString());

        this.add(content);

        rebuild();
    }

    public void rebuild() {
        content.removeAll();
        settingsRows.clear();

        this.add(content);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 1;
        constraints.gridwidth = 2;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        content.add(scoreTypes, constraints);
        scoreTypes.setEnabled(false); // Disabled until more rule types are implemented

        JLabel deleteSystem = new JLabel();
        setupImageIcon(deleteSystem, "Delete Rule", DELETE_ICON, DELETE_HOVER_ICON, () -> {
            int confirm = JOptionPane.showConfirmDialog(ScoringSettingPanel.this,
                    "Are you sure you want to permanently delete this scoring rule",
                    "Warning", JOptionPane.OK_CANCEL_OPTION);

            if(confirm == 0) {
                plugin.getScoreRules().deleteSystem(pointSystem);
                plugin.getPanel().getScorePanel().rebuild();
                plugin.updateScoreRules();
            }
        });
        constraints.gridx = 2;
        constraints.gridwidth = 1;
        content.add(deleteSystem, constraints);
        constraints.gridy++;

        constraints.gridx = 0;
        constraints.gridwidth = 3;
        JCheckBox calcEveryRoundCheckbox = new JCheckBox(" Calculate Every Round", pointSystem.isCalcEveryRound());
        calcEveryRoundCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                pointSystem.setCalcEveryRound(calcEveryRoundCheckbox.isSelected());
                plugin.updateScoreRules();
            }
        });
        calcEveryRoundCheckbox.setVisible(pointSystem.isCanBeCalculatedOnce());
        content.add(calcEveryRoundCheckbox, constraints);
        constraints.gridy++;
        constraints.gridwidth = 1;


        constraints.gridx = 0;
        content.add(new JLabel("Setting", SwingConstants.CENTER), constraints);
        constraints.gridx = 1;
        content.add(new JLabel("Points", SwingConstants.CENTER), constraints);

        constraints.gridx = 0;
        constraints.gridy++;

        addTextBoxes(pointSystem, constraints);

        repaint();
        revalidate();
    }

    public void addTextBoxes(PointSystem<T> pointSystem, GridBagConstraints constraints) {
        int index = 0;
        T prev_value = null;

        switch(pointSystem.getScoreType()) {
            case POSITION:
            case HINTS: {
                Integer val = 0;
                prev_value = (T) val;
                break;
            }
            case PERCENTILE:
                Integer val = 100;
                prev_value = (T) val;
                break;
            case NAME: {
                prev_value = (T) "";
                break;
            }
            case TIME: {
                prev_value = (T) LocalTime.ofSecondOfDay(0);
                break;
            }
        }

        for(ScoringPair<T> pair : pointSystem.getScorePairs()) {
            SettingsRow<T> row = new SettingsRow<>(plugin, pointSystem, pair, index, prev_value, content, constraints);
            settingsRows.add(row.settingBox);

            prev_value = pair.getSetting();
            constraints.gridy++;
            index++;
        }

        String fallThroughValue = "Others";
        boolean showFallThrough = true;
        switch(pointSystem.getScoreType()) {
            case HINTS:
            case POSITION: {
                fallThroughValue = ((int)prev_value + 1) + "+";
                break;
            }
            case TIME:{
               final LocalTime new_value = ((LocalTime) prev_value).plusSeconds(1);
               fallThroughValue = new_value + "+";
               break;
            }
            case NAME: {
                fallThroughValue = "Others";
                break;
            }
            case PERCENTILE: {
                showFallThrough = (int)prev_value != 0;
                break;
            }
        }
        JTextField fallThroughSetting = new JTextField(fallThroughValue);
        fallThroughSetting.setForeground(ColorScheme.MEDIUM_GRAY_COLOR);
        fallThroughSetting.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                fallThroughSettingSelected();
            }
        });

        JFormattedTextField fallThroughPoints = new JFormattedTextField(pointsFormatter);
        fallThroughPoints.setValue(pointSystem.getFallThroughScore());
        fallThroughPoints.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                selectText(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                pointSystem.setFallThroughScore((int)fallThroughPoints.getValue());
                rebuild();
                plugin.updateScoreRules();
            }
        });

        constraints.gridx = 0;
        content.add(fallThroughSetting, constraints);
        fallThroughSetting.setVisible(showFallThrough);
        constraints.gridx = 1;
        content.add(fallThroughPoints, constraints);
        fallThroughPoints.setVisible(showFallThrough);
    }

    public void fallThroughSettingSelected() {
        pointSystem.addSetting();
        plugin.updateScoreRules();
        this.rebuild();
        settingsRows.get(settingsRows.size() - 1).requestFocus();
    }
}

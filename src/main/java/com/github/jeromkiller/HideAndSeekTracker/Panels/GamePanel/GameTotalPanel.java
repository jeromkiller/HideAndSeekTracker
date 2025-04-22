package com.github.jeromkiller.HideAndSeekTracker.Panels.GamePanel;

import com.github.jeromkiller.HideAndSeekTracker.Util.HideAndSeekSettings;
import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekTrackerPlugin;
import com.github.jeromkiller.HideAndSeekTracker.Panels.BasePanel;
import com.github.jeromkiller.HideAndSeekTracker.Panels.Widgets.HideAndSeekTable;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class GameTotalPanel extends BasePanel {
    private final JLabel roundTitle;
    private final HideAndSeekTable resultTable;
    private final JLabel copyResultButton = new JLabel();
    private final JLabel importResultButton = new JLabel();
    private final JLabel exportResultButton = new JLabel();
    private final JLabel newRoundButton = new JLabel();
    private final JLabel statusLabel = new JLabel(" ");

    @Getter
    private final HideAndSeekTrackerPlugin plugin;
    private final HideAndSeekSettings settings;
    private final GamePanel parentPanel;

    GameTotalPanel(HideAndSeekTrackerPlugin plugin, GamePanel parentPanel) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.parentPanel = parentPanel;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 0, 0, 0));

        JPanel contents = new JPanel();
        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 2, 5, 2);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;

        roundTitle = new JLabel("Score Totals");
        contents.add(roundTitle, constraints);
        constraints.gridx = 1;

        setupImageIcon(newRoundButton, "Start new round",NEW_ROUND_ICON, NEW_ROUND_ICON_HOVER, parentPanel::newRound);
        JPanel roundButtonPanelContainer = new JPanel(new BorderLayout());
        roundButtonPanelContainer.add(newRoundButton, BorderLayout.LINE_END);
        contents.add(roundButtonPanelContainer, constraints);
        constraints.gridy++;

        resultTable = new HideAndSeekTable(plugin.game.getScoreTotals());
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        JScrollPane scrollPane = new JScrollPane(resultTable);
        contents.add(scrollPane, constraints);
        constraints.gridwidth = 1;
        constraints.gridy++;
        constraints.weighty = 0;

        JPanel bottomButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints bottomConstraints = new GridBagConstraints();

        setupImageIcon(copyResultButton, "Copy results to clipboard", COPY_ICON, COPY_ICON_HOVER, this::plainTextExport);
        //setupImageIcon(exportResultButton, "Export round data to clipboard", EXPORT_ICON, EXPORT_ICON_HOVER, () -> {});   // Implement in the future
        //setupImageIcon(importResultButton, "Import round data from clipboard", IMPORT_ICON, IMPORT_ICON_HOVER, () -> {}); // Implement in the future

        bottomConstraints.gridx = 0;
        bottomConstraints.ipadx = 4;
        bottomButtonPanel.add(copyResultButton, bottomConstraints);
        bottomConstraints.gridx = 1;
        bottomButtonPanel.add(exportResultButton, bottomConstraints);
        exportResultButton.setVisible(false);   // for future implementation
        bottomConstraints.gridx = 2;
        bottomButtonPanel.add(importResultButton, bottomConstraints);
        importResultButton.setVisible(false);

        JPanel bottomButtonWrapper = new JPanel(new BorderLayout());
        bottomButtonWrapper.add(bottomButtonPanel, BorderLayout.LINE_END);
        constraints.gridx = 1;
        contents.add(bottomButtonWrapper, constraints);
        constraints.gridy++;

        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(0, 2, 0, 2);
        contents.add(statusLabel, constraints);
        statusLabel.setHorizontalAlignment(JLabel.RIGHT);

        add(contents);
        updateHidePlayers();
        updatePlacements();
    }

    private void plainTextExport() {
        final String exportString = plugin.game.totalScoreExport();
        final StringSelection selection = new StringSelection(exportString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        showCopiedText();
    }

    private void showCopiedText() {
        statusLabel.setText("Copied to clipboard!");
        Timer hideStatusTimer = new Timer(1000, e -> statusLabel.setText(" "));
        hideStatusTimer.setRepeats(false);
        hideStatusTimer.start();
    }

    public void updatePlacements()
    {
        plugin.game.recalculateTotalScores();
        SwingUtilities.invokeLater(resultTable::update);
    }

    public void updateHidePlayers() {
        final boolean hide = settings.getHideUnfinished();
        resultTable.enableHidenPlayerFilter(hide);
    }
}

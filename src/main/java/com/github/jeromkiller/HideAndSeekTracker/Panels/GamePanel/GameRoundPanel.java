package com.github.jeromkiller.HideAndSeekTracker.Panels.GamePanel;

import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekRound;
import com.github.jeromkiller.HideAndSeekTracker.Util.HideAndSeekSettings;
import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekTrackerPlugin;
import com.github.jeromkiller.HideAndSeekTracker.Panels.BasePanel;
import com.github.jeromkiller.HideAndSeekTracker.Panels.Widgets.HideAndSeekTable;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class GameRoundPanel extends BasePanel {
    private final JLabel roundTitle;
    private final JButton btnDevExportDiscord;
    private final JButton btnDevExportDirect;
    private final JSpinner hintCount;
    private final HideAndSeekTable resultTable;
    private final JLabel numFinished = new JLabel("999/999 Finished");
    private final JLabel copyResultButton = new JLabel();
    private final JLabel importResultButton = new JLabel();
    private final JLabel exportResultButton = new JLabel();
    private final JLabel newRoundButton = new JLabel();
    private final JLabel deleteRoundButton = new JLabel();
    private final JLabel statusLabel = new JLabel(" ");
    private final JButton btnStartRound;
    private final JLabel roundTimeLabel = new JLabel();

    @Getter
    private final HideAndSeekRound gameRound;
    private final HideAndSeekTrackerPlugin plugin;
    private final GamePanel parentPanel;
    private final HideAndSeekSettings settings;

    @Getter
    private boolean roundFinished = false;

    GameRoundPanel(HideAndSeekTrackerPlugin plugin, GamePanel parentPanel, HideAndSeekRound round) {
        this.plugin = plugin;
        this.gameRound = round;
        this.parentPanel = parentPanel;
        this.settings = plugin.getSettings();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 0, 0, 1));

        JPanel contents = new JPanel();
        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 2, 5, 2);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;

        roundTitle = new JLabel();
        contents.add(roundTitle, constraints);

        JPanel roundButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints roundButtonConstraints = new GridBagConstraints();

        setupImageIcon(deleteRoundButton, "Delete this round", DELETE_ROUND_ICON, DELETE_ROUND_ICON_HOVER, this::deleteRound);
        setupImageIcon(newRoundButton, "Start new round",NEW_ROUND_ICON, NEW_ROUND_ICON_HOVER, parentPanel::newRound);
        roundButtonConstraints.anchor = GridBagConstraints.EAST;
        roundButtonConstraints.gridx = 0;
        roundButtonPanel.add(deleteRoundButton, roundButtonConstraints);
        roundButtonConstraints.gridx = 1;
        roundButtonPanel.add(newRoundButton, roundButtonConstraints);

        deleteRoundButton.setVisible(false);

        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        JPanel roundButtonPanelContainer = new JPanel(new BorderLayout());
        roundButtonPanelContainer.add(roundButtonPanel, BorderLayout.LINE_END);
        contents.add(roundButtonPanelContainer, constraints);

        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy++;

        btnDevExportDirect = new JButton("Export (dev)");
        btnDevExportDirect.addActionListener(e -> devExport(false));
        contents.add(btnDevExportDirect, constraints);

        constraints.gridx = 1;
        btnDevExportDiscord = new JButton("Export Discord");
        btnDevExportDiscord.addActionListener(e -> devExport(true));
        contents.add(btnDevExportDiscord, constraints);
        constraints.gridy++;

        if(!settings.getDevMode()) {
            btnDevExportDiscord.setVisible(false);
            btnDevExportDirect.setVisible(false);
        }

        constraints.gridx = 0;
        contents.add(new JLabel("Round Time:"), constraints);
        constraints.gridx = 1;
        btnStartRound = new JButton("Start Round");
        btnStartRound.addActionListener(e -> startRound());
        contents.add(btnStartRound, constraints);
        roundTimeLabel.setFont(FontManager.getDefaultFont());
        roundTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        roundTimeLabel.setVisible(false);
        roundTimeLabel.setBorder(new CompoundBorder(new LineBorder(ColorScheme.BORDER_COLOR), new EmptyBorder(1,0,0,0)));
        contents.add(roundTimeLabel, constraints);
        updateRoundTimer(0);
        constraints.gridy++;
        constraints.gridx = 0;

        JLabel txt_hints = new JLabel("Hints Given:");
        txt_hints.setFont(FontManager.getRunescapeFont());
        txt_hints.setHorizontalAlignment(SwingConstants.LEFT);
        contents.add(txt_hints, constraints);

        constraints.gridx = 1;
        hintCount = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        hintCount.addChangeListener(e -> updateHintCount());
        contents.add(hintCount, constraints);
        constraints.gridy++;
        constraints.gridx = 0;

        resultTable = new HideAndSeekTable(gameRound.getParticipants());
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        JScrollPane scrollPane = new JScrollPane(resultTable);
        contents.add(scrollPane, constraints);
        constraints.gridwidth = 1;
        constraints.gridy++;
        constraints.weighty = 0;
        constraints.weightx = 0;

        contents.add(numFinished, constraints);

        JPanel bottomButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints bottomConstraints = new GridBagConstraints();

        setupImageIcon(copyResultButton, "Copy results to clipboard", COPY_ICON, COPY_ICON_HOVER, this::plainTextExport);
        setupImageIcon(exportResultButton, "Export round data to clipboard", EXPORT_ICON, EXPORT_ICON_HOVER, this::exportRound);       // Implement in the future
        setupImageIcon(importResultButton, "Import round data from clipboard", IMPORT_ICON, IMPORT_ICON_HOVER, plugin::importRoundFromClip);     // Implement in the future

        bottomConstraints.gridx = 0;
        bottomConstraints.ipadx = 4;
        bottomButtonPanel.add(copyResultButton, bottomConstraints);
        bottomConstraints.gridx = 1;
        bottomButtonPanel.add(exportResultButton, bottomConstraints);
        bottomConstraints.gridx = 2;
        bottomButtonPanel.add(importResultButton, bottomConstraints);

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
        updateRoundLabel();
    }

    private void plainTextExport() {
        final String exportString = gameRound.plainTextExport();
        final StringSelection selection = new StringSelection(exportString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        setCopiedText("Copied to clipboard");
    }

    private void devExport(boolean discordExport)
    {
        final String exportString = gameRound.devExport(discordExport);
        final StringSelection selection = new StringSelection(exportString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        setCopiedText("Copied to clipboard");
    }

    private void setCopiedText(String text) {
        statusLabel.setText(text);
        Timer hideStatusTimer = new Timer(1000, e -> statusLabel.setText(" "));
        hideStatusTimer.setRepeats(false);
        hideStatusTimer.start();
    }

    private void updateHintCount()
    {
        gameRound.setHintsGiven((Integer) hintCount.getValue());
    }

    public void updatePlacements()
    {
        SwingUtilities.invokeLater(() -> {
            resultTable.update();
            updateNumFinished();
        });
    }

    private void updateNumFinished()
    {
        final int numPlaced = gameRound.getNumPlaced();
        final int numPlayers = gameRound.getNumParticipants();

        int roundFinishedPercentage = 0;
        if(numPlayers > 0) {
            roundFinishedPercentage = (int) (((double)numPlaced / (double)numPlayers) * 100);
        }
        String placedString = gameRound.getNumPlaced() +
                " / " +
                gameRound.getNumParticipants() +
                " Finished (" +
                roundFinishedPercentage +
                "%)";
        numFinished.setText(placedString);
    }

    public void updateRoundLabel() {
        String roundText = "Round: " + gameRound.getRoundNumber();
        if(!roundFinished) {
            roundText += " (Active)";
        }
        roundTitle.setText(roundText);
    }

    public void roundFinished() {
        roundFinished = true;
        updateRoundLabel();
        hintCount.setEnabled(false);
        deleteRoundButton.setVisible(true);
        newRoundButton.setVisible(false);
        btnStartRound.setVisible(false);
        roundTimeLabel.setVisible(true);
    }

    public void updateDevMode() {
        final boolean enable = settings.getDevMode();
        btnDevExportDiscord.setVisible(enable);
        btnDevExportDirect.setVisible(enable);
    }

    public void updateHidePlayers() {
        final boolean hide = settings.getHideUnfinished();
        resultTable.enableHidenPlayerFilter(hide);
    }

    public void deleteRound() {
        int confirm = JOptionPane.showConfirmDialog(GameRoundPanel.this,
                "Are you sure you want to permanently delete this round?",
                "Warning", JOptionPane.OK_CANCEL_OPTION);

        if(confirm != 0) {
            return;
        }

        final int roundNumber = gameRound.getRoundNumber() - 1;
        plugin.game.deleteRound(roundNumber);
        parentPanel.deleteRound(roundNumber);
        parentPanel.relabelRounds();
    }

    public void startRound() {
        plugin.game.startRound();
        btnStartRound.setVisible(false);
        roundTimeLabel.setVisible(true);
    }

    public void updateRoundTimer(int ticks) {
        SwingUtilities.invokeLater(() -> {
            roundTimeLabel.setText(ticksToTime(ticks));
        });
    }

    public void updateHints(int hints) {
        SwingUtilities.invokeLater(() -> {
            hintCount.setValue(hints);
        });
    }

    public String ticksToTime(int ticks) {
        int hours = ticks / 6000;
        ticks = ticks % 6000;
        int minutes = ticks / 100;
        ticks = ticks % 100;
        double rest = ticks * 0.6;
        return String.format("%02d:%02d:%04.1f ", hours, minutes, rest);
    }

    public void exportRound() {
        plugin.exportRoundToClip(java.util.List.of(gameRound.getRoundNumber() -1));
        setCopiedText("Exported round to Clipboard!");
    }
}

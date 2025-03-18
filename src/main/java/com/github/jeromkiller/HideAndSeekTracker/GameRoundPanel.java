package com.github.jeromkiller.HideAndSeekTracker;

import lombok.Getter;
import net.runelite.client.ui.FontManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

    @Getter
    private final HideAndSeekRound gameRound;
    private final HideAndSeekTrackerPlugin plugin;
    private final GamePanel parentPanel;
    private final HideAndSeekSettings settings;

    @Getter
    private boolean roundFinished = false;

    GameRoundPanel(HideAndSeekTrackerPlugin plugin, GamePanel parentPanel) {
        this.plugin = plugin;
        this.gameRound = plugin.game.getActiveRound();
        this.parentPanel = parentPanel;
        this.settings = plugin.getSettings();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 0, 0, 0));

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
        constraints.weighty = 1;
        JScrollPane scrollPane = new JScrollPane(resultTable);
        contents.add(scrollPane, constraints);
        constraints.gridwidth = 1;
        constraints.gridy++;
        constraints.weighty = 0;

        contents.add(numFinished, constraints);

        JPanel bottomButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints bottomConstraints = new GridBagConstraints();

        setupImageIcon(copyResultButton, "Copy results to clipboard", COPY_ICON, COPY_ICON_HOVER, this::plainTextExport);
        setupImageIcon(exportResultButton, "Export round data to clipboard", EXPORT_ICON, EXPORT_ICON_HOVER, () -> System.out.println("Implement in future"));
        setupImageIcon(importResultButton, "Import round data from clipboard", IMPORT_ICON, IMPORT_ICON_HOVER, () -> System.out.println("Implement in future"));

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
        updateRoundLabel();
    }

    private void plainTextExport() {
        final String exportString = gameRound.plainTextExport();
        final StringSelection selection = new StringSelection(exportString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        showCopiedText();
    }

    private void devExport(boolean discordExport)
    {
        final String exportString = gameRound.devExport(discordExport);
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
        final int roundNumber = gameRound.getRoundNumber() - 1;
        plugin.game.deleteRound(roundNumber);
        parentPanel.deleteRound(roundNumber);
        parentPanel.relabelRounds();
    }
}

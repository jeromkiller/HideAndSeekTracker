package com.github.jeromkiller.HideAndSeekTracker;

import lombok.Getter;
import net.runelite.client.ui.FontManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class GameRoundPanel extends JPanel {
    private final JLabel roundTitle;
    private final JSpinner hintCount;
    private final HideAndSeekTable resultTable;
    private final JLabel numFinished = new JLabel("999/999 Finished");

    @Getter
    private final HideAndSeekRound gameRound;
    private final HideAndSeekTrackerPlugin plugin;

    GameRoundPanel(HideAndSeekTrackerPlugin plugin) {
        this.plugin = plugin;
        this.gameRound = plugin.game.getActiveRound();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JPanel contents = new JPanel();
        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 2, 5, 2);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;

        roundTitle = new JLabel("Active Round");
        contents.add(roundTitle, constraints);
        constraints.gridwidth = 1;
        constraints.gridy++;


        JButton exportDirect = new JButton("Export Direct");
        exportDirect.addActionListener(e -> export(false));
        contents.add(exportDirect, constraints);

        constraints.gridx = 1;
        JButton exportDiscord = new JButton("Export Discord");
        exportDiscord.addActionListener(e -> export(true));
        contents.add(exportDiscord, constraints);
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
        constraints.weighty = 1;
        JScrollPane scrollPane = new JScrollPane(resultTable);
        contents.add(scrollPane, constraints);
        constraints.gridwidth = 1;
        constraints.gridy++;
        constraints.weighty = 0;

        contents.add(numFinished, constraints);

        add(contents);
        updatePlacements();
    }

    private void export(boolean discordExport)
    {
        final String exportString = gameRound.export(discordExport);
        final StringSelection selection = new StringSelection(exportString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
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
        String numPlaced = gameRound.getNumPlaced() +
                " / " +
                gameRound.getNumParticipants() +
                " Finished";
        numFinished.setText(numPlaced);
    }

    public void roundFinished(String RoundName) {
        roundTitle.setText(RoundName);
        hintCount.setEnabled(false);
    }
}

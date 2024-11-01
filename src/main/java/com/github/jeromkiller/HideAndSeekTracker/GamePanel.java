package com.github.jeromkiller.HideAndSeekTracker;

import net.runelite.client.ui.FontManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class GamePanel extends JPanel {
    private final HideAndSeekTrackerPlugin plugin;

    private final JSpinner hintCount;
    private final HideAndSeekTable resultTable;
    private final JButton exportDirect = new JButton("Export Direct");
    private final JButton exportDiscord = new JButton("Export Discord");
    private final JButton resetGame = new JButton("Reset");
    private final JLabel numFinished = new JLabel("999/999 Finished");

    GamePanel(HideAndSeekTrackerPlugin plugin)
    {
        this.plugin = plugin;

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

        exportDirect.addActionListener(e -> export(false));
        contents.add(exportDirect, constraints);

        constraints.gridx = 1;
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

        resultTable = new HideAndSeekTable(plugin.getParticipants());
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

        constraints.gridx = 1;
        resetGame.addActionListener(e -> reset());
        contents.add(resetGame, constraints);
        constraints.gridx = 0;
        constraints.gridy++;

        add(contents, BorderLayout.NORTH);
    }

    private void export(boolean discordExport)
    {
        final String exportString = plugin.game.export(discordExport);
        final StringSelection selection = new StringSelection(exportString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    private void reset()
    {
        plugin.game.newRound();
        hintCount.setValue(1);
    }

    private void updateHintCount()
    {
        plugin.game.setHintsGiven((Integer) hintCount.getValue());
    }

    public void updatePlacements()
    {
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            resultTable.update();
            updateNumFinished();
        }});
    }

    private void updateNumFinished()
    {
        StringBuilder numPlacedBuilder = new StringBuilder();
        numPlacedBuilder.append(plugin.game.getNumPlaced());
        numPlacedBuilder.append(" / ");
        numPlacedBuilder.append(plugin.game.getNumParticipants());
        numPlacedBuilder.append(" Finished");
        numFinished.setText(numPlacedBuilder.toString());
    }
}

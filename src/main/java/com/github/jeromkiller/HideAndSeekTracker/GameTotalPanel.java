package com.github.jeromkiller.HideAndSeekTracker;

import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class GameTotalPanel extends JPanel {
    private final JLabel roundTitle;
    private final HideAndSeekTable resultTable;

    @Getter
    private final HideAndSeekTrackerPlugin plugin;
    private final HideAndSeekSettings settings;

    GameTotalPanel(HideAndSeekTrackerPlugin plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();

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

        roundTitle = new JLabel("Score Totals");
        contents.add(roundTitle, constraints);
        constraints.gridwidth = 1;
        constraints.gridy++;


        JButton exportDirect = new JButton("Export Text");
        exportDirect.addActionListener(e -> plainTextExport());
        contents.add(exportDirect, constraints);
        constraints.gridy++;

        resultTable = new HideAndSeekTable(plugin.game.getScoreTotals());
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        JScrollPane scrollPane = new JScrollPane(resultTable);
        contents.add(scrollPane, constraints);
        constraints.gridwidth = 1;
        constraints.gridy++;
        constraints.weighty = 0;

        add(contents);
        updateHidePlayers();
        updatePlacements();
    }

    private void plainTextExport() {
        final String exportString = plugin.game.totalScoreExport();
        final StringSelection selection = new StringSelection(exportString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }


    public void updatePlacements()
    {
        plugin.game.recalculateTotalScores();
        SwingUtilities.invokeLater(() -> {
            resultTable.update();
        });
    }

    public void updateHidePlayers() {
        final boolean hide = settings.getHideUnfinished();
        resultTable.enableHidenPlayerFilter(hide);
    }
}

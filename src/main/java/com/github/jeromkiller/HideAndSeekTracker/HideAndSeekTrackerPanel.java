package com.github.jeromkiller.HideAndSeekTracker;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

public class HideAndSeekTrackerPanel extends PluginPanel {
    private final HideAndSeekTrackerPlugin plugin;

    private JLabel lbl_syncCode;
    private final JSpinner spn_hintCount;
    private HideAndSeekTable tbl_resultTable;

    public HideAndSeekTrackerPanel(HideAndSeekTrackerPlugin plugin)
    {
        this.plugin = plugin;

        setLayout(new GridBagLayout());
        // Create GridBagConstraints object to set component constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;

        // Set title constraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 2, 5, 2); // add padding
        JLabel txt_title = new JLabel("Hide and Seek");
        txt_title.setFont(FontManager.getDefaultBoldFont());
        txt_title.setHorizontalAlignment(SwingConstants.LEFT);
        add(txt_title, gbc);

        // Copy placers in range to clipboard
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JButton btn_loadPlayers = new JButton("Copy In Range Player Names");
        btn_loadPlayers.addActionListener(e -> exportPlayerNames());
        add(btn_loadPlayers, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        lbl_syncCode = new JLabel("Playernames Hash:");
        lbl_syncCode.setHorizontalAlignment(SwingConstants.LEFT);
        add(lbl_syncCode, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        lbl_syncCode = new JLabel("Not Synced");
        lbl_syncCode.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lbl_syncCode, gbc);

        // Add the Hint Counter
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel txt_hints = new JLabel("Hints Given:");
        txt_hints.setFont(FontManager.getRunescapeFont());
        txt_hints.setHorizontalAlignment(SwingConstants.LEFT);
        add(txt_hints, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        spn_hintCount = new JSpinner(new SpinnerNumberModel(1,1,10, 1));
        spn_hintCount.addChangeListener(e -> updateHintCount());
        updateHintCount();
        add(spn_hintCount, gbc);


        // Export button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        JButton btn_exportButton = new JButton("Export Direct");
        btn_exportButton.addActionListener(e -> export(false));
        add(btn_exportButton, gbc);

        // Export button
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        btn_exportButton = new JButton("Export Discord");
        btn_exportButton.addActionListener(e -> export(true));
        add(btn_exportButton, gbc);


        // Initializing the Table
        tbl_resultTable = new HideAndSeekTable(plugin.game.participants);
        tbl_resultTable.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(tbl_resultTable);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        // Reset button
        gbc.gridx = 1;
        gbc.gridy = 8;
        JButton btn_resetButton = new JButton("Reset");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btn_resetButton.addActionListener(e -> reset());
        add(btn_resetButton, gbc);

    }

    private void updateHintCount()
    {
        plugin.game.setHintsGiven((Integer) spn_hintCount.getValue());
    }

    public void setSyncString(String syncString)
    {
        lbl_syncCode.setText(syncString);
        tbl_resultTable.update();
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
        spn_hintCount.setValue(1);
    }

    private void exportPlayerNames()
    {
        List<String> inRangePlayers = plugin.getInRangePlayers();
        String exportString = String.join("\n", inRangePlayers);
        final StringSelection selection = new StringSelection(exportString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    public HideAndSeekTable getTable()
    {
        return tbl_resultTable;
    }
}

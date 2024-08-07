package com.github.jeromkiller.HideAndSeekTracker;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HideAndSeekTrackerPanel extends PluginPanel {
    private HideAndSeekTrackerPlugin plugin;

    private JButton btn_loadPlayers;
    private JLabel lbl_syncCode;
    private JSpinner spn_hintCount;
    private JButton btn_resetButton;
    private JButton btn_exportButton;
    //private HideAndSeekTableModel table_model;
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

        // Add the sync button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        btn_loadPlayers = new JButton("Load Player List");
        btn_loadPlayers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                syncPlayers();
            }
        });
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

//        // Add the Round Counter
//        gbc.gridx = 0;
//        gbc.gridy = 3;
//        gbc.gridwidth = 1;
//        JLabel txt_round = new JLabel("Round:");
//        add(txt_round, gbc);
//
//        gbc.gridx = 1;
//        gbc.gridy = 3;
//        spn_roundCount = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
//        spn_roundCount.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                updateRoundCount();
//            }
//        });
//        updateRoundCount();
//        add(spn_roundCount, gbc);

        // Add the Hint Counter
        gbc.gridx = 0;
        gbc.gridy = 4;
        //gbc.insets = new Insets(5, 0, 5, 0); // add padding
        JLabel txt_hints = new JLabel("Hints Given:");
        txt_hints.setFont(FontManager.getRunescapeFont());
        txt_hints.setHorizontalAlignment(SwingConstants.LEFT);
        add(txt_hints, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        spn_hintCount = new JSpinner(new SpinnerNumberModel(1,1,10, 1));
        spn_hintCount.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateHintCount();
            }
        });
        updateHintCount();
        add(spn_hintCount, gbc);


        // Export button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        btn_exportButton = new JButton("Export Direct");
        btn_exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                export(false);
            }
        });
        add(btn_exportButton, gbc);

        // Export button
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        btn_exportButton = new JButton("Export Discord");
        btn_exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                export(true);
            }
        });
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
        btn_resetButton = new JButton("Reset");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btn_resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        add(btn_resetButton, gbc);

    }

    private void updateHintCount()
    {
        plugin.game.setHintsGiven((Integer) spn_hintCount.getValue());
    }

    private void syncPlayers()
    {
        String syncString = plugin.loadStartingPlayers();
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

    public HideAndSeekTable getTable()
    {
        return tbl_resultTable;
    }
}

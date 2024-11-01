package com.github.jeromkiller.HideAndSeekTracker;

import joptsimple.internal.Strings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GameSetupPanel extends JPanel {
    private final JSpinner tickLeniency = new JSpinner(new SpinnerNumberModel(2, 0, 100, 1));
    private final JTextArea playerNames = new JTextArea();
    private final JLabel playerNamesHash = new JLabel();
    private final JCheckBox showRenderDist = new JCheckBox();
    private final JLabel copyStatus = new JLabel();

    private final HideAndSeekTrackerPlugin plugin;
    private final HideAndSeekSettings settings;

    GameSetupPanel(HideAndSeekTrackerPlugin plugin)
    {
        this.plugin = plugin;
        this.settings = plugin.getSettings();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JPanel contents = new JPanel();
        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 2, 5, 2);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        final JLabel leniencyLabel = new JLabel("Placement Leniency");
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridwidth = 1;
        contents.add(leniencyLabel, constraints);

        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridx = 1;
        tickLeniency.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                changeTickLeniency();
            }
        });
        contents.add(tickLeniency, constraints);
        constraints.gridy++;

        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        final JLabel showRenderDistLabel = new JLabel("Show Render Distance");
        contents.add(showRenderDistLabel, constraints);

        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridx = 1;
        showRenderDist.setSelected(settings.getShowRenderDist());
        showRenderDist.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                changeShowRenderDist();
            }
        });
        contents.add(showRenderDist, constraints);
        constraints.gridy++;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridwidth = 2;

        final JLabel playerNameLabel = new JLabel("Participant Names:");
        contents.add(playerNameLabel, constraints);
        constraints.gridy++;

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        playerNames.setRows(10);
        playerNames.setMinimumSize(new Dimension(0, 200));
        playerNames.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // nothing
            }

            @Override
            public void focusLost(FocusEvent e) {
                changePlayerNames();
            }
        });
        contents.add(playerNames, constraints);
        constraints.gridy++;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JButton copyPlayerNames = new JButton("Copy Player Names In Area(s)");
        copyPlayerNames.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getInAreaPlayers();
            }
        });
        contents.add(copyPlayerNames, constraints);
        constraints.gridy++;

        copyStatus.setVisible(false);
        contents.add(copyStatus, constraints);

        add(contents, BorderLayout.NORTH);
        loadSettings();
    }

    private void changePlayerNames() {
        ArrayList<String> nameList = new ArrayList<>(List.of(
                playerNames.getText()
                .replaceAll("\\r", "")
                .split("\\n")));
        // we don't need empty namelists
        if(!nameList.isEmpty()) {
            if(Strings.isNullOrEmpty(nameList.get(nameList.size() -1))) {
                nameList.clear();
            }
        }
        settings.setPlayerNames(nameList);
    }

    private void changeTickLeniency() {
        final int tickLeniencySetting = (int) tickLeniency.getValue();
        settings.setTickLenience(tickLeniencySetting);
    }

    private void changeShowRenderDist() {
        final boolean show = showRenderDist.isSelected();
        settings.setShowRenderDist(show);
    }

    public void loadSettings() {
        final int tickLeniencySetting = settings.getTickLenience();
        tickLeniency.setValue(tickLeniencySetting);

        final boolean showRenderDistSetting = settings.getShowRenderDist();
        showRenderDist.setSelected(showRenderDistSetting);

        final String playerNameString = String.join(System.lineSeparator(), settings.getPlayerNames());
        playerNames.setText(playerNameString);
    }

    private void getInAreaPlayers()
    {
        final List<String> inAreaPlayers = plugin.getInRangePlayers();
        final int numPlayers = inAreaPlayers.size();
        if(numPlayers == 0) {
            copyStatus.setText("No players found in area(s)");
        } else {
            exportPlayerNames(inAreaPlayers);
            copyStatus.setText(String.format("Copied %d names to clipboard", numPlayers));
        }

        copyStatus.setVisible(true);
        Timer hideTimer = new Timer(1000, e -> {copyStatus.setVisible(false);});
        hideTimer.setRepeats(false);
        hideTimer.start();
    }

    private void exportPlayerNames(List<String> playerNames)
    {
        List<String> inRangePlayers = playerNames;
        String exportString = String.join("\n", inRangePlayers);
        final StringSelection selection = new StringSelection(exportString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

}

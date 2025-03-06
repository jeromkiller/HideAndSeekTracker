package com.github.jeromkiller.HideAndSeekTracker;

import joptsimple.internal.Strings;
import lombok.Getter;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class GameSetupPanel extends JPanel {
    private final JSpinner tickLeniency = new JSpinner(new SpinnerNumberModel(2, 0, 100, 1));
    private final JTextArea playerNames = new JTextArea();
    private final JToggleButton showRenderDist = new JToggleButton(OFF_SWITCHER);
    private final JToggleButton autoFillButton = new JToggleButton(OFF_SWITCHER);
    private final JLabel notSavedWarning = new JLabel("Names not saved!");
    private final JLabel statusLabel = new JLabel();

    @Getter
    private final LinkedHashSet<String> playerNameList = new LinkedHashSet<>();

    private final HideAndSeekTrackerPlugin plugin;
    private final HideAndSeekSettings settings;
    private boolean isAutomaticUpdate;

    private static final ImageIcon ON_SWITCHER;
    private static final ImageIcon OFF_SWITCHER;

    static
    {
        BufferedImage onSwitcher = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "switcher_on.png");
        ON_SWITCHER = new ImageIcon(onSwitcher);
        OFF_SWITCHER = new ImageIcon(ImageUtil.flipImage(
                ImageUtil.luminanceScale(
                        ImageUtil.grayscaleImage(onSwitcher),
                        0.61f
                ),
                true,
                false
        ));
    }

    GameSetupPanel(HideAndSeekTrackerPlugin plugin)
    {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.isAutomaticUpdate = true;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JPanel contents = new JPanel();
        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 2, 5, 2);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        final JLabel leniencyLabel = new JLabel("Placement Leniency Ticks");
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
        showRenderDist.setSelectedIcon(ON_SWITCHER);
        showRenderDist.addItemListener(e -> changeShowRenderDist());
        SwingUtil.removeButtonDecorations(showRenderDist);
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
        Border border = BorderFactory.createLineBorder(ColorScheme.BORDER_COLOR );
        playerNames.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(3, 5, 3, 5)));

        playerNames.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                enableNotSavedWarning();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                enableNotSavedWarning();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // nothing
            }
        });
        contents.add(playerNames, constraints);
        constraints.gridy++;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        final JButton saveNamesButton = new JButton("Save Participant Names");
        saveNamesButton.addActionListener(e -> changePlayerNames());
        contents.add(saveNamesButton, constraints);
        constraints.gridy++;

        //contents.add(copyPlayerNames, constraints);
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        final JLabel autoFillLabel = new JLabel("Automatically Fill Names");
        contents.add(autoFillLabel, constraints);

        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        autoFillButton.setSelectedIcon(ON_SWITCHER);
        autoFillButton.addItemListener(e -> changeAutoFill());
        SwingUtil.removeButtonDecorations(autoFillButton);
        contents.add(autoFillButton, constraints);
        constraints.gridwidth = 2;
        constraints.gridx = 0;
        constraints.gridy++;

        constraints.anchor = GridBagConstraints.WEST;
        statusLabel.setVisible(false);
        contents.add(statusLabel, constraints);
        constraints.gridy++;

        notSavedWarning.setVisible(false);
        contents.add(notSavedWarning, constraints);
        constraints.gridy++;

        add(contents, BorderLayout.NORTH);
        loadSettings();

        isAutomaticUpdate = false;
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

        final LinkedHashSet<String> setNames = plugin.setPlayerNames(nameList);
        final int numRemoved = nameList.size() - setNames.size();
        if(numRemoved > 0) {
            setStatusLabel(String.format("Removed %d duplicates", numRemoved));
        }

        notSavedWarning.setVisible(false);
    }

    private void changeTickLeniency() {
        final int tickLeniencySetting = (int) tickLeniency.getValue();
        settings.setTickLenience(tickLeniencySetting);
    }

    private void changeShowRenderDist() {
        final boolean show = showRenderDist.isSelected();
        settings.setShowRenderDist(show);
    }

    private void changeAutoFill() {
        if(notSavedWarning.isVisible())
        {
            changePlayerNames();
        }
        final boolean autofill = autoFillButton.isSelected();
        plugin.setAutofillNames(autofill);
        playerNames.setEditable(!autofill);
    }

    public void loadSettings() {
        final int tickLeniencySetting = settings.getTickLenience();
        tickLeniency.setValue(tickLeniencySetting);

        final boolean showRenderDistSetting = settings.getShowRenderDist();
        showRenderDist.setSelected(showRenderDistSetting);

        loadPlayerNames(settings.getPlayerNames());
    }

    private void getInAreaPlayers()
    {
        final List<String> inAreaPlayers = plugin.getInRangePlayers();
        final int numPlayers = inAreaPlayers.size();
        if(numPlayers == 0) {
            setStatusLabel("No players found in area(s)");
        } else {
            exportPlayerNames(inAreaPlayers);
            setStatusLabel(String.format("Copied %d names to clipboard", numPlayers));
        }
    }

    private void enableNotSavedWarning()
    {
        if(!isAutomaticUpdate) {
            notSavedWarning.setVisible(true);
        }
    }

    private void setStatusLabel(String statusText)
    {
        statusLabel.setText(statusText);
        statusLabel.setVisible(true);
        Timer hideTimer = new Timer(1000, e -> {
            statusLabel.setVisible(false);});
        hideTimer.setRepeats(false);
        hideTimer.start();
    }

    public void loadPlayerNames(LinkedHashSet<String> names)
    {
        isAutomaticUpdate = true;
        playerNameList.clear();
        playerNameList.addAll(names);
        final String playerNameString = String.join(System.lineSeparator(), playerNameList);
        playerNames.setText(playerNameString);
        isAutomaticUpdate = false;

        settings.setPlayerNames(playerNameList);
    }

    public void addPlayerNames(LinkedHashSet<String> names)
    {
        isAutomaticUpdate = true;
        playerNameList.addAll(names);
        final String playerNameString = String.join(System.lineSeparator(), playerNameList);
        playerNames.setText(playerNameString);
        isAutomaticUpdate = false;

        settings.setPlayerNames(playerNameList);
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

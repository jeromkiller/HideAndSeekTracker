package com.github.jeromkiller.HideAndSeekTracker;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedList;

public class GamePanel extends JPanel {
    private final HideAndSeekTrackerPlugin plugin;

    private int currentCardIndex;

    private final JPanel cardsPanel;
    private final CardLayout roundCards;
    private final LinkedList<GameRoundPanel> roundPanels;
    private final JButton prevTableButton;
    private final JButton activeGameButton;
    private final JButton nextTableButton;

    private GameRoundPanel activeRoundPanel;

    GamePanel(HideAndSeekTrackerPlugin plugin)
    {
        this.plugin = plugin;
        this.currentCardIndex = 0;
        this.roundPanels = new LinkedList<>();

        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(5, 0, 10, 0));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        JPanel topButtons = new JPanel();
        topButtons.setLayout(new GridBagLayout());
        GridBagConstraints topButtonConstraints = new GridBagConstraints();
        topButtonConstraints.fill = GridBagConstraints.NONE;

        prevTableButton = new JButton("<-");
        prevTableButton.addActionListener(e -> prevRound());

        activeGameButton = new JButton("Active Game");
        activeGameButton.addActionListener(e -> activeRound());

        nextTableButton = new JButton("->");
        nextTableButton.addActionListener(e -> nextRound());

        topButtons.add(prevTableButton, topButtonConstraints);
        topButtonConstraints.gridx = 1;
        topButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
        topButtons.add(activeGameButton, topButtonConstraints);
        topButtonConstraints.gridx = 2;
        topButtonConstraints.fill = GridBagConstraints.NONE;
        topButtons.add(nextTableButton, topButtonConstraints);
        this.add(topButtons, constraints);
        constraints.gridy++;

        roundCards = new CardLayout();
        cardsPanel = new JPanel(roundCards);
        cardsPanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, ColorScheme.DARK_GRAY_COLOR),
                BorderFactory.createMatteBorder(0, 1, 1, 1, ColorScheme.BORDER_COLOR)));

        activeRoundPanel = new GameRoundPanel(plugin);
        addRoundPanel(activeRoundPanel);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        this.add(cardsPanel, constraints);

        constraints.gridy++;
        constraints.weighty = 0;
        JButton newRoundButton = new JButton("New Round");
        newRoundButton.addActionListener(e -> newRound());
        this.add(newRoundButton, constraints);

        updateCardFlipButtons();
    }

    private void addRoundPanel(GameRoundPanel panel) {
        cardsPanel.add(activeRoundPanel);
        roundPanels.add(activeRoundPanel);
    }

    private void prevRound() {
        roundCards.previous(cardsPanel);
        currentCardIndex -= 1;
        updateCardFlipButtons();
    }

    private void activeRound() {
        roundCards.last(cardsPanel);
        currentCardIndex = roundPanels.size() - 1;
        updateCardFlipButtons();
    }

    private void nextRound() {
        roundCards.next(cardsPanel);
        currentCardIndex += 1;
        updateCardFlipButtons();
    }

    private void newRound() {
        final int savedRoundIndex = plugin.game.newRound();
        activeRoundPanel.roundFinished("Round: " + (savedRoundIndex + 1));
        activeRoundPanel = new GameRoundPanel(plugin);
        addRoundPanel(activeRoundPanel);
        activeRound();
    }

    private void updateCardFlipButtons(){
        prevTableButton.setEnabled(currentCardIndex > 0);
        nextTableButton.setEnabled(currentCardIndex < roundPanels.size() - 1);
    }

    public void updatePlacements() {
        activeRoundPanel.updatePlacements();
    }

    public void updateDevModeSetting() {
        for(GameRoundPanel panel : roundPanels){
            panel.updateDevMode();
        }
    }

    public void updateHidePlayerSetting() {
        for(GameRoundPanel panel : roundPanels){
            panel.updateHidePlayers();
        }
    }
}

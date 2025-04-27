package com.github.jeromkiller.HideAndSeekTracker.Panels.GamePanel;


import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekTrackerPlugin;
import com.github.jeromkiller.HideAndSeekTracker.Panels.BasePanel;
import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekRound;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedList;

public class GamePanel extends BasePanel {
    private final HideAndSeekTrackerPlugin plugin;

    private int currentCardIndex;

    private final JPanel cardsPanel;
    private final CardLayout roundCards;
    private final LinkedList<GameRoundPanel> roundPanels;
    private final JLabel prevTableButton = new JLabel();
    private final JLabel nextTableButton = new JLabel();
    private final JButton activeRoundButton;
    private final JButton scoreTotalButton;

    @Getter
    private GameRoundPanel activeRoundPanel;
    private final GameTotalPanel scoreTotalPanel;

    public GamePanel(HideAndSeekTrackerPlugin plugin)
    {
        this.plugin = plugin;
        this.currentCardIndex = 0;
        this.roundPanels = new LinkedList<>();

        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(5, 0, 0, 0));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        JPanel topButtons = new JPanel();
        topButtons.setLayout(new GridBagLayout());
        GridBagConstraints topButtonConstraints = new GridBagConstraints();
        topButtonConstraints.fill = GridBagConstraints.NONE;

        setupImageIcon(prevTableButton, "View previous round", ARROW_LEFT_ICON, ARROW_LEFT_HOVER_ICON, () -> {
            if(prevTableButton.isEnabled()) {
                prevRound();
            }
        });

        activeRoundButton = new JButton("Active Round");
        activeRoundButton.addActionListener(e -> activeRound());

        scoreTotalButton = new JButton("Score Total");
        scoreTotalButton.addActionListener(e -> scoreTotal());

        setupImageIcon(nextTableButton, "View next round", ARROW_RIGHT_ICON, ARROW_RIGHT_HOVER_ICON, () -> {
            if(nextTableButton.isEnabled()) {
                nextRound();
            }
        });

        topButtonConstraints.fill = GridBagConstraints.NONE;
        topButtonConstraints.weightx = 0;
        topButtons.add(prevTableButton, topButtonConstraints);
        topButtonConstraints.gridx = 1;
        topButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
        topButtonConstraints.weightx = 1;
        topButtons.add(activeRoundButton, topButtonConstraints);
        topButtonConstraints.gridx = 2;
        topButtons.add(scoreTotalButton, topButtonConstraints);
        topButtonConstraints.gridx = 3;
        topButtonConstraints.weightx = 0;
        topButtonConstraints.fill = GridBagConstraints.NONE;
        topButtons.add(nextTableButton, topButtonConstraints);
        this.add(topButtons, constraints);
        constraints.gridy++;

        roundCards = new CardLayout();
        cardsPanel = new JPanel(roundCards);

        scoreTotalPanel = new GameTotalPanel(plugin, this);
        cardsPanel.add(scoreTotalPanel);

        activeRoundPanel = new GameRoundPanel(plugin, this, plugin.game.getActiveRound());
        addRoundPanel(activeRoundPanel);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        this.add(cardsPanel, constraints);

        activeRound();
    }

    private void addRoundPanel(JPanel panel) {
        cardsPanel.add(activeRoundPanel, cardsPanel.getComponentCount() - 1);
        roundPanels.add(activeRoundPanel);
    }

    public void importRound(HideAndSeekRound round) {
        GameRoundPanel importedPanel = new GameRoundPanel(plugin, this, round);
        importedPanel.roundFinished();
        importedPanel.updateRoundTimer(round.getGameTime());
        importedPanel.updateHints(round.getHintsGiven());

        // add before active round
        cardsPanel.add(importedPanel, cardsPanel.getComponentCount() - 2);
        roundPanels.add(roundPanels.size() - 1, importedPanel);

        activeRound();
    }

    private void firstRound() {
        roundCards.first(cardsPanel);
        currentCardIndex = 0;
        updateCardFlipButtons();
    }

    private void navTo(int index) {
        if(index >= cardsPanel.getComponentCount()) {
            return;
        }

        firstRound();
        for(int i = 0; i < index; i++) {
            roundCards.next(cardsPanel);
            currentCardIndex++;
        }
        updateCardFlipButtons();
    }

    private void prevRound() {
        roundCards.previous(cardsPanel);
        currentCardIndex -= 1;
        updateCardFlipButtons();
    }

    private void activeRound() {
        roundCards.last(cardsPanel);
        roundCards.previous(cardsPanel);
        currentCardIndex = cardsPanel.getComponentCount() - 2;
        updateCardFlipButtons();
    }

    private void scoreTotal() {
        roundCards.last(cardsPanel);
        currentCardIndex = cardsPanel.getComponentCount() - 1;
        updateCardFlipButtons();
    }

    private void nextRound() {
        roundCards.next(cardsPanel);
        currentCardIndex += 1;
        updateCardFlipButtons();
    }

    public void newRound() {
        plugin.game.newRound();
        activeRoundPanel.roundFinished();
        activeRoundPanel = new GameRoundPanel(plugin, this, plugin.game.getActiveRound());
        addRoundPanel(activeRoundPanel);
        activeRound();
    }

    private void updateCardFlipButtons(){
        final boolean notAtFirst = currentCardIndex > 0;
        final boolean notAtLast = currentCardIndex < cardsPanel.getComponentCount() - 1;
        final boolean notAtSecondToLast = currentCardIndex != cardsPanel.getComponentCount() - 2;
        prevTableButton.setEnabled(notAtFirst);
        nextTableButton.setEnabled(notAtLast);
        activeRoundButton.setEnabled(notAtSecondToLast);
        scoreTotalButton.setEnabled(notAtLast);
    }

    public void updatePlacements() {
        activeRoundPanel.updatePlacements();
        scoreTotalPanel.updatePlacements();
    }

    public void updateAllPlacements() {
        updatePlacements();
        for(GameRoundPanel panel : roundPanels) {
            panel.updatePlacements();
        }
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

    public void deleteRound(int index) {
        roundPanels.remove(index);
        cardsPanel.remove(index);
        navTo(index);
        invalidate();
        repaint();
    }

    public void relabelRounds() {
        for(GameRoundPanel panel : roundPanels) {
            panel.updateRoundLabel();
        }
    }

    public void updateTimer(int tick) {
        activeRoundPanel.updateRoundTimer(tick);
    }
}

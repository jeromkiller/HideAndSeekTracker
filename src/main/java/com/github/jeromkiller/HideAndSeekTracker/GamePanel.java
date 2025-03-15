package com.github.jeromkiller.HideAndSeekTracker;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class GamePanel extends JPanel {
    private final HideAndSeekTrackerPlugin plugin;

    private int currentCardIndex;

    private final JPanel cardsPanel;
    private final CardLayout roundCards;
    private final LinkedList<GameRoundPanel> roundPanels;
    private final JLabel prevTableButton;
    private final JLabel nextTableButton;
    private final JButton activeRoundButton;
    private final JButton scoreTotalButton;

    private GameRoundPanel activeRoundPanel;
    private final GameTotalPanel scoreTotalPanel;

    private static final ImageIcon ARROW_LEFT_ICON;
    private static final ImageIcon ARROW_LEFT_HOVER_ICON;
    private static final ImageIcon ARROW_RIGHT_ICON;
    private static final ImageIcon ARROW_RIGHT_HOVER_ICON;

    static {
        final BufferedImage arrowLeftImg = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "arrow_left_icon.png");
        final BufferedImage arrowLeftImgHover = ImageUtil.luminanceOffset(arrowLeftImg, -150);
        ARROW_LEFT_ICON = new ImageIcon(arrowLeftImg);
        ARROW_LEFT_HOVER_ICON = new ImageIcon(arrowLeftImgHover);

        final BufferedImage arrowRightImg = ImageUtil.flipImage(arrowLeftImg, true, false);
        final BufferedImage arrowRightImgHover = ImageUtil.flipImage(arrowLeftImgHover, true, false);
        ARROW_RIGHT_ICON = new ImageIcon(arrowRightImg);
        ARROW_RIGHT_HOVER_ICON = new ImageIcon(arrowRightImgHover);
    }


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

        prevTableButton = new JLabel(ARROW_LEFT_ICON);
        prevTableButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(prevTableButton.isEnabled()) {
                prevRound();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                prevTableButton.setIcon(ARROW_LEFT_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                prevTableButton.setIcon(ARROW_LEFT_ICON);
            }
        });

        activeRoundButton = new JButton("Active Round");
        activeRoundButton.addActionListener(e -> activeRound());

        scoreTotalButton = new JButton("Score Total");
        scoreTotalButton.addActionListener(e -> scoreTotal());

        nextTableButton = new JLabel(ARROW_RIGHT_ICON);
        nextTableButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(nextTableButton.isEnabled()) {
                    nextRound();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                nextTableButton.setIcon(ARROW_RIGHT_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                nextTableButton.setIcon(ARROW_RIGHT_ICON);
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
        cardsPanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, ColorScheme.DARK_GRAY_COLOR),
                BorderFactory.createMatteBorder(1, 1, 1, 1, ColorScheme.BORDER_COLOR)));

        scoreTotalPanel = new GameTotalPanel(plugin);
        cardsPanel.add(scoreTotalPanel);

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

        activeRound();
    }

    private void addRoundPanel(JPanel panel) {
        cardsPanel.add(activeRoundPanel, cardsPanel.getComponentCount() - 1);
        roundPanels.add(activeRoundPanel);
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

    private void newRound() {
        final int savedRoundIndex = plugin.game.newRound();
        activeRoundPanel.roundFinished("Round: " + (savedRoundIndex + 1));
        activeRoundPanel = new GameRoundPanel(plugin);
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
}

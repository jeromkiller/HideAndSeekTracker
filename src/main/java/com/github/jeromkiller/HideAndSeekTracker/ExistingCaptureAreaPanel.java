package com.github.jeromkiller.HideAndSeekTracker;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ColorUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ExistingCaptureAreaPanel extends CaptureAreaPanel
{
    private final JLabel colorIndicator = new JLabel();
    //private final JLabel fillColorIndicator = new JLabel();
    private final JLabel labelIndicator = new JLabel();
    private final JLabel statusLabel = new JLabel();
    private final JLabel shareLabel = new JLabel();
    private final JLabel visibilityLabel = new JLabel();
    private final JLabel deleteLabel = new JLabel();

    private final FlatTextField nameInput = new FlatTextField();
    private final JLabel save = new JLabel("Save");
    private final JLabel cancel = new JLabel("Cancel");
    private final JLabel rename = new JLabel("Rename");

    private final HideAndSeekTrackerPlugin plugin;
    private final CaptureArea captureArea;

    private boolean visible;
    private boolean showLabel;

    public ExistingCaptureAreaPanel(HideAndSeekTrackerPlugin plugin, CaptureArea captureArea)
    {
        this.plugin = plugin;
        this.captureArea = captureArea;
        this.visible = captureArea.isAreaVisible();
        this.showLabel = captureArea.isLabelVisible();

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JPanel nameWrapper = new JPanel(new BorderLayout());
        nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        nameWrapper.setBorder(NAME_BOTTOM_BORDER);

        JPanel nameActions = new JPanel(new BorderLayout(3,0));
        nameActions.setBorder(new EmptyBorder(0, 0, 0, 8));
        nameActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        save.setVisible(false);
        save.setFont(FontManager.getRunescapeSmallFont());
        save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
        save.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                save();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
            }
        });

        cancel.setVisible(false);
        cancel.setFont(FontManager.getRunescapeSmallFont());
        cancel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
        cancel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                cancel();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                cancel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                cancel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
            }
        });

        rename.setFont(FontManager.getRunescapeSmallFont());
        rename.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
        rename.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                nameInput.setEditable(true);
                updateNameActions(true);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                rename.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker().darker());
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                rename.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
            }
        });

        nameActions.add(save, BorderLayout.EAST);
        nameActions.add(cancel, BorderLayout.WEST);
        nameActions.add(rename, BorderLayout.CENTER);

        nameInput.setText(captureArea.getLabel());
        nameInput.setBorder(null);
        nameInput.setEditable(false);
        nameInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        nameInput.setPreferredSize(new Dimension(0, 24));
        nameInput.getTextField().setForeground(Color.WHITE);
        nameInput.getTextField().setBorder(new EmptyBorder(0, 8, 0, 0));
        nameInput.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    save();
                }
                else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    cancel();
                }
            }
        });
        nameInput.getTextField().addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                preview(true);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                preview(false);
            }
        });

        nameWrapper.add(nameInput, BorderLayout.CENTER);
        nameWrapper.add(nameActions, BorderLayout.EAST);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBorder(new EmptyBorder(8, 0, 8, 0));
        bottomContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        colorIndicator.setToolTipText("Edit area color");
        colorIndicator.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                openBorderColorPicker();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                colorIndicator.setIcon(COLOR_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                colorIndicator.setIcon(COLOR_ICON);
            }
        });

        labelIndicator.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                toggleLabelling(!showLabel);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                labelIndicator.setIcon(showLabel ? LABEL_HOVER_ICON : NO_LABEL_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                labelIndicator.setIcon(showLabel ? LABEL_ICON : NO_LABEL_ICON);
            }
        });

        leftActions.add(colorIndicator);
        leftActions.add(labelIndicator);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        statusLabel.setText("Copied!");
        statusLabel.setVisible(false);

        shareLabel.setIcon(COPY_AREA_ICON);
        shareLabel.setToolTipText("Copy area to clipboard");
        shareLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                plugin.copyCaptureAreaToClip(captureArea);
                showCopiedStatus();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                shareLabel.setIcon(COPY_AREA_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                shareLabel.setIcon(COPY_AREA_ICON);
            }
        });

        visibilityLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                toggle(!visible);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                visibilityLabel.setIcon(visible ? VISIBLE_HOVER_ICON : INVISIBLE_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                updateVisibility();
            }
        });

        deleteLabel.setIcon(DELETE_ICON);
        deleteLabel.setToolTipText("Delete capture area");
        deleteLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                int confirm = JOptionPane.showConfirmDialog(ExistingCaptureAreaPanel.this,
                        "Are you sure you want to permanently delete this capture area?",
                        "Warning", JOptionPane.OK_CANCEL_OPTION);

                if (confirm == 0)
                {
                    plugin.deleteCaptureArea(captureArea);
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                deleteLabel.setIcon(DELETE_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                deleteLabel.setIcon(DELETE_ICON);
            }
        });

        rightActions.add(statusLabel);
        rightActions.add(shareLabel);
        rightActions.add(visibilityLabel);
        rightActions.add(deleteLabel);

        bottomContainer.add(leftActions, BorderLayout.WEST);
        bottomContainer.add(rightActions, BorderLayout.EAST);
        bottomContainer.setPreferredSize(new Dimension(0, 37));

        add(nameWrapper, BorderLayout.NORTH);
        add(bottomContainer, BorderLayout.CENTER);

        updateVisibility();
        updateBorder();
        updateLabelling();
    }

    private void preview(boolean on)
    {
        if(visible) {
            return;
        }
        captureArea.setAreaVisible(on);
    }

    private void toggle(boolean on)
    {
        visible = on;
        captureArea.setAreaVisible(on);
        captureArea.setAreaActive(on);
        plugin.updateCaptureAreas();
        updateVisibility();
    }

    private void toggleLabelling(boolean on)
    {
        showLabel = on;
        captureArea.setLabelVisible(on);
        plugin.updateCaptureAreas();
        updateLabelling();
    }

    private void save()
    {
        captureArea.setLabel(nameInput.getText());
        plugin.updateCaptureAreas();

        nameInput.setEditable(false);
        updateNameActions(false);
        requestFocusInWindow();
    }

    private void cancel()
    {
        nameInput.setEditable(false);
        nameInput.setText(captureArea.getLabel());
        updateNameActions(false);
        requestFocusInWindow();
    }

    private void updateNameActions(boolean saveAndCancel)
    {
        save.setVisible(saveAndCancel);
        cancel.setVisible(saveAndCancel);
        rename.setVisible(!saveAndCancel);

        if (saveAndCancel)
        {
            nameInput.getTextField().requestFocusInWindow();
            nameInput.getTextField().selectAll();
        }
    }

    private void updateVisibility()
    {
        visibilityLabel.setIcon(visible ? VISIBLE_ICON : INVISIBLE_ICON);
        visibilityLabel.setToolTipText(visible ? "Hide capture area" : "Show capture area");
    }

    private void updateLabelling()
    {
        labelIndicator.setIcon(showLabel ? LABEL_ICON : NO_LABEL_ICON);
        labelIndicator.setToolTipText(showLabel ? "Hide label" : "Show label");
    }

    private void updateBorder()
    {
        Color color = captureArea.getColor();
        colorIndicator.setBorder(new MatteBorder(0, 0, 3, 0, ColorUtil.colorWithAlpha(color, MAX_ALPHA)));

        colorIndicator.setIcon(COLOR_ICON);
    }

    private void openBorderColorPicker()
    {
        final Color color = captureArea.getColor();
        RuneliteColorPicker colorPicker = plugin.getColorPickerManager().create(
                SwingUtilities.windowForComponent(this),
                color,
                captureArea.getLabel() + " Border",
                false);
        colorPicker.setLocationRelativeTo(this);
        colorPicker.setOnColorChange(c ->
        {
            captureArea.setColor(c);
            updateBorder();
        });
        colorPicker.setOnClose(c -> plugin.updateCaptureAreas());
        colorPicker.setVisible(true);
    }

    private void showCopiedStatus()
    {
        statusLabel.setVisible(true);
        Timer hideTimer = new Timer(1000, e -> hideCopiedStatus());
        hideTimer.setRepeats(false);
        hideTimer.start();
    }

    private void hideCopiedStatus()
    {
        statusLabel.setVisible(false);
    }
}

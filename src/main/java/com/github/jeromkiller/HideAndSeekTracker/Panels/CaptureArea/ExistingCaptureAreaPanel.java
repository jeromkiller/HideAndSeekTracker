package com.github.jeromkiller.HideAndSeekTracker.Panels.CaptureArea;

import com.github.jeromkiller.HideAndSeekTracker.game.CaptureArea;
import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekTrackerPlugin;
import com.github.jeromkiller.HideAndSeekTracker.Panels.BasePanel;
import com.github.jeromkiller.HideAndSeekTracker.Panels.Widgets.BlinklessToggleButton;
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

public class ExistingCaptureAreaPanel extends BasePanel {
    private final JLabel colorIndicator = new JLabel();
    private final JLabel statusLabel = new JLabel();
    private final JLabel shareLabel = new JLabel();
    private final JLabel deleteLabel = new JLabel();
    private final BlinklessToggleButton visibilityToggle;// = new JToggleButton(INVISIBLE_ICON);
    private final BlinklessToggleButton labelToggle;// = new JToggleButton(NO_LABEL_ICON);

    private final FlatTextField nameInput = new FlatTextField();
    private final JLabel save = new JLabel("Save");
    private final JLabel cancel = new JLabel("Cancel");
    private final JLabel rename = new JLabel("Rename");

    private final HideAndSeekTrackerPlugin plugin;
    private final CaptureArea captureArea;

    public ExistingCaptureAreaPanel(HideAndSeekTrackerPlugin plugin, CaptureArea captureArea)
    {
        this.plugin = plugin;
        this.captureArea = captureArea;

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

        setupImageIcon(colorIndicator, "Edit area color", COLOR_ICON, COLOR_HOVER_ICON, this::openBorderColorPicker);

        labelToggle = new BlinklessToggleButton(LABEL_ICON, LABEL_HOVER_ICON,
                NO_LABEL_ICON, NO_LABEL_HOVER_ICON,
                "Hide Area Label", "Show Area Label");
        labelToggle.setSelected(captureArea.isLabelVisible());
        labelToggle.addItemListener(this::toggleLabelling);

        leftActions.add(colorIndicator);
        leftActions.add(labelToggle);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        statusLabel.setText("Copied!");
        statusLabel.setVisible(false);

        setupImageIcon(shareLabel, "Copy area to clipboard", COPY_ICON, COPY_ICON_HOVER, () -> {
            plugin.copyCaptureAreaToClip(captureArea);
            showCopiedStatus();
        });

        visibilityToggle = new BlinklessToggleButton(VISIBLE_ICON, VISIBLE_HOVER_ICON,
                INVISIBLE_ICON, INVISIBLE_HOVER_ICON, "Hide Area", "Show Area");
        visibilityToggle.setSelected(captureArea.isAreaActive());
        visibilityToggle.addItemListener(this::toggleVisibility);

        setupImageIcon(deleteLabel, "Delete capture area", DELETE_ICON, DELETE_HOVER_ICON, () -> {
            int confirm = JOptionPane.showConfirmDialog(ExistingCaptureAreaPanel.this,
                    "Are you sure you want to permanently delete this capture area?",
                    "Warning", JOptionPane.OK_CANCEL_OPTION);

            if (confirm == 0)
            {
                plugin.deleteCaptureArea(captureArea);
            }
        });

        rightActions.add(statusLabel);
        rightActions.add(shareLabel);
        rightActions.add(visibilityToggle);
        rightActions.add(deleteLabel);

        bottomContainer.add(leftActions, BorderLayout.WEST);
        bottomContainer.add(rightActions, BorderLayout.EAST);
        bottomContainer.setPreferredSize(new Dimension(0, 37));

        add(nameWrapper, BorderLayout.NORTH);
        add(bottomContainer, BorderLayout.CENTER);

        updateBorder();
    }

    private void preview(boolean on)
    {
        if(visibilityToggle.isSelected()) {
            return;
        }
        captureArea.setAreaVisible(on);
    }

    private void toggleVisibility()
    {
        final boolean isVisible = visibilityToggle.isSelected();
        captureArea.setAreaVisible(isVisible);
        captureArea.setAreaActive(isVisible);
        plugin.updateCaptureAreas();
    }

    private void toggleLabelling()
    {
        final boolean showLabel = labelToggle.isSelected();
        captureArea.setLabelVisible(showLabel);
        plugin.updateCaptureAreas();
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

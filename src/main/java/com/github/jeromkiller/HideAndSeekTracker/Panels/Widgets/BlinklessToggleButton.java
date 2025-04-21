package com.github.jeromkiller.HideAndSeekTracker.Panels.Widgets;

import com.github.jeromkiller.HideAndSeekTracker.Panels.BasePanel;
import lombok.Getter;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BlinklessToggleButton extends JLabel {
    final ImageIcon selectedIcon;
    final ImageIcon selectedIconHover;
    final ImageIcon deselectedIcon;
    final ImageIcon deselectedIconHover;
    final String selectedTooltipText;
    final String deselectedTooltipText;

    @Getter
    boolean selected = false;

    public BlinklessToggleButton(ImageIcon selectedIcon, ImageIcon selectedIconHover,
                                 ImageIcon deselectedIcon, ImageIcon deselectedIconHover,
                                 String selectedTooltipText, String deselectedTooltipText) {
        this.selectedIcon = selectedIcon;
        this.selectedIconHover = selectedIconHover;
        this.deselectedIcon = deselectedIcon;
        this.deselectedIconHover = deselectedIconHover;
        this.selectedTooltipText = selectedTooltipText;
        this.deselectedTooltipText = deselectedTooltipText;

        updateSelected();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setIcon(selected ? selectedIconHover : deselectedIconHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setIcon(selected ? selectedIcon : deselectedIcon);
            }
        });
    }

    public BlinklessToggleButton(String selectedTooltipText) {
        this.selectedIcon = BasePanel.ON_SWITCHER;
        this.selectedIconHover = BasePanel.ON_SWITCHER_HOVER;
        this.deselectedIcon = BasePanel.OFF_SWITCHER;
        this.deselectedIconHover = BasePanel.OFF_SWITCHER_HOVER;
        this.selectedTooltipText = selectedTooltipText;
        this.deselectedTooltipText = selectedTooltipText;

        updateSelected();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setIcon(selected ? selectedIconHover : deselectedIconHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setIcon(selected ? selectedIcon : deselectedIcon);
            }
        });
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        updateSelected();
    }

    public void addItemListener(Runnable runnable) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selected = !selected;
                updateSelected();
                runnable.run();
            }
        });
    }

    private void updateSelected() {
        setIcon(selected ? selectedIcon : deselectedIcon);
        setToolTipText(selected ? selectedTooltipText : deselectedTooltipText);
    }
}

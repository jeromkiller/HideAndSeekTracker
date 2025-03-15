package com.github.jeromkiller.HideAndSeekTracker;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class HideAndSeekPlacementRenderer extends DefaultTableCellRenderer {
    Color defaultColor;

    HideAndSeekPlacementRenderer() {
        super();
        defaultColor = getForeground();
    }

    public void setValue(Object value) {
        String text = (String)value;
        Color printColor = defaultColor;

        if(text.contains("🥇")) {
            printColor = Color.ORANGE;
        } else if (text.contains("🥈")) {
            printColor = Color.LIGHT_GRAY;
        } else if (text.contains("🥉")) {
            printColor = new Color(205, 127, 50);
        }

        setForeground(printColor);
        setText(text);
    }
}

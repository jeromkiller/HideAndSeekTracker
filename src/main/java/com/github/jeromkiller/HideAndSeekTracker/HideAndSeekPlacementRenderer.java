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
        final String printString;
        Color printColor = defaultColor;
        switch ((HideAndSeekPlayer.Placement) value)
        {
            case DNF: {
                printString = "DNF";
                break;
            }
            case FIRST: {
                printString = "🥇";
                printColor = Color.ORANGE;
                break;
            }
            case SECOND: {
                printString = "🥈";
                printColor = Color.LIGHT_GRAY;
                break;
            }
            case THIRD: {
                printString = "🥉";
                printColor = new Color(205, 127, 50);
                break;
            }
            case OTHER: {
                printString = "🏁";
                break;
            }
            default: {
                printString = "";
            }
        }

        setForeground(printColor);
        setText(printString);
    }
}

package com.github.jeromkiller.HideAndSeekTracker.Panels.CaptureArea;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.runelite.client.util.ColorUtil;

import java.awt.*;

@Data
@NoArgsConstructor
public class CaptureCreationOptions {
    private static final String DEFAULT_AREA_NAME = "Capture Area";
    public static int MAX_AREA_SIZE = 15;

    private boolean currentlyCreating = false;
    private int north = 1;
    private int east = 1;
    private int south = 1;
    private int west = 1;
    private String label = DEFAULT_AREA_NAME;
    Color color = ColorUtil.colorWithAlpha(Color.GREEN, 50);
    private boolean labelVisible = true;

    public void resetOptions() {
        currentlyCreating = false;
        north = 1;
        east = 1;
        south = 1;
        west = 1;
        label = DEFAULT_AREA_NAME;
    }
}

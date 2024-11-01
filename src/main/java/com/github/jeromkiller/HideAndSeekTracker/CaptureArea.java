package com.github.jeromkiller.HideAndSeekTracker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nullable;
import java.awt.*;
import java.time.Instant;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptureArea {
    private static final int MAX_RENDER_DISTANCE = 50;

    private long id;
    private WorldPoint worldPoint;
    private int width;
    private int height;
    private String label;
    private Color color;
    private boolean areaVisible;
    private boolean labelVisible;

    public CaptureArea(WorldPoint worldPoint, int width, int height, Color color, @Nullable String label, boolean labelVisible) {
        this.id = Instant.now().toEpochMilli();
        this.worldPoint = worldPoint;
        this.width = width;
        this.height = height;
        this.label = Objects.requireNonNullElse(label, "");
        this.color = color;
        this.areaVisible = true;
        this.labelVisible = labelVisible;
    }

    public boolean notWorthChecking(WorldPoint playerLoc)
    {
        boolean notWorth = false;
        notWorth |= !areaVisible;
        notWorth |= playerLoc.getPlane() != worldPoint.getPlane();
        notWorth |= playerLoc.distanceTo(worldPoint) > MAX_RENDER_DISTANCE;
        return notWorth;
    }

    public boolean playerInArea(WorldPoint playerLoc)
    {
        final int playerX = playerLoc.getX();
        final int playerY = playerLoc.getY();
        final int areaX = worldPoint.getX();
        final int areaY = worldPoint.getY();
        return ((playerX >= areaX) && (playerX < areaX + width))
                && ((playerY >= areaY) && (playerY < areaY + height));
    }
}

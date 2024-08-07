package com.github.jeromkiller.HideAndSeekTracker;

import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.Perspective;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.GeneralPath;

public class HideAndSeekTrackerSceneOverlay extends Overlay
{
    private static final int LOCAL_TILE_SIZE = Perspective.LOCAL_TILE_SIZE;

    private final Client client;
    private final HideAndSeekTrackerConfig config;
    private final HideAndSeekTrackerPlugin plugin;

    @Inject
    private HideAndSeekTrackerSceneOverlay(Client client, HideAndSeekTrackerConfig config, HideAndSeekTrackerPlugin plugin)
    {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(PRIORITY_LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        WorldPoint playerLoc = client.getLocalPlayer().getWorldLocation();
        drawBox(graphics, playerLoc);
        return null;
    }

    private void drawBox(Graphics2D graphics, WorldPoint worldPoint)
    {
        GeneralPath path = new GeneralPath();



        LocalPoint localpoint = LocalPoint.fromWorld(client, worldPoint.getX() ,worldPoint.getY());

        int posXoffset = (config.eastOffset() + 1);
        int posYoffset = (config.northOffset() + 1);
        int negXoffset = -config.westOffset();
        int negYoffset = -config.southOffset();

        Boolean pointDrawn = false;
        pointDrawn = drawLine(path, negXoffset, negYoffset, negXoffset, posYoffset, worldPoint, pointDrawn);
        pointDrawn = drawLine(path, negXoffset, posYoffset, posXoffset, posYoffset, worldPoint, pointDrawn);
        pointDrawn = drawLine(path, posXoffset, posYoffset, posXoffset, negYoffset, worldPoint, pointDrawn);
        pointDrawn = drawLine(path, posXoffset, negYoffset, negXoffset, negYoffset, worldPoint, pointDrawn);

        Stroke stroke = new BasicStroke((float)config.areaBorderWidth());
        graphics.setStroke(stroke);
        graphics.setColor(config.areaColor());
        graphics.draw(path);
        graphics.setColor(config.fillColor());
        graphics.fill(path);
    }

    private boolean drawLine(GeneralPath path, int fromX, int fromY, int toX, int toY, WorldPoint worldPoint, boolean pointDrawn)
    {
        int x_step = fromX >= toX ? -1 : 1;
        int y_step = fromY >= toY ? -1 : 1;

        int worldX = worldPoint.getX();
        int worldY = worldPoint.getY();
        int z = worldPoint.getPlane();

        for(int x_offset = fromX; true; x_offset += x_step)
        {
            for(int y_offset = fromY; true; y_offset += y_step)
            {
                final int x = worldX + x_offset;
                final int y = worldY + y_offset;
                Point screenPoint = toScreenPoint(x, y, z);
                if(screenPoint == null)
                {
                    pointDrawn = false;
                }
                else {
                    if (pointDrawn) {
                        path.lineTo(screenPoint.getX(), screenPoint.getY());
                        pointDrawn = true;
                    } else {
                        path.moveTo(screenPoint.getX(), screenPoint.getY());
                        pointDrawn = true;
                    }
                }
                if (y_offset == toY)
                    break;
            }
            if (x_offset == toX)
                break;
        }
        return pointDrawn;
    }

    private void startPoint(GeneralPath path, int x, int y, int z)
    {
        Point screenPoint = toScreenPoint(x, y, z);
        if(screenPoint == null)
            return;

        path.moveTo(screenPoint.getX(), screenPoint.getY());
    }

    private void addLine(GeneralPath path, int x, int y, int z)
    {
        Point screenPoint = toScreenPoint(x, y, z);
        if(screenPoint == null)
            return;

        path.lineTo(screenPoint.getX(), screenPoint.getY());
    }

    private Point toScreenPoint(int x, int y, int z)
    {
        LocalPoint localPoint = LocalPoint.fromWorld(client, x, y);

        if (localPoint == null)
        {
            return null;
        }

        return Perspective.localToCanvas(
                client,
                new LocalPoint(localPoint.getX() - LOCAL_TILE_SIZE / 2, localPoint.getY() - LOCAL_TILE_SIZE / 2),
                z);
    }
}

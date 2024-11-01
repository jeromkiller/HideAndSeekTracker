package com.github.jeromkiller.HideAndSeekTracker;

import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.Perspective;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class HideAndSeekTrackerSceneOverlay extends Overlay
{
    private static final int LOCAL_TILE_SIZE = Perspective.LOCAL_TILE_SIZE;
    private static final int ENTITY_RENDER_LIMIT = 15;
    private static final int MAX_RENDER_DISTANCE = 50;

    private final Client client;
    private final HideAndSeekTrackerPlugin plugin;

    private Point firstPoint;
    private boolean pointDrawn;

    @Inject
    private HideAndSeekTrackerSceneOverlay(Client client, HideAndSeekTrackerPlugin plugin)
    {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(PRIORITY_LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        WorldPoint playerLoc = client.getLocalPlayer().getWorldLocation();

        drawCreationArea(graphics, plugin.getCaptureCreationOptions(), playerLoc);

        if(plugin.getSettings().getShowRenderDist()) {
            drawRenderDist(graphics, playerLoc);
        }

        for(CaptureArea area : plugin.getCaptureAreas()) {
            if(!area.isWorthChecking(playerLoc)) {
                continue;
            }
            drawBox(graphics, area, playerLoc);
        }


        return null;
    }

    private void drawRenderDist(Graphics2D graphics, WorldPoint PlayerLocation) {
        Canvas screen = client.getCanvas();
        final Rectangle2D fullScreen = new Rectangle2D.Float(0, 0, screen.getWidth(), screen.getHeight());
        WorldPoint entitryRenderOrigin = PlayerLocation.dx(-ENTITY_RENDER_LIMIT).dy(-ENTITY_RENDER_LIMIT);
        GeneralPath clippingSquare = createFloorBox(entitryRenderOrigin, ENTITY_RENDER_LIMIT * 2 + 1, ENTITY_RENDER_LIMIT * 2+ 1);  // simple clipping area

        // draw the clipping area
        graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 2}, 0));
        graphics.setColor(Color.blue);
        graphics.setClip(fullScreen);
        graphics.draw(clippingSquare);

    }

    private void drawCreationArea(Graphics2D graphics2D, CaptureCreationOptions options, WorldPoint PlayerLocation)
    {
        if(!options.isCurrentlyCreating())
            return;

        final int width = (options.getEast() + options.getWest() + 1);
        final int height = (options.getNorth() + options.getSouth() + 1);
        final int xOffset = -options.getWest();
        final int yOffset = -options.getSouth();

        WorldPoint swTile = PlayerLocation.dx(xOffset).dy(yOffset);
        CaptureArea setupArea = new CaptureArea(swTile, width, height, options.getColor(), options.getLabel(), options.isLabelVisible());
        drawBox(graphics2D, setupArea, PlayerLocation);
    }

    private void drawBox(Graphics2D graphics, CaptureArea captureArea, WorldPoint PlayerLocation)
    {
        //capture area tile
        GeneralPath outerSquare = createFloorBox(captureArea.getWorldPoint(), captureArea.getWidth(), captureArea.getHeight());
        WorldPoint entitryRenderOrigin = PlayerLocation.dx(-ENTITY_RENDER_LIMIT).dy(-ENTITY_RENDER_LIMIT);
        GeneralPath clippingSquare = createFloorBox(entitryRenderOrigin, ENTITY_RENDER_LIMIT * 2 + 1, ENTITY_RENDER_LIMIT * 2+ 1);  // simple clipping area

        Canvas screen = client.getCanvas();
        final Rectangle2D fullScreen = new Rectangle2D.Float(0, 0, screen.getWidth(), screen.getHeight());

        // draw inside square
        Stroke stroke = new BasicStroke(2);
        Color inside_border = captureArea.getColor();
        Color inside_fillColor = ColorUtil.colorWithAlpha(inside_border, 255);
        graphics.setClip(clippingSquare);
        graphics.setStroke(stroke);
        graphics.setColor(inside_border);
        graphics.fill(outerSquare);
        graphics.setColor(inside_fillColor);
        graphics.draw(outerSquare);

        //draw the outside square
        Area insideClip = new Area(fullScreen);
        Color outside_border = negateColor(captureArea.getColor());
        Color outside_fillColor = ColorUtil.colorWithAlpha(outside_border, 255);

        insideClip.subtract(new Area(clippingSquare));
        graphics.setClip(insideClip);
        graphics.setStroke(stroke);
        graphics.setColor(outside_border);
        graphics.fill(outerSquare);
        graphics.setColor(outside_fillColor);
        graphics.draw(outerSquare);

        if(captureArea.isLabelVisible()) {
            //draw the label in the middle of the capture area
            WorldPoint areaCenter = captureArea.getWorldPoint().dx(captureArea.getWidth() / 2).dy(captureArea.getHeight() / 2);
            LocalPoint localPoint = LocalPoint.fromWorld(client, areaCenter.getX(), areaCenter.getY());
            if (localPoint != null) {
                if (captureArea.getWidth() % 2 == 0) {
                    localPoint = localPoint.dx(-64);
                }
                if (captureArea.getHeight() % 2 == 0) {
                    localPoint = localPoint.dy(-64);
                }
                graphics.setClip(fullScreen);
                graphics.setColor(ColorUtil.colorWithAlpha(inside_border, 255));
                graphics.setFont(FontManager.getRunescapeFont());
                Point textLoc = Perspective.getCanvasTextLocation(client, graphics, localPoint, captureArea.getLabel(), 0);
                if (textLoc != null) {
                    graphics.drawString(captureArea.getLabel(), textLoc.getX(), textLoc.getY());
                }
            }
        }

    }

    private GeneralPath createFloorBox(WorldPoint origin, int width, int height)
    {
        GeneralPath path = new GeneralPath();

        pointDrawn = false;
        firstPoint = null;
        drawLine(path, 0, 0, 0, height, origin);
        drawLine(path, 0, height, width, height, origin);
        drawLine(path, width, height, width, 0, origin);
        drawLine(path, width, 0, 0, 0, origin);

        return path;
    }

    private boolean drawLine(GeneralPath path, int fromX, int fromY, int toX, int toY, WorldPoint worldPoint)
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
                if(screenPoint != null)
                {
                    if (pointDrawn) {
                        path.lineTo(screenPoint.getX(), screenPoint.getY());
                    } else {
                        path.moveTo(screenPoint.getX(), screenPoint.getY());
                        pointDrawn = true;
                        if(firstPoint == null) {
                            firstPoint = screenPoint;
                        }
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

    private Color negateColor(Color color) {
        final int maxColor = ColorUtil.MAX_RGB_VALUE;
        return new Color(maxColor - color.getRed(),
                    maxColor - color.getGreen(),
                    maxColor - color.getBlue(),
                    color.getAlpha()
                );
    }
}

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

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;


public class HideAndSeekTrackerSceneOverlay extends Overlay
{
    private static final int LOCAL_TILE_SIZE = Perspective.LOCAL_TILE_SIZE;
    private static final int ENTITY_RENDER_LIMIT = 15;

    private final Client client;
    private final HideAndSeekTrackerPlugin plugin;

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
            if(area.notWorthPainting(playerLoc)) {
                continue;
            }
            drawBox(graphics, area, playerLoc);
        }

        return null;
    }

    private void drawRenderDist(Graphics2D graphics, WorldPoint PlayerLocation) {
        Canvas screen = client.getCanvas();
        final Rectangle2D fullScreen = new Rectangle2D.Float(0, 0, screen.getWidth(), screen.getHeight());
        WorldPoint entityRenderOrigin = PlayerLocation.dx(-ENTITY_RENDER_LIMIT).dy(-ENTITY_RENDER_LIMIT);
        GeneralPath clippingSquare = createFloorBox(entityRenderOrigin, ENTITY_RENDER_LIMIT * 2 + 1, ENTITY_RENDER_LIMIT * 2+ 1);  // simple clipping area

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
        WorldPoint entityRenderOrigin = PlayerLocation.dx(-ENTITY_RENDER_LIMIT).dy(-ENTITY_RENDER_LIMIT);
        GeneralPath clippingSquare = createFloorBox(entityRenderOrigin, ENTITY_RENDER_LIMIT * 2 + 1, ENTITY_RENDER_LIMIT * 2+ 1);  // simple clipping area

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

        graphics.setClip(fullScreen);
        paintText(graphics, captureArea, inside_border);
    }

    private GeneralPath createFloorBox(WorldPoint origin, int width, int height)
    {
        GeneralPath path = new GeneralPath();

        pointDrawn = false;
        drawWestLine(path, height, origin);
        drawNorthLine(path, width, origin.dy(height));
        drawEastLine(path, height, origin.dx(width).dy(height));
        drawSouthLine(path, width, origin.dx(width));

        if(pointDrawn) {
            path.closePath();
        }
        return path;
    }

    private void drawWestLine(GeneralPath path, int length, WorldPoint worldPoint)
    {
        int x = worldPoint.getX();
        int y = worldPoint.getY();
        int z = worldPoint.getPlane();
        for (int y_offset = 0; y_offset < length; y_offset++) {
            LocalPoint startPoint = toLocalPoint( x, y + y_offset);
            if(startPoint == null) {
                continue;
            }
            LocalPoint endPoint = startPoint.dy(LOCAL_TILE_SIZE -1);
            if(y_offset != 0) {
                paintPoint(path, startPoint, z);
            }
            paintPoint(path, endPoint, z);
        }
    }

    private void drawNorthLine(GeneralPath path, int length, WorldPoint worldPoint)
    {
        int x = worldPoint.getX();
        int y = worldPoint.getY();
        int z = worldPoint.getPlane();
        for (int x_offset = 0; x_offset < length; x_offset++) {
            LocalPoint startPoint = toLocalPoint(x + x_offset, y);
            if(startPoint == null) {
                continue;
            }
            startPoint = startPoint.dy(-1);
            LocalPoint endPoint = startPoint.dx(LOCAL_TILE_SIZE -1);
            if(x_offset != 0) {
                paintPoint(path, startPoint, z);
            }
            paintPoint(path, endPoint, z);
        }
    }

    private void drawEastLine(GeneralPath path, int length, WorldPoint worldPoint)
    {
        int x = worldPoint.getX();
        int y = worldPoint.getY();
        int z = worldPoint.getPlane();
        for (int y_offset = 0; y_offset < length; y_offset++) {
            LocalPoint startPoint = toLocalPoint(x, y - y_offset);
            if(startPoint == null) {
                continue;
            }
            startPoint = startPoint.dy(-1);
            startPoint = startPoint.dx(-1);
            LocalPoint endPoint = startPoint.dy(-(LOCAL_TILE_SIZE -1));
            if(y_offset != 0) {
                paintPoint(path, startPoint, z);
            }
            paintPoint(path, endPoint, z);
        }
    }

    private void drawSouthLine(GeneralPath path, int length, WorldPoint worldPoint)
    {
        int x = worldPoint.getX();
        int y = worldPoint.getY();
        int z = worldPoint.getPlane();
        for (int x_offset = 0; x_offset < length; x_offset++) {
            LocalPoint startPoint = toLocalPoint(x - x_offset, y);
            if(startPoint == null) {
                continue;
            }
            startPoint = startPoint.dx(-1);
            LocalPoint endPoint = startPoint.dx(-(LOCAL_TILE_SIZE -1));
            if(x_offset != 0) {
                paintPoint(path, startPoint, z);
            }
            paintPoint(path, endPoint, z);
        }
    }

    private void paintText(Graphics2D graphics, CaptureArea captureArea, Color color) {
        if(captureArea.isLabelVisible()) {
            //draw the label in the middle of the capture area
            WorldPoint areaCenter = captureArea.getWorldPoint().dx(captureArea.getWidth() / 2).dy(captureArea.getHeight() / 2);
            LocalPoint localPoint = toLocalPoint(areaCenter.getX(), areaCenter.getY(), true);
            if (localPoint != null) {
                if (captureArea.getWidth() % 2 == 0) {
                    localPoint = localPoint.dx(-LOCAL_TILE_SIZE / 2);
                }
                if (captureArea.getHeight() % 2 == 0) {
                    localPoint = localPoint.dy(-LOCAL_TILE_SIZE / 2);
                }

                graphics.setColor(ColorUtil.colorWithAlpha(color, 255));
                graphics.setFont(FontManager.getRunescapeFont());
                Point textLoc = Perspective.getCanvasTextLocation(client, graphics, localPoint, captureArea.getLabel(), 0);
                if (textLoc != null) {
                    graphics.drawString(captureArea.getLabel(), textLoc.getX(), textLoc.getY());
                }
            }
        }
    }

    private void paintPoint(GeneralPath path, LocalPoint localPoint, int z){
        if (localPoint == null)
            return;

        Point canvasPoint = toCanvasPoint(localPoint, z);
        if(canvasPoint == null)
            return;

        if (pointDrawn) {
            path.lineTo(canvasPoint.getX(), canvasPoint.getY());
        } else {
            path.moveTo(canvasPoint.getX(), canvasPoint.getY());
            pointDrawn = true;
        }
    }

    private LocalPoint toLocalPoint(int x, int y)
    {
        return toLocalPoint(x, y, false);
    }

    private LocalPoint toLocalPoint(int x, int y, boolean getCenter)
    {
        LocalPoint localPointCenter = LocalPoint.fromWorld(client.getTopLevelWorldView(), x, y);
        if(localPointCenter == null) {
            return null;
        }
        if (getCenter) {
            return localPointCenter;
        }
        return localPointCenter.plus(- LOCAL_TILE_SIZE / 2, - LOCAL_TILE_SIZE / 2);
    }

    private Point toCanvasPoint(LocalPoint localPoint, int z) {
        return Perspective.localToCanvas(
                client,
                localPoint,
                //new LocalPoint(localPoint.getX() - LOCAL_TILE_SIZE / 2, localPoint.getY() - LOCAL_TILE_SIZE / 2),
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

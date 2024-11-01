package com.github.jeromkiller.HideAndSeekTracker;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.image.BufferedImage;

public class CaptureAreaPanel extends JPanel{
    protected static final int DEFAULT_FILL_OPACITY = 50;

    protected static final Border NAME_BOTTOM_BORDER = new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
            BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR));
    protected static final int MAX_ALPHA = 255;

    protected static final ImageIcon COLOR_ICON;
    protected static final ImageIcon COLOR_HOVER_ICON;

    protected static final ImageIcon COPY_AREA_ICON;
    protected static final ImageIcon COPY_AREA_HOVER_ICON;

    protected static final ImageIcon LABEL_ICON;
    protected static final ImageIcon LABEL_HOVER_ICON;
    protected static final ImageIcon NO_LABEL_ICON;
    protected static final ImageIcon NO_LABEL_HOVER_ICON;

    protected static final ImageIcon VISIBLE_ICON;
    protected static final ImageIcon VISIBLE_HOVER_ICON;
    protected static final ImageIcon INVISIBLE_ICON;
    protected static final ImageIcon INVISIBLE_HOVER_ICON;

    protected static final ImageIcon DELETE_ICON;
    protected static final ImageIcon DELETE_HOVER_ICON;

    static
    {
        final BufferedImage pencilImg = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "pencil_color_icon.png");
        final BufferedImage pencilImgHover = ImageUtil.luminanceOffset(pencilImg, -150);
        COLOR_ICON = new ImageIcon(pencilImg);
        COLOR_HOVER_ICON = new ImageIcon(pencilImgHover);

        final BufferedImage copyImg = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "copy_icon.png");
        final BufferedImage copyImgHover = ImageUtil.luminanceOffset(copyImg, -100);
        COPY_AREA_ICON = new ImageIcon(copyImg);
        COPY_AREA_HOVER_ICON = new ImageIcon(copyImgHover);

        final BufferedImage labelImg = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "label_icon.png");
        final BufferedImage labelImgHover = ImageUtil.luminanceOffset(labelImg, -150);
        LABEL_ICON = new ImageIcon(labelImg);
        LABEL_HOVER_ICON = new ImageIcon(labelImgHover);

        NO_LABEL_ICON = new ImageIcon(labelImgHover);
        NO_LABEL_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(labelImgHover, -100));

        final BufferedImage visibleImg = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "visible_icon.png");
        VISIBLE_ICON = new ImageIcon(visibleImg);
        VISIBLE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(visibleImg, -100));

        final BufferedImage invisibleImg = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "invisible_icon.png");
        INVISIBLE_ICON = new ImageIcon(invisibleImg);
        INVISIBLE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(invisibleImg, -100));

        final BufferedImage deleteImg = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "delete_icon.png");
        DELETE_ICON = new ImageIcon(deleteImg);
        DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(deleteImg, -100));
    }
}

package com.github.jeromkiller.HideAndSeekTracker.Panels;

import com.github.jeromkiller.HideAndSeekTracker.HideAndSeekTrackerPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class BasePanel extends JPanel {
    public static final Border NAME_BOTTOM_BORDER = new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
            BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR));
    public static final int MAX_ALPHA = 255;

    public static final ImageIcon COLOR_ICON;
    public static final ImageIcon COLOR_HOVER_ICON;

    public static final ImageIcon LABEL_ICON;
    public static final ImageIcon LABEL_HOVER_ICON;
    public static final ImageIcon NO_LABEL_ICON;
    public static final ImageIcon NO_LABEL_HOVER_ICON;

    public static final ImageIcon VISIBLE_ICON;
    public static final ImageIcon VISIBLE_HOVER_ICON;
    public static final ImageIcon INVISIBLE_ICON;
    public static final ImageIcon INVISIBLE_HOVER_ICON;

    public static final ImageIcon DELETE_ICON;
    public static final ImageIcon DELETE_HOVER_ICON;

    public static final ImageIcon COPY_ICON;
    public static final ImageIcon COPY_ICON_HOVER;
    public static final ImageIcon IMPORT_ICON;
    public static final ImageIcon IMPORT_ICON_HOVER;
    public static final ImageIcon EXPORT_ICON;
    public static final ImageIcon EXPORT_ICON_HOVER;
    public static final ImageIcon NEW_ROUND_ICON;
    public static final ImageIcon NEW_ROUND_ICON_HOVER;
    public static final ImageIcon DELETE_ROUND_ICON;
    public static final ImageIcon DELETE_ROUND_ICON_HOVER;

    public static final ImageIcon ARROW_LEFT_ICON;
    public static final ImageIcon ARROW_LEFT_HOVER_ICON;
    public static final ImageIcon ARROW_RIGHT_ICON;
    public static final ImageIcon ARROW_RIGHT_HOVER_ICON;

    public static final ImageIcon MINUS_ICON;
    public static final ImageIcon MINUS_HOVER_ICON;

    public static final ImageIcon ON_SWITCHER;
    public static final ImageIcon ON_SWITCHER_HOVER;
    public static final ImageIcon OFF_SWITCHER;
    public static final ImageIcon OFF_SWITCHER_HOVER;

    static {
        final BufferedImage copyImg = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "copy_icon.png");
        final BufferedImage copyImgHover = ImageUtil.luminanceOffset(copyImg, -100);
        COPY_ICON = new ImageIcon(copyImg);
        COPY_ICON_HOVER = new ImageIcon(copyImgHover);

        final BufferedImage importImg = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "import_icon.png");
        final BufferedImage importImgHover = ImageUtil.luminanceOffset(importImg, -100);
        IMPORT_ICON = new ImageIcon(importImg);
        IMPORT_ICON_HOVER = new ImageIcon(importImgHover);

        final BufferedImage exportImg = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "export_icon.png");
        final BufferedImage exportImgHover = ImageUtil.luminanceOffset(exportImg, -100);
        EXPORT_ICON = new ImageIcon(exportImg);
        EXPORT_ICON_HOVER = new ImageIcon(exportImgHover);

        final BufferedImage newRoundImg = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "new_round.png");
        final BufferedImage newRoundImgHover = ImageUtil.luminanceOffset(newRoundImg, -100);
        NEW_ROUND_ICON = new ImageIcon(newRoundImg);
        NEW_ROUND_ICON_HOVER = new ImageIcon(newRoundImgHover);

        final BufferedImage deleteRoundImg = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "delete_round.png");
        final BufferedImage deleteRoundImgHover = ImageUtil.luminanceOffset(deleteRoundImg, -100);
        DELETE_ROUND_ICON = new ImageIcon(deleteRoundImg);
        DELETE_ROUND_ICON_HOVER = new ImageIcon(deleteRoundImgHover);

        final BufferedImage pencilImg = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "pencil_color_icon.png");
        final BufferedImage pencilImgHover = ImageUtil.luminanceOffset(pencilImg, -150);
        COLOR_ICON = new ImageIcon(pencilImg);
        COLOR_HOVER_ICON = new ImageIcon(pencilImgHover);

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

        final BufferedImage arrowLeftImg = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "config_back_icon.png");
        final BufferedImage arrowLeftImgHover = ImageUtil.luminanceOffset(arrowLeftImg, -150);
        ARROW_LEFT_ICON = new ImageIcon(arrowLeftImg);
        ARROW_LEFT_HOVER_ICON = new ImageIcon(arrowLeftImgHover);

        final BufferedImage arrowRightImg = ImageUtil.flipImage(arrowLeftImg, true, false);
        final BufferedImage arrowRightImgHover = ImageUtil.flipImage(arrowLeftImgHover, true, false);
        ARROW_RIGHT_ICON = new ImageIcon(arrowRightImg);
        ARROW_RIGHT_HOVER_ICON = new ImageIcon(arrowRightImgHover);

        final BufferedImage removeImg = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "minus_icon.png");
        MINUS_ICON = new ImageIcon(removeImg);
        MINUS_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(removeImg, -50));

        BufferedImage onSwitcher = ImageUtil.loadImageResource(HideAndSeekTrackerPlugin.class, "switcher_on.png");
        ON_SWITCHER = new ImageIcon(onSwitcher);
        ON_SWITCHER_HOVER = new ImageIcon(ImageUtil.alphaOffset(onSwitcher, -50));
        BufferedImage offSwitcher = ImageUtil.flipImage(
                ImageUtil.luminanceScale(
                        ImageUtil.grayscaleImage(onSwitcher),
                        0.61f
                ),
                true,
                false
        );
        OFF_SWITCHER = new ImageIcon(offSwitcher);
        OFF_SWITCHER_HOVER = new ImageIcon(ImageUtil.alphaOffset(offSwitcher, -100));
    }

    protected void setupImageIcon(JLabel iconButton, String toolTip, ImageIcon icon, ImageIcon hover_icon, Runnable function) {
        iconButton.setIcon(icon);
        iconButton.setToolTipText(toolTip);
        iconButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                function.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                iconButton.setIcon(hover_icon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                iconButton.setIcon(icon);
            }
        });
    }
}

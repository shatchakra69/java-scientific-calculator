package com.calculator.gui;

import javax.swing.JButton;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A flat, rounded button with two small animations: it lightens while the
 * mouse hovers, and a white "flash" fades out each time it is clicked.
 *
 * <p>The flash is driven by a Swing {@link Timer} that steps an alpha value
 * down to zero and repaints, so the effect runs on the Event Dispatch Thread
 * without any external animation library.</p>
 */
public class AnimatedButton extends JButton {

    private static final int ARC = 16;
    private static final float FLASH_START = 0.5f;
    private static final float FLASH_STEP = 0.08f;

    private final Color baseColor;
    private float flashAlpha = 0f;
    private boolean hovered = false;
    private final Timer flashTimer;

    public AnimatedButton(String text, Color baseColor, Color foreground) {
        super(text);
        this.baseColor = baseColor;
        setForeground(foreground);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setFocusable(false); // keep keyboard focus on the expression field

        flashTimer = new Timer(16, null);
        flashTimer.addActionListener(e -> {
            flashAlpha -= FLASH_STEP;
            if (flashAlpha <= 0f) {
                flashAlpha = 0f;
                flashTimer.stop();
            }
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                flashAlpha = FLASH_START;
                if (!flashTimer.isRunning()) {
                    flashTimer.start();
                }
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Color bg = baseColor;
        if (getModel().isPressed()) {
            bg = blend(baseColor, Color.BLACK, 0.12f);
        } else if (hovered) {
            bg = blend(baseColor, Color.WHITE, 0.14f);
        }
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w - 1, h - 1, ARC, ARC);

        if (flashAlpha > 0f) {
            g2.setColor(new Color(1f, 1f, 1f, Math.min(flashAlpha, 1f)));
            g2.fillRoundRect(0, 0, w - 1, h - 1, ARC, ARC);
        }

        String text = getText();
        if (text != null && !text.isEmpty()) {
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(text)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(getForeground());
            g2.drawString(text, tx, ty);
        }

        g2.dispose();
    }

    private static Color blend(Color a, Color b, float t) {
        float u = 1f - t;
        return new Color(
                Math.round(a.getRed() * u + b.getRed() * t),
                Math.round(a.getGreen() * u + b.getGreen() * t),
                Math.round(a.getBlue() * u + b.getBlue() * t));
    }
}

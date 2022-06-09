package chess.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class GradButton extends JButton {

    public Color getColorLeft() {
        return color_l;
    }

    public void setColorLeft(Color color_l) {
        this.color_l = color_l;
    }

    public Color getColorRight() {
        return color_r;
    }

    public void setColorRight(Color color_r) {
        this.color_r = color_r;
    }

    private Color color_l = new Color(255, 255, 255);
    private Color color_r = new Color(255, 0, 0);
    private final Timer timer;
    private final Timer timerPressed;
    private float alpha = 0.3f;
    private boolean mouseOver;
    private float alphaPressed = 0.5f;
    private final Color pressedBgColor = new Color(255,255,255);

    public GradButton() {
        setContentAreaFilled(false);
        setForeground(Color.WHITE);
//        setBorder(new EmptyBorder(10, 20, 10, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setForeground(new Color(240, 240, 240));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                mouseOver = true;
                timer.start();
            }

            @Override
            public void mouseExited(MouseEvent me) {
                mouseOver = false;
                timer.start();
            }

            @Override
            public void mousePressed(MouseEvent me) {
                alphaPressed = 0.5f;
                timerPressed.setDelay(0);
                timerPressed.start();
            }
        });
        timer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (mouseOver) {
                    if (alpha < 0.6f) {
                        alpha += 0.05f;
                    } else {
                        alpha = 0.6f;
                        timer.stop();
                    }
                } else {
                    if (alpha > 0.3f) {
                        alpha -= 0.05f;
                    } else {
                        alpha = 0.3f;
                        timer.stop();
                    }
                }
                repaint();
            }
        });
        timerPressed = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (alphaPressed <= 0) {
                    timerPressed.stop();
                } else {
                    repaint();
                }
            }
        });
        setColorLeft(new java.awt.Color(245, 161, 66));
        setColorRight(new java.awt.Color(250, 74, 15));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        int width = getWidth();
        int height = getHeight();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint grad = new GradientPaint(0, 0, color_l, width, 0, color_r);
        g2.setPaint(grad);
        g2.fillRect(0, 0, width, height);
        createStyle(g2);

        if (getModel().isRollover())
            graphics.setColor(pressedBgColor);
        else
            graphics.setColor(getBackground());

        g2.dispose();
        graphics.drawImage(img, 0, 0, null);
        super.paintComponent(graphics);
    }

    private void createStyle(Graphics2D g2) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        int width = getWidth();
        int height = getHeight();

        g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, height, new Color(255, 255, 255, 60)));

        Path2D.Float f = new Path2D.Float();
        f.moveTo(0, 0);
        f.curveTo(0, 0, width / 2F, height / 2F, width, 0);

        g2.fill(f);
    }

}
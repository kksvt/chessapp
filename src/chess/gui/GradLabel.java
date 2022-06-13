package chess.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class GradLabel extends JLabel {
    private Color color_l = new Color(255, 255, 255);
    private Color color_r = new Color(255, 0, 0);

    public GradLabel(Color color_l, Color color_r) {
        //setForeground(new Color(240, 240, 240));
        setBorder(null);
        this.color_l = color_l;
        this.color_r = color_r;
        setBorder(new EmptyBorder(15, 5, 5, 5));
        this.setFont((new Font("Calibri", Font.BOLD, 40)));
        this.setForeground(Color.white);
        this.setPreferredSize(new Dimension(128, 64));
        this.setHorizontalAlignment(JLabel.CENTER);
        this.setVerticalAlignment(JLabel.CENTER);
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
        graphics.setColor(getBackground());

        g2.dispose();
        graphics.drawImage(img, 0, 0, null);
        super.paintComponent(graphics);
    }

    private void createStyle(Graphics2D g2) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.3f));
        int width = getWidth();
        int height = getHeight();

        g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, height, new Color(255, 255, 255, 60)));

        Path2D.Float f = new Path2D.Float();
        f.moveTo(0, 0);
        f.curveTo(0, 0, width / 2F, height / 2F, width, 0);

        g2.fill(f);
    }
}
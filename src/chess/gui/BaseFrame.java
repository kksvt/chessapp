package chess.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class BaseFrame extends JPanel {
    Image bgImage;

    protected void setBgImage(ImageIcon image){
        this.bgImage = image.getImage();
        setOpaque(true);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, null);
    }

    BaseFrame(){
        ImageIcon icon = new ImageIcon("sprites/background.jpg");
        setBgImage(icon);
//        setSize(800, 800);
        setVisible(true);
        setBorder(new EmptyBorder(20,20,20,20));
    }
}
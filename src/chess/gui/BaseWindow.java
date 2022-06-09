package chess.gui;

import chess.core.Controller;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.util.ArrayList;

public class BaseWindow extends JFrame {

    protected final BaseFrame baseFrame = new BaseFrame();
    protected GridBagConstraints gbc = new GridBagConstraints();
    private final Box menuLeft = Box.createHorizontalBox();
    private final Box menuRight = Box.createHorizontalBox();
    private final JLabel timeFormatLabelTitle = new JLabel("Time Format:");
    private final JLabel timeFormatLabel = new JLabel("10+0");
    public void setTimeFormatLabel(String text){
        timeFormatLabel.setText(text);
    }
    public String getTimeFormat(){
        return timeFormatLabel.getText();
    }
    public void addToLeftMenu(JComponent b){
        menuLeft.add(b);
    }

    public void addToRightMenu(JComponent b){
        menuRight.add(b);
    }

    BaseWindow(){
        ImageIcon appIcon = new ImageIcon("sprites/chess_icon.png");
        setIconImage(appIcon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        baseFrame.setLayout(new GridBagLayout());

        GradButton timeFormatButton = new GradButton();
        StylizeButton(timeFormatButton, "Time Format");

        GradButton optionsButton = new GradButton();
        StylizeButton(optionsButton, "Options");
        BaseWindow thisRef = this;
        optionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                OptionsWindow ow = new OptionsWindow((MenuWindow) thisRef);
                ow.setVisible(true);
            }
        });

        timeFormatLabelTitle.setFont((new Font("Calibri", Font.BOLD, 20)));
        timeFormatLabel.setFont((new Font("Calibri", Font.BOLD, 20)));

        menuLeft.add(optionsButton);
        menuLeft.add(Box.createHorizontalStrut(15));
        menuLeft.add(timeFormatLabelTitle);
        menuLeft.add(Box.createHorizontalStrut(15));
        menuLeft.add(timeFormatLabel);
        menuLeft.add(Box.createHorizontalStrut(15));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridheight = 1;
        gbc.weighty = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        baseFrame.add(menuLeft, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        baseFrame.add(menuRight, gbc);
    }

    public void StylizeButton(GradButton b, String name) {
        b.setText(name);
        b.setFont((new Font("Calibri", Font.BOLD, 20)));
        b.setBorder(new EmptyBorder(15, 10, 10, 10));
        b.setForeground(Color.white);
    }
}

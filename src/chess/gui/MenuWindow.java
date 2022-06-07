package chess.gui;

import chess.core.ChessPosition;
import chess.players.*;
import chess.core.ChessBoard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class MenuWindow extends JFrame {


    public MenuWindow(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BaseFrame baseFrame = new BaseFrame();

        GridBagConstraints gbc = new GridBagConstraints();

        baseFrame.setLayout(new GridBagLayout());

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new FlowLayout());
        menuPanel.setAlignmentX(0);
        menuPanel.add(new JLabel("Options"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridheight = 1;
        gbc.weightx = /*gbc.weighty = */ 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        baseFrame.add(menuPanel, gbc);

        JPanel chessPanel = new JPanel();
        chessPanel.setBorder(new EmptyBorder(10,0,0,0));
        chessPanel.setBackground(new Color(0, 0, 0, 0));
        chessPanel.add(new ChessBoard(8, 8, 80, Color.white, Color.black,
                new HumanPlayer("Player 1"),
                new HumanPlayer("Player 2"),
                ChessPosition.defaultPosition));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.weightx = 70;
        gbc.weighty = 1;
        baseFrame.add(chessPanel, gbc); // add component to the ContentPane

        JPanel menuButtons = new JPanel();
        menuButtons.setBackground(new Color(0, 0, 0, 0));
        menuButtons.setLayout(new GridLayout(2,1, 0, 3));
        menuButtons.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        ArrayList<GradButton> buttons = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            GradButton b = new GradButton();
            b.setColorLeft(new java.awt.Color(169, 235, 216));
            b.setColorRight(new java.awt.Color(68, 103, 227));
            b.setFont(new Font("Calibri", Font.BOLD, 40));
            b.setForeground(Color.white);
            menuButtons.add(b);
            buttons.add(b);
        }
        buttons.get(0).setText("Play computer");
        buttons.get(1).setText("Play duo");


        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = /*gbc.weighty = */ 20;
        gbc.insets = new Insets(2, 2, 2, 2);
        baseFrame.add(menuButtons, gbc);
        add(baseFrame);

        setSize(1000,800);
        setVisible(true);
    }

}


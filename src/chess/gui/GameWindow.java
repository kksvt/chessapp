package chess.gui;

import chess.core.ChessBoard;
import chess.core.ChessPosition;
import chess.players.ComputerPlayer;
import chess.players.HumanPlayer;
import chess.players.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class GameWindow extends BaseWindow{
    public GameWindow(MenuWindow parent, String mode){
//        super(parent.controller);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener( new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                parent.setVisible(true);
            }
        });

        JPanel chessPanel = new JPanel();
        chessPanel.setBorder(new EmptyBorder(5,0,0,0));
        chessPanel.setBackground(new Color(0, 0, 0, 0));

        // chess board setup
        Player p1 = new HumanPlayer(parent.controller.getPlayerName1());
        Player p2 = new HumanPlayer(parent.controller.getPlayerName2());

        Color lightSquare = parent.controller.getLightSquare();
        Color darkSquare = parent.controller.getDarkSquare();

        String position = parent.controller.getPosition();


        ChessBoard chessBoard = new ChessBoard(8, 8, 90,
                lightSquare, darkSquare, p1, p2, ChessPosition.defaultPosition);
        chessPanel.add(chessBoard);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.weightx = 70;
        gbc.weighty = 100;
        baseFrame.add(chessPanel, gbc);


        add(baseFrame);
        setMinimumSize(new Dimension(1150,1050));
        setVisible(true);
    }



}

package chess.gui;

import chess.core.ChessBoard;
import chess.core.ChessPosition;
import chess.players.ComputerPlayer;
import chess.players.HumanPlayer;
import chess.players.OpeningBook;
import chess.players.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public final class GameWindow extends BaseWindow{
    MenuWindow windowParent;
    public GameWindow(MenuWindow parent, int mode) throws IOException {
        OpeningBook.readFromFile();
        windowParent = parent;
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

        GradButton exportFen = new GradButton();
        StylizeButton(exportFen, "Export FEN");
        addToRightMenu(exportFen);
        EventHandler handler = new EventHandler();
        exportFen.addActionListener(handler);

        // chess board setup
        Player p1, p2;
        switch (mode){
            case 1:
                p1 = new HumanPlayer(parent.controller.getPlayerName1());
                p2 = new ComputerPlayer("Stockfish", "./engines/stockfish/stockfish_15_x64.exe");
                break;
            case 2:
                p1 = new HumanPlayer(parent.controller.getPlayerName1());
                p2 = new HumanPlayer(parent.controller.getPlayerName2());
                break;
            case 3:
                p1 = new ComputerPlayer("Stockfish", "./engines/stockfish/stockfish_15_x64.exe");
                p2 = new ComputerPlayer("Stockfish", "./engines/stockfish/stockfish_15_x64.exe",
                        "depth", 4);
                break;
            default:
                p1 = new HumanPlayer(parent.controller.getPlayerName1());
                p2 = new HumanPlayer(parent.controller.getPlayerName2());
                System.out.println("Wrong mode");
        }

        Color lightSquare = parent.controller.getLightSquare();
        Color darkSquare = parent.controller.getDarkSquare();

        String position = parent.chessBoard.getChessPosition().getFen().toString();
        ChessPosition cp;
        if(position.equals(ChessPosition.emptyPosition))
            position = ChessPosition.defaultPosition;


        ChessBoard chessBoard = new ChessBoard(8, 8, 80,
                lightSquare, darkSquare, p1, p2, position);
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
    class EventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            try {
                FileOutputStream fout = new FileOutputStream("exports/FEN.txt", false);
                char ch[] = windowParent.chessBoard.getChessPosition().getFen().toString().toCharArray();
                for (int i = 0; i < ch.length; i++)
                    fout.write(ch[i]);
                fout.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}

package chess.gui;

import chess.core.ChessBoard;
import chess.core.ChessClock;
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
import java.io.FileOutputStream;
import java.io.IOException;

public final class GameWindow extends BaseWindow{
    MenuWindow windowParent;
    ChessBoard chessBoard;
    public GameWindow(MenuWindow parent, int mode) throws IOException {
        windowParent = parent;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener( new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                chessBoard.playerStop();
                parent.setVisible(true);
            }
        });

        JPanel chessPanel = new JPanel();
        chessPanel.setBorder(new EmptyBorder(5,0,0,0));
        chessPanel.setBackground(new Color(0, 0, 0, 0));

        setTimeFormatLabel(parent.getTimeFormat());

        GradButton exportFen = new GradButton();
        StylizeButton(exportFen, "Export FEN");
        addToRightMenu(exportFen);
        addToRightMenu((JComponent) Box.createHorizontalStrut(5));
        EventHandler handler = new EventHandler();
        exportFen.addActionListener(handler);


        // chess board setup
        Player p1, p2;
        int engLvl;
        switch (mode){
            case 1:
                engLvl = parent.getEngineVsPlayerLvl();
                p1 = new HumanPlayer(parent.controller.getPlayerName1());
                p2 = new ComputerPlayer("Stockfish" + engLvl, "./engines/stockfish/stockfish_15_x64.exe",
                        "depth", engLvl);
                break;
            case 2:
                p1 = new HumanPlayer(parent.controller.getPlayerName1());
                p2 = new HumanPlayer(parent.controller.getPlayerName2());
                break;
            case 3:
                int engLvl1 = parent.getEngineVsEngineLvl1(),
                        engLvl2 = parent.getEngineVsEngineLvl2();
                p1 = new ComputerPlayer("Stockfish" + engLvl1, "./engines/stockfish/stockfish_15_x64.exe",
                        "depth", engLvl1);
                p2 = new ComputerPlayer("Stockfish" + engLvl2, "./engines/stockfish/stockfish_15_x64.exe",
                        "depth", engLvl2);
                break;
            case 4:
                engLvl = parent.getEngineVsPlayerLvl();
                p1 = new ComputerPlayer("Stockfish" + engLvl, "./engines/stockfish/stockfish_15_x64.exe",
                        "depth", engLvl);
                p2 = new HumanPlayer(parent.controller.getPlayerName2());
                break;
            default:
                p1 = new HumanPlayer(parent.controller.getPlayerName1());
                p2 = new HumanPlayer(parent.controller.getPlayerName2());
                System.out.println("Wrong mode");
        }

        Color lightSquare = parent.controller.getLightSquare();
        Color darkSquare = parent.controller.getDarkSquare();

        String position = parent.chessBoard.getChessPosition().getFen().toString();
        if(position.equals(ChessPosition.emptyPosition))
            position = ChessPosition.defaultPosition;

        if (position.compareTo(ChessPosition.defaultPosition) == 0) {
            OpeningBook.readFromFile("./book/book.txt");
        }

        chessBoard = new ChessBoard(8, 8, 80,
                lightSquare, darkSquare, p1, p2, position,
                parent.controller.getScore(),
                parent.controller.getMovesToMake(),
                true);
        ChessClock clock = null;

        String timeFormat = parent.controller.getTimeFormat();
        if (timeFormat.matches("[0-9]+\\+[0-9]+")) {
            clock = new ChessClock(parent.controller.getTimeFormat());
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.weightx = 70;
            gbc.weighty = 10;
            clock.linkChessBoard(chessBoard);
            chessBoard.addClocks(clock);
            chessPanel.add(clock, gbc);
        }
        chessPanel.add(chessBoard);
        GradButton undoMove = new GradButton();
        StylizeButton(undoMove, "Undo move");
        undoMove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chessBoard.undoMove();
            }
        });
        undoMove.setEnabled(chessBoard.canUndo());
        addToRightMenu(undoMove);
        addToRightMenu((JComponent) Box.createHorizontalStrut(5));
        GradButton rewindForward = new GradButton();
        StylizeButton(rewindForward, "->");
        rewindForward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chessBoard.moveHistoryRewind(true);
            }
        });
        GradButton rewindBackward = new GradButton();
        StylizeButton(rewindBackward, "<-");
        rewindBackward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chessBoard.moveHistoryRewind(false);
            }
        });
        addToRightMenu(rewindBackward);
        addToRightMenu(rewindForward);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 70;
        gbc.weighty = 10;
        clock.linkChessBoard(chessBoard);

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
        parent.controller.setMovesToMake(null);
    }
    class EventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            try {
                FileOutputStream fout = new FileOutputStream("exports/FEN.txt", false);
                char ch[] = chessBoard.getChessPosition().getFen().toString().toCharArray();
                for (int i = 0; i < ch.length; i++)
                    fout.write(ch[i]);
                fout.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

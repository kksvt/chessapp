package chess.core;

import chess.players.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ChessWindow extends JFrame {
    public ChessWindow() {
        this.setTitle("Chess App");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            if (false) {
                throw new IOException("");
            }
            //fixme: check if passedPosition.compareTo(defaultPosition) == 0 and only then load openings
            //passedPosition should be specified in the main menu
            OpeningBook.readFromFile("./book/book.txt");
            this.setLayout(new BorderLayout());
            this.add(new ChessBoard(8, 8, 96, Color.white, Color.black,
                    new HumanPlayer("Player 1"),
                    new HumanPlayer("Player 2"),
                    //new ComputerPlayer("Stockfish D2", "./engines/stockfish/stockfish_15_x64.exe", "depth", 2),
                    //new ComputerPlayer("Stockfish D10", true, "./engines/stockfish/stockfish_15_x64.exe", "depth", 10),
                    //new ComputerPlayer("Stockfish", "./engines/stockfish/stockfish_15_x64.exe"),
                    ChessPosition.defaultPosition
                    //"4k3/8/8/8/Pp1N4/5K2/8/8 b - a3 0 1"
                    //"8/8/8/8/K7/1N6/pk6/8 b - - 39 34"
                ),
                    BorderLayout.CENTER
        );
        } catch (IOException e) {
            //todo: engine not found
        }
        this.pack();
        //this.setResizable(false);
        this.setLocationRelativeTo(null);
        //this.setLayout(null);
        this.setVisible(true);
    }
}

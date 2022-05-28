package chess.core;

import chess.Player;

import javax.swing.*;
import java.awt.*;

public class ChessWindow extends JFrame {
    public ChessWindow() {
        this.setTitle("Chess App");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(new ChessBoard(8, 8, 96, Color.white, Color.black,
                new Player("Player 1", true, true),
                new Player("Player 2", false, true)));
        this.pack();
        //this.setResizable(false);
        this.setLocationRelativeTo(null);
        //this.setLayout(null);
        this.setVisible(true);
    }
}

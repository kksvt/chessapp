package chess;

import chess.core.ChessWindow;
import chess.core.Controller;
import chess.gui.BaseWindow;
import chess.gui.MenuWindow;

public class ChessApp {
    private static Controller controller = new Controller();;
    public static void main(String[] args) {
        controller.addWindow( new MenuWindow(controller));
    }
}
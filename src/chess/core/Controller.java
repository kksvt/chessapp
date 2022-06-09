package chess.core;

import chess.gui.BaseWindow;
import chess.gui.MenuWindow;
import chess.players.HumanPlayer;
import chess.players.Player;

import java.awt.*;

public class Controller {
    private static class Database {
        public String position = "8/8/8/8/8/8/8/8";
        public String playerName1 = "Player 1";
        public String playerName2 = "Player 2";

        public Player p1 = new HumanPlayer(playerName1);
        public Player p2 = new HumanPlayer(playerName2);

        public int squareSize = 80;

        public Color lightSquare = Color.white;
        public Color darkSquare = Color.black;

        public String timeFormat = "10+0";
    }

    final Database db = new Database();
    MenuWindow window;

    public Controller(){};

    public void setChessboardPosition(String mode){
        switch (mode){
            case "default":
                db.position = ChessPosition.defaultPosition;
            case "clear":
                db.position = ChessPosition.emptyPosition;
            default:
                db.position = mode;
        }
    }

    final public void addWindow(MenuWindow w){
        window=w;
    }


    public String getPlayerName1() {
        return db.playerName1;
    }

    public void setPlayerName1(String playerName1) {
        db.playerName1 = playerName1;
    }

    public String getPlayerName2() {
        return db.playerName2;
    }

    public void setPlayerName2(String playerName2) {
        db.playerName2 = playerName2;
    }

    public Player getP1() {
        return db.p1;
    }

    public void setP1(Player p1) {
        db.p1 = p1;
    }

    public Player getP2() {
        return db.p2;
    }

    public void setP2(Player p2) {
        db.p2 = p2;
    }

    public Color getLightSquare() {
        return db.lightSquare;
    }

    private void repaintChessboard(){
        window.chessBoard = new ChessBoard(8,8, db.squareSize,
                db.lightSquare, db.darkSquare, db.p1,db.p2, db.position);
    }

    public void setLightSquare(Color lightSquare) {
        db.lightSquare = lightSquare;
        repaintChessboard();
    }

    public Color getDarkSquare() {
        return db.darkSquare;
    }

    public void setDarkSquare(Color darkSquare) {
        db.darkSquare = darkSquare;
        repaintChessboard();
    }

    public String getTimeFormat() {
        return db.timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        db.timeFormat = timeFormat;
    }

    public String getPosition() {
        return db.position;
    }

    public void setPosition(String position) {
        db.position = position;
        repaintChessboard();
    }

}

package chess.players;

import chess.core.ChessBoard;

public abstract class Player {
    protected String name;
    protected boolean isHuman;
    public Player(String name) {
        this.name = name;
        this.isHuman = false;
    }
    public String getName() { return name; }
    public boolean getIsHuman() { return isHuman; }
    public void think(ChessBoard chessBoard, int delay) {}
    public void stop() {}
    public void undoMove() {}
}

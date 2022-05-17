package chess;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Piece  {
    //these dont change
    char sign;
    boolean canJump;
    boolean infinite;
    int value;
    List<PieceMove> moves;
    //these change -- fixme: move to ChessSquare
    //List<Point> legalMoves;
    //Point boardPosition;

    Piece(char sign) {//, int x, int y) {
        this.sign = sign;
        this.moves = new ArrayList<PieceMove>();
        //boardPosition = new Point(x, y);
    }

    public List<PieceMove> getMoves() {
        return moves;
    }

    //public Point getBoardPosition() { return boardPosition; }

    public boolean isCanJump() { return canJump; }

    public boolean isInfinite() { return infinite; }

    public boolean canMove(int file, int rank, PieceMove m, ChessPosition position) {
        return true;
    }

    public char getSign() {
        return sign;
    }

    public boolean isWhite() { return Character.isUpperCase(sign); }

    public void commitMove(RealMove m, ChessPosition position) {}

    public void commitCaptured(RealMove m, ChessPosition position) {}

}
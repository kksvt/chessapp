package chess;

import chess.pieces.Piece;

//a move that happened on the board
public class RealMove {
    public  ChessSquare from;
    public  ChessSquare to;
    public final Piece fromPiece;
    public final Piece toPiece;
    public int flags;
    public char arg;

    public RealMove(ChessSquare from, ChessSquare to, int flags, char arg) {
        this.from = from;
        this.to = to;
        this.fromPiece = from.getPiece();
        this.toPiece = to.getPiece();
        this.flags = flags;
        this.arg = arg;
    }

    public RealMove(ChessSquare from, ChessSquare to, int flags) {
        this(from, to, flags, '\0');
    }

    public RealMove(ChessSquare from, ChessSquare to) {
        this(from, to, 0, '\0');
    }

    public int getFileDestination() {
        return to.getFile();
    }

    public int getRankDestination() {
        return to.getRank();
    }

    public int getFileFrom() {
        return from.getFile();
    }

    public int getRankFrom() {
        return from.getRank();
    }

    public String getHumanMove() {
        return ""; //todo
    }

}

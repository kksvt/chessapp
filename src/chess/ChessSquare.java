package chess;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ChessSquare {
    Piece piece;
    int file;
    int rank;
    List<RealMove> legalMoves;
    List<MovePin> pins;

    ChessSquare(Piece piece, int file, int rank) {
        this.piece = piece;
        this.file = file;
        this.rank = rank;
    }
    public boolean isEmpty() {
        return (this.piece == null);
    }
    public void addPiece(Piece piece) {
        this.piece = piece;
    }
    public void removePiece() {
        this.piece = null;
        this.legalMoves = null;
        this.pins = null;
    }
    public Piece getPiece() { return piece; }

    public int getFile() { return file; }

    public int getRank() { return rank; }
}

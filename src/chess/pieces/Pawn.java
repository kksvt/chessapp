package chess.pieces;

import chess.ChessPosition;
import chess.PieceMove;

public class Pawn extends Piece {

    public Pawn(char sign) {
        super(sign);
    }

    @Override
    void setMoves() {
        if (this.isWhite()) {
            //moves
            this.moves.add(new PieceMove(0, -2, false, false));
            this.moves.add(new PieceMove(0, -1, false, false));
            //captures
            this.moves.add(new PieceMove(1, -1, true, true));
            this.moves.add(new PieceMove(-1, -1, true, true));
            //fixme: add en passant
        }
        else {
            //moves
            this.moves.add(new PieceMove(0, 2, false, false));
            this.moves.add(new PieceMove(0, 1, false, false));
            //captures
            this.moves.add(new PieceMove(1, 1, true, true));
            this.moves.add(new PieceMove(-1, 1, true, true));
            //fixme: add en passant
        }
    }

    public boolean canMove(int file, int rank, PieceMove m, ChessPosition position) {
        if (m.getVector().x == 0 && m.getVector().y == -2) {
            if (rank != position.height() - 2) {
                return false;
            }
            return rank - 1 >= 0 && position.getPiece(file, rank - 1) == null;
        }
        else if (m.getVector().x == 0 && m.getVector().y == 2) {
            if (rank != 1) {
                return false;
            }
            return rank + 1 < position.height() && position.getPiece(file, rank + 1) == null;
        }
        return true;
    }
}
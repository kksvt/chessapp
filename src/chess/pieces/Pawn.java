package chess.pieces;

import chess.core.*;

public class Pawn extends Piece {

    public Pawn(char sign) {
        super(sign);
    }

    @Override
    void setMoves() {
        if (this.isWhite()) {
            //moves
            this.moves.add(new PieceMove(0, -2, false, false, MoveFlags.RM_TWOSQRPAWN));
            this.moves.add(new PieceMove(0, -1, false, false));
            //captures
            this.moves.add(new PieceMove(1, -1, true, true));
            this.moves.add(new PieceMove(-1, -1, true, true));
            //en passant - not set as a capture because we don't capture a piece on the target square
            this.moves.add(new PieceMove(1, -1, false, false, MoveFlags.RM_ENPASSANT));
            this.moves.add(new PieceMove(-1, -1, false, false, MoveFlags.RM_ENPASSANT));
        }
        else {
            //moves
            this.moves.add(new PieceMove(0, 2, false, false, MoveFlags.RM_TWOSQRPAWN));
            this.moves.add(new PieceMove(0, 1, false, false));
            //captures
            this.moves.add(new PieceMove(1, 1, true, true));
            this.moves.add(new PieceMove(-1, 1, true, true));
            //en passant - not set as a capture because we don't capture a piece on the target square
            this.moves.add(new PieceMove(1, 1, false, false, MoveFlags.RM_ENPASSANT));
            this.moves.add(new PieceMove(-1, 1, false, false, MoveFlags.RM_ENPASSANT));
        }
    }

    public boolean canMove(int file, int rank, PieceMove m, ChessPosition position) {
        if (MoveFlags.hasFlag(m.flags(), MoveFlags.RM_ENPASSANT)) {
            if (position.isEnPassantAvailable() &&
                    rank + m.getVector().y == position.getEnPassant()[1] &&
                    file + m.getVector().x == position.getEnPassant()[0]) {
                return true;
            }
            return false;
        }
        else if (MoveFlags.hasFlag(m.flags(), MoveFlags.RM_TWOSQRPAWN)) {
            if (m.getVector().y == -2) {
                if (rank != position.height() - 2) {
                    return false;
                }
                return rank - 1 >= 0 && position.getPiece(file, rank - 1) == null;
            }
            else {
                if (rank != 1) {
                    return false;
                }
                return rank + 1 < position.height() && position.getPiece(file, rank + 1) == null;
            }
        }
        return true;
    }

    public void commitMove(RealMove m, ChessPosition position) {
        if (MoveFlags.hasFlag(m.flags(), MoveFlags.RM_TWOSQRPAWN)) {
            position.setEnPassant(m.from().getFile(), m.to().getRank() + (isWhite() ? 1 : -1));
            System.out.println("En passant is set to " + Character.toString(position.getEnPassant()[0] + 'a') + "" + (position.height() - position.getEnPassant()[1]));
        }
        else if (MoveFlags.hasFlag(m.flags(), MoveFlags.RM_ENPASSANT)) {
            ChessSquare sqr = position.getSquare(position.getEnPassant()[1] + (isWhite() ? 1 : -1), position.getEnPassant()[0]);
            sqr.getPiece().commitCaptured(m, position);
            sqr.removePiece();
        }
    }
}
package chess.pieces;

import chess.core.ChessPosition;
import chess.core.MoveFlags;
import chess.core.PieceMove;
import chess.core.RealMove;

public class Rook extends Piece {
    boolean kingSide;
    boolean queenSide;

    public Rook(char sign) {
        super(sign);
    }

    @Override
    void setMoves(){
        this.moves.add(new PieceMove(1, 0, true, false));
        this.moves.add(new PieceMove(-1, 0, true, false));
        this.moves.add(new PieceMove(0, 1, true, false));
        this.moves.add(new PieceMove(0, -1, true, false));
    }

    public Rook(char sign, boolean kingSide, boolean queenSide) {
        this(sign);
        this.kingSide = kingSide;
        this.queenSide = queenSide;
    }

    public boolean isKingSide() {
        return kingSide;
    }

    public boolean isQueenSide() {
        return queenSide;
    }

    public void commitMove(RealMove m, ChessPosition position) {
        if (!MoveFlags.hasFlag(m.flags(), MoveFlags.RM_ROOK_CASTLING)) {
            //System.out.println("Rook moved - castling will be invalidated");
            invalidateCastling(m, position);
        }
    }

    public void commitCaptured(RealMove m, ChessPosition position) {
        //System.out.println("Rook got captured - castling will be invalidated");
        invalidateCastling(m, position);
    }

    public void invalidateCastling(RealMove m, ChessPosition position) {
        if (this.isWhite()) {
            if (position.whiteCastleKingSide() && this.isKingSide()) {
                m.setFlagsBitwise(MoveFlags.RM_WHITE_CASTLE_KINGSIDE_IMPOSSIBLE);
                position.setCastleFlags(ChessPosition.WHITE_KINGSIDE);
            }
            if (position.whiteCastleQueenSide() && this.isQueenSide()) {
                m.setFlagsBitwise(MoveFlags.RM_WHITE_CASTLE_QUEENSIDE_IMPOSSIBLE);
                position.setCastleFlags(ChessPosition.WHITE_QUEENSIDE);
            }
        }
        else {
            if (position.blackCastleKingSide() && this.isKingSide()) {
                m.setFlagsBitwise(MoveFlags.RM_BLACK_CASTLE_KINGSIDE_IMPOSSIBLE);
                position.setCastleFlags(ChessPosition.BLACK_KINGSIDE);
            }
            if (position.blackCastleQueenSide() && this.isQueenSide()) {
                m.setFlagsBitwise(MoveFlags.RM_BLACK_CASTLE_QUEENSIDE_IMPOSSIBLE);
                position.setCastleFlags(ChessPosition.BLACK_QUEENSIDE);
            }
        }
    }
}

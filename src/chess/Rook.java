package chess;

public class Rook extends Piece {
    boolean kingSide;
    boolean queenSide;

    Rook(char sign) {
        super(sign);
        this.value = ChessPosition.ROOK_VALUE;
        this.canJump = false;
        this.infinite = true;
        //moves
        this.moves.add(new PieceMove(1, 0, true, false));
        this.moves.add(new PieceMove(-1, 0, true, false));
        this.moves.add(new PieceMove(0, 1, true, false));
        this.moves.add(new PieceMove(0, -1, true, false));
    }

    Rook(char sign, boolean kingSide, boolean queenSide) {
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
        System.out.println("Rook moved - castling will be invalidated");
        invalidateCastling(m, position);
    }

    public void commitCaptured(RealMove m, ChessPosition position) {
        System.out.println("Rook got captured - castling will be invalidated");
        invalidateCastling(m, position);
    }

    public void invalidateCastling(RealMove m, ChessPosition position) {
        if (position.canCastleKingSide() || position.canCastleQueenSide()) {
            if (this.isWhite()) {
                if (position.whiteCastleKingSide() && this.isKingSide()) {
                    m.flags |= MoveFlags.RM_WHITE_CASTLE_KINGSIDE_IMPOSSIBLE;
                    position.castleFlags &= ~(position.WHITE_KINGSIDE);
                }
                if (position.whiteCastleQueenSide() && this.isQueenSide()) {
                    m.flags |= MoveFlags.RM_WHITE_CASTLE_QUEENSIDE_IMPOSSIBLE;
                    position.castleFlags &= ~(position.WHITE_QUEENSIDE);
                }
            }
            else {
                if (position.blackCastleKingSide() && this.isKingSide()) {
                    m.flags |= MoveFlags.RM_BLACK_CASTLE_KINGSIDE_IMPOSSIBLE;
                    position.castleFlags &= ~(position.BLACK_KINGSIDE);
                }
                if (position.blackCastleQueenSide() && this.isQueenSide()) {
                    m.flags |= MoveFlags.RM_BLACK_CASTLE_QUEENSIDE_IMPOSSIBLE;
                    position.castleFlags &= ~(position.BLACK_QUEENSIDE);
                }
            }
        }
    }
}

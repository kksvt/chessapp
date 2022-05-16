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
}

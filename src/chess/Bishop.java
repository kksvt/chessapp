package chess;

public class Bishop extends Piece {
    Bishop(char sign) {
        super(sign);
        this.value = ChessPosition.BISHOP_VALUE;
        this.canJump = false;
        this.infinite = true;
        //moves
        this.moves.add(new PieceMove(1, 1, true, false));
        this.moves.add(new PieceMove(1, -1, true, false));
        this.moves.add(new PieceMove(-1, 1, true, false));
        this.moves.add(new PieceMove(-1, -1, true, false));
    }
}

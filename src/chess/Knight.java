package chess;

public class Knight extends Piece {

    Knight(char sign) {
        super(sign);
        this.value = ChessPosition.KNIGHT_VALUE;
        this.canJump = true;
        this.infinite = false;
        //moves
        this.moves.add(new PieceMove(2, 1, true, false));
        this.moves.add(new PieceMove(2, -1, true, false));
        this.moves.add(new PieceMove(-2, 1, true, false));
        this.moves.add(new PieceMove(-2, -1, true, false));
        this.moves.add(new PieceMove(1, 2, true, false));
        this.moves.add(new PieceMove(1, -2, true, false));
        this.moves.add(new PieceMove(-1, 2, true, false));
        this.moves.add(new PieceMove(-1, -2, true, false));
    }
}

package chess;

public class Queen extends Piece {
    Queen(char sign) {
        super(sign);
        this.value = ChessPosition.QUEEN_VALUE;
        this.canJump = false;
        this.infinite = true;
        //moves
        this.moves.add(new PieceMove(1, 1, true, false));
        this.moves.add(new PieceMove(1, -1, true, false));
        this.moves.add(new PieceMove(-1, 1, true, false));
        this.moves.add(new PieceMove(-1, -1, true, false));
        this.moves.add(new PieceMove(1, 0, true, false));
        this.moves.add(new PieceMove(-1, 0, true, false));
        this.moves.add(new PieceMove(0, 1, true, false));
        this.moves.add(new PieceMove(0, -1, true, false));
    }
}

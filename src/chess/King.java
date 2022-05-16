package chess;

public class King extends Piece {
    King(char sign) {
        super(sign);
        this.value = Integer.MAX_VALUE;
        this.canJump = false;
        this.infinite = false;
        //moves
        this.moves.add(new PieceMove(1, 1, true, false));
        this.moves.add(new PieceMove(1, -1, true, false));
        this.moves.add(new PieceMove(-1, 1, true, false));
        this.moves.add(new PieceMove(-1, -1, true, false));
        this.moves.add(new PieceMove(1, 0, true, false));
        this.moves.add(new PieceMove(-1, 0, true, false));
        this.moves.add(new PieceMove(0, 1, true, false));
        this.moves.add(new PieceMove(0, -1, true, false));
        //in chess960 the king may end up in different squares after castling, but it will be dealt with somewhere else
        this.moves.add(new PieceMove(2, 0, false, false));
        this.moves.add(new PieceMove(-2, 0, false, false));
    }
    public boolean canMove(int file, int rank, PieceMove m, ChessPosition position) {
        if (position.attackedSquares[rank + m.getVector().y][file + m.getVector().x]) {
            return false;
        }
        return true;
    }
}

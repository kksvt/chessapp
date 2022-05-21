package chess.pieces;

import chess.PieceMove;

public class Knight extends Piece {

    public Knight(char sign) {
        super(sign);
    }

    @Override
    void setMoves(){
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

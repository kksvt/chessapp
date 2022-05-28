package chess.pieces;

import chess.core.PieceMove;

public class Bishop extends Piece {
    public Bishop(char sign) {
        super(sign);
    }

    @Override
    void setMoves() {
        this.moves.add(new PieceMove(1, 1, true, false));
        this.moves.add(new PieceMove(1, -1, true, false));
        this.moves.add(new PieceMove(-1, 1, true, false));
        this.moves.add(new PieceMove(-1, -1, true, false));
    }


}

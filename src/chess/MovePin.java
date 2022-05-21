package chess;

import java.awt.*;

public class MovePin {
    ChessSquare pinnedBy;
    Point attackVector;

    public MovePin(ChessSquare pinnedBy, Point attackVector) {
        this.pinnedBy = pinnedBy;
        this.attackVector = attackVector;
    }
}

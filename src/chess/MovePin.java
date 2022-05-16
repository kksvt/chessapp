package chess;

import java.util.*;
import java.util.List;
import java.awt.*;

public class MovePin {
    ChessSquare pinnedBy;
    Point attackVector;

    public MovePin(ChessSquare pinnedBy, Point attackVector) {
        this.pinnedBy = pinnedBy;
        this.attackVector = attackVector;
    }
}

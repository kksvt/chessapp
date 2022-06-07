package chess.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RealMoveTest {
    @Test
    void algToRealMove() {
        ChessPosition chessPosition = new ChessPosition(8, 8, ChessPosition.defaultPosition);
        RealMove m = RealMove.algToRealMove(chessPosition, "Nf3");
        assertTrue(m != null);
        assertEquals(m.getRankDestination(), 5);
        assertEquals(m.getFileDestination(), 5);
        assertTrue(m.fromPiece() != null);
        assertEquals(m.fromPiece().getSign(), 'N');
        chessPosition.move(m, true);
        m = RealMove.algToRealMove(chessPosition, "e6");
        assertTrue(m != null);
        assertEquals(m.getRankDestination(), 2);
        assertEquals(m.getFileDestination(), 4);
        assertTrue(m.fromPiece() != null);
        assertEquals(m.fromPiece().getSign(), 'p');
        //assertTrue(m.getFileDestination() == 5 && m.getRankDestination() == 5 && m.fromPiece() != null && m.fromPiece().getSign() == 'N');
    }

}

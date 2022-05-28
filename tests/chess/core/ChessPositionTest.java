package chess.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChessPositionTest {

    @Test
    void parsePosition() {
        String default_position = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        ChessPosition chessPosition = new ChessPosition(8,8,default_position);

        assertTrue(chessPosition.parsePosition(default_position));
        assertTrue(chessPosition.parsePosition("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"));
        assertFalse(chessPosition.parsePosition("rnbqkbnr/ppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"));
        assertFalse(chessPosition.parsePosition(""));
        assertFalse(chessPosition.parsePosition(null));
    }
}
package chess.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChessPositionTest {

    private class ChessPositionSet {
        private String fen;
        private boolean isOk;

        public ChessPositionSet(String fen, boolean isOk) {
            this.fen = fen;
            this.isOk = isOk;
        }

        public boolean getIsOk() {
            return isOk;
        }

        public String getFen() {
            return fen;
        }
    }

    @Test
    void parsePosition() {
        ChessPositionSet testPositions[] = new ChessPositionSet[]{
                new ChessPositionSet("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", true),
                new ChessPositionSet("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", true),
                new ChessPositionSet("rnbqkbnr/ppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", false),
                new ChessPositionSet("", false),
                new ChessPositionSet(null, false)
        };
        ChessPosition chessPosition = new ChessPosition(8, 8, testPositions[0].getFen());
        for (ChessPositionSet p : testPositions) {
            if (p.getIsOk()) {
                assertTrue(chessPosition.parsePosition(p.getFen()));
                assertTrue(p.getFen().contentEquals(chessPosition.savePosition()));
            }
            else {
                assertFalse(chessPosition.parsePosition(p.getFen()));
            }
        }
    }
}
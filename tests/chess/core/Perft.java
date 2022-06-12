package chess.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Perft {
    private ChessPosition chessPosition;

    private void printMove(RealMove rm) {
        System.out.print(Character.toString(rm.getFileFrom() + 'a') + "" +
                Character.toString(chessPosition.height() - rm.getRankFrom() + '0'));
        System.out.print(Character.toString(rm.getFileDestination() + 'a') + "" +
                Character.toString(chessPosition.height() - rm.getRankDestination() + '0') );
        System.out.print(rm.getArg() != '\0' ? rm.getArg() : "");
    }

    private int perft_call(int startDepth, int depth, boolean printMoves) {
        if (depth == 0) {
            return 1;
        }
        int total = 0;
        for (RealMove mv : chessPosition.getAllMoves()) {
            if (depth == startDepth && printMoves) {
                printMove(mv);
            }
            chessPosition.move(mv, false);
            int addTotal = perft_call(startDepth,depth - 1, printMoves);
            if (depth == startDepth && printMoves) {
                System.out.println(": " + addTotal);
            }
            total += addTotal;
            chessPosition.undoMove(mv);
        }
        return total;
    }

    public int perft(int startDepth, boolean printMoves) {
        int total = perft_call(startDepth, startDepth, printMoves);
        System.out.println("For depth " + startDepth + " there are " + total + " nodes");
        return total;
    }

    @Test
    public void runPerft() {
        chessPosition = new ChessPosition(8, 8, ChessPosition.defaultPosition);
        assertEquals(perft(1, false), 20);
        assertEquals(perft(2, false), 400);
        assertEquals(perft(3, false), 8902);
        assertEquals(perft(4, false), 197281);
        assertEquals(perft(5, false), 4865609);
        assertEquals(perft(6, false), 119060324);
        chessPosition.parsePosition("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
        assertEquals(perft(1, false), 48);
        assertEquals(perft(2, false), 2039);
        assertEquals(perft(3, false), 97862);
        assertEquals(perft(4, false), 4085603);
        assertEquals(perft(5, false), 193690690);
        chessPosition.parsePosition("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");
        assertEquals(perft(1, false), 14);
        assertEquals(perft(2, false), 191);
        assertEquals(perft(3, false), 2812);
        assertEquals(perft(4, false), 43238);
        assertEquals(perft(5, false), 674624);
        assertEquals(perft(6, false), 11030083);
        chessPosition.parsePosition("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
        assertEquals(perft(1, false), 6);
        assertEquals(perft(2, false), 264);
        assertEquals(perft(3, false), 9467);
        assertEquals(perft(4, false), 422333);
        assertEquals(perft(5, false), 15833292);
        chessPosition.parsePosition("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
        assertEquals(perft(1, false), 44);
        assertEquals(perft(2, false), 1486);
        assertEquals(perft(3, false), 62379);
        assertEquals(perft(4, false), 2103487);
        assertEquals(perft(5, false), 89941194);
    }
}

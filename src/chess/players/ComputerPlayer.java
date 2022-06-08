package chess.players;

import chess.core.*;
import java.io.*;

public class ComputerPlayer extends Player {
    private Process engine;
    private BufferedReader reader;
    private BufferedWriter writer;

    private String name;

    private String param;
    private int paramValue;

    private Thread thread;

    private boolean outOfBook;

    public ComputerPlayer(String name, String path) throws IOException {
        this(name, path, "depth", 20);
    }

    public ComputerPlayer(String name, String path, String param, int paramValue) throws IOException {
        super(name);
        this.isHuman = false;
        this.param = param;
        this.paramValue = paramValue;
        this.outOfBook = false;
        ProcessBuilder processBuilder = new ProcessBuilder(path);
        engine = processBuilder.start();
        reader = new BufferedReader(new InputStreamReader(engine.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(engine.getOutputStream()));
        engineCmd(null);
    }

    private boolean engineCmd(String cmd) {
        try {
            writer.write(cmd == null ? "isready\n" : (cmd + "\nisready\n"));
            writer.flush();
            while (true) {
                if (reader.readLine().compareTo("readyok") == 0) {
                    break;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void stop() {
        if (thread != null && thread.isAlive()) {
            System.out.println("stopping the thread");
            thread.stop(); //fixme: this is not a good practice but we may have to forcefully stop the thread in case player undoes their move
        }
    }

    public void undoMove() {
        outOfBook = false;
    }

    public void think(ChessBoard chessBoard, int delay) {
        stop();
        thread = new Thread(() -> {
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!outOfBook) {
                if (OpeningBook.bookExists() && chessBoard.getMoveHistory().size() < 12) {
                    System.out.println("============");
                    String move = OpeningBook.getRandomMove(chessBoard.getMoveHistory());
                    if (move != null && move.length() > 0) {
                        System.out.println("The move we got is: " + move);
                        RealMove m = RealMove.algToRealMove(chessBoard.getChessPosition(), move);
                        if (m != null) {
                            chessBoard.move(m, true);
                        } else {
                            System.out.println("We are out of book: Couldn't find the book move");
                            outOfBook = true;
                        }
                    }
                    else {
                        outOfBook = true;
                    }
                }
                else {
                    System.out.println("We are out of book: book doesn't exist or more than 6 moves have been made");
                    outOfBook = true;
                }
            }
            if (outOfBook) {
                updatePosition(chessBoard);
                playMove(chessBoard, getBestMove());
            }
        });
        thread.start();
    }

    private boolean updatePosition(ChessBoard chessBoard) {
        StringBuilder cmd = new StringBuilder("position fen " + chessBoard.getStartPos() + " moves ");
        ChessPosition chessPosition = chessBoard.getChessPosition();
        for (RealMove m : chessBoard.getMoveHistory()) {
            cmd.append(m.getEngineMove(chessPosition.height()) + " ");
        }
        return engineCmd(cmd.toString());
    }

    private boolean playMove(ChessBoard chessBoard, String move) {
        ChessPosition chessPosition = chessBoard.getChessPosition();
        RealMove realMove = RealMove.getMoveForEngine(chessPosition, move);
        if (realMove == null) {
            System.out.println(chessPosition.savePosition());
            for (RealMove rm : chessPosition.getAllMoves()) {
                System.out.println("Legal move: " + rm.getEngineMove(chessPosition.height()));
                if (MoveFlags.hasFlag(rm.flags(), MoveFlags.RM_PROMOTION)) {
                    System.out.println("Promotion flag: " + rm.getArg());
                }
            }
            throw new IllegalPositionException("Engine wants to play a move that's deemed illegal: " + move);
        }
        else {
            chessBoard.move(realMove, true);
            return true;
        }
    }

    private String getBestMove() {
        try {
            writer.write("\ngo " + param + " " + paramValue + '\n');
            writer.flush();
            while (true) {
                String response = reader.readLine();
                if (response.contains("bestmove")) {
                    return response.split(" ")[1];
                }
            }
        }
        catch (IOException e) {
            return null;
        }
    }

}

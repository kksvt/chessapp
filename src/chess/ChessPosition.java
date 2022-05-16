package chess;

import java.util.List;
import java.util.ArrayList;
import java.awt.*;

public class ChessPosition {
    String fen = "";
    final static String defaultPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    final static int WHITE_KINGSIDE = 1;
    final static int WHITE_QUEENSIDE = 2;
    final static int BLACK_KINGSIDE = 4;
    final static int BLACK_QUEENSIDE = 8;

    final static int QUEEN_VALUE = 900;
    final static int ROOK_VALUE = 500;
    final static int BISHOP_VALUE = 300;
    final static int KNIGHT_VALUE = 300;
    final static int PAWN_VALUE = 100;

    int width;
    int height;
    boolean attackedSquares[][];
    boolean threatenedSquares[][];
    //Piece pieces[][];
    ChessSquare squares[][];
    boolean whiteToMove;
    int enPassant;
    int castleFlags;
    int halfMove;
    int fullMove;

    int numLegalMoves;
    boolean whiteCheck;
    boolean blackCheck;
    List<MovePin> kingThreats;
    ChessSquare kingSquare;

    static boolean isValidPiece(char piece) {
        switch (Character.toLowerCase(piece)) {
            case 'k':
            case 'q':
            case 'r':
            case 'b':
            case 'n':
            case 'p':
                return true;
            default:
                return false;
        }
    }

    ChessPosition(int width, int height, String position) {
        this.width = width;
        this.height = height;
        attackedSquares = new boolean[height][width];
        threatenedSquares = new boolean[height][width];
        squares = new ChessSquare[height][width];
        if (!parsePosition(position)) {
            System.out.println("Invalid FEN, applying default starting position...");
            parsePosition(defaultPosition);
        }
    }

    public String getFen() {
        return fen;
    }

    public Piece getPiece(int file, int rank) {
        return squares[rank][file].getPiece();
    }

    public boolean parsePosition(String position) {
        whiteToMove = true;
        fullMove = halfMove = enPassant = -1;
        castleFlags = 0;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                squares[i][j] = null;
            }
        }
        if (position.length() == 0) {
            return false;
        }
        for (int i = 0, rank = 0, file = 0; i < position.length(); ++i) {
            char c = position.charAt(i);
            if (Character.isSpaceChar(c)) {
                continue;
            }
            else if (rank == height - 1 && file == width) {

            }
            else {
                if (c == '\\' || c == '/') {
                    if (++rank == height || file != width) {
                        return false;
                    }
                    file = 0;
                } else {
                    if (Character.isDigit(c)) {
                        int skip = 0;
                        while (i < position.length()) {
                            c = position.charAt(i);
                            if (!Character.isDigit(c)) {
                                break;
                            }
                            skip *= 10;
                            skip += c - '0';
                            ++i;
                        }
                        --i;
                        //file += skip;
                        while (--skip >= 0) {
                            if (file == width) {
                                return false;
                            }
                            squares[rank][file] = new ChessSquare(null, file, rank);
                            ++file;
                        }
                    }
                    else if (isValidPiece(c)) {
                        if (file >= width || rank >= height) {
                            return false;
                        }
                        //pieces[rank][file++] = c;
                        Piece piece;
                        switch (Character.toLowerCase(c)) {
                            case 'k':
                                piece = new King(c);
                                break;
                            case 'q':
                                piece = new Queen(c);
                                break;
                            case 'n':
                                piece = new Knight(c);
                                break;
                            case 'b':
                                piece = new Bishop(c);
                                break;
                            case 'r':
                                piece = new Rook(c);
                                break;
                            default:
                                piece = new Pawn(c);
                                break;
                        }
                        squares[rank][file] = new ChessSquare(piece, file, rank);
                        ++file;
                    }
                    else {
                        return false;
                    }
                }
            }
        }
        calculateLegalMoves();
        fen = position;
        return true;
    }

    public void calculateLegalMoves() throws IllegalPositionException {
        numLegalMoves = 0;
        whiteCheck = blackCheck = false;
        kingThreats = null;
        kingSquare = null;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                attackedSquares[i][j] = threatenedSquares[i][j] = false;
                squares[i][j].pins = null;
                squares[i][j].legalMoves = null;
            }
        }
        for (ChessSquare[] sqr : squares) {
            for (ChessSquare s : sqr) {
                if (!s.isEmpty() && s.getPiece().isWhite() != whiteToMove) {
                    calculateThreats(s);
                }
            }
        }
        for (ChessSquare[] sqr : squares) {
            for (ChessSquare s : sqr) {
                if (!s.isEmpty() && s.getPiece().isWhite() == whiteToMove) {
                    calculateLegalMoves(s);
                }
            }
        }
        System.out.println("There are " + numLegalMoves + " legal moves in this position");
    }

    public void calculateThreats(ChessSquare s) {
        int x, y;
        Piece p = s.getPiece();
        for (PieceMove pm : s.getPiece().getMoves()) {
            if (!pm.isCanCapture() && !pm.isMustCapture()) {
                continue;
            }
            x = s.getFile() + pm.getVector().x;
            y = s.getRank() + pm.getVector().y;
            ChessSquare hitPieceSquare = null;
            while (x >= 0 && x < width &&
                 y >= 0 && y < height) {
                Piece target = getPiece(x, y);
                if (target == null) {
                    if (hitPieceSquare == null || p.isCanJump()) {
                        attackedSquares[y][x] = true;
                    }
                    else {
                        threatenedSquares[y][x] = true;
                    }
                }
                else if (target.isWhite() != p.isWhite()) {
                    if (hitPieceSquare == null) {
                        attackedSquares[y][x] = true;
                        if (Character.toLowerCase(target.getSign()) == 'k') {
                            if (whiteToMove) {
                                System.out.println("White is in check!");
                                whiteCheck = true;
                            }
                            else {
                                System.out.println("Black is in check!");
                                blackCheck = true;
                            }
                            if (kingThreats == null) {
                                kingThreats = new ArrayList<MovePin>();
                                kingSquare = squares[y][x];
                            }
                            kingThreats.add(new MovePin(s, new Point(pm.getVector().x, pm.getVector().y)));
                        }
                        else {//if we hit the king, then we have to make sure that the squares behind him will also be marked as attacked - he cant retreat to them or he would be in check
                            hitPieceSquare = squares[y][x];
                        }
                    }
                    else {
                        if (Character.toLowerCase(target.getSign()) == 'k') {
                            if (hitPieceSquare.pins == null) {
                                hitPieceSquare.pins = new ArrayList<MovePin>();
                            }
                            hitPieceSquare.pins.add(new MovePin(s, new Point(pm.getVector().x, pm.getVector().y)));
                            //System.out.println(s.getPiece().getSign() + " is pinning " + hitPieceSquare.getPiece().getSign());
                        }
                        threatenedSquares[y][x] = true;
                        if (!p.isCanJump()) {
                            break;
                        }
                    }
                }
                else {
                    attackedSquares[y][x] = true; //so that the enemy king cannot capture a defended piece
                    if ((!p.isCanJump())) {
                        break;
                    }
                }
                if (!p.isInfinite()) {
                    break;
                }
                x += pm.getVector().x;
                y += pm.getVector().y;
            }
        }
    }

    public void calculateLegalMoves(ChessSquare s) throws IllegalPositionException {
        s.legalMoves = new ArrayList<Point>();
        int x, y;
        Piece p = s.getPiece();
        for (PieceMove pm : s.getPiece().getMoves()) {
            x = s.getFile() + pm.getVector().x;
            y = s.getRank() + pm.getVector().y;
            while (x >= 0 && x < width &&
                y >= 0 && y < height &&
                pm.canMove(this, s)) {
                Piece target = getPiece(x, y);
                if ((target == null && !pm.isMustCapture()) ||
                        (target != null && pm.isCanCapture() && target.isWhite() != p.isWhite())) {
                    boolean neutralized = true;
                    if (kingInCheck()) {
                        //System.out.println("King is in check, there are " + kingThreats.size() + " threats");
                        if (kingThreats.size() > 1) {
                            neutralized = false;
                        }
                        else if (target == null) {
                            //king cant really block anything and moving onto attacked squares is covered in King::canMove
                            if (Character.toLowerCase(p.getSign()) != 'k') {
                                neutralized = false;
                                MovePin threat = kingThreats.get(0);
                                int blockRank = threat.pinnedBy.getRank() + threat.attackVector.y,
                                        blockFile = threat.pinnedBy.getFile() + threat.attackVector.x;
                                while (blockRank >= 0 && blockRank < height && blockFile >= 0 && blockFile < width) {
                                    if (kingSquare == squares[blockRank][blockFile]) {
                                        break;
                                    }
                                    if (y == blockRank && x == blockFile) {
                                        neutralized = true;
                                        break;
                                    }
                                    blockRank += threat.attackVector.y;
                                    blockFile += threat.attackVector.x;
                                }
                            }
                        }
                        else {
                            if (squares[y][x] != kingThreats.get(0).pinnedBy) {
                                neutralized = false;
                            }
                        }
                    }
                    if (target != null && Character.toLowerCase(target.getSign()) == 'k') {
                        throw new IllegalPositionException("The king can be captured!");
                    }
                    if (neutralized) {
                        s.legalMoves.add(new Point(x, y));
                        ++numLegalMoves;
                    }
                    if ((!p.isCanJump() && target != null) || !p.isInfinite()) {
                        break;
                    }
                    x += pm.getVector().x;
                    y += pm.getVector().y;
                }
                else {
                    break;
                }
            }
        }
        //System.out.println(p.getSign() + " piece has " + p.legalMoves.size() + " legal moves");
    }

    public boolean move(ChessSquare sqrFrom, ChessSquare sqrTo) {
        if (!sqrTo.isEmpty()) {
            sqrTo.removePiece();
        }
        sqrTo.piece = sqrFrom.getPiece();
        sqrFrom.removePiece();
        whiteToMove = !whiteToMove;
        calculateLegalMoves();
        return true;
    }

    public ChessSquare[][] getSquares() {
        return squares;
    }

    public boolean kingInCheck() {
        return whiteCheck || blackCheck;
    }
}

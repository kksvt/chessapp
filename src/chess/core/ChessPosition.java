package chess.core;

import chess.pieces.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChessPosition {
    String fen = "";
    public final static String defaultPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public final static int WHITE_KINGSIDE = 1;
    public final static int WHITE_QUEENSIDE = 2;
    public final static int BLACK_KINGSIDE = 4;
    public final static int BLACK_QUEENSIDE = 8;


    private int width;
    private int height;
    private boolean[][] attackedSquares;
    private boolean[][] threatenedSquares;
    private ChessSquare[][] squares;
    private boolean whiteToMove;

    private int castleFlags;

    private int numLegalMoves;
    private boolean whiteCheck;
    private boolean blackCheck;
    List<MovePin> kingThreats;
    ChessSquare kingSquare;

    int[][] rookPositionX;

    ChessPosition(int width, int height, String position) {
        this.width = width;
        this.height = height;
        attackedSquares = new boolean[height][width];
        threatenedSquares = new boolean[height][width];
        rookPositionX = new int[2][2];
        squares = new ChessSquare[height][width];
        if (!parsePosition(position)) {
            System.out.println("Invalid FEN, applying default starting position...");
            parsePosition(defaultPosition);
        }
    }

    public int getCastleFlags() {
        return castleFlags;
    }

    public ChessSquare getSquare(int x, int y){
        assert (x >=0 && x < height);
        assert (y >=0 && y < width);

        return squares[x][y];
    }

    public boolean isAttackedSquare(int x, int y){
        assert (x >=0 && x < height);
        assert (y >=0 && y < width);

        return attackedSquares[x][y];
    }

    public boolean isThreatenedSquare(int x, int y){
        assert (x >=0 && x < height);
        assert (y >=0 && y < width);

        return threatenedSquares[x][y];
    }

    public boolean isWhiteToMove() {
        return whiteToMove;
    }

    public int getNumLegalMoves() {
        return numLegalMoves;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public String getFen() {
        return fen;
    }

    public Piece getPiece(int file, int rank) {
        return squares[rank][file].getPiece();
    }

    public boolean parsePosition(String position) {
        if(position==null)
            return false;

        whiteToMove = true;
        int enPassant;
        int halfMove;
        int fullMove = halfMove = enPassant = -1;
        castleFlags = 0;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                squares[i][j] = null;
            }
        }
        if (position.length() == 0) {
            return false;
        }
        boolean placedWhiteKing = false, placedBlackKing = false;
        int whiteRooks = 0, blackRooks = 0;
        for (int i = 0, rank = 0, file = 0; i < position.length(); ++i) {
            char c = position.charAt(i);
            if (Character.isSpaceChar(c)) {
                continue;
            } else if (rank == height - 1 && file == width) {
                //todo
                castleFlags = (BLACK_KINGSIDE | BLACK_QUEENSIDE | WHITE_KINGSIDE | WHITE_QUEENSIDE);
            } else {
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
                        while (--skip >= 0) {
                            if (file == width) {
                                return false;
                            }
                            squares[rank][file] = new ChessSquare(null, file, rank);
                            ++file;
                        }
                    } else if (Piece.isValidPiece(c)) {
                        if (file >= width || rank >= height) {
                            return false;
                        }
                        Piece piece;
                        boolean isWhitePiece = Character.isLowerCase(c);
                        switch (Character.toLowerCase(c)) {
                            case 'k':
                                if (isWhitePiece) {
                                    placedWhiteKing = true;
                                } else {
                                    placedBlackKing = true;
                                }
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
                                if (isWhitePiece && whiteRooks < 2) {
                                    rookPositionX[1][placedWhiteKing ? 0 : 1] = file;
                                    piece = new Rook(c, placedWhiteKing, !placedWhiteKing);
                                    ++whiteRooks;
                                } else if (!isWhitePiece && blackRooks < 2) {
                                    rookPositionX[0][placedBlackKing ? 0 : 1] = file;
                                    piece = new Rook(c, placedBlackKing, !placedBlackKing);
                                    ++blackRooks;
                                } else {
                                    piece = new Rook(c);
                                }
                                break;
                            default:
                                piece = new Pawn(c);
                                break;
                        }
                        squares[rank][file] = new ChessSquare(piece, file, rank);
                        ++file;
                    } else {
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
                    } else {
                        threatenedSquares[y][x] = true;
                    }
                } else if (target.isWhite() != p.isWhite()) {
                    if (hitPieceSquare == null) {
                        attackedSquares[y][x] = true;
                        if (Character.toLowerCase(target.getSign()) == 'k') {
                            if (whiteToMove) {
                                System.out.println("White is in check!");
                                whiteCheck = true;
                            } else {
                                System.out.println("Black is in check!");
                                blackCheck = true;
                            }
                            if (kingThreats == null) {
                                kingThreats = new ArrayList<MovePin>();
                                kingSquare = squares[y][x];
                            }
                            kingThreats.add(new MovePin(s, new Point(pm.getVector().x, pm.getVector().y)));
                        } else {//if we hit the king, then we have to make sure that the squares behind him will also be marked as attacked - he cant retreat to them or he would be in check
                            hitPieceSquare = squares[y][x];
                        }
                    } else {
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
                } else {
                    attackedSquares[y][x] = true; //so that the enemy king cannot capture a defended piece
                    if ((!p.isCanJump())) {
                        break;
                    }
                }
                if (!p.isMoveRangeUnlimited()) {
                    break;
                }
                x += pm.getVector().x;
                y += pm.getVector().y;
            }
        }
    }

    public void calculateLegalMoves(ChessSquare s) throws IllegalPositionException {
        s.legalMoves = new ArrayList<RealMove>();
        int x, y;
        Piece p = s.getPiece();
        for (PieceMove pm : s.getPiece().getMoves()) {
            x = s.getFile() + pm.getVector().x;
            y = s.getRank() + pm.getVector().y;
            while ( x >= 0 && x < width &&
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
                        } else if (target == null) {
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
                        } else {
                            if (squares[y][x] != kingThreats.get(0).pinnedBy) {
                                neutralized = false;
                            }
                        }
                    }
                    if (target != null && Character.toLowerCase(target.getSign()) == 'k') {
                        throw new IllegalPositionException("The king can be captured!");
                    }
                    if (neutralized) {
                        //s.legalMoves.add(new Point(x, y));
                        if (Character.toLowerCase(p.getSign()) == 'p' && (y == 0 || y == height - 1)) {
                            if (p.isWhite() && y == 0) {
                                for (char c : ChessBoard.pieceArr) {
                                    if (Character.isUpperCase(c)) {
                                        s.legalMoves.add(new RealMove(s, squares[y][x], MoveFlags.RM_PROMOTION, c));
                                        ++numLegalMoves;
                                    }
                                }
                            } else if (!p.isWhite() && y == height - 1) {
                                for (char c : ChessBoard.pieceArr) {
                                    if (Character.isLowerCase(c)) {
                                        s.legalMoves.add(new RealMove(s, squares[y][x], MoveFlags.RM_PROMOTION, c));
                                        ++numLegalMoves;
                                    }
                                }
                            } else {
                                throw new IllegalPositionException("A pawn cannot be moved to this rank!");
                            }
                        } else {
                            if (Character.toLowerCase(p.getSign()) == 'k' && Math.abs(pm.getVector().x) == 2) {
                                s.legalMoves.add(new RealMove(s, squares[y][x],
                                        pm.getVector().x > 0 ? MoveFlags.RM_CASTLE_KINGSIDE : MoveFlags.RM_CASTLE_QUEENSIDE));
                            } else {
                                s.legalMoves.add(new RealMove(s, squares[y][x]));
                            }
                            ++numLegalMoves;
                        }
                    }
                    if ((!p.isCanJump() && target != null) || !p.isMoveRangeUnlimited()) {
                        break;
                    }
                    x += pm.getVector().x;
                    y += pm.getVector().y;
                } else {
                    break;
                }
            }
        }
        //System.out.println(p.getSign() + " piece has " + p.legalMoves.size() + " legal moves");
    }

    public boolean move(RealMove move) {
        move.fromPiece().commitMove(move, this);
        if (move.toPiece() != null) {
            move.toPiece().commitCaptured(move, this);
            move.to().removePiece();
        }
        if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_PROMOTION)) {
            switch (Character.toLowerCase(move.getArg())) {
                case 'b':
                    move.to().piece = new Bishop(move.getArg());
                    break;
                case 'n':
                    move.to().piece = new Knight(move.getArg());
                    break;
                case 'r':
                    move.to().piece = new Rook(move.getArg());
                    break;
                default:
                    move.to().piece = new Queen(move.getArg());
                    break;
            }
        } else {
            move.to().piece = move.fromPiece();
        }
        move.from().removePiece();
        if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_CASTLE_KINGSIDE)) {
            move(new RealMove(squares[move.from().getRank()][rookPositionX[whiteToMove ? 1 : 0][0]],
                    squares[move.from().getRank()][width - 3],
                    MoveFlags.RM_ROOK_CASTLING));
        } else if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_CASTLE_QUEENSIDE)) {
            move(new RealMove(squares[move.from().getRank()][rookPositionX[whiteToMove ? 1 : 0][1]],
                    squares[move.from().getRank()][3],
                    MoveFlags.RM_ROOK_CASTLING));
        }
        if (!MoveFlags.hasFlag(move.flags(), MoveFlags.RM_ROOK_CASTLING)) {
            whiteToMove = !whiteToMove;
            calculateLegalMoves();
        }
        return true;
    }

    public boolean undoMove(RealMove move) {
        if (!move.to().isEmpty()) {
            move.to().removePiece();
        }
        if (!move.from().isEmpty()) {
            move.from().removePiece();
        }
        if (move.fromPiece() != null) {
            move.from().piece = move.fromPiece();
        }
        if (move.toPiece() != null) {
            move.to().piece = move.toPiece();
        }
        if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_WHITE_CASTLE_KINGSIDE_IMPOSSIBLE)) {
            castleFlags |= WHITE_KINGSIDE;
        }
        if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_WHITE_CASTLE_QUEENSIDE_IMPOSSIBLE)) {
            castleFlags |= WHITE_QUEENSIDE;
        }
        if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_BLACK_CASTLE_KINGSIDE_IMPOSSIBLE)) {
            castleFlags |= BLACK_KINGSIDE;
        }
        if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_BLACK_CASTLE_QUEENSIDE_IMPOSSIBLE)) {
            castleFlags |= BLACK_QUEENSIDE;
        }
        //restoring the rook after undoing castling
        if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_CASTLE_KINGSIDE)) {
            if (!whiteToMove) {
                move(new RealMove(squares[move.from().getRank()][width - 3],
                        squares[move.from().getRank()][rookPositionX[1][0]],
                        MoveFlags.RM_ROOK_CASTLING));
            } else {
                move(new RealMove(squares[move.from().getRank()][width - 3],
                        squares[move.from().getRank()][rookPositionX[0][0]],
                        MoveFlags.RM_ROOK_CASTLING));
            }
        } else if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_CASTLE_QUEENSIDE)) {
            if (!whiteToMove) {
                move(new RealMove(squares[move.from().getRank()][3],
                        squares[move.from().getRank()][rookPositionX[1][1]],
                        MoveFlags.RM_ROOK_CASTLING));
            } else {
                move(new RealMove(squares[move.from().getRank()][3],
                        squares[move.from().getRank()][rookPositionX[0][1]],
                        MoveFlags.RM_ROOK_CASTLING));
            }
        }
        whiteToMove = !whiteToMove;
        calculateLegalMoves();
        return true;
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

    public boolean whiteCastleKingSide() {
        return (castleFlags & WHITE_KINGSIDE) == WHITE_KINGSIDE;
    }

    public boolean blackCastleKingSide() {
        return (castleFlags & BLACK_KINGSIDE) == BLACK_KINGSIDE;
    }

    public boolean whiteCastleQueenSide() {
        return (castleFlags & WHITE_QUEENSIDE) == WHITE_QUEENSIDE;
    }

    public boolean blackCastleQueenSide() {
        return (castleFlags & BLACK_QUEENSIDE) == BLACK_QUEENSIDE;
    }

    public boolean canCastleKingSide() {
        if (whiteToMove) {
            return whiteCastleKingSide();
        } else {
            return blackCastleKingSide();
        }
    }

    public boolean canCastleQueenSide() {
        if (whiteToMove) {
            return whiteCastleQueenSide();
        } else {
            return blackCastleQueenSide();
        }
    }

    public void setCastleFlags(int flags){
        castleFlags &= ~ (flags);
    }
}

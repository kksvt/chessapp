package chess.core;

import chess.pieces.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChessPosition {
    StringBuilder fen;
    public final static String defaultPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public final static String emptyPosition= "8/8/8/8/8/8/8/8 w - -";

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

    private int enPassant[];
    private boolean enPassantPinned;
    private int halfMove;
    private int fullMove; //its actually 2 * fullMove

    private List<RealMove> allLegalMoves;

    public ChessPosition(int width, int height, String position) {
        this.width = width;
        this.height = height;
        attackedSquares = new boolean[height][width];
        threatenedSquares = new boolean[height][width];
        rookPositionX = new int[2][2];
        enPassant = new int[2];
        squares = new ChessSquare[height][width];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                squares[i][j] = new ChessSquare(null, j, i);
            }
        }
        if (!parsePosition(position)) {
            System.out.println("Invalid FEN, applying default starting position...");
            parsePosition(defaultPosition);
        }
    }

    public ChessPosition(String position){
        this(8,8,position);
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

    public StringBuilder getFen() {
        return fen;
    }

    public Piece getPiece(int file, int rank) {
        return squares[rank][file].getPiece();
    }

    public StringBuilder savePosition() {
        fen = new StringBuilder();
        int emptyStreak = 0;
        //piece placement
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                ChessSquare s = squares[i][j];
                if (s.isEmpty()) {
                    ++emptyStreak;
                }
                else {
                    if (emptyStreak > 0) {
                        fen.append(emptyStreak);
                        emptyStreak = 0;
                    }
                    fen.append(s.getPiece().getSign());
                }
            }
            if (emptyStreak > 0) {
                fen.append(emptyStreak);
                emptyStreak = 0;
            }
            if (i < height - 1) {
                fen.append('/');
            }
            else {
                fen.append(' ');
            }
        }
        //turn
        if (isWhiteToMove()) {
            fen.append("w ");
        }
        else {
            fen.append("b ");
        }
        if (castleFlags == 0) {
            fen.append("- ");
        }
        else {
            if (whiteCastleKingSide()) {
                fen.append('K');
            }
            if (whiteCastleQueenSide()) {
                fen.append('Q');
            }
            if (blackCastleKingSide()) {
                fen.append('k');
            }
            if (blackCastleQueenSide()) {
                fen.append('q');
            }
            fen.append(' ');
        }
        //en passant
        if (enPassant[0] == -1 || enPassant[1] == -1) { //can't use isEnPassantAvailable() cuz it factors for pins
            fen.append("- ");
        }
        else {
            fen.append(Character.toString(enPassant[0] + 'a') + Character.toString(height - enPassant[1] + '0') + " ");
        }
        fen.append(halfMove + " " + getFullMove());
        return fen;
    }

    public boolean parsePosition(String position) {
        whiteToMove = true;
        fullMove = 0;
        halfMove = 2;
        enPassant[0] = enPassant[1] = -1;
        castleFlags = 0;
        rookPositionX[0][0] = rookPositionX[0][1] = rookPositionX[1][0] = rookPositionX[1][1] = -1;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                squares[i][j].removePiece();
            }
        }
        if (position == null || position.length() == 0) {
            return false;
        }
        boolean placedWhiteKing = false, placedBlackKing = false;
        int whiteRooks = 0, blackRooks = 0;
        for (int i = 0, rank = 0, file = 0; i < position.length(); ++i) {
            char c = position.charAt(i);
            if (Character.isSpaceChar(c)) {
                continue;
            } else if (rank == height - 1 && file == width) {
                //b KQkq e3 0 1
                //w KQkq - 0 1
                //w - - 0 1
                //move turn
                switch (c) {
                    case 'w':
                        whiteToMove = true;
                        //System.out.println("White to move");
                        break;
                    case 'b':
                        whiteToMove = false;
                        //System.out.println("Black to move");
                        break;
                    default:
                        return false;
                }
                ++i;
                while (i < position.length() && position.charAt(i) == ' ') { ++i; }
                if (i == position.length()) {
                    return false;
                }
                //castling
                if (position.charAt(i) == '-') {
                    ++i;
                    while (i < position.length() && position.charAt(i) == ' ') { ++i; }
                    if (i == position.length()) {
                        return false;
                    }
                }
                else {
                    while (i < position.length()) {
                        switch (position.charAt(i)) {
                            case 'K':
                                if (rookPositionX[1][1] != -1) { //fixme: these may not be sufficient, store the rook's rank as well?
                                    castleFlags |= WHITE_KINGSIDE;
                                    //System.out.println("parsePosition: White can castle kingside");
                                }
                                break;
                            case 'Q':
                                if (rookPositionX[1][0] != -1) {
                                    castleFlags |= WHITE_QUEENSIDE;
                                    //System.out.println("parsePosition: White can castle queenside");
                                }
                                break;
                            case 'k':
                                if (rookPositionX[0][1] != -1) {
                                    castleFlags |= BLACK_KINGSIDE;
                                    //System.out.println("parsePosition: Black can castle kingside");
                                }
                                break;
                            case 'q':
                                if (rookPositionX[0][0] != -1) {
                                    castleFlags |= BLACK_QUEENSIDE;
                                    //System.out.println("parsePosition: Black can castle kingside");
                                }
                                break;
                            case ' ':
                                break;
                            default:
                                return false;
                        }
                        if (position.charAt(i) == ' ') {
                            break;
                        }
                        ++i;
                    }
                }
                while (i < position.length() && position.charAt(i) == ' ') { ++i; }
                if (i == position.length()) {
                    return false;
                }
                //en passant
                if (position.charAt(i) == '-') {
                    //System.out.println("No en passant");
                    ++i;
                    while (i < position.length() && position.charAt(i) == ' ') { ++i; }
                    if (i == position.length()) {
                        halfMove = 0;
                        fullMove = 2;
                        break;
                    }
                }
                else {
                    if (i + 1 >= position.length()) {
                        return false;
                    }
                    int enPassantFile = position.charAt(i) - 'a', enPassantRank = height - position.charAt(i + 1) + '0';
                    if (enPassantFile < 0 || enPassantFile >= width || enPassantRank < 0 || enPassantRank >= height) {
                        return false;
                    }
                    //System.out.println("En passant is at " + position.charAt(i) + position.charAt(i + 1));
                    setEnPassant(enPassantFile, enPassantRank);
                    i += 2;
                }
                while (i < position.length() && position.charAt(i) == ' ') { ++i; }
                if (i == position.length()) {
                    halfMove = 0;
                    fullMove = 2;
                    break;
                }
                //halfmove
                halfMove = 0;
                while (i < position.length() && Character.isDigit(position.charAt(i))) {
                    halfMove *= 10;
                    halfMove = position.charAt(i) - '0';
                    ++i;
                }
                while (i < position.length() && position.charAt(i) == ' ') { ++i; }
                if (i == position.length()) {
                    fullMove = 2;
                    break;
                }
                //fullmove
                fullMove = 0;
                while (i < position.length() && Character.isDigit(position.charAt(i))) {
                    fullMove *= 10;
                    fullMove = position.charAt(i) - '0';
                    ++i;
                }
                fullMove *= 2;
                break;

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
                        /*while (--skip >= 0) {
                            if (file == width) {
                                return false;
                            }
                            squares[rank][file] = new ChessSquare(null, file, rank);
                            ++file;
                        }*/
                        file += skip;
                        if (file > width) {
                            return false;
                        }
                    } else if (Piece.isValidPiece(c)) {
                        if (file >= width || rank >= height) {
                            return false;
                        }
                        Piece piece;
                        boolean isWhitePiece = Character.isUpperCase(c);
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
                                if (rank == height - 1 && isWhitePiece && whiteRooks < 2) {
                                    rookPositionX[1][placedWhiteKing ? 0 : 1] = file;
                                    piece = new Rook(c, placedWhiteKing, !placedWhiteKing);
                                    ++whiteRooks;
                                } else if (rank == 0 && !isWhitePiece && blackRooks < 2) {
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
                        //squares[rank][file] = new ChessSquare(piece, file, rank);
                        squares[rank][file].addPiece(piece);
                        ++file;
                    } else {
                        return false;
                    }
                }
            }
        }
        calculateLegalMoves();
        fen = new StringBuilder(position);
        return true;
    }

    public int calculateLegalMoves() throws IllegalPositionException {
        numLegalMoves = 0;
        whiteCheck = blackCheck = false;
        kingThreats = null;
        kingSquare = null;
        enPassantPinned = false;
        allLegalMoves = new ArrayList<RealMove>();
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
        //System.out.println("There are " + numLegalMoves + " legal moves in this position");
        //System.out.println("halfMove: " + halfMove + " fullMove: " + getFullMove());
        return numLegalMoves;
    }

    private boolean pawnsMatchingEnPassant(ChessSquare s1, ChessSquare s2) {
        ChessSquare whiteSquare = s1.getPiece().isWhite() ? s1 : s2,
                blackSquare = !s1.getPiece().isWhite() ? s1 : s2;
        Piece whitePawn = whiteSquare.getPiece(),
                blackPawn = blackSquare.getPiece();
        if (whitePawn.getSign() != 'P' || blackPawn.getSign() != 'p') {
            return false;
        }
        if (s1.getRank() != s2.getRank() || Math.abs(s1.getFile() - s2.getFile()) != 1) {
            return false;
        }
        if ((whiteToMove && blackSquare.getRank() - 1 != getEnPassant()[1]) ||
                (!whiteToMove && whiteSquare.getRank() + 1 != getEnPassant()[1])) {
            return false;
        }
        return true;
    }

    private void calculateThreats(ChessSquare s) {
        int x, y;
        Piece p = s.getPiece();
        for (PieceMove pm : s.getPiece().getMoves()) {
            if (!pm.isCanCapture() && !pm.isMustCapture()) {
                continue;
            }
            x = s.getFile() + pm.getVector().x;
            y = s.getRank() + pm.getVector().y;
            ChessSquare hitPieceSquare = null;
            boolean searchForKing = false, searchForPawn = false;
            while (x >= 0 && x < width &&
                    y >= 0 && y < height) {
                Piece target = getPiece(x, y);
                boolean blockedByPieces = false;
                //EN PASSANT EXCEPTION BEGIN
                if (searchForKing) {
                    if (target != null) {
                        if ((target.getSign() == 'K' && whiteToMove) ||
                                (target.getSign() == 'k' && !whiteToMove)) {
                            enPassantPinned = true;
                        }
                        break;
                    }
                }
                else if (searchForPawn) {
                    if (target != null) {
                        if (pawnsMatchingEnPassant(hitPieceSquare, squares[y][x])) {
                            searchForKing = true;
                        }
                        else { break; }
                    }
                }
                //EN PASSANT EXCEPTION END
                else {
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
                                    //System.out.println("White is in check!");
                                    whiteCheck = true;
                                } else {
                                    //System.out.println("Black is in check!");
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
                                blockedByPieces = true;
                            }
                        }
                    } else {
                        if (hitPieceSquare == null || p.isCanJump()) {
                            attackedSquares[y][x] = true; //so that the enemy king cannot capture a defended piece
                        }
                        if ((!p.isCanJump())) {
                            blockedByPieces = true;
                        }
                    }
                }
                if (blockedByPieces) {
                    //EN PASSANT PIN EXCEPTION BEGIN
                    if (isEnPassantAvailable() && Character.toLowerCase(target.getSign()) == 'p') {
                        if (hitPieceSquare == null) {
                            hitPieceSquare = squares[y][x];
                            searchForPawn = true;
                        }
                        else {
                            if (pawnsMatchingEnPassant(hitPieceSquare, squares[y][x])) {
                                searchForKing = true;
                            }
                            else { break; }
                        }
                    }
                    //EN PASSANT EXCEPTION END
                    else break;
                }
                if (!p.isMoveRangeUnlimited()) {
                    break;
                }
                x += pm.getVector().x;
                y += pm.getVector().y;
            }
        }
    }

    private void calculateLegalMoves(ChessSquare s) throws IllegalPositionException {
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
                    if (Character.toLowerCase(p.getSign()) != 'k' && kingInCheck()) {
                        //System.out.println("King is in check, there are " + kingThreats.size() + " threats");
                        if (kingThreats.size() > 1) {
                            neutralized = false;
                        } else if (target == null) {
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
                            if (neutralized == false && isEnPassantAvailable() &&
                                    MoveFlags.hasFlag(pm.flags(), MoveFlags.RM_ENPASSANT) &&
                                    pawnsMatchingEnPassant(s, kingThreats.get(0).pinnedBy)) {
                                neutralized = true;
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
                                    if (Character.isUpperCase(c) && Piece.canPromoteTo(c)) {
                                        s.legalMoves.add(new RealMove(s, squares[y][x], MoveFlags.RM_PROMOTION | pm.flags(), c, halfMove));
                                        allLegalMoves.add(s.legalMoves.get(s.legalMoves.size() - 1));
                                        ++numLegalMoves;
                                    }
                                }
                            } else if (!p.isWhite() && y == height - 1) {
                                for (char c : ChessBoard.pieceArr) {
                                    if (Character.isLowerCase(c) && Piece.canPromoteTo(c)) {
                                        s.legalMoves.add(new RealMove(s, squares[y][x], MoveFlags.RM_PROMOTION | pm.flags(), c, halfMove));
                                        allLegalMoves.add(s.legalMoves.get(s.legalMoves.size() - 1));
                                        ++numLegalMoves;
                                    }
                                }
                            } else {
                                throw new IllegalPositionException("A pawn cannot be moved to this rank!");
                            }
                        } else {
                            /*if (Character.toLowerCase(p.getSign()) == 'k' && Math.abs(pm.getVector().x) == 2) {
                                s.legalMoves.add(new RealMove(s, squares[y][x],
                                        pm.getVector().x > 0 ? MoveFlags.RM_CASTLE_KINGSIDE : MoveFlags.RM_CASTLE_QUEENSIDE));
                            } else {
                                s.legalMoves.add(new RealMove(s, squares[y][x]));
                            }*/
                            s.legalMoves.add(new RealMove(s, squares[y][x], pm.flags(), halfMove));
                            allLegalMoves.add(s.legalMoves.get(s.legalMoves.size() - 1));
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
        return move(move, true);
    }

    public boolean move(RealMove move, boolean generateAlgebraicMove) {
        if (getEnPassant()[0] != -1 && getEnPassant()[1] != -1) { //has to be above !hasFlags TWOSQRPAWN in case of consecutive two square pawn moves
            move.setFlagsBitwise(MoveFlags.RM_ENPASSANT_IMPOSSIBLE);
            move.setEnPassant(enPassant);
        }
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
                    MoveFlags.RM_ROOK_CASTLING, -1));
        } else if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_CASTLE_QUEENSIDE)) {
            move(new RealMove(squares[move.from().getRank()][rookPositionX[whiteToMove ? 1 : 0][1]],
                    squares[move.from().getRank()][3],
                    MoveFlags.RM_ROOK_CASTLING, -1));
        }
        if (!MoveFlags.hasFlag(move.flags(), MoveFlags.RM_TWOSQRPAWN)) {
            setEnPassant(-1, -1);
        }
        if (!MoveFlags.hasFlag(move.flags(), MoveFlags.RM_ROOK_CASTLING)) {
            ++fullMove;
            whiteToMove = !whiteToMove;
            //fixme: is there a less stupid way of implementing this?
            if (generateAlgebraicMove) {
                move.generateAlgebraicMove(this);
                calculateLegalMoves();
                move.algAppendChecks(this);
                //System.out.println("Algebraic move: " + move.getAlgebraicMove());
            }
            else {
                calculateLegalMoves();
            }
        }
        return true;
    }

    public boolean undoMove(RealMove move) {
        int oldHalfMove = move.getHalfMove();
        if (oldHalfMove != -1) { halfMove = oldHalfMove; }
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
                        MoveFlags.RM_ROOK_CASTLING, -1));
            } else {
                move(new RealMove(squares[move.from().getRank()][width - 3],
                        squares[move.from().getRank()][rookPositionX[0][0]],
                        MoveFlags.RM_ROOK_CASTLING, -1));
            }
        } else if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_CASTLE_QUEENSIDE)) {
            if (!whiteToMove) {
                move(new RealMove(squares[move.from().getRank()][3],
                        squares[move.from().getRank()][rookPositionX[1][1]],
                        MoveFlags.RM_ROOK_CASTLING, -1));
            } else {
                move(new RealMove(squares[move.from().getRank()][3],
                        squares[move.from().getRank()][rookPositionX[0][1]],
                        MoveFlags.RM_ROOK_CASTLING, -1));
            }
        }
        if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_ENPASSANT_IMPOSSIBLE)) {
            setEnPassant(move.getEnPassant());
            if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_ENPASSANT)) {
                ChessSquare sqr = getSquare(move.getEnPassant()[1] + (whiteToMove ? -1 : 1), move.getEnPassant()[0]);
                sqr.addPiece(new Pawn(whiteToMove ? 'P' : 'p'));
            }
        } else {
            setEnPassant(-1, -1);
        }
        whiteToMove = !whiteToMove;
        calculateLegalMoves();
        --fullMove;
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

    public int[] getEnPassant() { return enPassant; }

    public void setEnPassant(int file, int rank) {
        this.enPassant[0] = file;
        this.enPassant[1] = rank;
    }

    public void setEnPassant(int enPassant[]) {
        this.enPassant[0] = enPassant[0];
        this.enPassant[1] = enPassant[1];
    }

    public boolean isEnPassantAvailable() { return !enPassantPinned && enPassant[0] != -1; }

    public int getWhiteKingsideRookFile() {
        return rookPositionX[1][1];
    }

    public int getWhiteQueensideRookFile() {
        return rookPositionX[1][0];
    }

    public int getBlackKingsideRookFile() {
        return rookPositionX[0][1];
    }

    public int getBlackQueensideRookFile() {
        return rookPositionX[0][0];
    }

    public int getFullMove() { return fullMove / 2; }

    public void resetHalfMove() { halfMove = 0; }

    public int incrementHalfMove() { return ++halfMove; }

    public int getHalfMove() { return halfMove; }

    public List<RealMove> getAllMoves() { return allLegalMoves; }

    public static String stripFenOfMoves(String fen) {
        return fen.split(" ")[0] + fen.split(" ")[1] + fen.split(" ")[2]
                + fen.split(" ")[3];
    }
}

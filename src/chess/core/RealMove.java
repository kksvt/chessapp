package chess.core;

import chess.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

//a move that happened on the board
public class RealMove {
    private final ChessSquare from;
    private ChessSquare to;

    private final Piece fromPiece;
    private final Piece toPiece;
    private int flags;
    private char arg;
    private int enPassant[]; //fixme: is there a better way?
    private int halfMove;

    private StringBuilder algMove;

    public RealMove(ChessSquare from, ChessSquare to, int flags, char arg, int halfMove) {
        this.from = from;
        this.to = to;
        this.fromPiece = from.getPiece();
        this.toPiece = to.getPiece();
        this.flags = flags;
        this.arg = arg;
        this.halfMove = halfMove;
    }

    public RealMove(ChessSquare from, ChessSquare to, int flags, int halfMove) {
        this(from, to, flags, '\0', halfMove);
    }

    public RealMove(ChessSquare from, ChessSquare to, int halfMove) {
        this(from, to, 0, '\0', halfMove);
    }

    public int getFileDestination() {
        return to.getFile();
    }

    public int getRankDestination() {
        return to.getRank();
    }

    public int getFileFrom() {
        return from.getFile();
    }

    public int getRankFrom() {
        return from.getRank();
    }

    public void setEnPassant(int file, int rank) {
        this.enPassant = new int[2];
        this.enPassant[0] = file;
        this.enPassant[1] = rank;
    }

    public void setEnPassant(int enPassant[]) {
        this.enPassant = new int[2];
        this.enPassant[0] = enPassant[0];
        this.enPassant[1] = enPassant[1];
    }

    public int[] getEnPassant() {
        return enPassant;
    }

//  ==============================================  GETTERS AND SETTERS ================================================

    public Piece fromPiece() {
        return fromPiece;
    }

    public Piece toPiece() {
        return toPiece;
    }

    public int flags() {
        return flags;
    }

    public void setFlagsBitwise(int val) {
        flags |= val;
    }

    public char getArg() {
        return arg;
    }

    public void setArg(char c){
        arg = c;
    }

    public void moveTo(ChessSquare sq){
        to = sq;
    }

    public ChessSquare from(){
        return from;
    }

    public ChessSquare to(){
        return to;
    }

    public int getHalfMove() { return halfMove; }

    private String signForPiece(char piece) {
        char p = Character.toUpperCase(piece);
        if (p == 'P') {
            return "";
        }
        return Character.toString(p);
    }

    public StringBuilder generateAlgebraicMove(ChessPosition chessPosition) {
        algMove = new StringBuilder();
        if (MoveFlags.hasFlag(flags, MoveFlags.RM_ROOK_CASTLING)) {
            return algMove;
        }
        if (MoveFlags.hasFlag(flags, MoveFlags.RM_CASTLE_KINGSIDE)) {
            algMove.append("0-0");
        }
        else if (MoveFlags.hasFlag(flags, MoveFlags.RM_CASTLE_QUEENSIDE)) {
            algMove.append("0-0-0");
        }
        else {
            char piece = fromPiece.getSign();
            List<RealMove> ambiguous = new ArrayList<RealMove>();
            if (Character.toLowerCase(piece) != 'p') {
                for (RealMove move : chessPosition.getAllMoves()) {
                    if (move == this) {
                        continue;
                    }
                    if (to() == move.to() && piece == move.fromPiece().getSign()) {
                        //System.out.println("adding ambiguous move");
                        ambiguous.add(move);
                    }
                }
            }
            switch (ambiguous.size()) {
                case 0:
                    if ((toPiece() != null || MoveFlags.hasFlag(flags, MoveFlags.RM_ENPASSANT)) &&
                            Character.toLowerCase(piece) == 'p') {
                        algMove.append(Character.toString(from.getFile() + 'a'));
                    }
                    else {
                        algMove.append(signForPiece(piece));
                    }
                    break;
                case 1:
                    if (ambiguous.get(0).from().getFile() == from.getFile()) {
                        algMove.append(signForPiece(piece) + (chessPosition.height() - from.getRank()));
                    } else {
                        algMove.append(signForPiece(piece) + Character.toString(from.getFile() + 'a'));
                    }
                    break;
                default:
                    algMove.append(signForPiece(piece) + Character.toString(from.getFile() + 'a') + (chessPosition.height() - from.getRank()));
                    break;
            }
            if (toPiece() != null || MoveFlags.hasFlag(flags, MoveFlags.RM_ENPASSANT)) {
                algMove.append('x');
            }
            algMove.append(Character.toString(to.getFile() + 'a') + (chessPosition.height() - to.getRank()));
            if (MoveFlags.hasFlag(flags, MoveFlags.RM_PROMOTION)) {
                algMove.append("=" + Character.toUpperCase(getArg()));
            }
        }
        return algMove;
    }

    public StringBuilder algAppendChecks(ChessPosition chessPosition) {
        if (chessPosition.kingInCheck()) {
            flags |= MoveFlags.RM_CHECK;
            if (chessPosition.getNumLegalMoves() == 0) {
                flags |= MoveFlags.RM_CHECKMATE;
            }
        }
        if (MoveFlags.hasFlag(flags, MoveFlags.RM_CHECKMATE)) {
            algMove.append('#');
        }
        else if (MoveFlags.hasFlag(flags, MoveFlags.RM_CHECK)) {
            algMove.append('+');
        }
        return algMove;
    }

    public String getAlgebraicMove() {
        return algMove != null ? algMove.toString() : "";
    }

    public String getEngineMove(int height) {
        //d2d4q
        String move = new String(
                Character.toString(from.getFile() + 'a') +
                Character.toString(height - from.getRank() + '0') +
                Character.toString(to.getFile() + 'a') +
                Character.toString(height - to.getRank() + '0') +
                (MoveFlags.hasFlag(flags, MoveFlags.RM_PROMOTION) ? Character.toLowerCase(arg) : "")
        );
        return move;
    }

    public static RealMove getMoveForEngine(ChessPosition chessPosition, String engineMove) {
        int fromFile = engineMove.charAt(0) - 'a', fromRank = chessPosition.height() - engineMove.charAt(1) + '0';
        if (fromFile < 0 || fromFile >= chessPosition.width() || fromRank < 0 || fromRank >= chessPosition.height()) {
            return null;
        }
        int toFile = engineMove.charAt(2) - 'a', toRank = chessPosition.height() - engineMove.charAt(3) + '0';
        if (toFile < 0 || toFile >= chessPosition.width() || toRank < 0 || toRank >= chessPosition.height()) {
            return null;
        }
        char promArg = engineMove.length() > 4 ? engineMove.charAt(4) : '\0';
        ChessSquare moveOrigin = chessPosition.getSquare(fromRank, fromFile), moveDest = chessPosition.getSquare(toRank, toFile);
        for (RealMove m : moveOrigin.legalMoves) {
            boolean hasProm = MoveFlags.hasFlag(m.flags(), MoveFlags.RM_PROMOTION);
            if (m.to() == moveDest &&
                    ((promArg == '\0' && !hasProm) || (promArg != '\0' && hasProm && promArg == Character.toLowerCase(m.getArg())))) {
                return m;
            }
        }
        return null;
    }

    public static RealMove algToRealMove(ChessPosition chessPosition, String algebraicMove) {
        //we assume that the algebraic notation move is correct
        if (algebraicMove.contains("0-0-0") || algebraicMove.contains("O-O-O")) {
            for (RealMove m : chessPosition.getAllMoves()) {
                if (MoveFlags.hasFlag(m.flags(), MoveFlags.RM_CASTLE_QUEENSIDE)) {
                    return m;
                }
            }
            return null;
        }
        if (algebraicMove.contains("0-0") || algebraicMove.contains("O-O")) {
            for (RealMove m : chessPosition.getAllMoves()) {
                if (MoveFlags.hasFlag(m.flags(), MoveFlags.RM_CASTLE_KINGSIDE)) {
                    return m;
                }
            }
            return null;
        }
        Character piece = 'P';
        int rankFrom = -1, rankTo = -1, fileFrom = -1, fileTo = -1, i = 0;
        char promotion = '\0';
        if (Character.isUpperCase(algebraicMove.charAt(0)) && Piece.isValidPiece(algebraicMove.charAt(0))) {
            piece = algebraicMove.charAt(0);
            ++i;
            int captureIndex = algebraicMove.indexOf('x');
            if (captureIndex != -1) {
                fileTo = algebraicMove.charAt(captureIndex + 1) - 'a';
                rankTo = chessPosition.height() - algebraicMove.charAt(captureIndex + 2) + '0';
                if (i != captureIndex) {
                    if (Character.isLetter(algebraicMove.charAt(i))) {
                        if (Character.isDigit(algebraicMove.charAt(i + 1))) {
                            rankFrom = chessPosition.height() - algebraicMove.charAt(i + 1) + '0';
                        }
                        fileFrom = algebraicMove.charAt(i) - 'a';
                    }
                }
            }
            else {
                int checkIndex = algebraicMove.indexOf('+');
                if (checkIndex == -1) {
                    checkIndex = algebraicMove.indexOf('#');
                }
                int realLen = algebraicMove.length() - Math.min(1, Math.max(checkIndex, 0));
                switch (realLen) {
                    case 3:
                        fileTo = algebraicMove.charAt(i) - 'a';
                        rankTo = chessPosition.height() - algebraicMove.charAt(i + 1) + '0';
                        break;
                    case 4:
                        if (Character.isLetter(algebraicMove.charAt(i))) {
                            fileFrom = algebraicMove.charAt(i) - 'a';
                        }
                        else {
                            rankFrom = chessPosition.height() - algebraicMove.charAt(i) + '0';
                        }
                        fileTo = algebraicMove.charAt(i + 1) - 'a';
                        rankTo = chessPosition.height() - algebraicMove.charAt(i + 2) + '0';
                        break;
                    case 5:
                        fileFrom = algebraicMove.charAt(i) - 'a';
                        rankFrom = chessPosition.height() - algebraicMove.charAt(i + 1) + '0';
                        fileTo = algebraicMove.charAt(i + 2) - 'a';
                        rankTo = chessPosition.height() - algebraicMove.charAt(i + 3) + '0';
                        break;
                    default:
                        System.out.println("algToRealMove: invalid length");
                        break;
                }
            }
        }
        else {
            fileFrom = algebraicMove.charAt(0) - 'a';
            if (algebraicMove.charAt(1) == 'x') {
                fileTo = algebraicMove.charAt(2) - 'a';
                rankTo = chessPosition.height() - algebraicMove.charAt(3) + '0';
                i += 4;
            }
            else {
                fileTo = fileFrom;
                rankTo = chessPosition.height() - algebraicMove.charAt(1) + '0';
                i += 2;
            }
            if (i < algebraicMove.length()) {
                if (algebraicMove.charAt(i) == '=') {
                    promotion = chessPosition.isWhiteToMove() ?
                            algebraicMove.charAt(i + 1)
                            :
                            Character.toLowerCase(algebraicMove.charAt(i + 1));
                }
            }
        }
        if (fileTo == -1 && rankTo == -1) {
            return null;
        }
        //System.out.println("fileFrom " + fileFrom + ", rankFrom " + rankFrom);
        //System.out.println("fileTo " + fileTo + ", rankTo " + rankTo);
        List<ChessSquare> startSquare = new ArrayList<ChessSquare>();
        piece = chessPosition.isWhiteToMove() ? piece : Character.toLowerCase(piece);
        if (fileFrom != -1 && rankFrom != -1) {
            startSquare.add(chessPosition.getSquare(rankFrom, fileFrom));
            if (fileTo != -1 && rankTo != -1) {
                ChessSquare endSquare = chessPosition.getSquare(rankTo, fileTo);
                for (RealMove m : startSquare.get(0).legalMoves) {
                    if (m.to() == endSquare) {
                        return m;
                    }
                }
            }
        }
        else {
            for (ChessSquare sqr[] : chessPosition.getSquares()) {
                for (ChessSquare s : sqr) {
                    if (s.isEmpty()) {
                        continue;
                    }
                    if ((fileFrom != -1 && fileFrom != s.getFile()) ||
                            (rankFrom != -1 && rankFrom != s.getRank()))  {
                        continue;
                    }
                    if (s.getPiece().getSign() == piece) {
                        startSquare.add(s);
                    }
                }
            }
        }
        if (startSquare.size() > 0) {
            /*System.out.println("startSquares found!: ");
            for (ChessSquare s : startSquare) {
                System.out.println("Rank: " + s.getRank() + ", File: " + s.getFile());
            }*/
            for (ChessSquare s : startSquare) {
                for (RealMove m : s.legalMoves) {
                    if (fileTo != -1 && fileTo != m.getFileDestination()) {
                        continue;
                    }
                    if (rankTo != -1 && rankTo != m.getRankDestination()) {
                        continue;
                    }
                    if (promotion != '\0' && (!MoveFlags.hasFlag(m.flags(), MoveFlags.RM_PROMOTION) || m.getArg() != promotion)) {
                        continue;
                    }
                    return m;
                }
            }
        }
        return null;
    }
}

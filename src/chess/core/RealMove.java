package chess.core;

import chess.pieces.Piece;

//a move that happened on the board
public class RealMove {
    private final ChessSquare from;
    private ChessSquare to;

    private final Piece fromPiece;
    private final Piece toPiece;
    private int flags;
    private char arg;
    private int enPassant[]; //fixme: is there a better way?

    public RealMove(ChessSquare from, ChessSquare to, int flags, char arg) {
        this.from = from;
        this.to = to;
        this.fromPiece = from.getPiece();
        this.toPiece = to.getPiece();
        this.flags = flags;
        this.arg = arg;
    }

    public RealMove(ChessSquare from, ChessSquare to, int flags) {
        this(from, to, flags, '\0');
    }

    public RealMove(ChessSquare from, ChessSquare to) {
        this(from, to, 0, '\0');
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

    public String getHumanMove() {
        return ""; //todo
    }

}

package chess.pieces;

import chess.core.ChessPosition;
import chess.core.PieceMove;
import chess.core.RealMove;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece  {

    final static int QUEEN_VALUE = 900;
    final static int ROOK_VALUE = 500;
    final static int BISHOP_VALUE = 300;
    final static int KNIGHT_VALUE = 300;
    final static int PAWN_VALUE = 100;

    private char sign;
    private boolean canJump;
    private boolean moveRangeUnlimited;
    private int value;
    protected List<PieceMove> moves; // TODO: decide privacy level for this var

    public Piece(char sign) {
        this.sign = sign;
        this.canJump = getCanJump(sign);
        this.moveRangeUnlimited = getMoveRange(sign);
        this.value = getPieceValue(sign);
        this.moves = new ArrayList<>();
        setMoves();
    }

    abstract void setMoves();

    public static int getPieceValue(char piece){
        if(!isValidPiece(piece))
            return -1; // TODO: choose correct exception
        switch (Character.toLowerCase(piece)) {
            case 'k':
                return Integer.MAX_VALUE;
            case 'q':
                return QUEEN_VALUE;
            case 'r':
                return ROOK_VALUE;
            case 'b':
                return BISHOP_VALUE;
            case 'n':
                return KNIGHT_VALUE;
            case 'p':
                return PAWN_VALUE;
            default:
                return 0;
        }

    }

    public static boolean getCanJump(char piece){
        return isValidPiece(piece) && Character.toLowerCase(piece) == 'n';
    }

    public static boolean getMoveRange(char piece){
        if (isValidPiece(piece))
            switch (Character.toLowerCase(piece)){
                case 'b':
                case 'r':
                case 'q':
                    return true;
                default:return false;
            }
        return false;
    }

    public static boolean isValidPiece(char piece) {
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


    public boolean canMove(int file, int rank, PieceMove m, ChessPosition position) {
        return true;
    }

    public boolean isWhite() { return Character.isUpperCase(sign); }

    public void commitMove(RealMove m, ChessPosition position) {}

    public void commitCaptured(RealMove m, ChessPosition position) {}

//  ==============================================  GETTERS AND SETTERS ================================================

    public void setSign(char sign) {
        this.sign = sign;
    }

    public char getSign() {
        return sign;
    }

    public void setCanJump(boolean canJump) {
        this.canJump = canJump;
    }

    public void setMoveRangeUnlimited(boolean moveRangeUnlimited) {
        this.moveRangeUnlimited = moveRangeUnlimited;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public List<PieceMove> getMoves() {
        return moves;
    }

    public boolean isCanJump() { return canJump; }

    public boolean isMoveRangeUnlimited() { return moveRangeUnlimited; }
}
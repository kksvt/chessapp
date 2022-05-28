package chess.core;

import java.awt.*;

public class PieceMove {
    Point v;
    boolean canCapture;  //czy tym ruchem mozna zbic - wyjatek dla pionka
    boolean mustCapture; //czy tym ruchem trzeba zbic - rowniez wyjatek dla pionka

    public PieceMove(int x, int y, boolean canCapture, boolean mustCapture) {
        this.v = new Point(x, y);
        this.canCapture = canCapture;
        this.mustCapture = mustCapture;
    }



    public Point getVector() { return v; }
    public boolean isCanCapture() { return canCapture; }
    public boolean isMustCapture() { return mustCapture; }
    public boolean canMove(ChessPosition position, ChessSquare sqr) {//List<MovePin> pins, int file, int rank) {
        if (sqr.pins != null) {
            for (MovePin p : sqr.pins) {
                if ((p.attackVector.x != v.x || p.attackVector.y != v.y) &&
                        (p.attackVector.x != -v.x || p.attackVector.y != -v.y)) {
                    //System.out.println("There's a pin by " + p.pinnedBy.getPiece().getSign());
                    return false;
                }
            }
        }
        return sqr.piece.canMove(sqr.getFile(), sqr.getRank(),this, position);
    }
}
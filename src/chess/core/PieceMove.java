package chess.core;

import java.awt.*;

public class PieceMove {
    private Point v;
    private boolean canCapture;  //czy tym ruchem mozna zbic - wyjatek dla pionka
    private boolean mustCapture; //czy tym ruchem trzeba zbic - rowniez wyjatek dla pionka
    private int flags;

    public PieceMove(int x, int y, boolean canCapture, boolean mustCapture) {
        this(x, y, canCapture, mustCapture, 0);
    }
    public PieceMove(int x, int y, boolean canCapture, boolean mustCapture, int flags) {
        assert(x != 0 || y != 0);
        this.v = new Point(x, y);
        this.canCapture = canCapture;
        this.mustCapture = mustCapture;
        this.flags = flags;
    }



    public Point getVector() { return v; }
    public boolean isCanCapture() { return canCapture; }
    public boolean isMustCapture() { return mustCapture; }
    public int flags() { return flags; }
    public boolean canMove(ChessPosition position, ChessSquare sqr) {//List<MovePin> pins, int file, int rank) {
        if (sqr.pins != null) {
            for (MovePin p : sqr.pins) {
                int gcd = gcd(v.x, v.y);
                int vx = v.x / gcd, vy = v.y / gcd;
                if ((p.attackVector.x != vx || p.attackVector.y != vy) &&
                        (p.attackVector.x != -vx || p.attackVector.y != -vy)) {
                    //System.out.println("There's a pin by " + p.pinnedBy.getPiece().getSign());
                    return false;
                }
            }
        }
        return sqr.piece.canMove(sqr.getFile(), sqr.getRank(),this, position);
    }
    private int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        if (a == 0) {
            return b;
        }
        return gcd(b, a % b);
    }
}
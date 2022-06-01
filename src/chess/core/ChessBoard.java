package chess.core;

import chess.Player;
import chess.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

class ChessVisualSquare extends JPanel {
    ChessSquare link;
    Color color;
    boolean legalDestination;

    //for sprite
    Point boardPoint;
    boolean spriteMoving;
    Image sprite;

    ChessVisualSquare( ChessSquare link, Color color, int squareSize) {
        this.link = link;
        this.color = color;
        this.spriteMoving = false;
        this.legalDestination = false;
        this.boardPoint = new Point(0, 0);
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(squareSize, squareSize));
        this.setBackground(color);
        this.setOpaque(true);
    }

    public void markAsLegalDestination(Color color) {
        this.setBackground(color);
        this.legalDestination = true;
    }

    public void unmarkAsLegalDestination() {
        this.setBackground(this.color);
        this.legalDestination = false;
    }

    public boolean isLegalDestination() {
        return this.legalDestination;
    }

    public void snapSpriteToSquare(int squareSize) {
        snapSpriteToSquare(link.getFile(), link.getRank(), squareSize);
    }

    public void snapSpriteToSquare(int file, int rank, int squareSize) {
        spriteMoving = false;
        boardPoint.setLocation(file * squareSize + getXAdjustment(squareSize),
                rank * squareSize + getYAdjustment(squareSize));
    }

    public boolean setupSprite(Image sprite) {
        if (this.link.getPiece() == null || sprite == null) {
            this.sprite = null;
            this.spriteMoving = false;
        }
        else {
            this.sprite = sprite;
            if (!this.spriteMoving) {
                this.snapSpriteToSquare(this.getHeight());
            }
        }
        return (this.sprite != null);
    }

    public void removePiece() {
        if (this.link.getPiece() != null) {
            this.link.removePiece();
            this.setupSprite(null);
        }
    }

    public Image getSprite() { return sprite; }

    public int getXAdjustment(int squareSize) {
        if (sprite != null) {
            return (squareSize - sprite.getWidth(null)) / 2;
        }
        return 0;
    }

    public int getYAdjustment(int squareSize) {
        if (sprite != null) {
            return (squareSize - sprite.getHeight(null)) / 2;
        }
        return 0;
    }

    public void setBoardPoint(int x, int y) {
        boardPoint.x = x;
        boardPoint.y = y;
    }

    public Point getBoardPoint() { return boardPoint; }

    public void addFileLetter(Color color, char c ) {
        JLabel letter = new JLabel(Character.toString(c));
        letter.setForeground(color);
        letter.setHorizontalAlignment(JLabel.LEFT);
        letter.setVerticalAlignment(JLabel.BOTTOM);
        this.add(letter, BorderLayout.WEST);
    }

    public void addRankNumber(Color color, int num) {
        JLabel number = new JLabel(Integer.toString(num));
        number.setForeground(color);
        number.setHorizontalAlignment(JLabel.RIGHT);
        number.setVerticalAlignment(JLabel.TOP);
        this.add(number, BorderLayout.EAST);
    }

    public void setSpriteMoving(boolean moving) {
        this.spriteMoving = moving;
    }

    public boolean isSpriteMoving() { return this.spriteMoving; }
}

class MovingSprite {
    ChessVisualSquare movingSprite;
    Point moveDest;
    Point moveVel;

    public MovingSprite(ChessVisualSquare from, ChessVisualSquare to, Image sprite) {//Point moveDest) {
        to.setupSprite(sprite); //this is kinda bad
        this.moveDest = new Point(to.boardPoint.x, to.boardPoint.y);
        this.moveVel = new Point(0, 0);
        to.snapSpriteToSquare(from.link.getFile(), from.link.getRank(), from.getHeight());
        to.spriteMoving = true;
        this.movingSprite = to;
    }

    public boolean isVelocitySet() {
        return (moveVel.x != 0 || moveVel.y != 0);
    }

    public void setVelocity(int x, int y) {
        moveVel.x = x;
        moveVel.y = y;
    }

    public void stopMoving() {
        if (isMoving()) {
            movingSprite.snapSpriteToSquare(movingSprite.getHeight());
            movingSprite.spriteMoving = false;
            movingSprite = null;
        }
    }

    boolean isMoving() {
        return (movingSprite != null && movingSprite.spriteMoving);
    }
}

class PieceIcons{
    final static ImageIcon invalidIcon = new ImageIcon("sprites/invalid.png");

    // ------- WHITE -------
    final static ImageIcon wPawnIcon = new ImageIcon("sprites/w_pawn_png_1024px.png");
    final static ImageIcon wKnightIcon = new ImageIcon("sprites/w_knight_png_1024px.png");
    final static ImageIcon wBishopIcon = new ImageIcon("sprites/w_bishop_png_1024px.png");
    final static ImageIcon wQueenIcon = new ImageIcon("sprites/w_queen_png_1024px.png");
    final static ImageIcon wKingIcon = new ImageIcon("sprites/w_king_png_1024px.png");
    final static ImageIcon wRookIcon = new ImageIcon("sprites/w_rook_png_1024px.png");
    // ------- BLACK -------
    final static ImageIcon bPawnIcon = new ImageIcon("sprites/b_pawn_png_1024px.png");
    final static ImageIcon bKnightIcon = new ImageIcon("sprites/b_knight_png_1024px.png");
    final static ImageIcon bBishopIcon = new ImageIcon("sprites/b_bishop_png_1024px.png");
    final static ImageIcon bQueenIcon = new ImageIcon("sprites/b_queen_png_1024px.png");
    final static ImageIcon bKingIcon = new ImageIcon("sprites/b_king_png_1024px.png");
    final static ImageIcon bRookIcon = new ImageIcon("sprites/b_rook_png_1024px.png");

    static ImageIcon getSpriteBySign(char sign) {
        switch (sign) {
            case 'P':
                return wPawnIcon;
            case 'p':
                return bPawnIcon;
            case 'N':
                return wKnightIcon;
            case 'n':
                return bKnightIcon;
            case 'B':
                return wBishopIcon;
            case 'b':
                return bBishopIcon;
            case 'Q':
                return wQueenIcon;
            case 'q':
                return bQueenIcon;
            case 'K':
                return wKingIcon;
            case 'k':
                return bKingIcon;
            case 'R':
                return wRookIcon;
            case 'r':
                return bRookIcon;
            default:
                return invalidIcon;
        }
    }
}

public class ChessBoard extends JPanel implements MouseListener, MouseMotionListener, ActionListener {

    public final static char[] pieceArr = new char[]{'P', 'B', 'N', 'R', 'Q', 'K', 'p', 'b', 'n', 'r', 'q', 'k'};

    static ImageIcon getResizedSprite(ImageIcon sprite, int squareSize) {
        return new ImageIcon(sprite.getImage().getScaledInstance(
                (sprite.getIconWidth() * squareSize / sprite.getIconHeight() * 3) / 4,
                (squareSize * 3) / 4, Image.SCALE_SMOOTH));
    }

    HashMap<Character, Image> scaledPieces;

    private Player white;
    private Player black;
    private ChessVisualSquare[][] squares;
    private ChessPosition chessPosition;
    private int squareSize;
    private ChessVisualSquare selection; //currently selected square
    //animating the move
    private Timer moveAnimTimer;
    private List<MovingSprite> movingSprites;
    //dragging a piece
    private boolean isDragged;
    private Point relativePosition;

    private Stack<RealMove> moveHistory;

    private void initAllSprites() {
        this.scaledPieces = new HashMap<Character, Image>();
        for (char piece : pieceArr) {
            this.scaledPieces.put(piece, getResizedSprite(PieceIcons.getSpriteBySign(piece), squareSize).getImage());
        }
    }

    ChessBoard(int width, int height, int squareSize, Color lightSquare, Color darkSquare, Player white, Player black) {
        this.setLayout(new GridLayout(width, height, 0, 0));
        chessPosition = new ChessPosition(width, height, ChessPosition.defaultPosition);
        this.squareSize = squareSize;
        this.white = white;
        this.black = black;
        this.squares = new ChessVisualSquare[height][width];
        this.initAllSprites();
        this.selection = null;
        this.moveHistory = new Stack<RealMove>();
        this.movingSprites = new ArrayList<MovingSprite>();
        this.isDragged = false;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                boolean isLight = (i + j) % 2 == 0; //light = light square + dark text
                ChessVisualSquare square = new ChessVisualSquare(chessPosition.getSquares()[i][j],
                        isLight ? lightSquare : darkSquare, squareSize);
                if (i == height - 1) {
                    square.addFileLetter(isLight ? darkSquare : lightSquare, (char)('a' + j));
                }
                if (j == width - 1) {
                    square.addRankNumber(isLight ? darkSquare : lightSquare, height - i);
                }
                square.addMouseListener(this);
                square.addMouseMotionListener(this);
                squares[i][j] = square;
                this.add(squares[i][j]);
            }
        }
        //this.addMouseMotionListener(this);
    }

    public void paint(Graphics g) {
        ChessVisualSquare dragging = null;
        super.paint(g);
        if (squareSize != squares[0][0].getHeight()) {
            stopMoving();
            squareSize = squares[0][0].getHeight();
            initAllSprites();
        }
        for (ChessVisualSquare[] vr : squares) {
            for (ChessVisualSquare r : vr) {
                if (!r.link.isEmpty() && r.setupSprite(scaledPieces.get(r.link.getPiece().getSign()))) {
                    if (isDragged && r.isSpriteMoving()) {
                        dragging = r;
                    }
                    else {
                        g.drawImage(r.getSprite(), r.getBoardPoint().x, r.getBoardPoint().y, null);
                    }
                }
            }
        }
        if (dragging != null) {
            g.drawImage(dragging.getSprite(), dragging.getBoardPoint().x, dragging.getBoardPoint().y, null);
        }
    }

    public void move(RealMove move, boolean sliding) {
        ChessVisualSquare sqrTo = squares[move.getRankDestination()][move.getFileDestination()],
                sqrFrom = squares[move.getRankFrom()][move.getFileFrom()];
        if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_PROMOTION)) {
            if ((white.getIsHuman() && chessPosition.isWhiteToMove()) ||
                (black.getIsHuman() && !chessPosition.isWhiteToMove())) {

                //todo select the promotion
                move.setArg(chessPosition.isWhiteToMove() ? 'B' : 'b');
            }
        }

        chessPosition.move(move);
        moveHistory.add(move);
        if (sliding) {
            movingSprites.add(new MovingSprite(sqrFrom, sqrTo, scaledPieces.get(sqrTo.link.getPiece().getSign())));
            if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_CASTLE_KINGSIDE)) {
                int rank = move.getRankDestination(),
                        file = chessPosition.rookPositionX[chessPosition.isWhiteToMove() ? 1 : 0][0];
                movingSprites.add(new MovingSprite(squares[rank][file],
                        squares[rank][chessPosition.width() - 3],
                        scaledPieces.get(squares[rank][chessPosition.width() - 3].link.getPiece().getSign())));
            } else if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_CASTLE_QUEENSIDE)) {
                int rank = move.getRankDestination(),
                        file = chessPosition.rookPositionX[chessPosition.isWhiteToMove() ? 1 : 0][1];
                movingSprites.add(new MovingSprite(squares[rank][file],
                        squares[rank][3],
                        scaledPieces.get(squares[rank][3].link.getPiece().getSign())));
            }
            moveAnimTimer = new Timer(1, this);
            moveAnimTimer.start();
        }
        else {
            this.repaint();
        }
        if (chessPosition.getNumLegalMoves() == 0) {
            if (chessPosition.kingInCheck()) {
                if (chessPosition.isWhiteToMove()) {
                    System.out.println("Black wins!");
                }
                else {
                    System.out.println("White wins!");
                }
            }
            else {
                System.out.println("Stalemate!");
            }
        }
    }

    private void stopMoving() {
        if (!movingSprites.isEmpty()) {
            for (MovingSprite ms : movingSprites) {
                ms.stopMoving();
            }
            movingSprites.clear();
            moveAnimTimer.stop();
        }
        this.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selection != null && isDragged) {
            int x = e.getLocationOnScreen().x - this.getLocationOnScreen().x - relativePosition.x,
                    y = e.getLocationOnScreen().y - this.getLocationOnScreen().y - relativePosition.y;
            selection.setSpriteMoving(true);
            selection.setBoardPoint(x, y);
            this.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    //perft test - fixme: move to tests/
    private class TestPerft {
        private String fen;
        private int minDepth;
        private int maxDepth;

        public TestPerft(String fen, int minDepth, int maxDepth) {
            this.fen = fen;
            this.minDepth = minDepth;
            this.maxDepth = maxDepth;
        }

        public String getFen() {
            return fen;
        }

        public int getMaxDepth() {
            return maxDepth;
        }

        public int getMinDepth() {
            return minDepth;
        }
    }

    private RealMove getMoveForSquare(ChessVisualSquare sqr) {
        if (sqr.isLegalDestination() && selection != null) {
            RealMove move = null;
            for (RealMove mv : selection.link.legalMoves) {
                if (mv.to().equals(sqr.link)) {
                    move = mv;
                    break;
                    //if there are multiple moves from the selected square to the same square then it's a promotion
                }
            }
            return move;
        }
        return null;
    }

    private void resetMoveSelection() {
        selection = null;
        isDragged = false;
        for (ChessVisualSquare vr[] : squares) {
            for (ChessVisualSquare r : vr ) {
                r.unmarkAsLegalDestination();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            boolean moveMade = false;
            stopMoving();
            ChessVisualSquare sqr = (ChessVisualSquare)e.getSource();
            RealMove move = getMoveForSquare(sqr);
            if (move != null) {
                move(move, true);
                moveMade = true;
            }
            resetMoveSelection();
            sqr.setBackground(new Color(36, 129, 183));
            if (!moveMade && (white.getIsHuman() && chessPosition.isWhiteToMove()) ||
                    (black.getIsHuman() && !chessPosition.isWhiteToMove())) {
                Piece p = sqr.link.getPiece();
                if (p != null) {
                    if (!moveMade) { isDragged = true; }
                    selection = sqr;
                    relativePosition = new Point(e.getX() - sqr.getXAdjustment(squareSize), e.getY() - sqr.getYAdjustment(squareSize));
                    if (sqr.link.legalMoves != null) {
                        for (RealMove rm : sqr.link.legalMoves) {
                            int y = rm.getRankDestination(), x = rm.getFileDestination();
                            squares[y][x].markAsLegalDestination((x + y) % 2 == 0 ? new Color(232, 12, 12) : new Color(194, 21, 15));
                        }
                    }
                }
            }
            this.repaint();
        }

        if (e.getButton() == MouseEvent.BUTTON3) {
            selection = null;
            isDragged = false;
            for (ChessVisualSquare vr[] : squares) {
                for (ChessVisualSquare r : vr ) {
                    r.unmarkAsLegalDestination();
                }
            }
            if (!moveHistory.isEmpty()) {
                chessPosition.undoMove(moveHistory.pop());
                System.out.println("undoing the last move");
                this.repaint();
            }
        }
        /*if (e.getButton() == 2) {
            //perft test - fixme: move to tests/
            TestPerft tests[] = new TestPerft[5];
            tests[0] = new TestPerft("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 1, 6);
            tests[1] = new TestPerft("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -", 1, 5);
            tests[2] = new TestPerft("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -", 1, 6);
            tests[3] = new TestPerft("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1", 1, 5);
            tests[4] = new TestPerft("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8", 1, 5);
            for (TestPerft t : tests) {
                System.out.println("Test: " + t.getFen());
                chessPosition.parsePosition(t.getFen());
                for (int i = t.getMinDepth(); i <= t.getMaxDepth(); ++i) {
                    System.out.println("For depth " + i + " there are " + perft(i, i, false) + " nodes");
                }
            }
            System.out.println("The end");
        }
         */
    }

    public void printMove(RealMove rm) {
        System.out.print(Character.toString(rm.getFileFrom() + 'a') + "" + Character.toString(squares.length - rm.getRankFrom() + '0'));
        System.out.print(Character.toString(rm.getFileDestination() + 'a') + "" + Character.toString(squares.length - rm.getRankDestination() + '0') );
        System.out.print(rm.getArg() != '\0' ? rm.getArg() : "");
    }

    /*
    Test: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
    For depth 1 there are 20 nodes
    For depth 2 there are 400 nodes
    For depth 3 there are 8902 nodes
    For depth 4 there are 197281 nodes
    For depth 5 there are 4865609 nodes
    For depth 6 there are 119060324 nodes

    Test: r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -
    For depth 1 there are 48 nodes
    For depth 2 there are 2039 nodes
    For depth 3 there are 97862 nodes
    For depth 4 there are 4085603 nodes
    For depth 5 there are 193690690 nodes

    Test: 8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -
    For depth 1 there are 14 nodes
    For depth 2 there are 191 nodes
    For depth 3 there are 2812 nodes
    For depth 4 there are 43238 nodes
    For depth 5 there are 674624 nodes
    For depth 6 there are 11030083 nodes

    Test: r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1
    For depth 1 there are 6 nodes
    For depth 2 there are 264 nodes
    For depth 3 there are 9467 nodes
    For depth 4 there are 422333 nodes
    For depth 5 there are 15833292 nodes

    Test: rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8
    For depth 1 there are 44 nodes
    For depth 2 there are 1486 nodes
    For depth 3 there are 62379 nodes
    For depth 4 there are 2103487 nodes
    For depth 5 there are 89941194 nodes
    The end*/
    //fixme: move to tests
    public int perft(int startDepth, int depth, boolean printMoves) {
        if (depth == 0) {
            return 1;
        }
        int total = 0;
        for (RealMove mv : chessPosition.getAllMoves()) {
            if (depth == startDepth && printMoves) {
                printMove(mv);
            }
            chessPosition.move(mv);
            int addTotal = perft(startDepth,depth - 1, printMoves);
            if (depth == startDepth && printMoves) {
                System.out.println(": " + addTotal);
            }
            total += addTotal;
            chessPosition.undoMove(mv);
        }
        return total;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isDragged) {
            int file = (e.getLocationOnScreen().x - this.getLocationOnScreen().x) / squareSize;
            int rank = (e.getLocationOnScreen().y - this.getLocationOnScreen().y) / squareSize;
            if (file >= 0 && file < chessPosition.width() && rank >= 0 && rank < chessPosition.height()) {
                System.out.println("Released on: " + Character.toString(file + 'a') + "" +
                        Character.toString(squares.length - rank + '0') );
                RealMove move = getMoveForSquare(squares[rank][file]);
                selection.setSpriteMoving(false);
                if (move != null) {
                    move(move, false);
                    resetMoveSelection();
                    squares[rank][file].setBackground(new Color(36, 129, 183));
                }
            }
            else {
                selection.setSpriteMoving(false);
            }
            relativePosition = null;
            isDragged = false;
            this.repaint();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
    //move timer
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!movingSprites.isEmpty()) {
            boolean stillMoving = false;
            for (MovingSprite ms : movingSprites) {
                if (!ms.isMoving()) {
                    continue;
                }
                stillMoving = true;
                int dx = ms.moveDest.x - ms.movingSprite.boardPoint.x,
                        dy = ms.moveDest.y - ms.movingSprite.boardPoint.y;
                int dxabs = Math.abs(dx), dyabs = Math.abs(dy);
                if (dxabs <= 2 && dyabs <= 2) {
                    ms.stopMoving();
                } else {
                    if (!ms.isVelocitySet()) {
                        if (dx != 0 && dy != 0) {
                            if (dxabs != dyabs) {
                                ms.moveVel.x = dx / Math.min(dxabs, dyabs);
                                ms.moveVel.y = dy / Math.min(dxabs, dyabs);
                            } else {
                                ms.moveVel.x = 2 * dx / dxabs;
                                ms.moveVel.y = 2 * dy / dyabs;
                            }
                        } else {
                            if (dx == 0) {
                                ms.moveVel.x = 0;
                                ms.moveVel.y = 2 * dy / dyabs;
                            } else {
                                ms.moveVel.x = 2 * dx / dxabs;
                                ms.moveVel.y = 0;
                            }
                        }
                    }
                    ms.movingSprite.boardPoint.x += ms.moveVel.x;
                    ms.movingSprite.boardPoint.y += ms.moveVel.y;
                }
            }
            if (stillMoving) {
                this.repaint();
            }
            else {
                stopMoving();
            }
        }
        else {
            moveAnimTimer.stop();
        }
    }
}

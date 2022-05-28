package chess.core;

import chess.Player;
import chess.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
            //this.boardPoint = null;
            this.sprite = null;
            this.spriteMoving = false;
        }
        else {//if (this.sprite == null) {
            this.sprite = sprite;
            //this.boardPoint.setLocation = new Point(0, 0);
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

public class ChessBoard extends JPanel implements MouseListener, ActionListener {

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
    //Point moveDest;
    //Point moveVel;
    //ChessVisualSquare movingSprite;
    private List<MovingSprite> movingSprites;

    //List<RealMove> moveHistory;
    private Stack<RealMove> moveHistory;

    private void initAllSprites() {
        this.scaledPieces = new HashMap<Character, Image>();
        //this.scaledPieces.put('P', getResizedSprite(ChessBoard.getSpriteBySign((this.link.getPiece().getSign())), this.getPreferredSize().height).getImage())
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
        this.squares = new ChessVisualSquare[width][height];
        this.initAllSprites();
        this.selection = null;
        this.moveHistory = new Stack<RealMove>();
        this.movingSprites = new ArrayList<MovingSprite>();
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
                squares[i][j] = square;
                this.add(squares[i][j]);
            }
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (squareSize != squares[0][0].getHeight()) {
            stopMoving();
            squareSize = squares[0][0].getHeight();
            initAllSprites();
        }
        for (ChessVisualSquare[] vr : squares) {
            for (ChessVisualSquare r : vr) {
                if (!r.link.isEmpty() && r.setupSprite(scaledPieces.get(r.link.getPiece().getSign()))) {
                    g.drawImage(r.getSprite(), r.getBoardPoint().x, r.getBoardPoint().y, null);
                }
            }
        }
    }

    public void move(RealMove move) {
        ChessVisualSquare sqrTo = squares[move.getRankDestination()][move.getFileDestination()],
                sqrFrom = squares[move.getRankFrom()][move.getFileFrom()];
        if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_PROMOTION)) {
            if ((white.getIsHuman() && chessPosition.isWhiteToMove()) ||
                (black.getIsHuman() && !chessPosition.isWhiteToMove())) {

                //todo select the promotion
                System.out.println("Congratulations, you are automatically promoting to a knight!");
                move.setArg(chessPosition.isWhiteToMove() ? 'N' : 'n');
            }
        }

        chessPosition.move(move);
        moveHistory.add(move);
        movingSprites.add(new MovingSprite(sqrFrom, sqrTo, scaledPieces.get(sqrTo.link.getPiece().getSign())));

        if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_CASTLE_KINGSIDE)) {
            int rank = move.getRankDestination(),
                    file = chessPosition.rookPositionX[chessPosition.isWhiteToMove() ? 1 : 0][0];
            movingSprites.add(new MovingSprite(squares[rank][file],
                    squares[rank][chessPosition.width() - 3],
                    scaledPieces.get(squares[rank][chessPosition.width() - 3].link.getPiece().getSign())));
        }
        else if (MoveFlags.hasFlag(move.flags(), MoveFlags.RM_CASTLE_QUEENSIDE)) {
            int rank = move.getRankDestination(),
                    file = chessPosition.rookPositionX[chessPosition.isWhiteToMove() ? 1 : 0][1];
            movingSprites.add(new MovingSprite(squares[rank][file],
                    squares[rank][3],
                    scaledPieces.get(squares[rank][3].link.getPiece().getSign())));
        }
        moveAnimTimer = new Timer(1, this);
        moveAnimTimer.start();
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
        /*if (movingSprite != null) {
            moveAnimTimer.stop();
            movingSprite.snapSpriteToSquare(squareSize);
            movingSprite.spriteMoving = false;
            movingSprite = null;
        }*/
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
        stopMoving();
        ChessVisualSquare sqr = (ChessVisualSquare)e.getSource();
        if (sqr.isLegalDestination() && selection != null) {
            RealMove move = null;
            for (RealMove mv : selection.link.legalMoves) {
                if (mv.to() == sqr.link) {
                    move = mv;
                    break;
                    //if there are multiple moves from the selected square to the same square then it's a promotion
                }
            }
            //move(selection, sqr);
            //TODO: change so arg cannot be null
            move(move);
        }
        selection = null;
        for (ChessVisualSquare vr[] : squares) {
            for (ChessVisualSquare r : vr ) {
                r.unmarkAsLegalDestination();
            }
        }
        if ((white.getIsHuman() && chessPosition.isWhiteToMove()) ||
                (black.getIsHuman() && !chessPosition.isWhiteToMove())) {
            Piece p = sqr.link.getPiece();
            if (p != null) {
                selection = squares[sqr.link.getRank()][sqr.link.getFile()];
                selection.setBackground(new Color(36, 129, 183));
                if (sqr.link.legalMoves != null) {
                    //for (Point v : sqr.link.legalMoves) {
                    //    squares[v.y][v.x].markAsLegalDestination((v.y + v.x) % 2 == 0 ? new Color(232, 12, 12) : new Color(194, 21, 15));
                    //}
                    for (RealMove rm : sqr.link.legalMoves) {
                        int y = rm.getRankDestination(), x = rm.getFileDestination();
                        squares[y][x].markAsLegalDestination((x + y) % 2 == 0 ? new Color(232, 12, 12) : new Color(194, 21, 15));
                    }
                }
            }
        }
        this.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 3) {
            if (!moveHistory.isEmpty()) {
                chessPosition.undoMove(moveHistory.pop());
                System.out.println("undoing the last move");
                this.repaint();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

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

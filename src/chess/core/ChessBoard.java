package chess.core;

import chess.players.HumanPlayer;
import chess.players.Player;
import chess.pieces.Piece;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

class ChessVisualSquare extends JPanel {
    protected ChessSquare link;
    private Color color;
    private boolean legalDestination;

    //for sprite
    private Point boardPoint;
    private boolean spriteMoving;
    private Image sprite;

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

    public void setNewColor(Color color) {
        this.color = color;
        this.setBackground(color);
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
    protected ChessVisualSquare movingSprite;
    protected Point moveDest;
    protected Point moveVel;

    public MovingSprite(ChessVisualSquare from, ChessVisualSquare to, Image sprite) {//Point moveDest) {
        to.setupSprite(sprite); //this is kinda bad
        this.moveDest = new Point(to.getBoardPoint().x, to.getBoardPoint().y);
        this.moveVel = new Point(0, 0);
        to.snapSpriteToSquare(from.link.getFile(), from.link.getRank(), from.getHeight());
        //to.spriteMoving = true;
        to.setSpriteMoving(true);
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
            //movingSprite.spriteMoving = false;
            movingSprite.setSpriteMoving(false);
            movingSprite = null;
        }
    }

    public void move() {
        movingSprite.getBoardPoint().x += moveVel.x;
        movingSprite.getBoardPoint().y += moveVel.y;

    }

    boolean isMoving() {
        return (movingSprite != null && movingSprite.isSpriteMoving());
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

class PromotionPanel extends JOptionPane implements ActionListener {
    public PromotionPanel(HashMap<Character, Image> scaledPieces, boolean white) {
        this.setMessage("Which piece do you want to promote to?");
        this.setMessageType(JOptionPane.QUESTION_MESSAGE);
        List<JButton> promButtons = new ArrayList<>();
        for (char piece : ChessBoard.pieceArr) {
            if (Character.isUpperCase(piece) == white && Piece.canPromoteTo(piece)) {
                promButtons.add(makeButton(scaledPieces, piece));
            }
        }
        this.setOptions(promButtons.toArray());
    }

    private JButton makeButton(HashMap<Character, Image> scaledPieces, char piece) {
        ImageIcon icon = new ImageIcon(scaledPieces.get(piece));
        JButton button = new JButton(icon);
        button.setToolTipText(Character.toString(piece));
        button.setFocusable(false);
        button.setBackground(Color.darkGray);
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        this.setValue(button.getToolTipText());
    }
}

class ZombieSprite {
    private Image sprite;
    private Point boardPoint;

    ZombieSprite(Image sprite, Point boardPoint) {
        this.sprite = sprite;
        this.boardPoint = boardPoint;
    }

    public Point getBoardPoint() { return boardPoint; }

    public Image getSprite() { return sprite; }
}

public class ChessBoard extends JPanel implements MouseListener, MouseMotionListener, ActionListener {

    public final static char[] pieceArr = new char[]{'P', 'B', 'N', 'R', 'Q', 'K', 'p', 'b', 'n', 'r', 'q', 'k'};

    static ImageIcon getResizedSprite(ImageIcon sprite, int squareSize) {
        return new ImageIcon(sprite.getImage().getScaledInstance(
                (sprite.getIconWidth() * squareSize / sprite.getIconHeight() * 3) / 4,
                (squareSize * 3) / 4, Image.SCALE_SMOOTH));
    }

    private HashMap<Character, Image> scaledPieces;
    private int score;
    private Player white;
    private Player black;
    private ChessClock clock;
    private ChessVisualSquare[][] squares;
    private ChessPosition chessPosition;
    private int squareSize;
    private ChessVisualSquare selection; //currently selected square
    //animating the move
    private Timer moveAnimTimer;
    private List<MovingSprite> movingSprites;
    private ZombieSprite zombie;
    //dragging a piece
    private boolean isDragged;
    private Point relativePosition;
    //for engines
    private String startPos;

    private boolean allowSave;

    private Deque<RealMove> moveHistory;

    private HashMap<String, Integer> positionHistory;

    private ChessPosition displayPosition;

    private RealMove currentDisplay;

    public ChessPosition getChessPosition() { return chessPosition;}

    public Deque<RealMove> getMoveHistory() { return moveHistory; }

    public String getStartPos() { return startPos; }

    private void initAllSprites() {
        this.scaledPieces = new HashMap<Character, Image>();
        for (char piece : pieceArr) {
            this.scaledPieces.put(piece, getResizedSprite(PieceIcons.getSpriteBySign(piece), squareSize).getImage());
        }
    }


    public ChessBoard(int width, int height, int squareSize,
                      Color lightSquare, Color darkSquare,
                      Player white, Player black, String fen,
                      int score, String movesToPlay, boolean allowSave) {
        this.setLayout(new GridLayout(width, height, 0, 0));
        chessPosition = new ChessPosition(width, height, fen);
        displayPosition = chessPosition;
        this.startPos = chessPosition.getFen().toString();
        this.squareSize = squareSize;
        //this.white = white;
        //this.black = black;
        this.allowSave = allowSave;
        this.score = 0;
        this.squares = new ChessVisualSquare[height][width];
        this.initAllSprites();
        this.selection = null;
        this.moveHistory = new ArrayDeque<RealMove>();
        this.movingSprites = new ArrayList<MovingSprite>();
        this.positionHistory = new HashMap<String, Integer>();
        this.isDragged = false;
        this.zombie = null;
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
        setupGame(white, black);
        if (movesToPlay != null && movesToPlay.length() > 0) {
            this.score = score;
            String movePairs[] = movesToPlay.split("[0-9]+\\.");
            for (int i = 0; i < movePairs.length; ++i) {
                if (movePairs[i].length() == 0) {
                    continue;
                }
                if (movePairs[i].contains(" ")) {
                    String singlePair[] = movePairs[i].split(" ");
                    if (singlePair.length >= 2) {
                        String sm1 = singlePair[0], sm2 = singlePair[1];
                        if (sm1.length() > 0 && sm2.length() > 0) {
                            RealMove m1 = RealMove.algToRealMove(chessPosition, sm1),
                                    m2 = null;
                            if (m1 == null) {
                                this.score = 0;
                                break;
                            }
                            move(m1, false, false);
                            m2 = RealMove.algToRealMove(chessPosition, sm2);
                            if (m2 == null) {
                                this.score = 0;
                                break;
                            }
                            move(m2, false, false);
                            continue;
                        }
                    }
                }
                RealMove m = RealMove.algToRealMove(chessPosition, movePairs[i]);
                if (m == null) {
                    this.score = 0;
                    break;
                }
                else {
                    move(m, false);
                }
            }
        }
        if (score == 0) {
            playerThink(1000);
        }
    }

    public ChessBoard(int squareSize){
        this(8, 8, squareSize, Color.white, Color.black,
                new HumanPlayer("Player 1"),
                new HumanPlayer("Player 2"),
                ChessPosition.emptyPosition, 0, null, false);
    }



    public void addMoveToHistory(RealMove m) {
        moveHistory.add(m);
    }

    public RealMove removeLastMoveFromHistory() {
        RealMove m = moveHistory.removeLast();
        return m;
    }

    public void paint(Graphics g) {
        ChessVisualSquare dragging = null;
        super.paint(g);
        if (squareSize != squares[0][0].getHeight()) {
            stopMoving();
            squareSize = squares[0][0].getHeight();
            initAllSprites();
        }
        if (zombie != null) {
            g.drawImage(zombie.getSprite(), zombie.getBoardPoint().x, zombie.getBoardPoint().y, null);
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

    private void resetMoveSelection() {
        selection = null;
        isDragged = false;
        for (ChessVisualSquare vr[] : squares) {
            for (ChessVisualSquare r : vr ) {
                r.unmarkAsLegalDestination();
            }
        }
    }

    private RealMove getMoveForSquare(ChessVisualSquare sqr) throws IllegalPositionException {
        if (sqr.isLegalDestination() && selection != null) {
            //RealMove move = null;
            List<RealMove> moves = new ArrayList<RealMove>();
            for (RealMove mv : selection.link.legalMoves) {
                if (mv.to().equals(sqr.link)) {
                    moves.add(mv);
                }
            }
            if (moves.size() == 1) {
                return moves.get(0);
            }
            else {
                PromotionPanel promotionPanel = new PromotionPanel(scaledPieces, chessPosition.isWhiteToMove());
                JDialog dialog = promotionPanel.createDialog(this, "Promotion");
                dialog.setVisible(true);
                if (promotionPanel.getValue() != null) {
                    for (RealMove mv : moves) {
                        if (!MoveFlags.hasFlag(mv.flags(), MoveFlags.RM_PROMOTION)) {
                            throw new IllegalPositionException("There are duplicate moves for the same target square");
                        }
                        else if (Character.toString(mv.getArg()).equals(promotionPanel.getValue())) {
                            return mv;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void playerThink(int delay) {
        if (chessPosition.isWhiteToMove()) {
            white.think(this, delay);
        }
        else {
            black.think(this, delay);
        }
    }

    public void playerStop() {
        white.stop();
        white.undoMove();
        black.stop();
        black.undoMove();
    }

    public void moveHistoryRewind(boolean forward) {
        if (moveHistory.isEmpty()) {
            return;
        }
        resetMoveSelection();
        if (currentDisplay == null) {
            if (!forward) {
                for (RealMove m : moveHistory) {
                    currentDisplay = m;
                }
            }
        }
        else {
            if (forward) {
                boolean setNow = false, updated = false;
                for (RealMove m : moveHistory) {
                    if (setNow) {
                        currentDisplay = m;
                        updated = true;
                        break;
                    }
                    if (m == currentDisplay) {
                        setNow = true;
                    }
                }
                if (!updated) {
                    currentDisplay = null;
                }
            }
            else {
                RealMove prev = null;
                if (currentDisplay == moveHistory.getFirst()) {
                    return;
                }
                for (RealMove m : moveHistory) {
                    if (m == currentDisplay) {
                        break;
                    }
                    prev = m;
                }
                currentDisplay = prev;
            }
        }
        if (currentDisplay == null) {
            displayPosition = chessPosition;
        }
        else {
            displayPosition = new ChessPosition(chessPosition.height(), chessPosition.width(), startPos);
            for (RealMove m : moveHistory) {
                if (m == currentDisplay) {
                    break;
                }
                displayPosition.move(new RealMove(
                        displayPosition.getSquare(m.from().getRank(), m.from().getFile()),
                        displayPosition.getSquare(m.to().getRank(), m.to().getFile()),
                        m.flags(),
                        m.getArg(),
                        m.getHalfMove()
                        ));
            }
        }
        refreshLinks();
    }

    public boolean canUndo() {
        return score == 0 && (white.getIsHuman() || black.getIsHuman());
    }

    public void undoMove() {
        if (canUndo()) {
            if (displayPosition != chessPosition) {
                displayPosition = chessPosition;
                currentDisplay = null;
                refreshLinks();
            }
            resetMoveSelection();
            zombie = null;
            if (!moveHistory.isEmpty()) {
                if (hasClock()) {
                    if (chessPosition.isWhiteToMove()) {
                        clock.stopWhite(false);
                        clock.startBlack();
                    }
                    else {
                        clock.stopBlack(false);
                        clock.startWhite();
                    }
                }
                playerStop();
                String fen = ChessPosition.stripFenOfMoves(chessPosition.getFen().toString());
                Integer historyValue = positionHistory.get(fen);
                if (historyValue != null) {
                    if (historyValue > 1) {
                        positionHistory.put(fen, historyValue - 1);
                    }
                    else {
                        positionHistory.remove(historyValue);
                    }
                }
                //chessPosition.undoMove(moveHistory.removeLast());
                chessPosition.undoMove(removeLastMoveFromHistory());
                stopMoving();
                chessPosition.savePosition();
                playerThink(1000);
            }
            this.repaint();
        }
    }

    public void move(RealMove move, boolean sliding) {
        move(move, sliding, true);
    }

    public void move(RealMove move, boolean sliding, boolean think) {
        stopMoving();
        int rankFrom = move.getRankFrom(), fileFrom = move.getFileFrom();
        ChessVisualSquare sqrTo = squares[move.getRankDestination()][move.getFileDestination()],
                sqrFrom = squares[rankFrom][fileFrom];
        if ((chessPosition.isWhiteToMove() && !white.getIsHuman()) ||
                !chessPosition.isWhiteToMove() && !black.getIsHuman()) {
            resetMoveSelection();
            sqrTo.setBackground(new Color(36, 129, 183));
            sqrFrom.setBackground((rankFrom + fileFrom) % 2 == 0 ? new Color(232, 12, 12) : new Color(194, 21, 15));
        }
        zombie = (sliding && !sqrTo.link.isEmpty()) ?
                new ZombieSprite(sqrTo.getSprite(), new Point(sqrTo.getBoardPoint())) : null;
        chessPosition.move(move);
        addMoveToHistory(move);
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
        this.repaint();
        String fenStrip = ChessPosition.stripFenOfMoves(chessPosition.savePosition().toString());
        Integer prevNum = positionHistory.put(fenStrip, positionHistory.getOrDefault(fenStrip, 0) + 1);
        if (chessPosition.getNumLegalMoves() == 0) {
            if (chessPosition.kingInCheck()) {
                if (chessPosition.isWhiteToMove()) {
                    finishGame("Black wins by checkmate!", -10);
                }
                else {
                    finishGame("White wins by checkmate!", 10);
                }
            }
            else {
                finishGame("Draw by stalemate!", 5);
            }
        }
        else if (chessPosition.getHalfMove() >= 100) {
            finishGame("Draw by the fifty-move rule!", 5);
        }
        else if (prevNum != null && prevNum >= 2) {
            finishGame("Draw by 3-fold repetition!", 5);
        }
        else if (chessPosition.isInsufficientMaterial()) {
            finishGame("Draw by insufficient material!", 5);
        }
        else {
            if (hasClock()) {
                if (chessPosition.isWhiteToMove()) {
                    clock.stopBlack(true);
                    clock.startWhite();
                }
                else {
                    clock.stopWhite(true);
                    clock.startBlack();
                }
            }
            if (think) {
                playerThink(500); //fixme: this may cause flagging problems, seeing as the bot will waste 0.5s after each move
            }
        }
        if (!allowSave) {
            return;
        }
        if (score == 0) {
            exportToPgn("lastGame.pgn");
        }
        else if (think) {
            saveFinishedGame();
            playerStop();
        }
    }

    public void finishGame(String message, int score) {
        if (hasClock()) {
            clock.stopBlack(false);
            clock.stopWhite(false);
        }
        this.score = score;
        resetMoveSelection();
        JOptionPane.showOptionDialog(
                this,
                message,
                "Game Over",
                JOptionPane.OK_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon(new ImageIcon("sprites/chess_icon.png").getImage().getScaledInstance(64, 64, 0)),
                new String[]{"Ok"},
                0
                );
    }

    private void stopMoving() {
        zombie = null;
        if (!movingSprites.isEmpty()) {
            for (MovingSprite ms : movingSprites) {
                ms.stopMoving();
            }
            movingSprites.clear();
            moveAnimTimer.stop();
        }
        this.repaint();
    }

    private void refreshLinks() {
        for (int i = 0; i < displayPosition.height(); ++i) {
            for (int j = 0; j < displayPosition.width(); ++j) {
                squares[i][j].link = displayPosition.getSquare(i, j);
            }
        }
        this.repaint();
    }

    private String getCurrentDate(String pattern) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public String exportToPgn() {
        StringBuilder pgn = new StringBuilder();
        pgn.append("[Event \"ChessApp Game\"]\n");
        pgn.append("[Site \"Software Engineering ChessApp Project\"]\n");
        pgn.append("[Date \"" + getCurrentDate("yyyy.MM.dd") + "\"]\n");
        pgn.append("[Round \"1\"]\n");
        pgn.append("[White \"" /*+ (white.getIsHuman() ? "Human, " : "Bot, ")*/ + white.getName() + "\"]\n");
        pgn.append("[Black \"" /*+ (black.getIsHuman() ? "Human, " : "Bot, ")*/ + black.getName() + "\"]\n");
        pgn.append("[Result \"");
        switch (score) {
            case 0:
                pgn.append("*");
                break;
            case 10:
                pgn.append("1-0");
                break;
            case 5:
                pgn.append("1/2-1/2");
                break;
            default:
                pgn.append("0-1");
                break;
        }
        pgn.append("\"]\n");
        if (hasClock()) {
            pgn.append("[TimeFormat \"" + (clock.getStartSeconds() / 60) + "+" + clock.getIncrement() + "\"]\n");
        }
        if (startPos.compareTo(ChessPosition.defaultPosition) != 0) {
            pgn.append("[SetUp \"1\"]\n[FEN \"" + startPos + "\"]\n");

        }
        pgn.append("\n");
        int i = 0;
        for (RealMove m : moveHistory) {
            if (++i % 2 != 0) {
                pgn.append(((i + 1) / 2) + ".");
            }
            pgn.append(m.getAlgebraicMove() + ' ');
        }
        switch (score) {
            case 10:
                pgn.append("1-0");
                break;
            case 5:
                pgn.append("1/2-1/2");
                break;
            case -10:
                pgn.append("0-1");
                break;
            default:
                break;
        }
        return pgn.toString();
    }

    public void exportToPgn(String filename) {
        try {
            FileOutputStream fout = new FileOutputStream("exports/" + filename, false);
            char ch[] = exportToPgn().toCharArray();
            for (int i = 0; i < ch.length; i++)
                fout.write(ch[i]);
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isDragged) {
            int file = (e.getLocationOnScreen().x - this.getLocationOnScreen().x) / squareSize;
            int rank = (e.getLocationOnScreen().y - this.getLocationOnScreen().y) / squareSize;
            if (file >= 0 && file < chessPosition.width() && rank >= 0 && rank < chessPosition.height()) {
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
    public void mousePressed(MouseEvent e) {
        if (displayPosition != chessPosition) {
            return;
        }
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
    }

    public void saveFinishedGame() {
        Path path = FileSystems.getDefault().getPath("exports/lastGame.pgn");
        try {
            Files.delete(path);
        } catch (IOException e) {
        }
        exportToPgn(
                white.getName() + "_vs_" +
                        black.getName() + "_" + getCurrentDate("yyyy.MM.dd_hh.mm.ss") +
                        ".pgn");
    }

    public void whiteFlagged() {
        clock.stopWhite(false);
        clock.stopBlack(false);
        finishGame("Black won on time!", -10);
        saveFinishedGame();
    }

    public void blackFlagged() {
        clock.stopWhite(false);
        clock.stopBlack(false);
        finishGame("White won on time!", 10);
        saveFinishedGame();
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
                int dx = ms.moveDest.x - ms.movingSprite.getBoardPoint().x,
                        dy = ms.moveDest.y - ms.movingSprite.getBoardPoint().y;
                int dxabs = Math.abs(dx), dyabs = Math.abs(dy);
                if (dxabs <= Math.abs(ms.moveVel.x) && dyabs <= Math.abs(ms.moveVel.y)) {
                    ms.stopMoving();
                } else {
                    if (!ms.isVelocitySet()) {
                        int dist = (int) Math.sqrt(dx * dx + dy * dy),
                                dMult = dist < 2 * squareSize ? 1 : Math.max(2, dist / squareSize - 1);
                        dMult *= 2;
                        if (dx != 0 && dy != 0) {
                            if (dxabs != dyabs) {
                                ms.setVelocity(2 * dx / Math.min(dxabs, dyabs),
                                        2 * dy / Math.min(dxabs, dyabs));
                            } else {
                                ms.setVelocity(dMult * dx / dxabs, dMult * dy / dyabs);
                            }
                        } else {
                            ms.setVelocity(dx == 0 ? 0 : dMult * dx / dxabs, dy == 0 ? 0 : dMult * dy / dyabs);
                        }
                    }
                    ms.move();
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

    public void setNewColor(Color darkSquare, Color lightSquare) {
        for (int i = 0; i < squares.length; ++i) {
            for (int j = 0; j < squares[i].length; ++j) {
                boolean isLight = (i + j) % 2 == 0;
                if (isLight && lightSquare == null) {
                    continue;
                }
                if (!isLight && darkSquare == null) {
                    continue;
                }
                squares[i][j].setNewColor(isLight ? lightSquare : darkSquare);
            }
        }
        this.repaint();
    }

    public void setupGame(Player white, Player black) {
        this.white = white;
        this.black = black;
    }

    public void addClocks(ChessClock clock) {
        this.clock = clock;
    }

    public boolean hasClock() { return clock != null; }

    public ChessClock getClock() { return clock; }

    public void removeClocks() {
        if (hasClock()) {
            clock.stopWhite(false);
            clock.stopBlack(false);
            clock = null;
        }
    }

    //unused, but they still have to be here
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}

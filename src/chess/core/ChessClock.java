package chess.core;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

class GradLabel extends JLabel {
    private Color color_l = new Color(255, 255, 255);
    private Color color_r = new Color(255, 0, 0);

    public GradLabel(Color color_l, Color color_r) {
        //setBorder(new EmptyBorder(20, 20, 20, 20));
        //setForeground(new Color(240, 240, 240));
        setBorder(null);
        this.color_l = color_l;
        this.color_r = color_r;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        int width = getWidth();
        int height = getHeight();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint grad = new GradientPaint(0, 0, color_l, width, 0, color_r);
        g2.setPaint(grad);
        g2.fillRect(0, 0, width, height);
        createStyle(g2);
        graphics.setColor(getBackground());

        g2.dispose();
        graphics.drawImage(img, 0, 0, null);
        super.paintComponent(graphics);
    }

    private void createStyle(Graphics2D g2) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.3f));
        int width = getWidth();
        int height = getHeight();

        g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, height, new Color(255, 255, 255, 60)));

        Path2D.Float f = new Path2D.Float();
        f.moveTo(0, 0);
        f.curveTo(0, 0, width / 2F, height / 2F, width, 0);

        g2.fill(f);
    }
}

public class ChessClock extends JPanel {
    private int startSeconds;
    private int increment;
    private ChessBoard chessBoard;

    private int whiteTime;
    private GradLabel whiteText;
    private Timer whiteTimer;

    private int blackTime;
    private GradLabel blackText;
    private Timer blackTimer;

    private Color color_l = new Color(245, 161, 66);
    private Color color_r = new Color(250, 74, 15);

    public ChessClock(String timeFormat) {
        this.blackTime = this.whiteTime = this.startSeconds = Integer.parseInt(timeFormat.split("\\+")[0]) * 60;
        this.increment = Integer.parseInt(timeFormat.split("\\+")[1]);
        setOpaque(false);
        setLayout(new GridLayout(2, 1));
        //setPreferredSize(new Dimension(128, 256));
        whiteText = formatClockText();
        blackText = formatClockText();
        add(blackText);
        add(whiteText);
        whiteTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getTextForTime(whiteText, --whiteTime);
                if (whiteTime <= 0) {
                    chessBoard.whiteFlagged();
                    whiteTimer.stop();
                }
            }
        });
        blackTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getTextForTime(blackText, --blackTime);
                if (blackTime <= 0) {
                    chessBoard.blackFlagged();
                    blackTimer.stop();
                }
            }
        });
    }

    public void stopBlack(boolean inc) {
        blackTimer.stop();
        if (inc) {
            getTextForTime(blackText, blackTime += increment);
        }
    }

    public void startBlack() {
        blackTimer.start();
    }

    public void stopWhite(boolean inc) {
        whiteTimer.stop();
        if (inc) {
            getTextForTime(whiteText, whiteTime += increment);
        }
    }

    public void startWhite() {
        whiteTimer.start();
    }

    public void linkChessBoard(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }

    private GradLabel formatClockText() {
        GradLabel text = new GradLabel(color_l, color_r);
        getTextForTime(text, startSeconds);
        text.setFont((new Font("Calibri", Font.BOLD, 40)));
        text.setBorder(new EmptyBorder(15, 10, 10, 10));
        text.setForeground(Color.white);
        text.setPreferredSize(new Dimension(128, 64));
        text.setHorizontalAlignment(JLabel.CENTER);
        text.setVerticalAlignment(JLabel.CENTER);
        return text;
    }

    private void getTextForTime(GradLabel label, int time) {
        int minutes = (time/ 60), seconds = (time % 60);
        label.setText(minutes + ":" + (seconds < 10 ? "0" + seconds : seconds));
    }

    public int getStartSeconds() { return startSeconds; }

    public int getIncrement() { return increment; }
}

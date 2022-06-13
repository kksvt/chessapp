package chess.core;

import chess.gui.GradLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        /*text.setFont((new Font("Calibri", Font.BOLD, 40)));
        text.setForeground(Color.white);
        text.setPreferredSize(new Dimension(128, 64));
        text.setHorizontalAlignment(JLabel.CENTER);
        text.setVerticalAlignment(JLabel.CENTER);*/
        return text;
    }

    private void getTextForTime(GradLabel label, int time) {
        int minutes = (time/ 60), seconds = (time % 60);
        label.setText(minutes + ":" + (seconds < 10 ? "0" + seconds : seconds));
    }

    public int getStartSeconds() { return startSeconds; }

    public int getIncrement() { return increment; }
}

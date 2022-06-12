package chess.gui;

import chess.core.ChessPosition;
import chess.core.Controller;
import chess.core.ChessBoard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MenuWindow extends BaseWindow {
    GradButton[] buttons = {new GradButton(), new GradButton()};
    GradButton simulationButton = new GradButton();
    MenuWindow referenceToThis;
    Controller controller;
    public ChessBoard chessBoard;

    public int getEngineVsPlayerLvl() {
        return engineVsPlayerLvl;
    }

    public int getEngineVsEngineLvl1() {
        return engineVsEngineLvl1;
    }

    public int getEngineVsEngineLvl2() {
        return engineVsEngineLvl2;
    }

    private int engineVsPlayerLvl = 8;
    private int engineVsEngineLvl1 = 8;
    private int engineVsEngineLvl2 = 8;
//    Stockfish constraints
    private final int minDepth = 1;
    private final int maxDepth = 40;

    GradButton clearPosition = new GradButton();
    GradButton defaultPosition = new GradButton();

    public MenuWindow(Controller c){
        controller=c;
        referenceToThis=this;

        GradButton timeFormatButton = new GradButton();
        timeFormatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                TimeFormatWindow ow = new TimeFormatWindow(referenceToThis);
                ow.setVisible(true);
            }
        });
        GradButton engineLevelButton = new GradButton();
        engineLevelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                EngineSettingWindow ew = new EngineSettingWindow(referenceToThis);
                ew.setVisible(true);
            }
        });


        StylizeButton(engineLevelButton, "Engine Level");
        StylizeButton(timeFormatButton, "Time Format");
        StylizeButton(clearPosition, "Clear");
        StylizeButton(defaultPosition, "Initial");
        StylizeButton(simulationButton, "Engine vs Engine");

        addToRightMenu(simulationButton);
        addToRightMenu((JComponent) Box.createHorizontalStrut(5));
        addToRightMenu(engineLevelButton);
        addToRightMenu((JComponent) Box.createHorizontalStrut(5));
        addToRightMenu(timeFormatButton);
        addToRightMenu((JComponent) Box.createHorizontalStrut(5));
        addToRightMenu(defaultPosition);
        addToRightMenu((JComponent) Box.createHorizontalStrut(5));
        addToRightMenu(clearPosition);

        JPanel chessPanel = new JPanel();
        chessPanel.setBorder(new EmptyBorder(5,0,0,0));
        chessPanel.setBackground(new Color(0, 0, 0, 0));

        chessBoard = new ChessBoard();
        chessPanel.add(chessBoard);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.weightx = 70;
        gbc.weighty = 100;
        baseFrame.add(chessPanel, gbc);


        JPanel menuButtons = new JPanel();
        menuButtons.setBackground(new Color(0, 0, 0, 0));
        menuButtons.setLayout(new GridLayout(2,1, 0, 3));
        menuButtons.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        MenuEventHandler handler = new MenuEventHandler();
        clearPosition.addActionListener(handler);
        defaultPosition.addActionListener(handler);
        simulationButton.addActionListener(handler);

        for (int i = 0; i < 2; i++) {
            buttons[i].setFont(new Font("Calibri", Font.BOLD, 40));
            buttons[i].setForeground(Color.white);
            buttons[i].addActionListener(handler);
            menuButtons.add(buttons[i]);
        }
        buttons[0].setText("Computer");
        buttons[1].setText("Player");

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 20;
        gbc.weighty = 80;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(2, 2, 2, 2);
        baseFrame.add(menuButtons, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weighty = 2;
        JPanel footer = new JPanel();
        footer.setBackground(new Color(0,0,0,0));
        footer.setLayout(new FlowLayout());
        footer.setBorder(new EmptyBorder(5,0,0,0));
        baseFrame.add(footer, gbc);

        add(baseFrame);

        setMinimumSize(new Dimension(1400,1050));
        setVisible(true);
    }

    public void setEngine(int level) {
        if(level < minDepth)
            level = minDepth;
        if(level > maxDepth)
            level = maxDepth;

        engineVsPlayerLvl = level;
    }

    public void setEngines(int level1, int level2) {
        if(level1 < minDepth)
            level1 = minDepth;
        else if(level1 > maxDepth)
            level1 = maxDepth;

        if(level2 < minDepth)
            level2 = minDepth;
        else if(level2 > maxDepth)
            level2 = maxDepth;

        engineVsEngineLvl1 = level1;
        engineVsEngineLvl2 = level2;
    }

    class MenuEventHandler implements ActionListener {
        GameWindow gameWindow;

        @Override
        public void actionPerformed(ActionEvent event) {
            if(event.getSource()==defaultPosition){
                chessBoard.getChessPosition().parsePosition(ChessPosition.defaultPosition);
                chessBoard.repaint();
                return;
            }
            else if(event.getSource()==clearPosition){
                chessBoard.getChessPosition().parsePosition(ChessPosition.emptyPosition);
                chessBoard.repaint();
                return;
            }


            ChessPosition chessPosition = new ChessPosition(controller.getPosition());
            if (!chessPosition.parsePosition(controller.getPosition())){
                JOptionPane.showMessageDialog(referenceToThis,
                        "Please rearrange pieces.",
                        "Illegal position",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            referenceToThis.setVisible(false);

            if (event.getSource() == buttons[0]) {
                try {
                    gameWindow = new GameWindow(referenceToThis, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                gameWindow.setVisible(true);
            }
            else if (event.getSource() == buttons[1]) {
                try {
                    gameWindow = new GameWindow(referenceToThis, 2);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (event.getSource() == simulationButton) {
                try {
                    gameWindow = new GameWindow(referenceToThis, 3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


package chess.gui;

import chess.core.ChessPosition;
import chess.core.Controller;
import chess.core.ChessBoard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class MenuWindow extends BaseWindow {
    GradButton[] buttons = {new GradButton(), new GradButton(), new GradButton()};
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
    private final int maxDepth = 24;

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

        GradButton importPgn = new GradButton();
        importPgn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser("./exports/");
                fileChooser.setFileFilter(new FileFilter() {
                    public String getDescription() {
                        return "Portable Game Notation (.pgn)";
                    }
                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return true;
                        }
                        return f.getName().toLowerCase().endsWith(".pgn");
                    }
                });
                int response = fileChooser.showOpenDialog(referenceToThis);
                if (response == JFileChooser.APPROVE_OPTION) {
                    File f = new File(fileChooser.getSelectedFile().getAbsolutePath());
                    if (!importPgnFile(f)) {
                        JOptionPane.showMessageDialog(
                                referenceToThis,
                                "Error reading the PGN file",
                                "Invalid PGN",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });

        GradButton continueLast = new GradButton();
        continueLast.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File f = new File("./exports/lastGame.pgn");
                if (f.exists()) {
                    if (!importPgnFile(f)) {
                        JOptionPane.showMessageDialog(
                                referenceToThis,
                                "Error reading the PGN file",
                                "Invalid PGN",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
                else {
                    JOptionPane.showMessageDialog(
                            referenceToThis,
                            "There's no last game save to read",
                            "No last game",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        });

        GradButton importFen = new GradButton();
        importFen.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               JFileChooser fileChooser = new JFileChooser("./exports/");
               int response = fileChooser.showOpenDialog(referenceToThis);
               if (response == JFileChooser.APPROVE_OPTION) {
                   File f = new File(fileChooser.getSelectedFile().getAbsolutePath());
                   if (!importFenFile(f)) {
                       JOptionPane.showMessageDialog(
                               referenceToThis,
                               "Error reading the FEN file",
                               "Invalid FEN",
                               JOptionPane.ERROR_MESSAGE
                       );
                   }
               }
           }
        });

        GradButton exportFen = new GradButton();
        exportFen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser("./exports/");
                int response = fileChooser.showSaveDialog(referenceToThis);
                if (response == JFileChooser.APPROVE_OPTION) {
                    File saveFen = fileChooser.getSelectedFile();
                    byte bytes[] = chessBoard.getChessPosition().getFen().toString().getBytes();
                    try {
                        Files.write(fileChooser.getSelectedFile().getAbsoluteFile().toPath(), bytes);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(
                                referenceToThis,
                                "Couldn't save the file!",
                                "Save error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });

        StylizeButton(engineLevelButton, "Engine Level");
        StylizeButton(timeFormatButton, "Time Format");
        StylizeButton(clearPosition, "Clear");
        StylizeButton(defaultPosition, "Initial");
        StylizeButton(simulationButton, "Engine vs Engine");
        StylizeButton(importPgn, "Import PGN");
        StylizeButton(continueLast, "Continue last game");
        StylizeButton(importFen, "Import FEN");
        StylizeButton(exportFen, "Export FEN");

        addToRightMenu(simulationButton);
        addToRightMenu((JComponent) Box.createHorizontalStrut(5));
        addToRightMenu(engineLevelButton);
        addToRightMenu((JComponent) Box.createHorizontalStrut(5));
        addToRightMenu(timeFormatButton);
        addToRightMenu((JComponent) Box.createHorizontalStrut(5));
        addToRightMenu(defaultPosition);
        addToRightMenu((JComponent) Box.createHorizontalStrut(5));
        addToRightMenu(clearPosition);
        //addToRightMenu((JComponent) Box.createHorizontalStrut(5));
        //addToRightMenu(importPgn);

        JPanel chessPanel = new JPanel();
        chessPanel.setBorder(new EmptyBorder(5,0,0,0));
        chessPanel.setBackground(new Color(0, 0, 0, 0));

        chessBoard = new ChessBoard();
        chessPanel.add(chessBoard);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.gridheight = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weightx = 70;
        gbc.weighty = 100;
        chessPanel.add(continueLast, gbc);
        chessPanel.add(importPgn, gbc);
        chessPanel.add(importFen, gbc);
        chessPanel.add(exportFen, gbc);

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
        menuButtons.setLayout(new GridLayout(3,1, 0, 3));
        menuButtons.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        MenuEventHandler handler = new MenuEventHandler();
        clearPosition.addActionListener(handler);
        defaultPosition.addActionListener(handler);
        simulationButton.addActionListener(handler);

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setFont(new Font("Calibri", Font.BOLD, 40));
            buttons[i].setForeground(Color.white);
            buttons[i].addActionListener(handler);
            menuButtons.add(buttons[i]);
        }
        buttons[0].setText("Black Computer");
        buttons[1].setText("White Computer");
        buttons[2].setText("Player");

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

    public boolean importFenFile(File f) {
        Scanner reader = null;
        try {
            reader = new Scanner(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        StringBuilder fen = new StringBuilder();
        while (reader.hasNextLine()) {
            fen.append(reader.nextLine());
        }
        controller.setPosition(fen.toString());
        if (!chessBoard.getChessPosition().parsePosition(controller.getPosition())){
            controller.setPosition(ChessPosition.defaultPosition);
            chessBoard.getChessPosition().parsePosition(controller.getPosition());
            chessBoard.repaint();
            return false;
        }
        chessBoard.repaint();
        return true;
    }

    public boolean importPgnFile(File f) {
        int engLvl2 = -1, engLvl = -1;
        boolean gotBlack = false, gotWhite = false;
        boolean whiteHuman = false, parsedPosition = false;
        StringBuilder pgnMoves = new StringBuilder();
        try {
            Scanner reader = new Scanner(f);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.length() == 0) {
                    continue;
                }
                if (line.matches("\\[[A-Za-z]+ \".+\"\\]")) {
                    if (line.matches("\\[White \".+\"\\]")) {
                        if (gotWhite) {
                            return false;
                        }
                        if (line.matches("\\[White \"Stockfish[0-9]+\"\\]")) {
                            engLvl = Integer.parseInt(line.split("Stockfish")[1].split("\"")[0]);;
                            gotWhite = true;
                        }
                        else {
                            controller.setPlayerName1(line.split("\\[White \"")[1].split("\"")[0]);
                            whiteHuman = gotWhite = true;
                        }
                    }
                    else if (line.matches("\\[Black \".+\"\\]")) {
                        if (gotBlack) {
                            return false;
                        }
                        if (line.matches("\\[Black \"Stockfish[0-9]+\"\\]")) {
                            if (engLvl != -1) {
                                engLvl2 = Integer.parseInt(line.split("Stockfish")[1].split("\"")[0]);
                            }
                            else {
                                engLvl = Integer.parseInt(line.split("Stockfish")[1].split("\"")[0]);;
                            }
                            gotBlack = true;
                        }
                        else {
                            controller.setPlayerName2(line.split("\\[Black \"")[1].split("\"")[0]);
                            gotBlack = true;
                        }
                    }
                    else if (line.matches("\\[FEN \".+\"\\]")) {
                        controller.setPosition(line.split("\\[FEN \"")[1].split("\"")[0]);
                        if (!chessBoard.getChessPosition().parsePosition(controller.getPosition())) {
                            chessBoard.getChessPosition().parsePosition(ChessPosition.emptyPosition);
                            chessBoard.repaint();
                            controller.setPosition(chessBoard.getChessPosition().getFen().toString());
                            return false;
                        }
                        parsedPosition = true;
                    }
                    else if (line.matches("\\[TimeFormat \"[0-9]+\\+[0-9]+\"\\]")) {
                        controller.setTimeFormat(line.split("\\[TimeFormat \"")[1].split("\"")[0]);
                        setTimeFormatLabel(controller.getTimeFormat());
                    }
                }
                else {
                    String stripComments[] = line.split(";")[0].split("\\{.+\\}");
                    for (int i = 0; i < stripComments.length; ++i) {
                        pgnMoves.append(stripComments[i]);
                    }
                    pgnMoves.append(" ");
                }
            }
            reader.close();
            if (!gotWhite || !gotBlack || pgnMoves.length() == 0) {
                return false;
            }
            String finalMoves = pgnMoves.toString().replaceAll(" +", " ");
            if (finalMoves.endsWith("1-0") || finalMoves.endsWith("1-0 ")) {
                controller.setScore(10);
                finalMoves = finalMoves.split("1-0")[0];
            }
            else if (finalMoves.endsWith("0-1") || finalMoves.endsWith("0-1 ")) {
                controller.setScore(-10);
                finalMoves = finalMoves.split("0-1")[0];
            }
            else if (finalMoves.endsWith("1/2-1/2") || finalMoves.endsWith("1/2-1/2 ")) {
                controller.setScore(5);
                finalMoves = finalMoves.split("1/2-1/2")[0];
            }
            if (!parsedPosition) {
                chessBoard.getChessPosition().parsePosition(ChessPosition.defaultPosition);
                controller.setPosition(chessBoard.getChessPosition().getFen().toString());
            }
            chessBoard.repaint();
            controller.setMovesToMake(finalMoves);
            GameWindow gameWindow;
            try {
                if (engLvl != -1 && engLvl2 != -1) {
                    setEngines(engLvl, engLvl2);
                    gameWindow = new GameWindow(referenceToThis, 3);
                } else if (engLvl != -1) {
                    setEngine(engLvl);
                    if (whiteHuman) {
                        gameWindow = new GameWindow(referenceToThis, 1);
                    }
                    else {
                        gameWindow = new GameWindow(referenceToThis, 4);
                    }
                } else {
                    gameWindow = new GameWindow(referenceToThis, 2);
                }
                referenceToThis.setVisible(false);
                gameWindow.setVisible(true);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    class MenuEventHandler implements ActionListener {
        GameWindow gameWindow;

        @Override
        public void actionPerformed(ActionEvent event) {
            if(event.getSource()==defaultPosition){
                controller.setPosition(ChessPosition.defaultPosition);
                chessBoard.getChessPosition().parsePosition(ChessPosition.defaultPosition);
                chessBoard.repaint();
                return;
            }
            else if(event.getSource()==clearPosition){
                controller.setPosition(ChessPosition.emptyPosition);
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
                    gameWindow = new GameWindow(referenceToThis, 4);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (event.getSource() == buttons[2]) {
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


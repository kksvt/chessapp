package chess.gui;

import chess.core.Controller;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class OptionsWindow extends BaseWindow {
    private BaseWindow windowParent;
    private final Color[] light={Color.white,new Color(247, 224, 158),new Color(198, 216, 247),new Color(194, 194, 194)};
    private final Color[] dark={Color.black,new Color(71, 53, 0),new Color(18, 37, 69),new Color(56, 56, 56)};
    private final JComboBox<Color> lightBox = new JComboBox<>(light);
    private final JComboBox<Color> darkBox = new JComboBox<>(dark);
    private int lightIdx = 0, darkIdx = 0;
    private static class PlainButton extends JButton {
        PlainButton(String name){
            setText(name);
            setFont((new Font("Calibri", Font.BOLD, 20)));
            setBorder(new LineBorder(Color.black));
            setBackground(new Color(222, 121, 67));
            setForeground(Color.white);
        }
    }

    OptionsWindow(BaseWindow parent){
        windowParent = parent;
        setSize(500,200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(1,2));
        setTitle("Choose Tiles Color");


        lightBox.setMaximumRowCount(4);
        darkBox.setMaximumRowCount(4);
        lightBox.setPreferredSize(new Dimension(50,20));
        darkBox.setPreferredSize(new Dimension(50,20));
        lightBox.setRenderer(new Renderer());
        darkBox.setRenderer(new Renderer());

        setLayout(new GridLayout(2,2));
        JLabel lSqr = new JLabel("Light Squares");
        JLabel dSqr = new JLabel("Dark Squares");
        lSqr.setFont((new Font("Calibri", Font.BOLD, 20)));
        dSqr.setFont((new Font("Calibri", Font.BOLD, 20)));
        lSqr.setHorizontalAlignment(JLabel.CENTER);
        dSqr.setHorizontalAlignment(JLabel.CENTER);
        add(lSqr);
        add(dSqr);
        add(lightBox);add(darkBox);


        OptionsWindow.EventHandler handler = new OptionsWindow.EventHandler();
        lightBox.addActionListener(handler);
        darkBox.addActionListener(handler);
//        PlainButton[] buttons = new PlainButton[btnNames.length];


        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    }

    class EventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if(event.getSource()==lightBox){
                try {
                    MenuWindow mw = (MenuWindow)windowParent;
                    mw.controller.setLightSquare((Color) lightBox.getSelectedItem());
                    mw.controller.repaintChessboard();
                }
                catch (ClassCastException e) {
                    GameWindow gw = (GameWindow)windowParent;
                    gw.chessBoard.setNewColor(null, (Color) lightBox.getSelectedItem());
                }
            }
            else if(event.getSource()==darkBox){
                try {
                    MenuWindow mw = (MenuWindow)windowParent;
                    mw.controller.setDarkSquare((Color) darkBox.getSelectedItem());
                    mw.controller.repaintChessboard();
                }
                catch (ClassCastException e) {
                    GameWindow gw = (GameWindow)windowParent;
                    gw.chessBoard.setNewColor((Color) darkBox.getSelectedItem(), null);
                }
            }

        }
    }

    static class Renderer extends JButton implements ListCellRenderer {
        public Renderer() {
            setOpaque(true);
        }
        boolean b=false;
        @Override
        public void setBackground(Color bg) {
                        super.setBackground(bg);
        }
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,

                boolean isSelected,
                boolean cellHasFocus)
        {

            b=true;
            setText(" ");
            setBackground((Color)value);
            b=false;
            return this;
        }
    }
}

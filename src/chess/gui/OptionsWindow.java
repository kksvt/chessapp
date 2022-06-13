package chess.gui;

import chess.core.Controller;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class OptionsWindow extends BaseWindow {
    private BaseWindow windowParent;
    private final Color[] light={Color.white,new Color(247, 224, 158),new Color(198, 216, 247),new Color(194, 194, 194)};
    private final Color[] dark={Color.black,new Color(71, 53, 0),new Color(18, 37, 69),new Color(56, 56, 56)};
    private final JComboBox<Color> lightBox = new JComboBox<>(light);
    private final JComboBox<Color> darkBox = new JComboBox<>(dark);

    private static Color lightSquares = Color.white;
    private static Color darkSquares = Color.BLACK;

    OptionsWindow(BaseWindow parent){
        windowParent = parent;
        setSize(500,200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(1,2));
        setTitle("Options");


        lightBox.setEditable(false);
        darkBox.setEditable(false);

        lightBox.setBackground(lightSquares);
        lightBox.getEditor().getEditorComponent().setForeground(lightSquares);
        darkBox.getEditor().getEditorComponent().setForeground(darkSquares);
        darkBox.getEditor().getEditorComponent().setBackground((Color) darkSquares);
        lightBox.getEditor().setItem("");
        darkBox.getEditor().setItem("");

        lightBox.setMaximumRowCount(4);
        darkBox.setMaximumRowCount(4);
        lightBox.setPreferredSize(new Dimension(50,20));
        darkBox.setPreferredSize(new Dimension(50,20));
        lightBox.setRenderer(new Renderer());
        darkBox.setRenderer(new Renderer());

        setLayout(new GridLayout(2,2));
        JLabel lSqr = new JLabel("Pick squares color");
        JLabel dSqr = new JLabel("");
        lSqr.setFont((new Font("Calibri", Font.BOLD, 20)));
        dSqr.setFont((new Font("Calibri", Font.BOLD, 20)));
        lSqr.setHorizontalAlignment(JLabel.CENTER);
        dSqr.setHorizontalAlignment(JLabel.CENTER);
        add(lSqr);
        add(dSqr);
        add(lightBox);add(darkBox);


        EventHandler handler = new EventHandler();
        lightBox.addActionListener(handler);
        darkBox.addActionListener(handler);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    }

    class EventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if(event.getSource()==lightBox){
                try {
                    MenuWindow mw = (MenuWindow)windowParent;
                    Color c = (Color) lightBox.getSelectedItem();
                    mw.controller.setLightSquare(c);
                    mw.controller.repaintChessboard();

                    lightBox.getEditor().getEditorComponent().setBackground(c);
                    lightBox.getEditor().getEditorComponent().setForeground(c);

                    lightSquares = c;
                    lightBox.getEditor().setItem("");
                }
                catch (ClassCastException e) {
                    GameWindow gw = (GameWindow)windowParent;
                    Color c = (Color) lightBox.getSelectedItem();
                    gw.chessBoard.setNewColor(null, c);
                    lightBox.getEditor().getEditorComponent().setBackground(c);
                    lightBox.getEditor().getEditorComponent().setForeground(c);
                    lightSquares = c;
                    lightBox.getEditor().setItem("");
                }
            }
            else if(event.getSource()==darkBox){
                try {
                    MenuWindow mw = (MenuWindow)windowParent;
                    Color c = (Color) darkBox.getSelectedItem();
                    mw.controller.setDarkSquare(c);
                    mw.controller.repaintChessboard();

                    darkBox.getEditor().getEditorComponent().setBackground(c);
                    darkBox.getEditor().getEditorComponent().setForeground(c);

                    darkSquares = c;
                    darkBox.getEditor().setItem("");
                }
                catch (ClassCastException e) {
                    GameWindow gw = (GameWindow)windowParent;
                    Color c = (Color) darkBox.getSelectedItem();
                    gw.chessBoard.setNewColor((Color) c, null);
                    darkBox.getEditor().getEditorComponent().setBackground(c);
                    darkBox.getEditor().getEditorComponent().setForeground(c);
                    darkSquares = c;
                    darkBox.getEditor().setItem("");
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

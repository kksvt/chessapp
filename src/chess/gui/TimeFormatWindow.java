package chess.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class TimeFormatWindow extends BaseWindow {
    private MenuWindow windowParent;

    private class PlainButton extends JButton{
        PlainButton(String name){
            setText(name);
            setFont((new Font("Calibri", Font.BOLD, 20)));
            setBorder(new LineBorder(Color.black));
            setBackground(new Color(222, 121, 67));
            setForeground(Color.white);
        }
    }

    TimeFormatWindow(MenuWindow parent){
        windowParent = parent;
        setSize(500,500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3,3));
        setTitle("Choose time format");

        EventHandler handler = new EventHandler();
        String[] btnNames={"1+0", "2+1","3+0","5+0","10+0","10+5","15+10","30+0","Uncapped"};
//        PlainButton[] buttons = new PlainButton[btnNames.length];
        for (String btnName : btnNames) {
            PlainButton pb = new PlainButton(btnName);
            pb.addActionListener(handler);
            add(pb);
        }

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    }

    class EventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            JButton myButton = (JButton)event.getSource();
            windowParent.setTimeFormatLabel(myButton.getText());
            windowParent.controller.setTimeFormat(myButton.getText());
        }
    }

}

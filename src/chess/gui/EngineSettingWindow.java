package chess.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EngineSettingWindow extends BaseWindow {
    private final GradButton vsCompBttn = new GradButton();
    private GradButton enginesButton = new GradButton();
    private final MenuWindow parent;
    private final JTextField vsComputer = new JTextField();
    private JTextField engLvl1 = new JTextField();
    private JTextField engLvl2 = new JTextField();

    EngineSettingWindow(MenuWindow p){
        parent = p;
        setSize(400,150);
        setMaximumSize(new Dimension(400,150));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Engine Level");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        vsComputer.setHorizontalAlignment(SwingConstants.CENTER);
        engLvl1.setHorizontalAlignment(SwingConstants.CENTER);
        engLvl2.setHorizontalAlignment(SwingConstants.CENTER);

        vsCompBttn.setText("OK");
        enginesButton.setText("OK");

        setLayout(new GridLayout(2,3));
        add(new JLabel("Engine depth for\nPlayer vs Computer"));
        add(vsComputer);
        add(vsCompBttn);
        add(new JLabel("Engine depth for\nEngine vs Engine"));

        Box enginesPanel = Box.createHorizontalBox();
        enginesPanel.add(engLvl1);
        enginesPanel.add(engLvl2);

        EventHandler handler = new EventHandler();
        vsCompBttn.addActionListener(handler);
        enginesButton.addActionListener(handler);

        add(enginesPanel);
        add(enginesButton);

    }

    private boolean isInt(String s){
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Input String cannot be parsed to Integer.");
        }
        return false;
    }


    class EventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if(event.getSource()==vsCompBttn){
                if(isInt(vsComputer.getText()))
                    parent.setEngine(Integer.parseInt(vsComputer.getText()));
                else
                    vsComputer.setText("");
            }
            else if(event.getSource()==enginesButton){
                if(isInt(engLvl1.getText()) && isInt(engLvl2.getText())){
                    parent.setEngines(Integer.parseInt(engLvl1.getText()),
                            Integer.parseInt(engLvl2.getText()));
                }
                else{
                    if(!isInt(engLvl1.getText()))
                        engLvl1.setText("");
                    if(!isInt(engLvl2.getText()))
                        engLvl2.setText("");
                }
            }
        }
    }
}

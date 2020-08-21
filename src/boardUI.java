import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class boardUI {
    JFrame mainFrame;

    public boardUI(Game g){
        mainFrame = new JFrame("CLUEDO");
        mainFrame.setSize(600,600);
        JDialog setUpFrame = new JDialog(mainFrame, "Game Set Up");
        setUpFrame.setSize(275,120);


        JPanel p = new JPanel();
        JLabel noPlayersLabel = new JLabel("Number of Players");
        JTextField entry = new JTextField(4);
        JButton submit = new JButton("Go");

        JLabel errorText = new JLabel("Choose between 3, 4, 5 and 6 players.");
        errorText.setForeground(Color.RED);
        errorText.setVisible(false);

        //created this so user pressing enter does the same as pressing Go button
        ActionListener submitActionListener = e -> {
            int amountOfPlayers = 0;
            try {
                amountOfPlayers = Integer.parseInt(entry.getText());
                if(amountOfPlayers >= 3 && amountOfPlayers <= 6){
                    setUpFrame.dispose();
                    callSetUp(amountOfPlayers, g);

                }else{
                    errorText.setVisible(true);

                }
            }catch(NumberFormatException ex){
                errorText.setVisible(true);
            }
        };

        entry.addActionListener(submitActionListener);
        submit.addActionListener(submitActionListener);
        p.add(noPlayersLabel);
        p.add(entry);
        p.add(submit);
        p.add(errorText);
        setUpFrame.getContentPane().add(p);

        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setUpFrame.setVisible(true);

    }

    public void callSetUp(int i, Game g){
        g.setUp(i, this);
    }



}

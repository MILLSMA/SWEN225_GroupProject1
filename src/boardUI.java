import javax.swing.*;
import java.awt.event.*;

public class boardUI {

    public boardUI(Game g){
        JFrame ui = new JFrame("CLUEDO");
        ui.setSize(600,600);


        JPanel p = new JPanel();
        JLabel noPlayersLabel = new JLabel("Number of Players");
        JTextField entry = new JTextField(4);
        JButton submit = new JButton("Go");

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int amountOfPlayers = 0;
                try {
                    amountOfPlayers = Integer.parseInt(entry.getText());
                    if(amountOfPlayers >= 3 && amountOfPlayers <= 6){
                        g.setUp(amountOfPlayers);
                    }else{
                        System.out.println("Choose between 3, 4, 5 and 6 players.");
                    }
                    }catch(NumberFormatException ex){
                        System.out.println("Choose between 3, 4, 5 and 6 players.");
                    }
                }

        });
        p.add(noPlayersLabel);
        p.add(entry);
        p.add(submit);
        ui.getContentPane().add(p);

        ui.setVisible(true);


    }


}

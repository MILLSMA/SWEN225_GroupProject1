import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class CluedoView {
    static JFrame mainFrame;
    static Board board;
    static Canvas canvas = new Canvas();
    Border blackLineBorder = BorderFactory.createLineBorder(Color.black);

    public CluedoView(Game g){
        mainFrame = new JFrame("Cluedo");
        mainFrame.setSize(600,600);
        mainFrame.setLocationRelativeTo(null);

        JDialog setUpFrame = makeSetupFrame(g);

        mainFrame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel turnPanel = new JPanel();
        JPanel cardPanel = new JPanel();
        turnPanel.add(new Button("turnPanel"));
        cardPanel.add(new Button("cardPanel"));

        //creates the layout with the canvas taking up 80% of the height
        constraints.weightx = 1;
        constraints.weighty = 0.8;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        mainFrame.getContentPane().add(canvas, constraints);
        constraints.weighty = 0.20;
        constraints.gridwidth = 1;
        constraints.gridx = 1;
        constraints.gridy = 1;
        mainFrame.getContentPane().add(cardPanel, constraints);
        constraints.gridx = 2;
        constraints.gridy = 1;
        mainFrame.getContentPane().add(turnPanel, constraints);

        canvas.setBorder(blackLineBorder);
        turnPanel.setBorder(blackLineBorder);
        cardPanel.setBorder(blackLineBorder);

        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setUpFrame.setVisible(true);

    }

    private JDialog makeSetupFrame(Game g) {
        JDialog setUpFrame = new JDialog(mainFrame, "Game Set Up");
        setUpFrame.setSize(275,120);
        setUpFrame.setLocationRelativeTo(null);

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
                    g.setUp(amountOfPlayers, this);
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
        return setUpFrame;
    }

//    public void callSetUp(int i, Game g){
//        g.setUp(i, this);
//    }

    public static void createCanvas(Board board){
        CluedoView.board = board;
        canvas.revalidate();
        canvas.repaint();
    }

    public static class Canvas extends JPanel{
        public void paintComponent (Graphics g)
        {
            super.paintComponent(g);
            System.out.println(CluedoView.board == null);
            if(board != null) {
                System.out.println("repaint");
                int cellWidth = mainFrame.getWidth() / Board.COLS;
                int cellHeight = mainFrame.getHeight() / Board.ROWS;
                int widthCount = 0;
                int heightCount = 0;
                for (int xIndex = 0; xIndex < Board.ROWS; xIndex++) {
                    for (int yIndex = 0; yIndex < Board.COLS; yIndex++) {
                        if (board.board[xIndex][yIndex] == null) continue;
                        Cell cellToDraw = board.board[xIndex][yIndex];
                        g.setColor(cellToDraw.getColor());
                        g.drawRect(widthCount, heightCount, cellWidth, cellHeight);
                        widthCount += cellWidth;
                        System.out.println("drawn Rect");
                    }
                    heightCount += cellHeight;
                    widthCount = 0;
                }
            }
        }

        public void drawBoard(Graphics g){

        }
        public void updateBoard(){

        }
    }


}

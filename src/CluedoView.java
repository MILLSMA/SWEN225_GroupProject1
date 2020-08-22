import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class CluedoView {
    private static final int CELL_SIZE = 28;
    private static final int BOARD_HEIGHT = CELL_SIZE*Board.ROWS;
    private static final int BOARD_WIDTH = CELL_SIZE*Board.COLS;
    static JFrame mainFrame;
    static Canvas canvas;
    Border blackLineBorder = BorderFactory.createLineBorder(Color.black);

    public CluedoView(Game g){
        SwingUtilities.invokeLater(() -> init(g));
    }

    private void init(Game g){

        mainFrame = new JFrame("CLUEDO");
        mainFrame.setResizable(false);
        mainFrame.setSize(BOARD_WIDTH,BOARD_HEIGHT*5/4);
        JDialog setUpFrame = new JDialog(mainFrame, "Game Set Up");
        setUpFrame.setSize(275,120);
        mainFrame.setLocationRelativeTo(null);
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

        mainFrame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel turnPanel = new JPanel();
        JPanel cardPanel = new JPanel();
        canvas = new Canvas();
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
        cardPanel.setBorder(blackLineBorder);
        turnPanel.setBorder(blackLineBorder);

        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas.setVisible(true);
        setUpFrame.setVisible(true);

    }

    public void callSetUp(int i, Game g){
        g.setUp(i, this);
    }
    public static void createCanvas(Board board){
        canvas.board = board;
        canvas.drawBoard();
    }

    public class Canvas extends JPanel{
        private Board board;
        private int cellWidth, cellHeight;
        private HashMap<Cell, drawTile> tilesToDraw = new HashMap<>();

        public void paint(Graphics g) {
           // g.drawString("I am painting", 50, 50);
            //System.out.println("i am painting");
            for (drawTile tile : tilesToDraw.values()) {
                g.setColor(tile.tileColor);
                g.fillRect(tile.xPosition, tile.yPosition, cellWidth, cellHeight);
            }
        }

        public void drawBoard(){
            tilesToDraw.clear();
            cellWidth = this.getWidth() / Board.COLS;
            cellHeight = this.getHeight() / Board.ROWS;
            int widthCount = 0;
            int heightCount = 0;
            for (int xIndex = 0; xIndex < Board.ROWS; xIndex++) {
                for (int yIndex = 0; yIndex < Board.COLS; yIndex++) {
                    if (board.board[xIndex][yIndex] == null) continue;
                    Cell cellToDraw = board.board[xIndex][yIndex];
                    drawTile newTile = new drawTile(cellToDraw.getColor(), widthCount, heightCount, cellToDraw);
                    tilesToDraw.put(cellToDraw, newTile);
                    widthCount += cellWidth;
                }
                heightCount += cellHeight;
                widthCount = 0;
            }
            this.repaint();
        }
    }

    private class drawTile {
        Color tileColor;
        int xPosition, yPosition;
        Cell cell;

        drawTile(Color tc, int x, int y, Cell c){
            this.tileColor = tc;
            this.xPosition = x;
            this.yPosition = y;
            this.cell = c;
        }
    }

}

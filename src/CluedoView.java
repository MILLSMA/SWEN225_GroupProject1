import sun.security.util.Cache;

import javax.swing.*;
import javax.swing.border.Border;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class CluedoView {
    private static final int CELL_SIZE = 27;
    private static final int BOARD_HEIGHT = CELL_SIZE*Board.ROWS;
    private static final int BOARD_WIDTH = CELL_SIZE*Board.COLS;
    static JFrame mainFrame;
    static Canvas boardCanvas;
    Border blackLineBorder = BorderFactory.createLineBorder(Color.black);

    public CluedoView(Game g){
        SwingUtilities.invokeLater(() -> init(g));
    }

    private void init(Game g){
        mainFrame = new JFrame("Cluedo");
        mainFrame.setResizable(false);
        mainFrame.setSize(BOARD_WIDTH,BOARD_HEIGHT*5/4);

        createPlayerSelectionDialog(g, 1);
//        // Setup for number of players
//        JDialog setUpFrame = new JDialog(mainFrame, "Game Set Up");
//        setUpFrame.setSize(275,120);
//        mainFrame.setLocationRelativeTo(null);
//        setUpFrame.setLocationRelativeTo(null);
//
//        JPanel panel = new JPanel();
//        JTextField entry = new JTextField(5);
//        JButton submit = new JButton("Go");
//        JLabel errorText = new JLabel("Choose between 3, 4, 5 and 6 players.");
//        errorText.setForeground(Color.RED);
//        errorText.setVisible(false);
//
//        //created this so user pressing enter does the same as pressing Go button
//        ActionListener submitActionListener = e -> {
//            int amountOfPlayers;
//            try {
//                amountOfPlayers = Integer.parseInt(entry.getText());
//                if(amountOfPlayers >= 3 && amountOfPlayers <= 6){
//                    setUpFrame.dispose();
//                    g.setUp(amountOfPlayers);
//                }else{
//                    errorText.setVisible(true);
//
//                }
//            }catch(NumberFormatException ex){
//                errorText.setVisible(true);
//            }
//        };
//
//        entry.addActionListener(submitActionListener);
//        submit.addActionListener(submitActionListener);
//        panel.add(new JLabel("Number of players"));
//        panel.add(entry);
//        panel.add(submit);
//        panel.add(errorText);
//        setUpFrame.getContentPane().add(panel);

        mainFrame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel turnPanel = new JPanel();
        JPanel cardPanel = new JPanel();
        boardCanvas = new Canvas();
        turnPanel.add(new Button("turnPanel"));
        cardPanel.add(new Button("cardPanel"));

        //creates the layout with the canvas taking up 80% of the height
        constraints.weightx = 1;
        constraints.weighty = 0.8;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        mainFrame.getContentPane().add(boardCanvas, constraints);
        constraints.weighty = 0.20;
        constraints.gridwidth = 1;
        constraints.gridx = 1;
        constraints.gridy = 1;
        mainFrame.getContentPane().add(cardPanel, constraints);
        constraints.gridx = 2;
        constraints.gridy = 1;
        mainFrame.getContentPane().add(turnPanel, constraints);

        boardCanvas.setBorder(blackLineBorder);
        cardPanel.setBorder(blackLineBorder);
        turnPanel.setBorder(blackLineBorder);

        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        boardCanvas.setVisible(true);
//        setUpFrame.setVisible(true);

    }

    public static void createCanvas(Board board){
        boardCanvas.board = board;
        boardCanvas.drawBoard();
    }

    public static void createPlayerSelectionDialog(Game g, int number) {
        // Setup for number of players
        JDialog dialog = new JDialog(mainFrame, "Add Player");
        dialog.setSize(350,200);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6,2,0, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Add Player "+number));
        panel.add(new JLabel("Player Name"));
        JTextField entry = new JTextField();
        panel.add(entry);

        panel.add(new JLabel("Token"));
        JLabel errorText = new JLabel("Please choose a token.");
        errorText.setForeground(Color.RED);
        errorText.setVisible(false);
        panel.add(errorText);
        ButtonGroup tokens = new ButtonGroup();
        for (CharacterCard c : CharacterCard.values()) {
            JRadioButton rb = new JRadioButton();
            rb.setText(c.getName());
            if (g.isTokenTaken(c)) rb.setEnabled(false);
            tokens.add(rb);
            panel.add(rb);
        }

        JButton add = new JButton("Next Player");
        JButton start = new JButton("Add and Start");
        if (number < 3) start.setEnabled(false);
        if (number >= 6) add.setEnabled(false);

        ActionListener addAction = e -> {
            for (Enumeration<AbstractButton> buttons = tokens.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();
                if (button.isSelected()) {
                    g.createPlayer(entry.getText(), CharacterCard.getToken(button.getText()), true);
                    dialog.dispose();
                }
            }
            errorText.setVisible(true);
        };
        ActionListener startAction = e -> {
            for (Enumeration<AbstractButton> buttons = tokens.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();
                if (button.isSelected()) {
                    g.createPlayer(entry.getText(), CharacterCard.getToken(button.getText()), false);
                    g.startGame();
                    dialog.dispose();
                }
            }
            errorText.setVisible(true);
        };

        if (number < 6) entry.addActionListener(addAction);
        else entry.addActionListener(startAction);
        add.addActionListener(addAction);
        start.addActionListener(startAction);

        panel.add(start);
        panel.add(add);
        dialog.add(panel);
        dialog.setVisible(true);

        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    public class Canvas extends JPanel{
        private Board board;
        private int cellWidth, cellHeight;
        private HashMap<Cell, drawTile> tilesToDraw = new HashMap<>();

		//paints the board (not overly efficient)
		public void paint(Graphics g) {
			tilesToDraw.values().forEach(tile -> {
				g.setColor(tile.tileColor);
				g.fillRect(tile.xPosition, tile.yPosition, tile.cellWidth, tile.cellHeight);
			});
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
        /**
         * Gets the cell the user clicks on - allows the game to wait
         * for the uses response
         * @return - A promise of the cell the player clicks on
         */
        public CompletableFuture<Cell> getCell(){
            CompletableFuture<Cell> futureReturn = new CompletableFuture<>();
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    for (drawTile tile : tilesToDraw.values()) {
                        if (tile.getRect().contains(e.getPoint())) futureReturn.complete(tile.cell);
                    }
                }
            });
            return futureReturn;
        }
    }


	/**
	 * Class for drawing tiles on the canvas
	 */
	private class drawTile {
		Color tileColor;
		int xPosition, yPosition;
		int cellHeight, cellWidth;
		Rectangle rect;
		Cell cell;

		drawTile(Color tc, int x, int y, Cell c) {
			this.tileColor = tc;
			this.xPosition = x;
			this.yPosition = y;
			this.cell = c;
			rect = new Rectangle(xPosition, yPosition, cellWidth, cellHeight);
		}

		public Rectangle getRect() {
			return this.rect;
		}
	}

}

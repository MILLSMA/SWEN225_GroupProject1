import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
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

        mainFrame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel turnPanel = new JPanel();
        JPanel cardPanel = new JPanel();
        boardCanvas = new Canvas();
        boardCanvas.setSize(BOARD_WIDTH, BOARD_HEIGHT);
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
        constraints.weighty = 0.2;
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
    }

    public static void createCanvas(Board board){
        boardCanvas.addMouseMotionListener(boardCanvas);
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

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    public void displayPlayerInformation(Player p){
        JPanel turnPanel = (JPanel)mainFrame.getContentPane().getComponent(0);
        
    }

    public class Canvas extends JPanel implements MouseMotionListener{
        private Board board;
        protected int cellWidth, cellHeight;
        private drawTile lastHoveredTile;
        private Color lastColor;
        private HashMap<Integer, drawTile> tilesToDraw = new HashMap<>();

		//paints the board (not overly efficient)
		public void paint(Graphics g) {
			tilesToDraw.values().forEach(tile -> {
				g.setColor(tile.tileColor);
				g.fillRect(tile.xPosition, tile.yPosition, cellWidth, cellHeight);
				g.setColor(Color.LIGHT_GRAY);
                g.drawRect(tile.xPosition, tile.yPosition, cellWidth, cellHeight);
			});
		}

        /**
         * updates all the data structures needed to paint the board
         */
        public void drawBoard(){
            tilesToDraw.clear();
            cellWidth = this.getWidth() / Board.COLS + 1;
            cellHeight = this.getHeight() / Board.ROWS;
            int widthCount = 0;
            int heightCount = 0;
            for (int xIndex = 0; xIndex < Board.ROWS; xIndex++) {
                for (int yIndex = 0; yIndex < Board.COLS; yIndex++) {
                    if (board.board[xIndex][yIndex] == null) continue;
                    Cell cellToDraw = board.board[xIndex][yIndex];
                    drawTile newTile = new drawTile(cellToDraw.getColor(), widthCount, heightCount, cellToDraw);
                    tilesToDraw.put(cellToDraw.position.hashCode(), newTile);
                    widthCount += cellWidth;
                }
                heightCount += cellHeight;
                widthCount = 0;
            }
            this.repaint();
        }

        /**
         * updates the board, only slightly more efficient than draw board
         */
        public void updateBoard(){
            for (int xIndex = 0; xIndex < Board.ROWS; xIndex++) {
                for (int yIndex = 0; yIndex < Board.COLS; yIndex++) {
                    Position tilePos = new Position(xIndex,yIndex);
                    drawTile tileToUpdate = tilesToDraw.get(tilePos.hashCode());
                    Cell superCell = board.board[xIndex][yIndex];
                    if(superCell.getColor() != tileToUpdate.tileColor)
                       if(tileToUpdate != lastHoveredTile) tileToUpdate.tileColor = superCell.getColor();
                }
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
                        if (tile.getRect().contains(e.getPoint())) {
                            futureReturn.complete(tile.cell);
                        }
                    }
                }
            });
            return futureReturn;
        }

        private Position screenCoordToPos(int x, int y){
            int xpos = x / cellWidth;
            int ypos = y / cellHeight;
            return new Position(ypos, xpos);
        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            try {
                if (lastColor != null && lastHoveredTile != null) lastHoveredTile.tileColor = lastColor;

                Position pos = screenCoordToPos(e.getX(), e.getY());
                drawTile hoverTile = tilesToDraw.get(pos.hashCode());
                lastColor = hoverTile.tileColor;
                hoverTile.tileColor = new Color(57, 255, 20, 75);
                this.repaint();
                this.lastHoveredTile = hoverTile;
            }catch(NullPointerException exception){

            }
        }
    }


	/**
	 * Class for drawing tiles on the canvas
	 */
	private class drawTile {
		Color tileColor;
		int xPosition, yPosition;
		Rectangle rect;
		Cell cell;

		drawTile(Color tc, int x, int y, Cell c) {
			this.tileColor = tc;
			this.xPosition = x;
			this.yPosition = y;
			this.cell = c;
			rect = new Rectangle(xPosition, yPosition, CluedoView.boardCanvas.cellWidth, CluedoView.boardCanvas.cellHeight);
		}

		public Rectangle getRect() {
			return this.rect;
		}
	}

}

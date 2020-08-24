import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class CluedoView {
	static JFrame mainFrame;
	static Canvas canvas;
	Border blackLineBorder = BorderFactory.createLineBorder(Color.black);

	public CluedoView(Game g) {
		SwingUtilities.invokeLater(() -> init(g));
	}

	private void init(Game g) {

		mainFrame = new JFrame("CLUEDO");
		mainFrame.setResizable(false);
		mainFrame.setSize(600, 600);
		JDialog setUpFrame = new JDialog(mainFrame, "Game Set Up");
		setUpFrame.setSize(275, 120);
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
				if (amountOfPlayers >= 3 && amountOfPlayers <= 6) {
					setUpFrame.dispose();
					callSetUp(amountOfPlayers, g);
				} else {
					errorText.setVisible(true);

				}
			} catch (NumberFormatException ex) {
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

	public void callSetUp(int i, Game g) {
		g.setUp(i, this);
	}

	public static void createCanvas(Board board) {
		canvas.board = board;
		canvas.drawBoard();
	}

	/**
	 * Canvas to draw the board
	 */
	public class Canvas extends JPanel{
		private static final int ROWS = 25, COLS = 24;
		private Board board;
		private HashMap<Cell, drawTile> tilesToDraw = new HashMap<>();

		//paints the board (not overly efficient)
		public void paint(Graphics g) {
			tilesToDraw.values().forEach(tile -> {
				g.setColor(tile.tileColor);
				g.fillRect(tile.xPosition, tile.yPosition, tile.cellWidth, tile.cellHeight);
			});
		}

		/**
		 * sets up the datastructures for the paint method to paint the board
		 */
		public void drawBoard() {
			tilesToDraw.clear();
			//keeps the width and height of the cells relative to canvas size
			int cellWidth = this.getWidth() / COLS;
			int cellHeight = this.getHeight() / ROWS;
			int widthCount = 0;
			int heightCount = 0;
			//Creates tiles to be drawn based on their position in the board
			for (int xIndex = 0; xIndex < ROWS; xIndex++) {
				for (int yIndex = 0; yIndex < COLS; yIndex++) {
					if (board.board[xIndex][yIndex] == null) continue;
					Cell cellToDraw = board.board[xIndex][yIndex];
					drawTile newTile = new drawTile(cellToDraw.getColor(), cellWidth, cellHeight, widthCount, heightCount, cellToDraw);
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

		drawTile(Color tc, int cw, int ch, int x, int y, Cell c) {
			this.tileColor = tc;
			this.xPosition = x;
			this.yPosition = y;
			this.cell = c;
			this.cellHeight = ch;
			this.cellWidth = cw;
			rect = new Rectangle(xPosition, yPosition, cellWidth, cellHeight);
		}

		public Rectangle getRect() {
			return this.rect;
		}
	}

}

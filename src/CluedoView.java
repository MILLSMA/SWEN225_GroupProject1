import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class CluedoView {
    private static final int CELL_SIZE = 27;
    private static final int BOARD_HEIGHT = CELL_SIZE*Board.ROWS;
    private static final int BOARD_WIDTH = CELL_SIZE*Board.COLS;
    static JFrame mainFrame;
    static Canvas boardCanvas;
    static Border blackLineBorder = BorderFactory.createLineBorder(Color.black);

    static JPanel turnPanel = new JPanel();
    static JButton suggestionButton = new JButton("Suggestion");
    static JButton accusationButton = new JButton("Accusation");


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

        JPanel cardPanel = new JPanel();
        Dimension smallPanelDimensions = new Dimension((int)(mainFrame.getWidth() * 0.5), (int)(mainFrame.getHeight()*0.2));
        boardCanvas = new Canvas();
        turnPanel.add(new Button("turnPanel"));

        cardPanel.add(new Button("cardPanel"));

        //creates the layout with the canvas taking up 80% of the height
        constraints.weightx = 1;
        constraints.weighty = 0.7;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        mainFrame.getContentPane().add(boardCanvas, constraints);
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 0.5;
        mainFrame.getContentPane().add(cardPanel, constraints);
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.weightx = 0.5;
        cardPanel.setMaximumSize(smallPanelDimensions);
        turnPanel.setMaximumSize(smallPanelDimensions);
        mainFrame.getContentPane().add(turnPanel, constraints);

        boardCanvas.setBorder(blackLineBorder);
        cardPanel.setBorder(blackLineBorder);
        turnPanel.setBorder(blackLineBorder);

        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        boardCanvas.setVisible(true);
    }


    /**
     * displays options to a player if they have entered a room
     * @param g
     * @param p
     */
    public static void turnRoomFrame(Game g, Player p){
        suggestionButton.setEnabled(true);
        accusationButton.setEnabled(true);

        AtomicBoolean turn = new AtomicBoolean(false);

        suggestionButton.addActionListener(e -> {
            suggestionFrame(g, p, true);
            //g.makeSuggestion(p);
            System.out.println(p.getToken().getName() + " is making a suggestion");
            turn.set(true);
        });

        accusationButton.addActionListener(e -> {
            //g.makeAccusation(p);
            suggestionFrame(g, p, false);
            System.out.println(p.getToken().getName() + " is making an accusation");

        });

        while(!turn.get()){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void suggestionFrame(Game g, Player p, boolean suggestion) {
        suggestionButton.setEnabled(false);
        accusationButton.setEnabled(false);

        JDialog guessFrame = new JDialog(mainFrame, "Enter Guess");
        guessFrame.setSize(350,500);
        guessFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(14,2,0, 0));
        panel.add(new JLabel("Select Character"));

        JLabel charError = new JLabel("Please choose a character.");
        charError.setForeground(Color.RED);
        charError.setVisible(false);
        panel.add(charError);
        ButtonGroup characters = new ButtonGroup();
        for (CharacterCard c : CharacterCard.values()) {
            JRadioButton rb = new JRadioButton();
            rb.setText(c.getName());
            characters.add(rb);
            panel.add(rb);
        }

        panel.add(new JLabel("Select Weapon"));

        JLabel weaponError = new JLabel("Please choose a weapon.");
        weaponError.setForeground(Color.RED);
        weaponError.setVisible(false);
        panel.add(weaponError);
        ButtonGroup weapons = new ButtonGroup();
        for (WeaponCard c : WeaponCard.values()) {
            JRadioButton rb = new JRadioButton();
            rb.setText(c.getName());
            weapons.add(rb);
            panel.add(rb);
        }

        if(!suggestion)panel.add(new JLabel("Select Room"));

        JLabel roomError = new JLabel("Please choose a Room.");
        roomError.setForeground(Color.RED);
        roomError.setVisible(false);
        panel.add(roomError);
        ButtonGroup rooms = new ButtonGroup();
        for (RoomCard c : RoomCard.values()) {
            JRadioButton rb = new JRadioButton();
            rb.setText(c.getName());
            rooms.add(rb);
            if(!suggestion)panel.add(rb);
        }

        JButton add = new JButton("Submit");
        ActionListener startAction = e -> {

            AbstractButton selectChar = isSelectCard(charError, characters);
            if(selectChar == null) charError.setVisible(true);

            AbstractButton selectWeap = selectWeap = isSelectCard(weaponError, weapons);
            if(selectWeap == null) weaponError.setVisible(true);

            AbstractButton selectRoom=null;
            if(!suggestion) {
                selectRoom = isSelectCard(roomError, rooms);
                if (selectRoom == null) roomError.setVisible(true);
            }





            if(selectChar != null && selectWeap != null && (selectRoom != null || suggestion)){
                guessFrame.dispose();
                g.makeSuggestion(p, selectChar.getText(), selectWeap.getText());
            }

        };

        add.addActionListener(startAction);
        panel.add(add);



        guessFrame.add(panel);
        guessFrame.setVisible(true);

        guessFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    /**
     * checks a gets a selected option from given radio buttons
     * @param charError
     * @param characters
     * @return
     */
    private static AbstractButton isSelectCard(JLabel charError, ButtonGroup characters) {
        for (Enumeration<AbstractButton> buttons = characters.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                //System.out.println(button.getText());
                charError.setVisible(false);;
                return button;
            }
        }
        return null;
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

    public static void displayPlayerInformation(Player p, int moves){
        Font displayFont = new Font("Serif", Font.PLAIN, 14);
        System.out.println("displaying user information");
        JPanel turnPanel = (JPanel)mainFrame.getContentPane().getComponent(1);
        turnPanel.setLayout(new GridLayout(3,0));
        turnPanel.removeAll();
        turnPanel.invalidate();

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout());
        JLabel imageLabel = new JLabel(new ImageIcon(playerImage(p.getToken().getName())));
        namePanel.add(imageLabel);
        JLabel nameLabel = new JLabel(p.getToken().getName());
        nameLabel.setFont(displayFont);
        namePanel.add(nameLabel);
        namePanel.setBorder(blackLineBorder);

        JPanel movePanel = new JPanel();
        movePanel.setLayout(new FlowLayout());
        JLabel moveLabel = new JLabel("You may move " + moves + " tiles");
        movePanel.setFont(displayFont);
        movePanel.add(moveLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(blackLineBorder);
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(new JButton("Roll Dice"));
        suggestionButton.setVisible(true);
        accusationButton.setVisible(true);
        suggestionButton.setEnabled(false);
        accusationButton.setEnabled(false);
        buttonPanel.add(suggestionButton);
        buttonPanel.add(accusationButton);
        turnPanel.add(namePanel);
        turnPanel.add(movePanel);
        turnPanel.add(buttonPanel);
        namePanel.repaint();
        turnPanel.setVisible(true);
        turnPanel.revalidate();
    }

    private static BufferedImage playerImage(String playerName){
        BufferedImage image = null;
        String fileName = "Resources/" +  playerName.replace(' ', '_')+".png";
        try {
            image = ImageIO.read(new File(fileName));
        }catch(IOException e){
            CluedoView.showDialog(e.getMessage());
        }
        return image;
    }

    public static void showDialog(String message){
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainFrame, message));
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
			    g.setColor(tile.cell.getColor());
                g.fillRect(tile.xPosition, tile.yPosition, cellWidth, cellHeight);
				g.setColor(Color.LIGHT_GRAY);
                g.drawRect(tile.xPosition, tile.yPosition, cellWidth, cellHeight);
                if(tile.cell.getObject() instanceof CharacterCard) {
                    String playername = tile.cell.getObject().getName();
                    g.drawImage(playerImage(playername), tile.xPosition, tile.yPosition, null);
                }
            });
		}

        /**
         * updates all the data structures needed to paint the board
         */
        public void drawBoard(){
            tilesToDraw.clear();
            cellWidth = this.getWidth() / Board.COLS + 1;
            cellHeight = this.getHeight() / Board.ROWS -3;
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

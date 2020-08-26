import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    static JPanel turnPanel = new JPanel();
    static JButton suggestionButton = new JButton("Suggestion");
    static JButton accusationButton = new JButton("Accusation");

    static boolean nextTurn = true;


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

        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        boardCanvas.setVisible(true);
    }

    /**
     * displays options to a player if they have entered a room
     * @param g
     * @param p
     */
    public static void enableRoomButtons(Game g, Player p){
        suggestionButton.setEnabled(true);
        accusationButton.setEnabled(true);

        AtomicBoolean turn = new AtomicBoolean(false);

        // remove all action listeners
        for (ActionListener a : suggestionButton.getActionListeners()) {
            suggestionButton.removeActionListener(a);
        }
        suggestionButton.addActionListener(e -> {
            createGuessDialog(g, p, true);
            //g.makeSuggestion(p);
            //System.out.println(p.getToken().getName() + " is making a suggestion");
            turn.set(true);
        });

        // remove all action listeners
        for (ActionListener a : accusationButton.getActionListeners()) {
            accusationButton.removeActionListener(a);
        }
        accusationButton.addActionListener(e -> {
            //g.makeAccusation(p);
            createGuessDialog(g, p, false);
            System.out.println(p.getToken().getName() + " is making an accusation");

        });

        while(!nextTurn){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createGuessDialog(Game g, Player p, boolean suggestion) {
        suggestionButton.setEnabled(false);
        accusationButton.setEnabled(false);

        JDialog guessFrame = new JDialog(mainFrame, "Enter Guess");
        guessFrame.setSize(350,180);
        guessFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4,2,0, 0));
        if (suggestion) panel.setBorder(BorderFactory.createTitledBorder("New Suggestion"));
        else panel.setBorder(BorderFactory.createTitledBorder("New Accusation"));

        //CHARACTER
        panel.add(new JLabel("Select Character"));
        String[] characters = Arrays.stream(CharacterCard.values()).map(CharacterCard::getName).toArray(String[]::new);

        JComboBox<String> characterSelect = new JComboBox<>(characters);
        panel.add(characterSelect);

        //WEAPON
        panel.add(new JLabel("Select Weapon"));
        String[] weapons = Arrays.stream(WeaponCard.values()).map(WeaponCard::getName).toArray(String[]::new);

        JComboBox<String> weaponSelect = new JComboBox<>(weapons);
        panel.add(weaponSelect);

        //ROOM (ACCUSATION ONLY)

        JComboBox<String> roomSelect;
        panel.add(new JLabel("Select Room"));
        if(!suggestion){
            String[] rooms = Arrays.stream(RoomCard.values()).map(RoomCard::getName).toArray(String[]::new);

            roomSelect = new JComboBox<>(rooms);
        } else {
            roomSelect = new JComboBox<>();
            roomSelect.addItem(p.getLocation().getRoom().getName());
            roomSelect.setEnabled(false);

        }
        panel.add(roomSelect);

        JButton add = new JButton("Submit");
        ActionListener startAction = e -> {
            //do not need error for blank entry as default value always set
            guessFrame.dispose();
            if(suggestion) g.makeSuggestion(p,(String) characterSelect.getSelectedItem(), (String)weaponSelect.getSelectedItem());
            else g.makeAccusation(p, (String) characterSelect.getSelectedItem(), (String)weaponSelect.getSelectedItem(), (String)roomSelect.getSelectedItem() );
        };

        add.addActionListener(startAction);
        panel.add(add);



        guessFrame.add(panel);
        guessFrame.setVisible(true);

        guessFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    public static void createRefutationDialog(Game g, Player p, ArrayList<Card> cards, CardTriplet suggestion){
        JDialog refDialog = new JDialog(mainFrame, "Refutation");
        refDialog.setSize(350,200);
        refDialog.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5,1,0, 0));
        mainPanel.add(new JLabel("Suggestion: " + suggestion.toString()));
        mainPanel.add(new JLabel(p.getToken().getName() + " make refutation"));


        String[] cardList = new String[cards.size()];
        for(int i = 1; i <  cards.size(); i++){
            cardList[i] = cards.get(i).getName();
        }

        JComboBox<String> cardSelect = new JComboBox<>(cardList);

        JButton submitRefutation = new JButton(("Refute"));
        ActionListener refuteAction = e -> {
            refDialog.dispose();
        };
        submitRefutation.addActionListener(refuteAction);

        JButton toggleShow = new JButton("Reveal");
        ActionListener showAction = e -> {
            toggleShow.setVisible(false);
            submitRefutation.setVisible(true);
            cardSelect.setVisible(true);
        };
        toggleShow.addActionListener(showAction);

        mainPanel.add(toggleShow);
        toggleShow.setVisible(true);

        mainPanel.add(submitRefutation);
        submitRefutation.setVisible(false);

        mainPanel.add(cardSelect);
        cardSelect.setVisible(false);

        mainPanel.setVisible(true);
        refDialog.add(mainPanel);
        refDialog.setVisible(true);

        refDialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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

    public static void flagNextTurn(){
        nextTurn = true;
    }

    public static void resetNextTurn(){
        nextTurn = false;
    }

    public static boolean getNextTurn(){
        return nextTurn;
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

        JPanel movePanel = new JPanel();
        movePanel.setLayout(new FlowLayout());
        JLabel moveLabel = new JLabel("You may move " + moves + " tiles");
        movePanel.setFont(displayFont);
        movePanel.add(moveLabel);

        JPanel buttonPanel = new JPanel();
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
			    if(tile.cell.getRoom().getType().equals("Hallway")) {
                    g.drawImage(playerImage("hallway"), tile.xPosition, tile.yPosition, null);
                }else  if(tile.cell.getRoom().getType().equals("Wall")) {
                    g.drawImage(playerImage("wall"), tile.xPosition, tile.yPosition, null);
                }else if(tile.cell.getRoom().getType().equals("Door")){
                    g.drawImage(playerImage("door_" + doorDirection(tile.cell)), tile.xPosition, tile.yPosition, null);
                }
			    else if(RoomCard.getRooms().contains(tile.cell.getRoom().getCard())){
                    g.drawImage(playerImage("floorboard"), tile.xPosition, tile.yPosition, null);
                }else {
                    g.setColor(tile.cell.getColor());
                    g.fillRect(tile.xPosition, tile.yPosition, cellWidth, cellHeight);
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect(tile.xPosition, tile.yPosition, cellWidth, cellHeight);
                }
                if(tile.cell.getObject() instanceof CharacterCard) {
                    String playername = tile.cell.getObject().getName();
                    g.drawImage(playerImage(playername), tile.xPosition, tile.yPosition, null);
                }
            });
		}

		private String doorDirection(Cell door){
		    Cell above, left, right, below;
		    above = board.getNeighbourCell(door, Locatable.Direction.NORTH);
		    below = board.getNeighbourCell(door, Locatable.Direction.SOUTH);
            left = board.getNeighbourCell(door, Locatable.Direction.WEST);
            right = board.getNeighbourCell(door, Locatable.Direction.EAST);

            if(RoomCard.isProperRoom(above.getRoom()) && RoomCard.isProperRoom(below.getRoom())){
                if(RoomCard.isProperRoom(left.getRoom())) return "right";
                if(RoomCard.isProperRoom(right.getRoom())) return "left";
            }else if(RoomCard.isProperRoom(left.getRoom()) && RoomCard.isProperRoom(right.getRoom())){
                if(RoomCard.isProperRoom(above.getRoom())) return "down";
                if(RoomCard.isProperRoom(below.getRoom())) return "up";
            } else if(RoomCard.isProperRoom(below.getRoom())) return "up";
            return "down";

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

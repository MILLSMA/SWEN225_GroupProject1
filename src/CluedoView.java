import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

public class CluedoView {
    private static final int CELL_SIZE = 27;
    private static final int BOARD_HEIGHT = CELL_SIZE*Board.ROWS;
    private static final int BOARD_WIDTH = CELL_SIZE*Board.COLS;

    private static final JFrame mainFrame = new JFrame("Cluedo");
    private static Canvas boardCanvas;
    private static final JButton suggestionButton = new JButton("Suggest");
    private static final JButton accusationButton = new JButton("Accuse");
    private static final JButton endButton = new JButton("End Turn");
    private static final JLabel playerInfo = new JLabel();

    static boolean nextTurn = true;


    public CluedoView(Game g){
        SwingUtilities.invokeLater(() -> init(g));
    }

    private void init(Game g){
        mainFrame.setResizable(false);
        mainFrame.setSize(BOARD_WIDTH,BOARD_HEIGHT*5/4);
        createPlayerSelectionDialog(g, 1);

        mainFrame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JPanel turnPanel = new JPanel();
        JPanel cards = new JPanel();
        JScrollPane cardPanel = new JScrollPane(cards);
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
        constraints.weightx = 0.1;
        mainFrame.getContentPane().add(turnPanel, constraints);
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.weightx = 0.9;
        turnPanel.setMaximumSize(smallPanelDimensions);
        cardPanel.setMaximumSize(smallPanelDimensions);
        mainFrame.getContentPane().add(cardPanel, constraints);

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
        endButton.setEnabled(false);

        //AtomicBoolean turn = new AtomicBoolean(false);

        // remove all action listeners
        for (ActionListener a : suggestionButton.getActionListeners()) {
            suggestionButton.removeActionListener(a);
        }
        suggestionButton.addActionListener(e -> {
            createGuessDialog(g, p, true);
            //g.makeSuggestion(p);
            //System.out.println(p.getToken().getName() + " is making a suggestion");
            //turn.set(true);
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

    public static void createRefutationDialog(Game g, Player toReveal, ArrayList<Card> cards, CardTriplet suggestion, Player toReceive){
        JDialog refDialog = new JDialog(mainFrame, "ATTENTION " + toReveal.getToken().getName().toUpperCase());
        refDialog.setSize(250,300);
        refDialog.setLocationRelativeTo(null);

        int columns = cards.size();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5,1,0, 0));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.1;
        mainPanel.add(new JLabel(toReceive.getName() + " suggested this:"), gbc);

        JPanel suggestionCardDisplay = new JPanel();
        suggestionCardDisplay.setLayout(new GridLayout(1, 3,0,0));
        for (Card card : suggestion.getSet()) {
            suggestionCardDisplay.add(new JLabel(new ImageIcon(boardCanvas.playerImage(card.getName() + "_Card"))));
        }
        gbc.gridy = 1;
        gbc.weighty = 0.4;
        mainPanel.add(suggestionCardDisplay, gbc);

        gbc.gridy = 2;
        gbc.weighty = 0.1;
        mainPanel.add(new JLabel("Click a card below to refute with it"), gbc);
        JPanel refuteCardDisplay = new JPanel();
        refuteCardDisplay.setLayout(new GridLayout(1,columns,0,0));
        for (Card card : cards) {
            JLabel cardLabel = new JLabel(new ImageIcon(boardCanvas.playerImage(card.getName() + "_Card")));
            cardLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    refDialog.dispose();
                    displayCard(card, toReceive.getName());
                }
            });
            refuteCardDisplay.add(cardLabel);
        }
        gbc.gridy = 3;
        gbc.weighty = 1;
        mainPanel.add(refuteCardDisplay, gbc);
        //mainPanel.add(new JLabel("Suggestion: " + suggestion.toString()));


//        String[] cardList = cards.stream().map(Card::getName).toArray(String[]::new);
//        JComboBox<String> cardSelect = new JComboBox<>(cardList);
//
//        JButton button = new JButton((toReveal.getToken().getName() + ": push make refutation"));
//
//        ActionListener refuteAction = e -> {
//            refDialog.dispose();
//            displayCard((String) cardSelect.getSelectedItem(), toReceive.getToken().getName());
//        };
//
//        ActionListener display = e ->{
//            button.setText("Submit Refute");
//            button.addActionListener(refuteAction);
//            cardSelect.setVisible(true);
//
//
//        };
//
//        button.addActionListener(display);

       // mainPanel.add(cardSelect);
//        cardSelect.setVisible(false);

//        mainPanel.add(button);
//        button.setVisible(true);
//        refDialog.pack();
        mainPanel.setVisible(true);
        refDialog.add(mainPanel);
        refDialog.setVisible(true);

        refDialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    public static void displayCard(Card cardChosen, String playerName){
        JDialog cardDisplayDialog = new JDialog(mainFrame, "ATTENTION " + playerName.toUpperCase());
        cardDisplayDialog.setSize(300,200);
        cardDisplayDialog.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2,1,0, 0));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weighty = 0.1;
        JLabel label = new JLabel(playerName + " click the blank card to reveal it");
        mainPanel.add(label, gbc);

        gbc.gridy = 1;
        JLabel card = new JLabel(new ImageIcon(boardCanvas.playerImage(cardChosen.getName() + "_Card")));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                cardDisplayDialog.dispose();
                flagNextTurn();
            }
        });

        JLabel blankCard = new JLabel(new ImageIcon(boardCanvas.playerImage( "Blank_Card")));
        blankCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                blankCard.setIcon(new ImageIcon(boardCanvas.playerImage(cardChosen.getName() + "_Card")));
                blankCard.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        super.mouseReleased(e);
                        cardDisplayDialog.dispose();
                        flagNextTurn();
                    }
                });
                label.setText("Click again to close window");
                mainPanel.revalidate();
            }
        });
        mainPanel.add(blankCard, gbc);
        mainPanel.add(card);
        card.setVisible(false);

        mainPanel.setVisible(true);
        cardDisplayDialog.add(mainPanel);
        cardDisplayDialog.setVisible(true);

        cardDisplayDialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    }

    public static void noReveal(Game g, Player p){
        JDialog noRevealDialog = new JDialog(mainFrame, "ATTENTION " + p.getToken().getName());
        noRevealDialog.setSize(350,200);
        noRevealDialog.setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel();
        JLabel text = new JLabel("No cards were revealed");
        JPanel buttonPanel = new JPanel();
        JButton end = new JButton("End Turn");
        JButton acc = new JButton("Accuse");

        ActionListener endTurn = e ->{
            noRevealDialog.dispose();
            flagNextTurn();
        };
        end.addActionListener(endTurn);

        ActionListener accAction = e ->{
            noRevealDialog.dispose();
            createGuessDialog(g,p,false);
        };
        acc.addActionListener(accAction);

        buttonPanel.add(acc);
        buttonPanel.add(end);
        mainPanel.add(text);
        mainPanel.add(buttonPanel);
        noRevealDialog.add(mainPanel);
        mainPanel.setVisible(true);
        noRevealDialog.setVisible(true);
    }

    public static void gameOver(Player p, CardTriplet s){
        JDialog gameOverDialog = new JDialog(mainFrame, "GAME OVER");
        gameOverDialog.setSize(350,200);
        gameOverDialog.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Game over."));
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel gameOver = new JLabel("All players are out. The solution was: ");
        if(p != null){
            gameOver.setText(p.getName() + " has won! The solution was: ");
        }

        JPanel cardDisplay = new JPanel();
        cardDisplay.setLayout(new GridLayout(1, 3,0,0));
        for (Card card : s.getSet()) {
            BufferedImage image = boardCanvas.playerImage(card.getName() + "_Card");
            cardDisplay.add(new JLabel(new ImageIcon(image)));
        }

        JButton button = new JButton("Close");
        ActionListener endGame = e -> System.exit(0);

        button.addActionListener(endGame);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(gameOver, gbc);
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(cardDisplay, gbc);
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(button, gbc);

        mainPanel.setVisible(true);
        gameOverDialog.add(mainPanel);
        gameOverDialog.setVisible(true);

    }

    public static void playerOut(Player p){
        JDialog playerOutDialog = new JDialog(mainFrame, "ATTENTION " + p.getToken().getName());
        playerOutDialog.setSize(350,200);
        playerOutDialog.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        JLabel playerLabel = new JLabel(p.getToken().getName());
        JLabel text2 = new JLabel("Your accusation was incorrect, you are now out");

        JButton button = new JButton("Close");
        ActionListener closeFrame = e ->{
            playerOutDialog.dispose();
        };

        button.addActionListener(closeFrame);

        mainPanel.add(playerLabel);
        mainPanel.add(text2);
        mainPanel.add(button);

        mainPanel.setVisible(true);
        playerOutDialog.add(mainPanel);
        playerOutDialog.setVisible(true);


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
        JLabel errorText = new JLabel();
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
            if (entry.getText().isEmpty()) {
                errorText.setText("Please enter a name.");
                errorText.setVisible(true);
                return;
            }
            for (Enumeration<AbstractButton> buttons = tokens.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();
                if (button.isSelected()) {
                    g.createPlayer(entry.getText(), CharacterCard.getToken(button.getText()), true);
                    dialog.dispose();
                }
            }
            errorText.setText("Please choose a token.");
            errorText.setVisible(true);
        };
        ActionListener startAction = e -> {
            if (entry.getText().isEmpty()) {
                errorText.setText("Please enter a name.");
                errorText.setVisible(true);
                return;
            }
            for (Enumeration<AbstractButton> buttons = tokens.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();
                if (button.isSelected()) {
                    g.createPlayer(entry.getText(), CharacterCard.getToken(button.getText()), false);
                    g.startGame();
                    mainFrame.setVisible(true);
                    dialog.dispose();
                }
            }
            errorText.setText("Please choose a token.");
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

    public static void displayPlayerInformation(Player p, int moves){
        JPanel turnPanel = (JPanel)mainFrame.getContentPane().getComponent(1);
        turnPanel.setLayout(new GridLayout(2,0));
        turnPanel.removeAll();
        turnPanel.invalidate();

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.05;
        ImageIcon image = new ImageIcon(boardCanvas.playerImage(p.getToken().getName()));
        JLabel imageLabel = new JLabel(image);
        namePanel.add(imageLabel, gbc);
        gbc.gridx = 1;
        gbc.weighty = 0.2;
        JLabel nameLabel = new JLabel(p.getToken().getName());
        namePanel.add(nameLabel, gbc);
        gbc.gridx = 2;
        gbc.weighty = 0.75;
        playerInfo.setText("You may move " + moves + " tiles");
        namePanel.add(playerInfo, gbc);

        displayPlayerCards(p);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2,2,0,0));
        buttonPanel.add(new JButton("Roll Dice"));
        suggestionButton.setEnabled(false);
        accusationButton.setEnabled(false);
        endButton.setEnabled(true);
        buttonPanel.add(suggestionButton);
        buttonPanel.add(accusationButton);

        endButton.addActionListener(e -> {
            try {
                boardCanvas.cancelPromise();
            } catch (Exception ignored) {

            }
        });
        buttonPanel.add(endButton);

        turnPanel.add(namePanel);
        turnPanel.add(buttonPanel);
        namePanel.repaint();
        turnPanel.setVisible(true);
        turnPanel.revalidate();
    }

    private static void displayPlayerCards(Player p){
        JScrollPane cardPanel = (JScrollPane)mainFrame.getContentPane().getComponent(2);
        JPanel cards = (JPanel)cardPanel.getViewport().getComponent(0);
        cards.removeAll();

        cardPanel.invalidate();
        cardPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        cardPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        for (Card card : p.getCards()) {
            System.out.println(card.getName());
            cards.add(new JLabel(new ImageIcon(boardCanvas.playerImage(card.getName() + "_Card"))));
        }
        cardPanel.repaint();
        cardPanel.revalidate();
    }

    public static void changePlayerInfo(String text) {
        playerInfo.setText(text);
    }

    /**
     * displays a dialog box with the message in the parameter
     * @param message - string, message you want to display
     */
    public static void showDialog(String message){
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainFrame, message));
    }

    public static void updateBoard() {
        boardCanvas.updateBoard();
    }

    public static Cell getCell() {
        try {
            return boardCanvas.getCell().get();
        } catch (CancellationException e) {
            throw new CancellationException(e.getMessage());
        } catch (Exception e) {
            CluedoView.showDialog(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public class Canvas extends JPanel implements MouseMotionListener{
        private Board board;
        protected int cellWidth, cellHeight;
        private drawTile lastHoveredTile;
        private Color lastColor;
        private HashMap<Integer, drawTile> tilesToDraw = new HashMap<>();
        private CompletableFuture<Cell> promisedCell;

		//paints the board (not overly efficient)
		public void paint(Graphics g) {
			tilesToDraw.values().forEach(tile -> {
                g.drawImage(tile.image, tile.xPosition, tile.yPosition, null);
                if(tile.cell.getObject() instanceof CharacterCard || tile.cell.getObject() instanceof WeaponCard) {
                    String CardName = tile.cell.getObject().getName();
                    g.drawImage(playerImage(CardName), tile.xPosition, tile.yPosition, null);
                }

            });
		}

        /**
         * gets the image that corresponds to the name given
         * @param playerName - name of image excluding extension
         * @return - buffered image
         */
        private BufferedImage playerImage(String playerName){
            BufferedImage image = null;
            String fileName = "Resources/" +  playerName.replace(' ', '_')+".png";
            try {
                image = ImageIO.read(new File(fileName));
            }catch(IOException e){
                CluedoView.showDialog(e.getMessage());
            }
            return image;
        }

        /**
         * returns to the image that belongs to the given cell
         * @param cell - Cell of image you want to receive
         * @return - image of the cell to display
         */
        private BufferedImage cellImage(Cell cell){
            String name;
            if(cell.getRoom().isWall()) name = "wall";
            else if(cell.getRoom().isHallway()) name = "hallway";
            else if(cell.getRoom().isDoor()) name = "door " + doorDirection(cell);
            else name = "floorboard";
            return playerImage(name);
        }

        /**
         * gets the direction the door is supposed to facing, for viewing purposes
         * @param door - cell that is a door
         * @return
         */
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
                    newTile.setImage(cellImage(cellToDraw));
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
            promisedCell = new CompletableFuture<>();
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    for (drawTile tile : tilesToDraw.values()) {
                        if (tile.getRect().contains(e.getPoint())) {
                            promisedCell.complete(tile.cell);
                        }
                    }
                }
            });
            return promisedCell;
        }

        public void cancelPromise() {
            try {
                promisedCell.cancel(true);
            } catch (Exception ignored) {
            }
        }

        private Position screenCoordToPos(int x, int y){
            int xPos = x / cellWidth;
            int yPos = y / cellHeight;
            return new Position(yPos, xPos);
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
		BufferedImage image;
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

        public void setImage(BufferedImage image) {
            this.image = image;
        }

        public Rectangle getRect() {
			return this.rect;
		}
	}

}

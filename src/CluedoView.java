import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

/**
 * The "view" component of the MVC model. Stores how the model is visually conveyed to user.
 */
public class CluedoView {
    private static final int CELL_SIZE = 27;
    private static final int BOARD_HEIGHT = CELL_SIZE*Board.ROWS;
    private static final int BOARD_WIDTH = CELL_SIZE*Board.COLS;

    private static final JFrame mainFrame = new JFrame("Cluedo");
    private static BoardView boardView;
    private static final JButton suggestionButton = new JButton("Suggest");
    private static final JButton accusationButton = new JButton("Accuse");
    private static final JButton endButton = new JButton("End Turn");
    private static final JLabel playerInfo = new JLabel();
    private static JPanel dicePanel;
    private static boolean nextTurn = true;

    /**
     * Constructor sets up main components of the main window (mainFrame):
     * - board (boardCanvas)
     * - player information (turnPanel)
     * - player hand (cardPanel)
     * @param g Game model, needed for player creation
     */
    public CluedoView(Game g){
        SwingUtilities.invokeLater(() -> {
            mainFrame.setResizable(false);
            mainFrame.setSize(BOARD_WIDTH,BOARD_HEIGHT*5/4);
            createPlayerSelectionDialog(g, 1);
            mainFrame.setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();

            boardView = new BoardView();
            // make tooltip quicker to show
            ToolTipManager.sharedInstance().setInitialDelay(50);
            ToolTipManager.sharedInstance().setDismissDelay(4000);
            JPanel turnPanel = new JPanel();
            JPanel cards = new JPanel();
            JScrollPane cardPanel = new JScrollPane(cards);
            cardPanel.setBorder(BorderFactory.createEmptyBorder());
            Dimension smallPanelDimensions = new Dimension(mainFrame.getWidth()/2, mainFrame.getHeight()/5);
            turnPanel.add(new Button("turnPanel"));
            cardPanel.add(new Button("cardPanel"));

            //creates the layout with the canvas taking up 80% of the height
            constraints.weightx = 1;
            constraints.weighty = 0.7;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            mainFrame.getContentPane().add(boardView, constraints);

            constraints.gridwidth = 1;
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.weightx = 1;
            constraints.weighty = 0;
            turnPanel.setSize(smallPanelDimensions);
            mainFrame.getContentPane().add(turnPanel, constraints);

            constraints.gridx = 1;
            constraints.weightx = 0;
            constraints.ipadx = 0;
            cardPanel.setSize(smallPanelDimensions);
            mainFrame.getContentPane().add(cardPanel, constraints);

            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            boardView.addMouseMotionListener(boardView);
            boardView.setVisible(true);
        });
    }

    /**
     * Connects board model to view
     * @param board board model
     */
    public static void setBoard(Board board){
        boardView.drawBoard(board);
    }

    /**
     * Set up and show dialog setting up player n's name and token
     * @param g game model
     * @param number how many players there will be after this dialog completes
     */
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

    /**
     * Enable suggestion/accusation buttons when user enters room, disable end turn button.
     * Modifies enabled buttons to handle the given player.
     * @param g Game model
     * @param p Player that entered room
     */
    public static void enableRoomButtons(Game g, Player p){
        suggestionButton.setEnabled(true);
        accusationButton.setEnabled(true);
        endButton.setEnabled(false);

        // remove all action listeners
        for (ActionListener a : suggestionButton.getActionListeners()) {
            suggestionButton.removeActionListener(a);
        }
        suggestionButton.addActionListener(e -> createGuessDialog(g, p, true));

        // remove all action listeners
        for (ActionListener a : accusationButton.getActionListeners()) {
            accusationButton.removeActionListener(a);
        }
        accusationButton.addActionListener(e -> createGuessDialog(g, p, false));

        while(!nextTurn){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets up and shows dialog handling a player guess (i.e. suggestion/accusation).
     * @param g Game model
     * @param p player doing the guess
     * @param isSuggestion true if a suggestion
     */
    public static void createGuessDialog(Game g, Player p, boolean isSuggestion) {
        suggestionButton.setEnabled(false);
        accusationButton.setEnabled(false);

        JDialog guessFrame = new JDialog(mainFrame, "Enter Guess");
        guessFrame.setSize(350,180);
        guessFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4,2,0, 0));
        if (isSuggestion) panel.setBorder(BorderFactory.createTitledBorder("New Suggestion"));
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
        if(!isSuggestion){
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
            if(isSuggestion) g.makeSuggestion(p,(String) characterSelect.getSelectedItem(), (String)weaponSelect.getSelectedItem());
            else g.makeAccusation(p, (String) characterSelect.getSelectedItem(), (String)weaponSelect.getSelectedItem(), (String)roomSelect.getSelectedItem(), null );
        };

        add.addActionListener(startAction);
        panel.add(add);

        guessFrame.add(panel);
        guessFrame.setVisible(true);

        guessFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Sets up and shows dialog handling a player's refutation of a given suggestion.
     * @param toReveal player refuting
     * @param cards cards player may refute the suggestion with
     * @param suggestion card triplet of what was suggested
     * @param toReceive player suggesting
     */
    public static void createRefutationDialog(Player toReveal, ArrayList<Card> cards, CardTriplet suggestion, Player toReceive){
        JDialog refDialog = new JDialog(mainFrame, "Refutations");
        refDialog.setSize(250,300);
        refDialog.setLocationRelativeTo(null);

        int columns = cards.size();

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createTitledBorder("ATTENTION " + toReveal.getToken().getName().toUpperCase()));
        mainPanel.setLayout(new GridLayout(5,1,0, 0));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.1;
        mainPanel.add(new JLabel(toReceive.getName() + " suggested:"), gbc);

        JPanel suggestionCardDisplay = new JPanel();
        suggestionCardDisplay.setLayout(new GridLayout(1, 3,0,0));
        for (Card card : suggestion.getList()) {
            suggestionCardDisplay.add(new JLabel(new ImageIcon(boardView.playerImage(card.getName() + "_Card"))));
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
            JLabel cardLabel = new JLabel(new ImageIcon(boardView.playerImage(card.getName() + "_Card")));
            cardLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    refDialog.dispose();
                    revealRefutation(card, toReceive.getName());
                }
            });
            refuteCardDisplay.add(cardLabel);
        }
        gbc.gridy = 3;
        gbc.weighty = 1;
        mainPanel.add(refuteCardDisplay, gbc);
        mainPanel.setVisible(true);
        refDialog.add(mainPanel);
        refDialog.setVisible(true);

        refDialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Shows dialog with card that refutes player's suggestion
     * @param cardChosen refuted card
     * @param playerName suggesting player's name
     */
    public static void revealRefutation(Card cardChosen, String playerName){
        JDialog cardDisplayDialog = new JDialog(mainFrame, "Refutation Result");
        cardDisplayDialog.setSize(300,200);
        cardDisplayDialog.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createTitledBorder("ATTENTION " + playerName.toUpperCase()));
        mainPanel.setLayout(new GridLayout(2,1,0, 0));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weighty = 0.1;
        JLabel label = new JLabel("Click the blank card to reveal it");
        mainPanel.add(label, gbc);

        gbc.gridy = 1;
        JLabel card = new JLabel(new ImageIcon(boardView.playerImage(cardChosen.getName() + "_Card")));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                cardDisplayDialog.dispose();
                flagNextTurn();
            }
        });

        JLabel blankCard = new JLabel(new ImageIcon(boardView.playerImage("Blank_Card")));
        blankCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                blankCard.setIcon(new ImageIcon(boardView.playerImage(cardChosen.getName() + "_Card")));
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

    /**
     * Set up and show dialog conveying to player that no refutation could be made.
     * Allows them to make an accusation.
     * @param g Game model
     * @param p suggesting player
     */
    public static void noRefutation(Game g, Player p){
        JDialog noRevealDialog = new JDialog(mainFrame, "Unsuccessful Suggestion");
        noRevealDialog.setSize(350,200);
        noRevealDialog.setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createTitledBorder("ATTENTION " + p.getToken().getName()));
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

    /**
     * Set up and show dialog conveying to player that the game is finished.
     * If a player is given, that means they won the game. If null, all lost.
     * @param p winning player, should be null if lost
     * @param s solution from Game.envelope
     */
    public static void gameOver(Player p, CardTriplet s){
        JDialog gameOverDialog = new JDialog(mainFrame, "GAME OVER");
        gameOverDialog.setSize(350,200);
        gameOverDialog.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Game over"));
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel gameOver = new JLabel("All players are out. The solution was: ");
        if(p != null){
            gameOver.setText(p.getName() + " has won! The solution was: ");
        }

        JPanel cardDisplay = new JPanel();
        cardDisplay.setLayout(new GridLayout(1, 3,0,0));
        for (Card card : s.getList()) {
            BufferedImage image = boardView.playerImage(card.getName() + "_Card");
            cardDisplay.add(new JLabel(new ImageIcon(image)));
        }

        JButton button = new JButton("Close");
        ActionListener endGame = e -> System.exit(0);

        button.addActionListener(endGame);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5,5,5,5);
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

    /**
     * Set up and show dialog conveying to player that an incorrectly accusing player is now out.
     * @param p player now out
     */
    public static void playerOut(Player p){
        JDialog playerOutDialog = new JDialog(mainFrame, "ATTENTION " + p.getToken().getName());
        playerOutDialog.setSize(350,200);
        playerOutDialog.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        JLabel playerLabel = new JLabel(p.getToken().getName());
        JLabel text2 = new JLabel("Your accusation was incorrect, you are now out");

        JButton button = new JButton("Close");
        ActionListener closeFrame = e -> playerOutDialog.dispose();

        button.addActionListener(closeFrame);

        mainPanel.add(playerLabel);
        mainPanel.add(text2);
        mainPanel.add(button);

        mainPanel.setVisible(true);
        playerOutDialog.add(mainPanel);
        playerOutDialog.setVisible(true);
    }

    /**
     * Sends signal to view that current player's turn is over
     */
    public static void flagNextTurn(){
        nextTurn = true;
    }

    /**
     * Clears nextTurn signal
     */
    public static void resetNextTurn(){
        nextTurn = false;
    }

    /**
     * Sets up and shows panel with player information
     * - player icon/name
     * - dice roll
     * - suggest/accuse/end turn buttons
     * @param p player whose turn it currently is
     * @param moveNumber how many moves they can play
     * @param diceRollPromise async container filled when dice is rolled
     * @return filled container with dice roll
     */
    public static CompletableFuture<Integer> displayPlayerInformation(Player p, int moveNumber, CompletableFuture<Integer> diceRollPromise){
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
        gbc.gridheight = 2;
        gbc.insets = new Insets(5,5,5,5); // padding
        ImageIcon image = new ImageIcon(boardView.playerImage(p.getToken().getName()));
        JLabel imageLabel = new JLabel(image);
        namePanel.add(imageLabel, gbc);
        gbc.gridx = 1;
        gbc.gridheight = 1;
        JLabel nameLabel = new JLabel(p.getToken().getName());
        namePanel.add(nameLabel, gbc);
        gbc.gridy = 1;
        namePanel.add(playerInfo, gbc);

        if(moveNumber == 0){
            playerInfo.setText("Click the dice to roll");
            endButton.setEnabled(false);
            rollDice(diceRollPromise);
        }else{
            playerInfo.setText("You can move " + moveNumber + " more tiles");
            endButton.setEnabled(true);
            diceRollPromise.complete(moveNumber);
        }
        displayPlayerCards(p);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2,2,0,0));
        buttonPanel.add(dicePanel);

        endButton.addActionListener(e -> {
            try {
                boardView.cancelPromise();
            } catch (Exception ignored) {
            }
        });
        buttonPanel.add(endButton);

        suggestionButton.setEnabled(false);
        accusationButton.setEnabled(false);
        buttonPanel.add(suggestionButton);
        buttonPanel.add(accusationButton);

        turnPanel.add(namePanel);
        turnPanel.add(buttonPanel);
        namePanel.repaint();
        turnPanel.setVisible(true);
        turnPanel.revalidate();

        return diceRollPromise;
    }

    /**
     * Wait for a dice roll and fill the diceRollPromise container.
     * @param diceRollPromise async container filled when dice is rolled
     */
    private static void rollDice(CompletableFuture<Integer> diceRollPromise){
        dicePanel = new JPanel();
        dicePanel.setLayout(new GridLayout(1,2));
        JLabel firstDice = new JLabel(new ImageIcon(boardView.playerImage("Dice_Blank")));
        JLabel secondDice = new JLabel(new ImageIcon(boardView.playerImage("Dice_Blank")));
        dicePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int firstDiceRoll = new Random().nextInt(6) + 1;
                int secondDiceRoll = new Random().nextInt(6) + 1;
                int moveNumber = firstDiceRoll + secondDiceRoll;

                firstDice.setIcon(new ImageIcon(boardView.playerImage("Dice_" + firstDiceRoll)));
                secondDice.setIcon(new ImageIcon(boardView.playerImage("Dice_" + secondDiceRoll)));
                playerInfo.setText("You can move " + moveNumber + " tiles");
                endButton.setEnabled(true);
                dicePanel.removeMouseListener(this);
                diceRollPromise.complete(moveNumber);
            }
        });
        dicePanel.add(firstDice);
        dicePanel.add(secondDice);
    }

    /**
     * Displays player's hand in card panel.
     * @param p player whose turn it currently is
     */
    private static void displayPlayerCards(Player p){
        JScrollPane cardPanel = (JScrollPane)mainFrame.getContentPane().getComponent(2);
        JPanel cards = (JPanel)cardPanel.getViewport().getComponent(0);
        cards.removeAll();

        cardPanel.invalidate();
        cardPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        cardPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        for (Card card : p.getCards()) {
            cards.add(new JLabel(new ImageIcon(boardView.playerImage(card.getName() + "_Card"))));
        }
        cardPanel.repaint();
        cardPanel.revalidate();
    }

    /**
     * Modify text shown in player information. For use outside of the view.
     * @param text message to show
     */
    public static void changePlayerInfo(String text) {
        playerInfo.setText(text);
    }

    /**
     * Displays a dialog box with the message in the parameter
     * @param message - string, message you want to display
     */
    public static void showDialog(String message){
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainFrame, message));
    }

    /**
     * Redraws board canvas
     */
    public static void updateBoard() {
        boardView.updateBoard();
    }

    /**
     * Retrieves cell based on mouse click
     * @return clicked cell
     */
    public static Cell getCell() {
        try {
            return boardView.getCell().get();
        } catch (CancellationException e) {
            throw new CancellationException(e.getMessage());
        } catch (Exception e) {
            CluedoView.showDialog(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}

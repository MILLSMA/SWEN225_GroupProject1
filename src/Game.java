import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Game {

	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Game Attributes
	private boolean won;
	//Game Associations
	private List<Player> players;
	private Board board;
	private CardTriplet envelope;
	private Scanner input;


	//------------------------
	// STATIC INITIALISATION METHODS
	//------------------------

	public static void main(String... args) {
		Game currentGame = new Game();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final boardUI GUI = new boardUI(currentGame);
			}
		});


	}

	public Board getBoard() {
		return this.board;
	}

	//------------------------
	// CONSTRUCTOR
	//------------------------

	//private Game() {

	//}

	/**
	 * set up a new game with given number of players
	 *
	 * @param amountOfPlayers: no. players in this game
	 */
	public void setUp(int amountOfPlayers, boardUI boardUI) {
		//input = new Scanner(System.in);
		//int amountOfPlayers = 0;
		// Ask users for an integer between 3 and 6, repeat until valid integer received
		/*do {
			System.out.print("How many players will be participating? (3 - 6): ");
			try {
				amountOfPlayers = input.nextInt();
			}catch(InputMismatchException e){
				System.out.println("Choose between 3, 4, 5 and 6 players.");
				input.nextLine();
			}
		} while (amountOfPlayers < 3 || amountOfPlayers > 6);*/

		players = createPlayers(amountOfPlayers);
		board = new Board(players);
		won = false;
		System.out.println("== Players in this Game == ");
		for (int i = 0; i < players.size(); i++) {
			System.out.println("Player " + (i + 1) + ": " + players.get(i).getToken().getName());
		}
		//collect all the cards for dealing
		List<Card> allCards = new ArrayList<>();
		allCards.addAll(CharacterCard.getCharacters());
		allCards.addAll(WeaponCard.getWeapons());
		allCards.addAll(RoomCard.getRooms());
		envelope = decideSolution(allCards);
		dealCards(allCards);
		runGame();
	}

	/**
	 * Creates a new player object for each player in the game
	 *
	 * @param numPlayers: number of players in the came
	 * @return : List of player objects
	 */
	private List<Player> createPlayers(int numPlayers) {
		//creates an arraylist for the new characters to be stored
		ArrayList<Player> tempPlayerList = new ArrayList<>(6);
		//creates an arrayList with all of the characters in the Character enum
		ArrayList<CharacterCard> characters = new ArrayList<>(CharacterCard.getCharacters());
		//creates a defined number of players with random Character card assigned
		for (int index = 0; index < numPlayers; index++) {
			int randomCardIndex = new Random().nextInt(characters.size());
			CharacterCard randomCharacter = characters.get(randomCardIndex);
			Player tempPlayer = new Player(randomCharacter, null, false);
			tempPlayerList.add(tempPlayer);
			characters.remove(randomCardIndex);
		}
		return tempPlayerList;
	}

	/**
	 * Deals deck of cards to players
	 *
	 * @param cards collection of all cards that aren't the solution
	 */
	private void dealCards(Collection<Card> cards) {
		Random rand = new Random();
		ArrayList<Card> tempCardBag = new ArrayList<>(cards);
		//decideSolution(tempCardBag);
		System.out.println("The solution is: " + envelope);
		tempCardBag.removeAll(envelope.getSet());

		//deal rest of the cards to the players
		while (!tempCardBag.isEmpty()) {
			for (Player player : players) {
				if (tempCardBag.isEmpty()) continue;
				int cardIndex = rand.nextInt(tempCardBag.size());
				player.addCard(tempCardBag.get(cardIndex));
				tempCardBag.remove(cardIndex);
			}
		}
	}

	/**
	 * Selects a random room, weapon and character card from deck to set as solution
	 *
	 * @param cards : all the possible cards
	 */
	private CardTriplet decideSolution(Collection<Card> cards) {
		if (envelope != null) throw new RuntimeException("Solution already decided.");
		Random rand = new Random();
		CharacterCard envelopeCharacter = (CharacterCard) cards.stream()
				.filter(card -> card instanceof CharacterCard)
				.skip(rand.nextInt(CharacterCard.size()))
				.findAny().orElse(null);
		WeaponCard envelopeWeapon = (WeaponCard) cards.stream()
				.filter(card -> card instanceof WeaponCard)
				.skip(rand.nextInt(WeaponCard.size()))
				.findAny().orElse(null);
		RoomCard envelopeRoom = (RoomCard) cards.stream()
				.filter(card -> card instanceof RoomCard)
				.skip(rand.nextInt(RoomCard.size()))
				.findAny().orElse(null);
		return new CardTriplet(envelopeCharacter, envelopeWeapon, envelopeRoom);
	}

	/**
	 * Roll two six-sided die
	 *
	 * @return int : sum of die
	 */
	private int rollDice() {
		Random rand = new Random();
		int firstDice = rand.nextInt(6) + 1;
		int secondDice = rand.nextInt(6) + 1;
		return firstDice + secondDice;
	}

	private void runGame() {
		boardUI.createCanvas(board);
		//This legitimately took hours too figure out, basically swing is a bastard and
		//you have to do weird as shit to get it to update properly. just dont fuck with it plz <3
		new Thread(() -> {
			while (!won || !allPlayersOut()) {

				SwingUtilities.invokeLater(() -> {
					Timer t = new Timer(1000, new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e) {
							boardUI.canvas.drawBoard();
						}
					});
					t.start();
				});
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
				for (Player player : players) {
					if (!won && !player.getExcluded()) doTurn(player);
				}
			}
			if (allPlayersOut()) {
				System.out.println("All Players are out!");
			}
		}).start();
	}

	/**
	 * check if all players are out of the game
	 *
	 * @return boolean: true if all out
	 */
	private boolean allPlayersOut() {
		for (Player p : players) {
			if (!p.getExcluded()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * main method for a single turn
	 *
	 * @param p : the player making the turn
	 */
	private void doTurn(Player p) {
		//place holder code
		System.out.println("\n== " + p.getToken().getName() + "'s turn ==");
		move(p);

		if (p.getLocation().getRoom().isProperRoom()) {
			System.out.println("You've entered the " + p.getLocation().getRoom().getName());
			Room currentRoom = findRoom(p);
			p.setCell(currentRoom.findEmptyCell());
			turnEntry(p);
		}

	}

	/**
	 * retrieves and validates turn entry by player
	 *
	 * @param p : player whose turn it is
	 */
	private void turnEntry(Player p) {
		Scanner sc = new Scanner(System.in);
		String turnEntry;
		do {
			System.out.print("Accusation (A) | Suggestion (S) | View Cards (C): ");
			turnEntry = sc.next();
		} while (!(turnEntry.matches("(?i)a|s|c|accusation|suggestion|view cards|")));

		if (turnEntry.matches("(?i)a|accusation")) {
			makeAccusation(p);
		} else if (turnEntry.matches("(?i)s|suggestion")) {
			makeSuggestion(p);
		} else {
			p.displayHand();
			turnEntry(p);
		}
	}

	/**
	 * Rolls the dice, controls a player's moves
	 *
	 * @param p : the player moving
	 */
	private void move(Player p) {
		if (p.getLocation().getRoom().isProperRoom()) {
			System.out.println("You are currently in the " + p.getLocation().getRoom().getName());
			System.out.println("You may move anywhere in your current room, " +
					"your turn will start once you exit.");
			while (true) {
				//prints the board to the screen
				System.out.println(board);
				Cell.Direction chosenDirection = Cell.Direction.input(p, board);
				if (chosenDirection == null) return;
				Cell oldPlayerCell = p.getLocation();
				Cell newPlayerCell = board.move(p, chosenDirection);
				p.setCell(newPlayerCell);
				if (newPlayerCell.getRoom().getCard() != oldPlayerCell.getRoom().getCard()) break;
			}
		}
		ArrayList<Cell> cellsMovedTo = new ArrayList<>();
		int numberOfMoves = rollDice();
		System.out.println("You rolled " + numberOfMoves + ". You may move " + numberOfMoves + " spaces");
		for (int moveNumber = 0; moveNumber < numberOfMoves; moveNumber++) {
			//prints the board to the screen
			System.out.println(board);
			//displays whose turn it is and how many moves they have left
			System.out.println(p.getToken().getName() + " you have " + (numberOfMoves - moveNumber) + " moves left");
			System.out.println("You may move in these directions: ");
			Cell.Direction chosenDirection = Cell.Direction.input(p, board);
			if (chosenDirection == null) break;
			//moves the player on the board based on their answer
			Cell newPlayerCell = board.move(p, chosenDirection);
			p.setCell(newPlayerCell);
			cellsMovedTo.add(newPlayerCell);
			newPlayerCell.setUsedInRound(true);
			if (p.getLocation().getRoom().isProperRoom()) break;
		}
		for (Cell cell : cellsMovedTo) {
			cell.setUsedInRound(false);
		}
	}

	/**
	 * checks for adjacent rooms cells if player is in doorway.
	 *
	 * @param p player to check
	 */
	private Room findRoom(Player p) {
		System.out.println(p.getLocation().getRoom().getRoomSize());
		if (p.getLocation().getRoom().getType().equals("Door")) {//in doorway
			return board.checkSurroundingCells(p);
		} else if (p.getLocation().getRoom().getRoomSize() > 1) {//inside room
			return p.getLocation().getRoom();
		}
		throw new RuntimeException("There should be a room near here!");
	}

	/**
	 * controls suggestion input
	 *
	 * @param p: player whose turn it is
	 */
	private void makeSuggestion(Player p) {
		System.out.println("Making a suggestion here");
		CardTriplet guess = new CardTriplet(p.getLocation().getRoom().getCard());
		System.out.println("Suggestion is: " + guess);

		//character and weapon tokens move to room
		Room currentRoom = findRoom(p);
		Cell newCell = currentRoom.addCard(guess.getCharacter());
		currentRoom.addCard(guess.getWeapon());
		moveSuggestedPlayer(guess.getCharacter(), newCell);

		boolean refuted = doRefutations(p, guess);
		if (!refuted) {
			System.out.println("\nNo cards were revealed this round");
			Scanner sc = new Scanner(System.in);
			String accusationChoice;
			do {
				System.out.print(" Make Accusation (Y/N): ");
				accusationChoice = sc.next();
			} while (!(accusationChoice.matches("(?i)y|n|yes|no")));

			if (accusationChoice.matches("(?i)y|yes")) {
				makeAccusation(p);
			}
		}
	}

	/**
	 * change a players position if they're character is used in a suggesting
	 *
	 * @param ch: characterCard used
	 */
	private void moveSuggestedPlayer(CharacterCard ch, Cell newCell) {
		for (Player p : players) {
			if (p.getToken().equals(ch)) {//this is the player to move
				p.setCell(newCell);
				return;
			}
		}
	}

	/**
	 * Iterate players in search of cards matching suggestion
	 *
	 * @param p:     player who made suggestion
	 * @param guess: guess to refute
	 * @return boolean: true if a card is shown
	 */
	private boolean doRefutations(Player p, CardTriplet guess) {
		boolean found = false;
		int asked = 0;
		while (!found && asked < players.size() - 1) {
			Player asking;
			if (players.indexOf(p) + asked + 1 >= players.size()) {
				//loop back through char collection from index zero
				asking = players.get(asked - (players.size() - players.indexOf(p)) + 1);
			} else {
				//ask from player position
				asking = players.get(players.indexOf(p) + asked + 1);
			}
			asked++;
			System.out.println("\nChecking cards...");
			waitForPlayer(asking);
			//Does the next player have any possible cards to show
			ArrayList<Card> possibleCards = new ArrayList<>();
			for (Card c : guess.getSet()) {
				if (asking.getCards().contains(c))
					possibleCards.add(c);
			}
			if (possibleCards.isEmpty()) {
				System.out.println("You have no cards that match this guess");
			} else {
				System.out.println("You must reveal one of these cards");
				for (Card c : possibleCards) {
					System.out.println((possibleCards.indexOf(c) + 1) + " : " + c.getName());
					found = true;
				}
				int itemToShow = 0;
				do {
					System.out.print("Enter a number to reveal a card: ");
					try {
						itemToShow = input.nextInt();
					} catch (InputMismatchException e) {
						System.out.println("Must be between 1 and " + possibleCards.size());
						input.nextLine();
					}
				} while (itemToShow < 1 || itemToShow > possibleCards.size());

				System.out.println("\nRevealing card...");
				waitForPlayer(p);
				System.out.println("The revealed card is: " + possibleCards.get(itemToShow - 1).getName());
			}
		}
		return found;
	}


	/**
	 * precedes sensitive information intended for a single player to view
	 * waits to display output after input from player
	 *
	 * @param p: player who is performing action
	 */
	private void waitForPlayer(Player p) {
		System.out.println("=== SENSITIVE INFORMATION FOR PLAYER " + (players.indexOf(p) + 1) + " (" + p.getToken().getName() + ") ===");
		Scanner sc = new Scanner(System.in);
		System.out.print("Push a character and enter key to continue");
		sc.next();
	}

	/**
	 * Gets a player's accusation and checks if it is correct
	 * the player wins if correct, else is out
	 *
	 * @param p: player making accusation
	 */
	private void makeAccusation(Player p) {
		System.out.println("Making an accusation here");
		CardTriplet guess = new CardTriplet();
		System.out.println("Accusation is: " + guess);
		if (guess.equals(envelope)) {
			//correct, game won
			won = true;
			System.out.println("This is the correct solution");
			System.out.println("Player " + (players.indexOf(p) + 1) + " : " + p.getToken().getName() + " has won the game!");
		} else {
			//the player was incorrect and so is  now out
			System.out.println("Incorrect solution");
			System.out.println(p.getToken().getName() + " is out!");
			System.out.println("You still need to make refutations");
			p.setIsExcluded(true);
		}
	}
}
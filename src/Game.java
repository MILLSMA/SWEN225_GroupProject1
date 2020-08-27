import javax.swing.*;
import javax.swing.Timer;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Game {

	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Game Attributes
	private boolean won;
	//Game Associations
	private List<Player> players = new ArrayList<>();
	private Board board;
	private CardTriplet envelope;
	private Pathfinder<Cell> pathfinder;

	//------------------------
	// STATIC INITIALISATION METHODS
	//------------------------

	public static void main(String... args) {
		Game currentGame = new Game();
		SwingUtilities.invokeLater(() -> {
			final CluedoView GUI = new CluedoView(currentGame);
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
	 */
	public void startGame() {
		board = new Board(players);
		pathfinder = new Pathfinder<>(board);
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

	public void createPlayer(String name, CharacterCard token, boolean more) {
		players.add(new Player(token, null, name));
		if (more) CluedoView.createPlayerSelectionDialog(this, players.size()+1);
	}

	public boolean isTokenTaken(CharacterCard c) {
		for (Player p : players) {
			if (p.getToken().equals(c)) return true;
		}
		return false;
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
		CluedoView.createCanvas(board);
		new Thread(() -> {
			while (!won && !allPlayersOut()) {

				SwingUtilities.invokeLater(() -> {
					Timer t = new Timer(400, e -> CluedoView.updateBoard());
					t.start();
				});

				for (Player player : players) {
					if (!won && !player.getExcluded()) doTurn(player);
				}
			}
			if (allPlayersOut()) SwingUtilities.invokeLater(() -> CluedoView.gameOver(null, envelope));

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
		CluedoView.resetNextTurn();
		//place holder code
		System.out.println("\n== " + p.getToken().getName() + "'s turn ==");
		int diceRoll = rollDice();
		SwingUtilities.invokeLater(() -> CluedoView.displayPlayerInformation(p, diceRoll));
		List<Cell> cellsMovedTo = new ArrayList<>();
		move(p, diceRoll, cellsMovedTo);
		for (Cell cell : cellsMovedTo) {
			cell.setUsedInRound(false);
		}

		if (p.getLocation().getRoom().isProperRoom()) {
			CluedoView.changePlayerInfo("You've entered the " + p.getLocation().getRoom().getName());

			Room currentRoom = findRoom(p);
			p.setCell(currentRoom.findEmptyCell());
			CluedoView.enableRoomButtons(this, p);
			//turnEntry(p);
		}else{
			CluedoView.flagNextTurn();
		}
	}

//	/**
//	 * Rolls the dice, controls a player's moves
//	 *
//	 * @param p : the player moving
//	 */
//	private void move(Player p) {
//		if (p.getLocation().getRoom().isProperRoom()) {
//			System.out.println("You are currently in the " + p.getLocation().getRoom().getName());
//			System.out.println("You may move anywhere in your current room, " +
//					"your turn will start once you exit.");
//			while (true) {
//				//prints the board to the screen
//				System.out.println(board);
//				Cell.Direction chosenDirection = Cell.Direction.input(p, board);
//				if (chosenDirection == null) return;
//				Cell oldPlayerCell = p.getLocation();
//				Cell newPlayerCell = board.move(p, chosenDirection);
//				p.setCell(newPlayerCell);
//				if (newPlayerCell.getRoom().getCard() != oldPlayerCell.getRoom().getCard()) break;
//			}
//		}
//		ArrayList<Cell> cellsMovedTo = new ArrayList<>();
//		int numberOfMoves = rollDice();
//		System.out.println("You rolled " + numberOfMoves + ". You may move " + numberOfMoves + " spaces");
//		for (int moveNumber = 0; moveNumber < numberOfMoves; moveNumber++) {
//			//prints the board to the screen
//			System.out.println(board);
//			//displays whose turn it is and how many moves they have left
//			System.out.println(p.getToken().getName() + " you have " + (numberOfMoves - moveNumber) + " moves left");
//			System.out.println("You may move in these directions: ");
//			Cell.Direction chosenDirection = Cell.Direction.input(p, board);
//			if (chosenDirection == null) break;
//			//moves the player on the board based on their answer
//			Cell newPlayerCell = board.move(p, chosenDirection);
//			p.setCell(newPlayerCell);
//			cellsMovedTo.add(newPlayerCell);
//			newPlayerCell.setUsedInRound(true);
//			if (p.getLocation().getRoom().isProperRoom()) break;
//		}
//		for (Cell cell : cellsMovedTo) {
//			cell.setUsedInRound(false);
//		}
//	}

	/**
	 * new move method for testing the gui movement
	 *
	 */
	private void move(Player p, int roll, List<Cell> cellsMovedTo) {
		int amountOfMoves = roll;
		System.out.println("You can move " + roll + " tiles.");
		Cell goalCell;
		try {
			//waits for the player to click on a cell and the gets it
			goalCell = CluedoView.getCell();
			Cell playerCell = p.getLocation();

			goalCell = closestDoor(goalCell, playerCell);
			playerCell = closestDoor(playerCell, goalCell);

			if(goalCell.getRoom().isWall() || getEstimate(playerCell, goalCell) > roll) amountOfMoves = Integer.MIN_VALUE;
			//finds the shortest path to the selected cell
			ArrayList<Locatable> selectedCells = new ArrayList<>();
			if(amountOfMoves > 0) selectedCells.addAll(pathfinder.findPath(playerCell, goalCell));
			//if the path length is within the allowed amount of moves, move the player step by step
			if (selectedCells.size() - 1 <= amountOfMoves) {
				for (Locatable cell : selectedCells) {
					Cell c = (Cell) cell;
					p.setCell(c);
					cellsMovedTo.add(c);
					c.setUsedInRound(true);
					TimeUnit.MILLISECONDS.sleep(400);
				}
				if (roll > selectedCells.size()-1 && p.getLocation().getRoom().isHallway()) {
					int newRoll = roll-(selectedCells.size()-1);
					SwingUtilities.invokeLater(() -> CluedoView.displayPlayerInformation(p, newRoll));
					move(p, newRoll, cellsMovedTo);
				}
			} else {
				CluedoView.showDialog("You cannot move here");
				move(p, roll, cellsMovedTo);
			}

		} catch (Exception e) {
			CluedoView.showDialog("Move could not be made due to " + e.getMessage() + "\nPlease try again");
			move(p, roll, cellsMovedTo);
		}
	}

	private double getEstimate(Cell first, Cell second){
		Position firstPosition = first.getPosition();
		Position secondPosition = second.getPosition();
		return Math.sqrt((secondPosition.getRow()-firstPosition.getRow())*(secondPosition.getRow()-firstPosition.getRow()) +
				(secondPosition.getCol()-firstPosition.getCol())*((secondPosition.getCol()-firstPosition.getCol())));
	}

	private Cell closestDoor(Cell cellToChange, Cell measuringCell){
		double closest = Double.MAX_VALUE;
		Cell changedCell = cellToChange;
		if(RoomCard.getRooms().contains(cellToChange.getRoom().getCard())){
			for (Cell door : cellToChange.getRoom().getCard().getDoors()) {
				double distanceEstimate = getEstimate(measuringCell, door);
				if(distanceEstimate < closest){
					closest = distanceEstimate;
					changedCell = door;
				}
			}
		}
		return changedCell;
	}

	/**
	 * checks for adjacent rooms cells if player is in doorway.
	 *
	 * @param p player to check
	 */
	private Room findRoom(Player p) {
		if (p.getLocation().getRoom().isDoor()) {//in doorway
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
	public void makeSuggestion(Player p, String character, String weapon) {
		CardTriplet guess = new CardTriplet( character, weapon, p.getLocation().getRoom().getCard());
		System.out.println("Suggestion is: " + guess);

		//character and weapon tokens move to room
		Room currentRoom = findRoom(p);
		Cell newCell = currentRoom.addCard(guess.getCharacter());
		currentRoom.addCard(guess.getWeapon());
		moveSuggestedPlayer(guess.getCharacter(), newCell);

		boolean refuted = doRefutations(p, guess);
		if (!refuted) {
			System.out.println("\nNo cards were revealed this round");

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
		int asked = 0;
		while (asked < players.size() - 1) {
			Player asking;
			if (players.indexOf(p) + asked + 1 >= players.size()) {
				//loop back through char collection from index zero
				asking = players.get(asked - (players.size() - players.indexOf(p)) + 1);
			} else {
				//ask from player position
				asking = players.get(players.indexOf(p) + asked + 1);
			}
			asked++;
			//Does the next player have any possible cards to show
			ArrayList<Card> possibleCards = new ArrayList<>();
			for (Card c : guess.getSet()) {
				if (asking.getCards().contains(c))
					possibleCards.add(c);
			}
			if (!possibleCards.isEmpty()) {
				SwingUtilities.invokeLater(() -> CluedoView.createRefutationDialog(this, asking, possibleCards, guess, p));
				return true;
			}
		}
		SwingUtilities.invokeLater(() -> CluedoView.noReveal(this, p));
		//CluedoView.flagNextTurn();
		return false;
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
	public void makeAccusation(Player p, String character, String weapon, String room) {
		CardTriplet guess = new CardTriplet(character, weapon, room);
		System.out.println("Accusation is: " + guess);
		if (guess.equals(envelope)) {
			//correct, game won
			won = true;
			SwingUtilities.invokeLater(() -> CluedoView.gameOver(p, envelope));
		} else {
			//the player was incorrect and so is  now out
			p.setIsExcluded(true);
			SwingUtilities.invokeLater(() -> CluedoView.playerOut(p));
			CluedoView.flagNextTurn();
		}


	}
}
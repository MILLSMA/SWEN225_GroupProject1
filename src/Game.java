import javax.swing.*;
import javax.swing.Timer;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The "model" component of the MVC model. Stores the current state of the game and underlying mechanics.
 */
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
	private CluedoView gui;

	//------------------------
	// STATIC INITIALISATION METHODS
	//------------------------

	/**
	 * Initialise game model and create view
	 * @param args ignored
	 */
	public static void main(String... args) {
		new Game();
	}

	/**
	 * Initialise the game by setting up a new GUI
	 */
	public Game() {
		SwingUtilities.invokeLater(() -> gui = new CluedoView(this));
	}

	/**
	 * Set up the game model based on added players
	 */
	public void startGame() {
		board = new Board(players);
		pathfinder = new Pathfinder<>(board);
		won = false;
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
	 * Create player based on information from view
	 * @param name inputted name
	 * @param token character chosen
	 * @param more if they requested to add another player
	 */
	public void createPlayer(String name, CharacterCard token, boolean more) {
		players.add(new Player(token, name));
		if (more) gui.createPlayerSelectionDialog(this, players.size()+1);
	}

	/**
	 * Checks if a certain character is taken
	 * @param c certain character
	 * @return if they're taken
	 */
	public boolean isTokenTaken(CharacterCard c) {
		for (Player p : players) {
			if (p.getToken().equals(c)) return true;
		}
		return false;
	}

	/**
	 * Deals deck of cards to players
	 * @param cards collection of all cards that aren't the solution
	 */
	private void dealCards(Collection<Card> cards) {
		Random rand = new Random();
		ArrayList<Card> tempCardBag = new ArrayList<>(cards);
		System.out.println("The solution is: " + envelope);
		tempCardBag.removeAll(envelope.getList());

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
	 * @param cards : all the possible cards
	 * @return solution
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
	 * Runs the thread that the game is executed in
	 */
	private void runGame() {
		gui.setBoard(board);
		new Thread(() -> {
			while (!won && !allPlayersOut()) {

				SwingUtilities.invokeLater(() -> {
					Timer t = new Timer(400, e -> gui.updateBoard());
					t.start();
				});

				for (Player player : players) {
					if (!won && player.isStillIn()) doTurn(player);
				}
			}
			if (allPlayersOut()) SwingUtilities.invokeLater(() -> gui.gameOver(null, envelope));

		}).start();
	}

	/**
	 * check if all players are out of the game
	 *
	 * @return boolean: true if all out
	 */
	private boolean allPlayersOut() {
		for (Player p : players) {
			if (p.isStillIn()) {
				return false;
			}
		}
		return true;
	}

	CompletableFuture<Integer> diceRollPromise;
	/**
	 * main method for a single turn
	 *
	 * @param p : the player making the turn
	 */
	private void doTurn(Player p) {
		gui.resetNextTurn();
		//place holder code
		int diceRoll = 0;
		diceRollPromise = new CompletableFuture<>();
		SwingUtilities.invokeLater(()-> diceRollPromise = gui.displayPlayerInformation(p, 0, diceRollPromise));
		try {
			diceRoll = diceRollPromise.get();
		}catch(Exception ignored){}

		waitForMove(p, diceRoll);
		p.clearCellsMovedTo();

		if (p.getLocation().getRoom().isProperRoom()) {
			gui.changePlayerInfo("You're in the " + p.getLocation().getRoom().getName());

			Room currentRoom = findRoom(p);
			p.setCell(currentRoom.findEmptyCell());
			gui.enableRoomButtons(this, p);
		}else{
			gui.flagNextTurn();
		}
	}

	/**
	 * Wait for a player move player in the view
	 * @param p player whose turn it is
	 * @param roll how many moves they have
	 */
	private void waitForMove(Player p, int roll) {
		Cell goalCell;
		try {
			//waits for the player to click on a cell and the gets it
			goalCell = gui.getCell();
			Cell playerCell = p.getLocation();

			goalCell = closestDoor(goalCell, playerCell);
			playerCell = closestDoor(playerCell, goalCell);

			if (goalCell.getRoom().isWall() || getEstimate(playerCell, goalCell) > roll)
				roll = Integer.MIN_VALUE;
			//finds the shortest path to the selected cell
			ArrayList<Locatable> selectedCells = new ArrayList<>();
			if (roll > 0) selectedCells.addAll(pathfinder.findPath(playerCell, goalCell));
			//if the path length is within the allowed amount of moves, move the player step by step
			if (selectedCells.size() - 1 <= roll) {
				for (Locatable cell : selectedCells) {
					Cell c = (Cell) cell;
					p.setCell(c);
					p.addCellsMovedTo(c);
					c.setUsedInRound(true);
					TimeUnit.MILLISECONDS.sleep(400);
				}
				if (roll > selectedCells.size() - 1 && p.getLocation().getRoom().isHallway()) {
					int newRoll = roll - (selectedCells.size() - 1);
					SwingUtilities.invokeLater(() -> gui.displayPlayerInformation(p, newRoll, new CompletableFuture<>()));
					waitForMove(p, newRoll);
				}
			} else {
				gui.showDialog("You cannot move here");
				waitForMove(p, roll);
			}
		} catch (CancellationException ignored) {

		} catch (Exception e) {
			gui.showDialog("Move could not be made due to " + e.getMessage() + "\nPlease try again");
			waitForMove(p, roll);
		}
	}

	/**
	 * Calculates Euclidean distance from one cell to another
	 * @param first starting node
	 * @param second ending node
	 * @return Euclidean distance between given nodes
	 */
	private double getEstimate(Cell first, Cell second){
		Position firstPosition = first.getPosition();
		Position secondPosition = second.getPosition();
		return Math.sqrt((secondPosition.getRow()-firstPosition.getRow())*(secondPosition.getRow()-firstPosition.getRow()) +
				(secondPosition.getCol()-firstPosition.getCol())*((secondPosition.getCol()-firstPosition.getCol())));
	}

	/**
	 * TODO: do I understand this?
	 * Find cell that has the closest door
	 * @param cellToChange originating cell
	 * @param measuringCell goal cell
	 * @return cell with closest door
	 */
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
	 * @return room the player is now in
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
	 * Accepts suggestion from view
	 *
	 * @param p: player whose turn it is
	 * @param character : character suggested
	 * @param weapon : weapon suggested
	 */
	public void makeSuggestion(Player p, String character, String weapon) {
		CardTriplet guess = new CardTriplet( character, weapon, p.getLocation().getRoom().getCard());

		//character and weapon tokens move to room
		Room currentRoom = findRoom(p);
		Cell newCell = currentRoom.addCard(guess.getCharacter());
		currentRoom.addCard(guess.getWeapon());
		moveSuggestedPlayer(guess.getCharacter(), newCell);

		doRefutations(p, guess, null);
	}

	/**
	 * change a players position if their character is used in a suggestion
	 *
	 * @param ch: characterCard used
	 * @param newCell : cell to move character to
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
	 * @param p : player who made suggestion
	 * @param guess : guess to refute
	 * @param forTesting : players to test via JUnit, should be null if in game
	 * @return cards that a player could refute with
	 */
	public ArrayList<Card> doRefutations(Player p, CardTriplet guess, List<Player> forTesting) {
		int asked = 0;
		if(forTesting != null) players = forTesting;
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
			for (Card c : guess.getList()) {
				if (asking.getCards().contains(c))
					possibleCards.add(c);
			}
			if (!possibleCards.isEmpty()) {
				SwingUtilities.invokeLater(() -> gui.createRefutationDialog(asking, possibleCards, guess, p));
				return possibleCards;
			}
		}
		SwingUtilities.invokeLater(() -> gui.noRefutation(this, p));
		return null;
	}



	/**
	 * Gets a player's accusation from view and checks if it is correct
	 * the player wins if correct, else is out
	 *
	 * @param p: player making accusation
	 * @param character : accused character
	 * @param weapon : accused weapon
	 * @param room : accused room
	 * @param testAnswer : answer to test via JUnit, should be null if in game
	 * @return true if accusation is correct
	 */
	public boolean makeAccusation(Player p, String character, String weapon, String room, CardTriplet testAnswer) {
		if(testAnswer != null) envelope = testAnswer;
		CardTriplet guess = new CardTriplet(character, weapon, room);
		if (guess.equals(envelope)) {
			//correct, game won
			won = true;
			SwingUtilities.invokeLater(() -> gui.gameOver(p, envelope));
			return true;
		} else {
			//the player was incorrect and so is  now out
			if(testAnswer == null) {
				p.setPlayerOut();
				SwingUtilities.invokeLater(() -> gui.playerOut(p));
				gui.flagNextTurn();
			}
			return false;
		}


	}
}
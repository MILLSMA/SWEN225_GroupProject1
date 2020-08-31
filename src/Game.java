import javax.swing.*;
import javax.swing.Timer;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Game {

	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Game Attributes
	private boolean won;
	//Game Associations
	private final List<Player> players = new ArrayList<>();
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

	/**
	 * set up a new game with given number of players
	 *
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

	private void runGame() {
		CluedoView.createCanvas(board);
		new Thread(() -> {
			while (!won && !allPlayersOut()) {

				SwingUtilities.invokeLater(() -> {
					Timer t = new Timer(400, e -> CluedoView.updateBoard());
					t.start();
				});

				for (Player player : players) {
					if (!won && player.isStillIn()) doTurn(player);
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
		CluedoView.resetNextTurn();
		//place holder code
		int diceRoll = 0;
		diceRollPromise = new CompletableFuture<>();
		SwingUtilities.invokeLater(()-> diceRollPromise = CluedoView.displayPlayerInformation(p, 0, diceRollPromise));
		try {
			diceRoll = diceRollPromise.get();
		}catch(Exception ignored){}

		move(p, diceRoll);
		p.clearCellsMovedTo();

		if (p.getLocation().getRoom().isProperRoom()) {
			CluedoView.changePlayerInfo("You're in the " + p.getLocation().getRoom().getName());

			Room currentRoom = findRoom(p);
			p.setCell(currentRoom.findEmptyCell());
			CluedoView.enableRoomButtons(this, p);
			//turnEntry(p);
		}else{
			CluedoView.flagNextTurn();
		}
	}

	/**
	 * new move method for testing the gui movement
	 *
	 */
	private void move(Player p, int roll) {
		Cell goalCell;
		try {
			//waits for the player to click on a cell and the gets it
			goalCell = CluedoView.getCell();
			System.out.println(goalCell.position);
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
					SwingUtilities.invokeLater(() -> CluedoView.displayPlayerInformation(p, newRoll, new CompletableFuture<>()));
					move(p, newRoll);
				}
			} else {
				CluedoView.showDialog("You cannot move here");
				move(p, roll);
			}
		} catch (CancellationException ignored) {

		} catch (Exception e) {
			CluedoView.showDialog("Move could not be made due to " + e.getMessage() + "\nPlease try again");
			move(p, roll);
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

		//character and weapon tokens move to room
		Room currentRoom = findRoom(p);
		Cell newCell = currentRoom.addCard(guess.getCharacter());
		currentRoom.addCard(guess.getWeapon());
		moveSuggestedPlayer(guess.getCharacter(), newCell);

		doRefutations(p, guess);
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
	private void doRefutations(Player p, CardTriplet guess) {
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
				SwingUtilities.invokeLater(() -> CluedoView.createRefutationDialog(asking, possibleCards, guess, p));
				return;
			}
		}
		SwingUtilities.invokeLater(() -> CluedoView.noReveal(this, p));
		//CluedoView.flagNextTurn();
	}

	/**
	 * Gets a player's accusation and checks if it is correct
	 * the player wins if correct, else is out
	 *
	 * @param p: player making accusation
	 */
	public void makeAccusation(Player p, String character, String weapon, String room) {
		CardTriplet guess = new CardTriplet(character, weapon, room);
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
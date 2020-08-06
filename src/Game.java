import java.util.*;

public class Game
{

	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Game Attributes
	private boolean won;
	//Game Associations
	private final List<Player> players;
	private final Board board;
	private CardTriplet envelope;
	private final Scanner input = new Scanner(System.in);


	//------------------------
	// STATIC INITIALISATION METHODS
	//------------------------

	public static void main(String...args){
		new Game();
	}

	private static List<Player> createPlayers(int numPlayers){
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

	//------------------------
	// CONSTRUCTOR
	//------------------------

	private Game() {
		Scanner input = new Scanner(System.in);
		int amountOfPlayers = 0;
		// Ask users for an integer between 3 and 6, repeat until valid integer received
		do {
			System.out.print("How many players will be participating? (3 - 6): ");
			try {
				amountOfPlayers = input.nextInt();
			}catch(InputMismatchException e){
				System.out.println("Choose between 3, 4, 5 and 6 players.");
				input.nextLine();
			}
		} while (amountOfPlayers < 3 || amountOfPlayers > 6);

		players = createPlayers(amountOfPlayers);
		board = new Board(players);
		won = false;
		System.out.println("Players in this Game: ");
		for (int i = 0; i < players.size(); i++) {
			System.out.println("Player " + (i+1) + ": " + players.get(i).getToken().getName());
		}
		//collect all the cards for dealing
		List<Card> allCards = new ArrayList<>();
		allCards.addAll(CharacterCard.getCharacters());
		allCards.addAll(WeaponCard.getWeapons());
		allCards.addAll(RoomCard.getRooms());
		decideSolution(allCards);
		dealCards(allCards);
		runGame();
	}

	/**
	 * Deals deck of cards to players
	 * @param cards collection of all cards that aren't the solution
	 */
	public void dealCards(Collection<Card> cards){
		Random rand = new Random();
		ArrayList<Card> tempCardBag = new ArrayList<>(cards);
		//decideSolution(tempCardBag);
		System.out.println(envelope);
		tempCardBag.removeAll(envelope.getSet());

		//deal rest of the cards to the players
		while(!tempCardBag.isEmpty()){
			for (Player player : players) {
				if(tempCardBag.isEmpty()) continue;
				int cardIndex = rand.nextInt(tempCardBag.size());
				player.addCard(tempCardBag.get(cardIndex));
				tempCardBag.remove(cardIndex);
			}
		}
	}

	/**
	 * Selects a random room, weapon and character card from deck to set as solution
	 * @param cards : all the possible cards
	 */
	public void decideSolution(Collection<Card> cards) {
		//create envelope with card triplet
		Random rand = new Random();
		CharacterCard envelopeCharacter = (CharacterCard) cards.stream()
				.filter(card -> card instanceof CharacterCard)
				.skip(rand.nextInt(CharacterCard.size()))
				.findAny().get();
		WeaponCard envelopeWeapon = (WeaponCard) cards.stream()
				.filter(card -> card instanceof WeaponCard)
				.skip(rand.nextInt(WeaponCard.size()))
				.findAny().get();
		RoomCard envelopeRoom = (RoomCard) cards.stream()
				.filter(card -> card instanceof RoomCard)
				.skip(rand.nextInt(RoomCard.size()))
				.findAny().get();
		envelope = new CardTriplet(envelopeCharacter, envelopeWeapon, envelopeRoom);
	}

	/**
	 * Roll two six-sided die
	 * @return int : sum of die
	 */
	public int rollDice() {
		Random rand = new Random();
		int firstDice = rand.nextInt(6)+1;
		int secondDice = rand.nextInt(6)+1;
		//return firstDice + secondDice;
		return 40;//TODO: CHANGE THIS BACK
	}

	public void runGame(){
		while(!won || !allPlayersOut()){
			for (Player player : players) {
				if(!won && !player.isExcluded()) doTurn(player);
			}
		}
		if(allPlayersOut()){
			System.out.println("All Players are out!");
		}
	}

	public boolean allPlayersOut(){
		for(Player p : players){
			if(!p.isExcluded()){
				return false;
			}
		}
		return true;
	}

	/**
	 * main method for a single turn
	 * @param p : the player making the turn
	 */
	public void doTurn(Player p){
		//place holder code
		System.out.println("\n"+p.getToken().getName() + "'s turn:");
		move(p);

		if(p.getLocation().getRoom().isProperRoom()){
			System.out.println("You've entered the " + p.getLocation().getRoom().getCard().getName());
			turnEntry(p);
		}

	}

	/**
	 * retrieves and validates turn entry by player
	 * @param p : player whose turn it is
	 */
	public void turnEntry(Player p){
		Scanner sc = new Scanner(System.in);
		String turnEntry;
		do {
			System.out.print("Accusation (A) | Suggestion (S) | View Cards (C): ");
			turnEntry = sc.next();
		}while(!(turnEntry.matches("(?i)a|s|c|accusation|suggestion|view cards|")));



		if(turnEntry.matches("(?i)a|accusation")){
			makeAccusation(p);
		} else if(turnEntry.matches("(?i)s|suggestion")){
			makeSuggestion(p);
		}else {
			p.displayHand();
			turnEntry(p);

		}
	}

	/**
	 * Rolls the dice, controls a player's moves
	 * @param p : the player moving
	 */
	public void move(Player p){
		if(p.getLocation().getRoom().isProperRoom()){
			System.out.println("You are currently in the " + p.getLocation().getRoom());
			System.out.println("You may move anywhere in your current room, " +
					"your turn will start once you exit.");
			while(true){
				//prints the board to the screen
				System.out.println(board);
				Cell.Direction chosenDirection = askForDirection(p);
				Cell oldPlayerCell = p.getLocation();
				Cell newPlayerCell = board.move(p, chosenDirection);
				p.updatePosition(newPlayerCell);
				if(newPlayerCell.getRoom().getCard() != oldPlayerCell.getRoom().getCard()) break;
			}
		}
		int numberOfMoves = rollDice();
		System.out.println("You rolled: " + numberOfMoves);
		System.out.println("You may move " + numberOfMoves + " spaces");
		for (int moveNumber = 0; moveNumber < numberOfMoves; moveNumber++) {
			//prints the board to the screen
			System.out.println(board);
			//displays whose turn it is and how many moves they have left
			System.out.println(p.getToken().getName()+ " you have " + (numberOfMoves - moveNumber) + " moves left");
			System.out.println("You may move in these directions: ");
			Cell.Direction chosenDirection = askForDirection(p);
			//moves the player on the board based on their answer
			Cell newPlayerCell = board.move(p, chosenDirection);
			p.updatePosition(newPlayerCell);
			if(p.getLocation().getRoom().isProperRoom()) return;
		}
	}

	/**
	 *  retrieves and validates direction input from user
	 * @param p: player making move
	 * @return direction enum to move the player
	 */
	public Cell.Direction askForDirection(Player p){
		Scanner input = new Scanner(System.in);
		//Stores the directions that the player can legally move
		ArrayList<Cell.Direction> correctAnswers = new ArrayList<>(p.getLocation().directionsAvailable);
		for (Cell.Direction direction : p.getLocation().directionsAvailable) {
			System.out.println(direction.toString().toLowerCase() + "(" + correctAnswers.indexOf(direction) + ")");
		}
		while(true){
			try {
				int answer = input.nextInt();
				if(answer >= 0 && answer < correctAnswers.size()){
					return correctAnswers.get(answer);
				}
				System.out.println("Value must be between 0 and " + (correctAnswers.size()-1));
			} catch (InputMismatchException e) {
				System.out.println("Please enter a valid direction");
				input.nextLine();
			}
		}
	}

	// line 21 "model.ump"

	/**
	 * checks for adjacent rooms cells if player is in doorway;
	 * @param p
	 */
	public Room checkForRoom(Player p){
		System.out.println(p.getLocation().getRoom().getRoomSize());
		if(p.getLocation().getRoom().getRoomSize() <=1){//in doorway
			return board.checkSurroundingCells(p);
		}else if(p.getLocation().getRoom().getRoomSize() > 1){//inside room
			return p.getLocation().getRoom();
		}
		throw new RuntimeException("There should be a room near here!");
	}

	public void makeSuggestion(Player p){
		System.out.println("Making a suggestion here");
		CardTriplet guess = new CardTriplet(p.getLocation().getRoom().getCard());
		System.out.println("Suggestion is: " + guess.getCharacter().getName() + " with the " + guess.getWeapon().getName() + " in the " + guess.getRoom().getName());

		checkForRoom(p);
		//character and weapon tokens move to room
		Room currentRoom = checkForRoom(p);
		currentRoom.addCard(guess.getCharacter());
		currentRoom.addCard(guess.getWeapon());
		//TODO: player location stored in character location instead

		boolean found = doRefutations(guess, p);
		if(!found){
			System.out.println("\nNo cards were reveled this round");
			Scanner sc = new Scanner(System.in);
			String accusationChoice;
			do {
				System.out.print(" Make Accusation (Y/N): ");
				accusationChoice = sc.next();
			}while(!(accusationChoice.matches("(?i)y|n|yes|no")));
				if(accusationChoice.matches("(?i)y|yes")){
					makeAccusation(p);
				}
			}
	}


	/**
	 * precedes sensitive information intended for a single player to view
	 * waits to display output after input from player
	 * @param p: player who is performing action
	 */
	public void waitForPlayer(Player p){
		System.out.println("ACTION REQUIRED: Player " + (players.indexOf(p)+1) + " : " + p.getToken().getName());
		Scanner sc = new Scanner(System.in);
		System.out.print("Push a character and enter key to continue");
		sc.next();
	}

	/**
	 * Gets a player's accusation and checks if it is correct
	 * the player wins if correct, else is out
	 * @param p: player making accusation
	 */
	public void makeAccusation(Player p){
		System.out.println("Making an accusation here");
		CardTriplet guess = new CardTriplet();
		System.out.println("Accusation is: " + guess.getCharacter().getName() + " with the " + guess.getWeapon().getName() + " in the " + guess.getRoom().getName());
		if(guess.getCharacter().equals(envelope.getCharacter()) && guess.getWeapon().equals(envelope.getWeapon()) && guess.getRoom().equals(envelope.getRoom())){
			//correct, game won
			won = true;
			System.out.println("This is the correct solution");
			System.out.println("Player " + (players.indexOf(p)+1) + " : " + p.getToken().getName() + " has won the game!");
		}else{
			//the player was incorrect and so is  now out
			System.out.println("Incorrect solution");
			System.out.println(p.getToken().getName() + " is out!");
			System.out.println("You still need to make refutations");
			p.setIsExcluded(true);
		}
	}

	// line 27 "model.ump"
	public Boolean doRefutations(CardTriplet guess, Player p){
		boolean found = false;
		int asked = 0;
		while(!found && asked < players.size()-1){
			Player asking;
			if(players.indexOf(p) + asked + 1 >= players.size() ){
				//loop back through char collection from index zero
				//System.out.println("Asking: " + players.get(asked - (players.size() - players.indexOf(p)) + 1).getToken().getName());
				asking = players.get(asked - (players.size() - players.indexOf(p)) + 1);
			}else{
				//ask from player position
				//System.out.println("Asking: " + players.get(players.indexOf(p) + asked + 1).getToken().getName());
				asking = players.get(players.indexOf(p) + asked + 1);
			}
			asked ++;
			System.out.println("\nChecking cards...");
			waitForPlayer(asking);
			//TODO: put this in doRefutations
			//Does the next player have any possible cards to show
			ArrayList<Card> possibleCards = new ArrayList<>();
			if(asking.getCards().contains(guess.getCharacter())){
				possibleCards.add(guess.getCharacter());
			}
			if(asking.getCards().contains(guess.getWeapon())){
				possibleCards.add(guess.getWeapon());
			}
			if(asking.getCards().contains(guess.getRoom())){
				possibleCards.add(guess.getRoom());
			}
			if(possibleCards.isEmpty()){
				System.out.println("You have no cards that match this guess");
			}else{
				System.out.println("You must reveal one of these cards");
				for(Card c : possibleCards){
					System.out.println((possibleCards.indexOf(c)+1 ) + " : " + c.getName());
					found = true;
				}
				int itemToShow = 0;
				do {
					System.out.print("Enter a number to reveal a card: ");
					try {
						itemToShow = input.nextInt();
					}catch(InputMismatchException e){
						System.out.println("Must be between 1 and " + possibleCards.size());
						input.nextLine();
					}
				} while (itemToShow < 1 || itemToShow > possibleCards.size());

				System.out.println("\nRevealing card...");
				waitForPlayer(p);
				System.out.println("The revealed card is: " + possibleCards.get(itemToShow-1).getName());
			}
		}

		return found;
	}
}
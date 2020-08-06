/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.0.515.d9da8f6c modeling language!*/


import java.util.*;

// line 2 "model.ump"
// line 98 "model.ump"
public class Game
{
	private static Game instance;

	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Game Attributes
	private boolean won;

	//Game Associations
	private final List<Player> players;
	private Board board;
	private CardTriplet envelope;
	private final Scanner input = new Scanner(System.in);


	//------------------------
	// STATIC INITIALISATION METHODS
	//------------------------

	public static void main(String...args){
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

		List<Player> gamePlayers = createGamePlayers(amountOfPlayers);
		Board newBoard = new Board(gamePlayers);
		//create new instance of game (Singleton Pattern)
		instance = new Game(newBoard, gamePlayers);
	}

	private static List<Player> createGamePlayers(int numPlayers){
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

	private Game(Board aBoard, Collection<Player> allPlayers) {
		won = false;
		players = new ArrayList<Player>();
		setPlayers(allPlayers);
		if (aBoard == null) {
			throw new RuntimeException("Unable to create Game due to no Board found or no Players being added");
		}
		board = aBoard;
		System.out.println("Players in this Game: ");
		for (int i = 0; i < players.size(); i++) {
			System.out.println("Player " + (int)(i+1) + ": " + players.get(i).getToken().getName());
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


	//------------------------
	// INTERFACE
	//------------------------

//	public boolean getWon()
//	{
//		return won;
//	}

//	/* Code from template association_GetMany */
//	public Player getPlayer(int index)
//	{
//		return players.get(index);
//	}
//
//	public List<Player> getPlayers()
//	{
//		return Collections.unmodifiableList(players);
//	}
//
//	/* Code from template association_GetOne */
//	public Board getBoard()
//	{
//		return board;
//	}

	/* Code from template association_SetUnidirectionalN */
	public void setPlayers(Collection<Player> newPlayers)
	{
		//boolean wasSet = false;
		ArrayList<Player> verifiedPlayers = new ArrayList<Player>();
		for (Player aPlayer : newPlayers)
		{
			if (verifiedPlayers.contains(aPlayer))
			{
				continue;
			}
			verifiedPlayers.add(aPlayer);
		}

		players.clear();
		players.addAll(verifiedPlayers);

	}

	/**
	 * deals cards to players
	 * @param cards
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

	// line 8 "model.ump"

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

	// line 14 "model.ump"

	/**
	 * roll two six sided die
	 * @return int : sum of die
	 */
	public int rollDice() {
		Random rand = new Random();
		int firstDice = rand.nextInt(6)+1;
		int secondDice = rand.nextInt(6)+1;
		return firstDice + secondDice;
	}

	// line 16 "model.ump"
	public void runGame(){
		while(!won){
			for (Player player : players) {
				if(!won) doTurn(player);
			}
		}

	}

	// line 18 "model.ump"

	/**
	 * main method for a single turn
	 * @param p : the player making the turn
	 */
	public void doTurn(Player p){
		//place holder code
		System.out.println("\n"+p.getToken().getName() +"\'s turn:");
		move(p);

		if(p.getLocation().getRoom().isProperRoom()){
			System.out.println("You've entered the " + p.getLocation().getRoom().getCard().getName());
			turnEntry(p);
		}

	}

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

	public Cell.Direction askForDirection(Player p){
		Scanner input = new Scanner(System.in);
		//Stores the directions that the player can legally move
		ArrayList<Cell.Direction> correctAnswers = new ArrayList<>(p.getLocation().directionsAvailable);
		int answer = 0;
		boolean correctAnswer = false;
		//repeats asking the question until player enters a correct answer
		while(!correctAnswer){
			//prints the available directions and which number key to press to choose
			for (Cell.Direction direction : p.getLocation().directionsAvailable) {
				System.out.println(direction.toString().toLowerCase() + "(" + correctAnswers.indexOf(direction) + ")" );
			}
			//if the user puts in an answer that isn't an integer it will set there answer to 5
			//which will always be outside of the index range
			try {
				answer = input.nextInt();
			}catch(InputMismatchException e){
				answer = 5;
			}
			//if the answer is correct break, else tell them to pick a correct direction
			if(answer >= 0 && answer < correctAnswers.size()){
				correctAnswer = true;
			}else System.out.println("Please enter a correct direction");
		}
		return correctAnswers.get(answer);
	}

	// line 21 "model.ump"
	public void makeSuggestion(Player p){
		System.out.println("Making a suggestion here");
		CardTriplet guess = new CardTriplet(p.getLocation().getRoom().getCard());//TODO: change when movement implemented - room is always that which token is in
		System.out.println("Suggestion is: " + guess.getCharacter().getName() + " with the " + guess.getWeapon().getName() + " in the " + guess.getRoom().getName());
		//TODO: character and weapon tokens move to room
		p.getLocation().getRoom().addCard(guess.getCharacter());
		p.getLocation().getRoom().addCard(guess.getWeapon());
		//TODO: player location stored in character location instead

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
					System.out.println((int)(possibleCards.indexOf(c)+1 )+ " : " + c.getName());
					found = true;
				}
				int itemToShow = 0;
				Scanner sc = new Scanner(System.in);
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
		if(!found)System.out.println("\nNo cards were reveled this round");
	}

	/**
	 * precedes sensitive information intended for a single player to view
	 * waits to display output after input from player
	 * @param p: player who is performin action
	 */
	public void waitForPlayer(Player p){
		System.out.println("ACTION REQUIRED: Player " + (int)(players.indexOf(p)+1) + " : " + p.getToken().getName());
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
			System.out.println("Player " + (int)(players.indexOf(p)+1) + " : " + p.getToken().getName() + " has won the game!");
		}else{
			//the player was incorrect and so is  now out
			System.out.println("Incorrect solution");
			System.out.println(p.getToken().getName() + " is out!");
			System.out.println("You still need to make refutations");
			p.setIsExcluded(true);
		}
	}

	// line 27 "model.ump"
	public void doRefutations(){

	}
}
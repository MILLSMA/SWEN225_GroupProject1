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
  private List<Player> players;
  private Board board;
  private CardTriplet envelope;
  private Scanner input = new Scanner(System.in);


  //------------------------
  // STATIC INITIALISATION METHODS
  //------------------------

  public static void main(String...args){
    Scanner input = new Scanner(System.in);
    int amountOfPlayers = 0;
    //ask users for an integer between 3 and 6, repeat until valid integer recieved
    do {
      System.out.print("How many Players will be participating? (3 - 6): ");
      try {
        amountOfPlayers = input.nextInt();
      }catch(InputMismatchException e){
        System.out.println("Must be an integer between 3 - 6");
        input.nextLine();
      }
    }while(amountOfPlayers < 3 || amountOfPlayers > 6);
    ArrayList<Player> gamePlayers = new ArrayList<>(createGamePlayers(amountOfPlayers));
    Board newBoard = new Board(gamePlayers);
    //create new instance of game (Singleton Pattern)
    instance = new Game(newBoard, gamePlayers);
  }
  private static Collection<Player> createGamePlayers(int numPlayers){
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
    if (aBoard == null || aBoard.getGame() != null || players.size() == 0) {
      throw new RuntimeException("Unable to create Game due to no Board found or no Players being added");
    }
    board = aBoard;
    System.out.println("Players in this Game: ");
    for (int i = 0; i < players.size(); i++) {
      System.out.println("Player " + (int)(i+1) + ": " + players.get(i).getToken().toString());
    }
    //collect all the cards for dealing
    List<Card> allCards = new ArrayList<>();
    allCards.addAll(CharacterCard.getCharacters());
    allCards.addAll(WeaponCard.getWeapons());
    allCards.addAll(Room.getRoomCards());
    decideSolution(allCards);
    dealCards(allCards);
    doTurn(players.get(0));
  }


  //------------------------
  // INTERFACE
  //------------------------



  public boolean getWon()
  {
    return won;
  }

  /* Code from template association_GetMany */
  public Player getPlayer(int index)
  {
    Player aPlayer = players.get(index);
    return aPlayer;
  }

  public List<Player> getPlayers()
  {
    List<Player> newPlayers = Collections.unmodifiableList(players);
    return newPlayers;
  }

  /* Code from template association_GetOne */
  public Board getBoard()
  {
    return board;
  }

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

  public void delete()
  {
    players.clear();
    Board existingBoard = board;
    board = null;
    if (existingBoard != null)
    {
      existingBoard.delete();
    }
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
    for(Player p : players){
      p.displayHand();
    }
  }

  // line 8 "model.ump"

  /**
   * selects a random room, weapon and character card from deck to set as solution
   * @param cards : all the possible cards
   */
   public void decideSolution(Collection<Card> cards){
     //create envelope with card triplet
     Random rand = new Random();
     CharacterCard envelopeCharacter = (CharacterCard)cards.stream().filter(card -> card instanceof CharacterCard).skip(rand.nextInt(5)).findAny().get();
     WeaponCard envelopeWeapon = (WeaponCard)cards.stream().filter(card -> card instanceof WeaponCard).skip(rand.nextInt(5)).findAny().get();
     RoomCard envelopeRoom = (RoomCard)cards.stream().filter(card -> card instanceof RoomCard).skip(rand.nextInt(8)).findAny().get();
     envelope = new CardTriplet(envelopeCharacter, envelopeWeapon, envelopeRoom);

  }

  // line 14 "model.ump"

  /**
   * roll two six sided die
   * @return int : sum of die
   */
   public int rollDice(){
    Random rand = new Random();
    int firstDice = rand.nextInt(6);
    int secondDice = rand.nextInt(6);
    return firstDice + secondDice;
  }

  // line 16 "model.ump"
   public void runGame(){
    while(!won){

      for (Player player : players) {
        doTurn(player);
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
     System.out.println(p.getToken().toString() +"\'s turn:");

     move(p);

     //TODO: The player can make either an accusation or a suggestion only if they are in a room.
     Scanner sc = new Scanner(System.in);
     String turnEntry;
     do {
       System.out.print("Accusation (A) | Suggestion (S): ");
       turnEntry = sc.next();
     }while(!(turnEntry.matches("(?i)a|s|accusation|suggestion")));
     System.out.printf("valid  entry"); //TODO: cont. here
     if(turnEntry.matches("(?i)a|accusation")){
       makeSuggestion();
     }
     else{
       makeAccusation();
     }
  }

  /**
   * Rolls the dice, controls a player's moves
   * @param p : the player moving
   */
  public void move(Player p){
    int numberOfMoves = rollDice();
    System.out.println("You rolled: " + numberOfMoves);
    System.out.println("You may move " + numberOfMoves + " spaces");
    for (int moveNumber = 0; moveNumber < numberOfMoves; moveNumber++) {
      //TODO: display what moves player can make from a their position and give options to move in those directions.
    }
  }
  // line 21 "model.ump"
   public void makeSuggestion(){
    System.out.println("Making a suggestion here");
  }

  // line 24 "model.ump"
   public void makeAccusation(){
     System.out.println("Making an accusation here");
  }

  // line 27 "model.ump"
   public void doRefutations(){
    
  }


  public String toString()
  {
    return super.toString() + "["+
            "won" + ":" + getWon()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "board = "+(getBoard()!=null?Integer.toHexString(System.identityHashCode(getBoard())):"null");
  }
}
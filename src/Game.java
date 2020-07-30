/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.0.515.d9da8f6c modeling language!*/

import java.util.*;

// line 2 "model.ump"
// line 98 "model.ump"
public class Game
{
  private static Game instance = null;

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
    ArrayList<Character> characters = new ArrayList<>(Character.getCharacters());
    //creates a defined number of players with random Character card assigned
    for (int index = 0; index < numPlayers; index++) {
      int randomCardIndex = new Random().nextInt(characters.size());
      Character randomCharacter = characters.get(randomCardIndex);
      Player tempPlayer = new Player(randomCharacter, null, false);
      tempPlayerList.add(tempPlayer);
      characters.remove(randomCardIndex);
    }
    return tempPlayerList;
  }

  //------------------------
  // CONSTRUCTOR
  //------------------------

  private Game(Board aBoard, Collection<Player> allPlayers)
  {
    won = false;
    players = new ArrayList<Player>();
    if (aBoard == null || aBoard.getGame() != null || !setPlayers(allPlayers))
    {
      throw new RuntimeException("Unable to create Game due to no Board found or no Players being added");
    }
    board = aBoard;
    for (Player player : players) {
      System.out.println(player.getToken());
    }
    //collect all the cards for dealing
    List<Card> allCards = new ArrayList<>();
    allCards.addAll(Character.getCharacters());
    allCards.addAll(Weapon.getWeapons());
    allCards.addAll(Room.getRooms());
    dealCards(allCards);
  }


  //------------------------
  // INTERFACE
  //------------------------



  public boolean setWon(boolean aWon)
  {
    boolean wasSet = false;
    won = aWon;
    wasSet = true;
    return wasSet;
  }

  public boolean getWon()
  {
    return won;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isWon()
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

  public int numberOfPlayers()
  {
    int number = players.size();
    return number;
  }

  public boolean hasPlayers()
  {
    boolean has = players.size() > 0;
    return has;
  }

  public int indexOfPlayer(Player aPlayer)
  {
    int index = players.indexOf(aPlayer);
    return index;
  }
  /* Code from template association_GetOne */
  public Board getBoard()
  {
    return board;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfPlayers()
  {
    return 6;
  }
  /* Code from template association_MaximumNumberOfMethod */
  public static int maximumNumberOfPlayers()
  {
    return 6;
  }
  /* Code from template association_SetUnidirectionalN */
  public boolean setPlayers(Collection<Player> newPlayers)
  {
    boolean wasSet = false;
    ArrayList<Player> verifiedPlayers = new ArrayList<Player>();
    for (Player aPlayer : newPlayers)
    {
      if (verifiedPlayers.contains(aPlayer))
      {
        continue;
      }
      verifiedPlayers.add(aPlayer);
    }

    if (verifiedPlayers.size() != newPlayers.size())
    {
      return wasSet;
    }

    players.clear();
    players.addAll(verifiedPlayers);
    wasSet = true;
    return wasSet;
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

  public void dealCards(Collection<Card> cards){
    Random rand = new Random();
    ArrayList<Card> tempCardBag = new ArrayList<>(cards);
    Character envelopeCharacter = (Character)cards.stream().filter(card -> card instanceof Character).skip(rand.nextInt(5)).findAny().get();
    Weapon envelopeWeapon = (Weapon)cards.stream().filter(card -> card instanceof Weapon).skip(rand.nextInt(5)).findAny().get();
    Room envelopeRoom = (Room)cards.stream().filter(card -> card instanceof Room).skip(rand.nextInt(8)).findAny().get();
    envelope = new CardTriplet(envelopeCharacter, envelopeWeapon, envelopeRoom);

    while(!tempCardBag.isEmpty()){
      for (Player player : players) {
        int cardIndex = rand.nextInt(tempCardBag.size());
        player.addCard(tempCardBag.get(cardIndex));
        tempCardBag.remove(cardIndex);
      }
    }
  }

  // line 8 "model.ump"
   public void decideSolution(){
    
  }

  // line 11 "model.ump"
   public void deal(){
    
  }

  // line 14 "model.ump"
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
   public void doTurn(Player p){
    //place holder code
    int numberOfMoves = rollDice();
     System.out.println("You rolled: " + numberOfMoves);
     System.out.println("You may move " + numberOfMoves + " spaces");
     for (int moveNumber = 0; moveNumber < numberOfMoves; moveNumber++) {
       //TODO: display what moves player can make from a their position and give options to move in those directions.
     }
  }

  // line 21 "model.ump"
   public void makeSuggestion(){
    
  }

  // line 24 "model.ump"
   public void makeAccusation(){
    
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
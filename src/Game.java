/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.0.515.d9da8f6c modeling language!*/


import java.util.*;

// line 2 "model.ump"
// line 98 "model.ump"
public class Game
{

  //------------------------
  // ENUMERATIONS
  //------------------------

  public enum CharacterCard { MISS_SCARLETT }

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Game Attributes
  private boolean won;

  //Game Associations
  private List<Player> players;
  private Board board;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Game(Board aBoard, Player... allPlayers)
  {
    won = false;
    players = new ArrayList<Player>();
    boolean didAddPlayers = setPlayers(allPlayers);
    if (!didAddPlayers)
    {
      throw new RuntimeException("Unable to create Game, must have 6 players. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    if (aBoard == null || aBoard.getGame() != null)
    {
      throw new RuntimeException("Unable to create Game due to aBoard. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    board = aBoard;
  }

  public Game(Player... allPlayersForBoard, Player... allPlayers)
  {
    won = false;
    players = new ArrayList<Player>();
    boolean didAddPlayers = setPlayers(allPlayers);
    if (!didAddPlayers)
    {
      throw new RuntimeException("Unable to create Game, must have 6 players. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    board = new Board(this, allPlayersForBoard);
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
  /* Code from template association_RequiredNumberOfMethod */
  public static int requiredNumberOfPlayers()
  {
    return 6;
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
  public boolean setPlayers(Player... newPlayers)
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

    if (verifiedPlayers.size() != newPlayers.length || verifiedPlayers.size() != requiredNumberOfPlayers())
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

  // line 8 "model.ump"
   public void decideSolution(){
    
  }

  // line 11 "model.ump"
   public void deal(){
    
  }

  // line 14 "model.ump"
   public int rollDice(){
    
  }

  // line 16 "model.ump"
   public void runGame(){
    
  }

  // line 18 "model.ump"
   public void doTurn(Player p){
    
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
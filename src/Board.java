/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.0.515.d9da8f6c modeling language!*/


import java.util.*;

// line 30 "model.ump"
// line 104 "model.ump"
public class Board
{

  //------------------------
  // ENUMERATIONS
  //------------------------

  public enum CharacterCard { MISS_SCARLETT }

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Board Associations
  private List<Cell> cells;
  private Game game;
  private List<Player> players;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Board(Collection<Player> allPlayers)
  {
    cells = new ArrayList<Cell>();
    players = new ArrayList<Player>();
    setPlayers(allPlayers);

  }


  //------------------------
  // INTERFACE
  //------------------------
  /* Code from template association_GetMany */
  public Cell getCell(int index)
  {
    Cell aCell = cells.get(index);
    return aCell;
  }

  public List<Cell> getCells()
  {
    List<Cell> newCells = Collections.unmodifiableList(cells);
    return newCells;
  }

  public int numberOfCells()
  {
    int number = cells.size();
    return number;
  }

  public boolean hasCells()
  {
    boolean has = cells.size() > 0;
    return has;
  }

  public int indexOfCell(Cell aCell)
  {
    int index = cells.indexOf(aCell);
    return index;
  }
  /* Code from template association_GetOne */
  public Game getGame()
  {
    return game;
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
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfCells()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public Cell addCell(Position aPos, Position aPosition, Room... allRooms)
  {
    return new Cell(aPos, aPosition, this, allRooms);
  }

  public boolean addCell(Cell aCell)
  {
    boolean wasAdded = false;
    if (cells.contains(aCell)) { return false; }
    Board existingBoard = aCell.getBoard();
    boolean isNewBoard = existingBoard != null && !this.equals(existingBoard);
    if (isNewBoard)
    {
      aCell.setBoard(this);
    }
    else
    {
      cells.add(aCell);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeCell(Cell aCell)
  {
    boolean wasRemoved = false;
    //Unable to remove aCell, as it must always have a board
    if (!this.equals(aCell.getBoard()))
    {
      cells.remove(aCell);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addCellAt(Cell aCell, int index)
  {  
    boolean wasAdded = false;
    if(addCell(aCell))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfCells()) { index = numberOfCells() - 1; }
      cells.remove(aCell);
      cells.add(index, aCell);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveCellAt(Cell aCell, int index)
  {
    boolean wasAdded = false;
    if(cells.contains(aCell))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfCells()) { index = numberOfCells() - 1; }
      cells.remove(aCell);
      cells.add(index, aCell);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addCellAt(aCell, index);
    }
    return wasAdded;
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

    if (verifiedPlayers.size() != newPlayers.size() || verifiedPlayers.size() != requiredNumberOfPlayers())
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
    while (cells.size() > 0)
    {
      Cell aCell = cells.get(cells.size() - 1);
      aCell.delete();
      cells.remove(aCell);
    }
    
    Game existingGame = game;
    game = null;
    if (existingGame != null)
    {
      existingGame.delete();
    }
    players.clear();
  }

  // line 34 "model.ump"
   public Position checkValidMove(Player p, String code, int steps){

    return null;
  }

  // line 36 "model.ump"
   public void move(Player p, Position old){
    
  }

}
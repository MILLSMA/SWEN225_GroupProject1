import java.util.*;

public class Board
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Board Associations
  private final List<Cell> cells;
  private final List<Player> players;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Board(Collection<Player> allPlayers)
  {
    cells = new ArrayList<>();
    players = new ArrayList<>();
    setPlayers(allPlayers);
  }


  //------------------------
  // INTERFACE
  //------------------------
  /* Code from template association_GetMany */
  public Cell getCell(int index)
  {
    return cells.get(index);
  }

  public List<Cell> getCells()
  {
    return Collections.unmodifiableList(cells);
  }

  public int numberOfCells()
  {
    return cells.size();
  }

  /* Code from template association_GetMany */
  public Player getPlayer(int index)
  {
    return players.get(index);
  }

  public List<Player> getPlayers()
  {
    return Collections.unmodifiableList(players);
  }

  public int numberOfPlayers()
  {
    return players.size();
  }

  /* Code from template association_AddManyToOne */
  public Cell addCell(Position aPosition, Room... allRooms)
  {
    return new Cell(aPosition, this, allRooms);
  }

  public boolean addCell(Cell aCell)
  {
    if (cells.contains(aCell)) { return false; }
    cells.add(aCell);
    return true;
  }
  /* Code from template association_RequiredNumberOfMethod */
  public static int requiredNumberOfPlayers()
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

  // line 34 "model.ump"
   public Position checkValidMove(Player p, String code, int steps){

    return null;
  }

  // line 36 "model.ump"
   public void move(Player p, Position old){
    
  }

}
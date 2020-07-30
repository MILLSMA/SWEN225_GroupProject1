/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.0.515.d9da8f6c modeling language!*/


import java.util.*;

// line 43 "model.ump"
// line 112 "model.ump"
public class Cell
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Cell Attributes
  private Position pos;

  //Cell Associations
  private Position position;
  private List<Room> rooms;
  private Board board;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Cell(Position aPos, Position aPosition, Board aBoard, Room... allRooms)
  {
    pos = aPos;
    if (!setPosition(aPosition))
    {
      throw new RuntimeException("Unable to create Cell due to aPosition. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    rooms = new ArrayList<Room>();
    boolean didAddRooms = setRooms(allRooms);
    if (!didAddRooms)
    {
      throw new RuntimeException("Unable to create Cell, must have at least 1 rooms. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    boolean didAddBoard = setBoard(aBoard);
    if (!didAddBoard)
    {
      throw new RuntimeException("Unable to create cell due to board. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setPos(Position aPos)
  {
    boolean wasSet = false;
    pos = aPos;
    wasSet = true;
    return wasSet;
  }

  public Position getPos()
  {
    return pos;
  }
  /* Code from template association_GetOne */
  public Position getPosition()
  {
    return position;
  }
  /* Code from template association_GetMany */
  public Room getRoom(int index)
  {
    Room aRoom = rooms.get(index);
    return aRoom;
  }

  public List<Room> getRooms()
  {
    List<Room> newRooms = Collections.unmodifiableList(rooms);
    return newRooms;
  }

  public int numberOfRooms()
  {
    int number = rooms.size();
    return number;
  }

  public boolean hasRooms()
  {
    boolean has = rooms.size() > 0;
    return has;
  }

  public int indexOfRoom(Room aRoom)
  {
    int index = rooms.indexOf(aRoom);
    return index;
  }
  /* Code from template association_GetOne */
  public Board getBoard()
  {
    return board;
  }
  /* Code from template association_SetUnidirectionalOne */
  public boolean setPosition(Position aNewPosition)
  {
    boolean wasSet = false;
    if (aNewPosition != null)
    {
      position = aNewPosition;
      wasSet = true;
    }
    return wasSet;
  }
  /* Code from template association_IsNumberOfValidMethod */
  public boolean isNumberOfRoomsValid()
  {
    boolean isValid = numberOfRooms() >= minimumNumberOfRooms();
    return isValid;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfRooms()
  {
    return 1;
  }
  /* Code from template association_AddManyToManyMethod */
  public boolean addRoom(Room aRoom)
  {
    boolean wasAdded = false;
    if (rooms.contains(aRoom)) { return false; }
    rooms.add(aRoom);
    if (aRoom.indexOfCell(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aRoom.addCell(this);
      if (!wasAdded)
      {
        rooms.remove(aRoom);
      }
    }
    return wasAdded;
  }
  /* Code from template association_AddMStarToMany */
  public boolean removeRoom(Room aRoom)
  {
    boolean wasRemoved = false;
    if (!rooms.contains(aRoom))
    {
      return wasRemoved;
    }

    if (numberOfRooms() <= minimumNumberOfRooms())
    {
      return wasRemoved;
    }

    int oldIndex = rooms.indexOf(aRoom);
    rooms.remove(oldIndex);
    if (aRoom.indexOfCell(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aRoom.removeCell(this);
      if (!wasRemoved)
      {
        rooms.add(oldIndex,aRoom);
      }
    }
    return wasRemoved;
  }
  /* Code from template association_SetMStarToMany */
  public boolean setRooms(Room... newRooms)
  {
    boolean wasSet = false;
    ArrayList<Room> verifiedRooms = new ArrayList<Room>();
    for (Room aRoom : newRooms)
    {
      if (verifiedRooms.contains(aRoom))
      {
        continue;
      }
      verifiedRooms.add(aRoom);
    }

    if (verifiedRooms.size() != newRooms.length || verifiedRooms.size() < minimumNumberOfRooms())
    {
      return wasSet;
    }

    ArrayList<Room> oldRooms = new ArrayList<Room>(rooms);
    rooms.clear();
    for (Room aNewRoom : verifiedRooms)
    {
      rooms.add(aNewRoom);
      if (oldRooms.contains(aNewRoom))
      {
        oldRooms.remove(aNewRoom);
      }
      else
      {
        aNewRoom.addCell(this);
      }
    }

    for (Room anOldRoom : oldRooms)
    {
      anOldRoom.removeCell(this);
    }
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addRoomAt(Room aRoom, int index)
  {  
    boolean wasAdded = false;
    if(addRoom(aRoom))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfRooms()) { index = numberOfRooms() - 1; }
      rooms.remove(aRoom);
      rooms.add(index, aRoom);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveRoomAt(Room aRoom, int index)
  {
    boolean wasAdded = false;
    if(rooms.contains(aRoom))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfRooms()) { index = numberOfRooms() - 1; }
      rooms.remove(aRoom);
      rooms.add(index, aRoom);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addRoomAt(aRoom, index);
    }
    return wasAdded;
  }
  /* Code from template association_SetOneToMany */
  public boolean setBoard(Board aBoard)
  {
    boolean wasSet = false;
    if (aBoard == null)
    {
      return wasSet;
    }

    Board existingBoard = board;
    board = aBoard;
    if (existingBoard != null && !existingBoard.equals(aBoard))
    {
      existingBoard.removeCell(this);
    }
    board.addCell(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    position = null;
    ArrayList<Room> copyOfRooms = new ArrayList<Room>(rooms);
    rooms.clear();
    for(Room aRoom : copyOfRooms)
    {
      aRoom.removeCell(this);
    }
    Board placeholderBoard = board;
    this.board = null;
    if(placeholderBoard != null)
    {
      placeholderBoard.removeCell(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "pos" + "=" + (getPos() != null ? !getPos().equals(this)  ? getPos().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "position = "+(getPosition()!=null?Integer.toHexString(System.identityHashCode(getPosition())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "board = "+(getBoard()!=null?Integer.toHexString(System.identityHashCode(getBoard())):"null");
  }
}
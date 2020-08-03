import java.util.*;

public class Cell
{

	public enum Directions{
		NORTH,
		SOUTH,
		EAST,
		WEST
	}
	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Cell Associations
	private Position position;
	private final List<Room> rooms;

	//------------------------
	// CONSTRUCTOR
	//------------------------

	public Cell( Position aPosition, Board aBoard, Room... room)
	{
		if (!setPosition(aPosition))
		{
			throw new RuntimeException("Unable to create Cell due to aPosition. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
		}
		rooms = new ArrayList<Room>();
		boolean didAddRooms = setRooms(room);
		if (!didAddRooms)
		{
			throw new RuntimeException("Unable to create Cell, must have at least 1 rooms. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
		}
	}

	//------------------------
	// INTERFACE
	//------------------------

	/* Code from template association_GetOne */
	public Position getPosition()
	{
		return position;
	}
	/* Code from template association_GetMany */
	public Room getRoom(int index)
	{
		return rooms.get(index);
	}

	public List<Room> getRooms()
	{
		return Collections.unmodifiableList(rooms);
	}

	public int numberOfRooms()
	{
		return rooms.size();
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

	/* Code from template association_AddManyToManyMethod */
	public boolean addRoom(Room aRoom)
	{
		if(!rooms.contains(aRoom)) {
			rooms.add(aRoom);
			return true;
		}
		return false;
	}

	/* Code from template association_SetMStarToMany */
	public boolean setRooms(Room... newRooms)
	{
		ArrayList<Room> verifiedRooms = new ArrayList<Room>();
		for (Room aRoom : newRooms) {
			if (verifiedRooms.contains(aRoom)) {
				continue;
			}
			verifiedRooms.add(aRoom);
		}

		if (verifiedRooms.size() != newRooms.length || verifiedRooms.size() < 1) {
			return false;
		}

		ArrayList<Room> oldRooms = new ArrayList<Room>(rooms);
		rooms.clear();
		for (Room aNewRoom : verifiedRooms) {
			rooms.add(aNewRoom);
			if (oldRooms.contains(aNewRoom)) {
				oldRooms.remove(aNewRoom);
			}
			else
			{
				aNewRoom.addCell(this);
			}
		}

		for (Room anOldRoom : oldRooms) {
			anOldRoom.removeCell(this);
		}
		return true;
	}


	public String toString()
	{
		return ""; //TODO
	}
}
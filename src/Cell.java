import java.util.*;

public class Cell
{

	public enum Direction{
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
	public List<Direction> directionsAvailable = new ArrayList<>();
	private final Room room;
	private Card object;
	//------------------------
	// CONSTRUCTOR
	//------------------------

	public Cell( Position aPosition, Room room)
	{
		if (!setPosition(aPosition))
		{
			throw new RuntimeException("Unable to create Cell due to aPosition. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
		}
		this.room = room;
		putCellInRoom();
	}

	//------------------------
	// INTERFACE
	//------------------------

	public void setObject(Card card){
		this.object = card;
	}

	private void putCellInRoom(){
		room.addCell(this);
	}

	public void setDirection(Direction dir, boolean possible){
			if (possible && !directionsAvailable.contains(dir)) directionsAvailable.add(dir);
			else directionsAvailable.remove(dir);
	}
//	/* Code from template association_GetOne */
//	public Position getPosition()
//	{
//		return position;
//	}

	/* Code from template association_GetMany */
	public Room getRoom()
	{
		return room;
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

   public Card getObject(){
		return object;
   }

	public String toString()
	{
		if(object != null) return object.toString();
		return room.toString();
	}
}
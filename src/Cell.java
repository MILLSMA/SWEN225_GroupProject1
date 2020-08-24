import java.awt.*;
import java.util.*;
import java.util.List;

public class Cell extends Locatable
{
	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Cell Associations
	private final Room room;
	private Card object;
	private boolean isUsedInRound;
	private Color color;
	//------------------------
	// CONSTRUCTOR
	//------------------------

	public Cell( Position aPosition, Room room)
	{
		directionsAvailable = new ArrayList<>();
		if (!setPosition(aPosition))
		{
			throw new RuntimeException("Unable to create Cell due to aPosition. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
		}
		this.room = room;
		room.addCell(this);
	}

	//------------------------
	// INTERFACE
	//------------------------

	public Color getColor(){
		if(object != null && object instanceof CharacterCard) return ((CharacterCard) object).getColour();
		return this.color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isUsedInRound(){
		return isUsedInRound;
	}
	public void setUsedInRound(boolean b){
		isUsedInRound = b;
	}

	public void setObject(Card card){
		this.object = card;
	}

	public void setDirection(Direction dir, boolean possible){
			if (possible && !directionsAvailable.contains(dir)) directionsAvailable.add(dir);
			else directionsAvailable.remove(dir);
	}

	public Room getRoom() {
		return room;
	}

	public boolean setPosition(Position aNewPosition) {
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

	public String toString() {
		if(object != null) return object.toString();
		return room.toString();
	}
}
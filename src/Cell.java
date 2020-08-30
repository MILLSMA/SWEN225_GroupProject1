import java.awt.*;
import java.util.*;

/**
 * Stores information about individual Cell on board
 */
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

	/**
	 * Construct new Cell
	 * @param aPosition row/col position it's in
	 * @param room room it belongs to
	 */
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

	/**
	 * Return this cell's colour
	 * @return cell colour
	 */
	public Color getColor(){
		return this.color;
	}

	/**
	 * Change this cell's colour
	 * @param color new cell colour
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * If cell was already used in this player's turn, return true
	 * @return true if used in turn
	 */
	public boolean isUsedInRound(){
		return isUsedInRound;
	}

	/**
	 * Set usage of cell in round
	 * @param b true if just been used, false to clear
	 */
	public void setUsedInRound(boolean b){
		isUsedInRound = b;
	}

	/**
	 * Place a Card (character, weapon) in Cell
	 * @param card object card to place
	 */
	public void setObject(Card card){
		this.object = card;
	}

	/**
	 * Set the possibility of moving to a given direction from this cell
	 * @param dir direction from cell
	 * @param possible true if possible to move to given direction
	 */
	public void setDirection(Direction dir, boolean possible){
			if (possible && !directionsAvailable.contains(dir)) directionsAvailable.add(dir);
			else directionsAvailable.remove(dir);
	}

	/**
	 * Get room this cell belongs to
	 * @return room this cell belongs to
	 */
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

	/**
	 * Get object contained in cell
	 * @return object in cell
	 */
   	public Card getObject(){
		return object;
   }

	public String toString() {
		return position.toString();
	}
}
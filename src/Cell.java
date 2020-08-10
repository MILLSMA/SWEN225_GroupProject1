import java.util.*;

public class Cell
{
	public enum Direction{
		NORTH,
		SOUTH,
		EAST,
		WEST;

		/**
		 *  retrieves and validates direction input from user
		 * @param p: player making move
		 * @return direction enum to move the player
		 */
		public static Cell.Direction input(Player p, Board board){
			Scanner input = new Scanner(System.in);
			//Stores the directions that the player can legally move
			ArrayList<Cell.Direction> correctAnswers = new ArrayList<>(p.getLocation().directionsAvailable);
			for (Cell.Direction direction : p.getLocation().directionsAvailable) {
				if(!board.isCellUsed(p, direction)){
					System.out.println(direction);
				}else correctAnswers.remove(direction);

			}
			System.out.println("End Turn [X]");
			while(true){
				System.out.print("Move: ");
				char answer = input.next().toUpperCase().charAt(0);
				if (answer == 'X') return null;

				for (Direction d : correctAnswers) {
					if (answer == d.code()) return d;
				}
				System.out.println("Please use the given code in [ ].");
				input.nextLine();
			}
		}

		public String toString() {
			return "[" + name().substring(0, 1) + "]" + name().substring(1).toLowerCase();
		}

		public char code() {
			return name().charAt(0);
		}
	}
	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Cell Associations
	private Position position;
	public List<Direction> directionsAvailable = new ArrayList<>();
	private final Room room;
	private Card object;
	private boolean isUsedInRound;
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
		room.addCell(this);
	}

	//------------------------
	// INTERFACE
	//------------------------

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


	public Position getPosition() {
		return position;
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
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
		public static Cell.Direction askForDirection(Player p, Board board){
			Scanner input = new Scanner(System.in);
			//Stores the directions that the player can legally move
			ArrayList<Cell.Direction> correctAnswers = new ArrayList<>(p.getLocation().directionsAvailable);
			for (Cell.Direction direction : p.getLocation().directionsAvailable) {
				if(!board.checkMove(p, direction).hasBeenMovedToo()){
					System.out.println(direction.toString().toLowerCase() + "(" + correctAnswers.indexOf(direction) + ")");
				}else correctAnswers.remove(direction);
				System.out.println("End Turn (" + p.getLocation().directionsAvailable.size() + ")");
			}
			while(true){
				try {
					int answer = input.nextInt();
					if(answer >= 0 && answer <= correctAnswers.size()){
						if(answer == correctAnswers.size()) return null;
						return correctAnswers.get(answer);
					}
					System.out.println("Value must be between 0 and " + (correctAnswers.size()-1));
				} catch (InputMismatchException e) {
					System.out.println("Please enter a valid direction");
					input.nextLine();
				}
			}
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
	private boolean hasBeenMovedToo;
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

	public boolean hasBeenMovedToo(){
		return hasBeenMovedToo;
	}
	public void setHasBeenMovedToo(boolean b){
		hasBeenMovedToo = b;
	}

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

	public Room getRoom()
	{
		return room;
	}

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
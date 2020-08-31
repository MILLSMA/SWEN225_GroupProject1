import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Enumeration of possible rooms in Cluedo
 */
public enum RoomCard implements Card {
	KITCHEN,
	BALLROOM,
	CONSERVATORY,
	BILLIARD_ROOM,
	DINING_ROOM,
	LOUNGE,
	HALL,
	LIBRARY,
	STUDY;

	private final ArrayList<Cell> doors = new ArrayList<>();
	private static final String[] codes = {"K", "O", "C", "B", "D", "L", "H", "I", "S"};

	/**
	 * Convert enum to unmodifiable collection
	 * @return collection of all room cards
	 */
	public static Collection<RoomCard> getRooms(){
		return Collections.unmodifiableCollection(Arrays.asList(values()));
	}

	/**
	 * Get number of possible rooms
	 * @return number of rooms
	 */
	public static int size(){
		return values().length;
	}

	/**
	 * Get all doors belonging to this room
	 * @return doors in room
	 */
	public ArrayList<Cell> getDoors() {
		return doors;
	}

	/**
	 * Add a door cell to this room
	 * @param door door cell
	 */
	public void addDoor(Cell door) {
		this.doors.add(door);
	}

	@Override
	public String getName(){
		StringBuilder sb = new StringBuilder();
		boolean capital = true;
		// Change enum names to lower case, underscores as spaces
		for (char c : name().toLowerCase().replace("_", " ").toCharArray()) {
			if (capital) c = Character.toUpperCase(c);
			sb.append(c);
			// Capitalise next character if the current character is a space
			capital = c == ' ';
		}
		return sb.toString();
	}

	@Override
	public String toString(){
		return codes[ordinal()];
	}
}
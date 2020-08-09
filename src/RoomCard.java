import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

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

	private static final String[] codes = {"K", "O", "C", "B", "D", "L", "H", "I", "S"};

	public static Collection<RoomCard> getRooms(){
		return Arrays.asList(values());
	}

	public static int size(){
		return values().length;
	}

	/**
	 * Get Valid room
	 * Match given string with RoomCard enum
	 * should only be called when accusation made
	 * @return enum RoomCard value
	 */
	public static RoomCard input(){
		while(true) {
			Scanner sc = new Scanner(System.in);
			System.out.println("== Rooms, enter code in [ ] ==");
			for (int i=0; i<size(); i++) {
				System.out.println(values()[i].getName() + "[" + codes[i] + "]");
			}
			System.out.print("Enter room: ");
			String guess = sc.next();
			for (int i=0; i<size(); i++) {
				if(codes[i].equalsIgnoreCase(guess)){
					return values()[i];
				}
			}
			System.out.println("Please use the given code in [ ].\n");
		}
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
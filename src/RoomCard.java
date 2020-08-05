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

	public static Collection<RoomCard> getRooms(){
		return Arrays.asList(values());
	}

	public static int size() {
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
			System.out.print("Enter Room: ");
			String roomGuess = sc.next();
			for (RoomCard r : RoomCard.values()) {
				if(r.name().equalsIgnoreCase(roomGuess)){
					return r;
				}
			}
		}
	}

	@Override
	public String getName() {
		return name().toLowerCase();
	}

	@Override
	public String toString() {
		String[] codes = {"K", "O", "C", "B", "D", "L", "H", "I", "S"};
		return codes[ordinal()];
	}
}
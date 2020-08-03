import java.util.Arrays;
import java.util.Collection;

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
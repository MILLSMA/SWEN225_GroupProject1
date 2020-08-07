import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

public enum WeaponCard implements Card{
	CANDLESTICK,
	DAGGER,
	LEAD_PIPE,
	REVOLVER,
	ROPE,
	SPANNER;

	private Cell location;
	private Room room;

	public static Collection<WeaponCard> getWeapons(){
		return Arrays.asList(values());
	}

	public static int size() {
		return values().length;
	}

	/**
	 * Check for valid weapon entry, match with weaponCard enum
	 * @return enum WeaponCard value
	 */
	public static WeaponCard input(){
		while(true) {
			Scanner sc = new Scanner(System.in);
			// TODO: give key for weapons
			System.out.print("Enter weapon: ");
			String weaponGuess = sc.next();
			for (WeaponCard w : WeaponCard.values()) {
				if(w.name().equalsIgnoreCase(weaponGuess)){
					return w;
				}
			}
		}
	}


	public void moveToCell(Cell c){
		if(this.location != null) this.location.setObject(null);
		this.location = c;
		c.setObject(this);
	}

	//TODO: potentially remove this and use cell only
	public void moveToRoom(Room r){
		this.room = r;
	}

	@Override
	public String getName() {
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
	public String toString() {
		String[] codes = {"+", "#", "|", "%", "@", "?"};
		return codes[ordinal()];
	}
}

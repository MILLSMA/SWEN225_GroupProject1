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
	private static final String[] codes = {"+", "#", "|", "%", "@", "?"};

	public static Collection<WeaponCard> getWeapons(){
		return Arrays.asList(values());
	}

	public static int size(){
		return values().length;
	}

	/**
	 * Check for valid weapon entry, match with weaponCard enum
	 * @return enum WeaponCard value
	 */
	public static WeaponCard input(){
		while(true) {
			Scanner sc = new Scanner(System.in);
			System.out.println("== Weapons, enter code in [ ] ==");
			for (int i=0; i<size(); i++) {
				System.out.println(values()[i].getName() + " [" + codes[i] + "]");
			}
			System.out.print("Enter weapon: ");
			String guess = sc.next();
			for (int i=0; i<size(); i++) {
				if(codes[i].equalsIgnoreCase(guess)){
					return values()[i];
				}
			}
			System.out.println("Please use the given code in [ ].\n");
		}
	}

	/**
	 * move the weapon token to a new cell
	 * @param c: cell to move to
	 */
	public void moveToCell(Cell c){
		if(location != null) location.setObject(null);
		location = c;
		c.setObject(this);
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

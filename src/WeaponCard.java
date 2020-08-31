import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Enumeration of possible weapons in Cluedo
 */
public enum WeaponCard implements Card{
	CANDLESTICK,
	DAGGER,
	LEAD_PIPE,
	REVOLVER,
	ROPE,
	SPANNER;

	private Cell location;
	private static final String[] codes = {"+", "#", "|", "%", "@", "?"};

	/**
	 * Convert enum to unmodifiable collection
	 * @return collection of all weapon cards
	 */
	public static Collection<WeaponCard> getWeapons(){
		return Collections.unmodifiableCollection(Arrays.asList(values()));
	}

	/**
	 * Get number of possible weapons
	 * @return number of weapons
	 */
	public static int size(){
		return values().length;
	}

	/**
	 * move the weapon token to a new cell
	 * @param c: cell to move to
	 */
	public void setCell(Cell c){
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
		return "\u001b[34m" + codes[ordinal()] + CharacterCard.ANSI_RESET;
	}
}

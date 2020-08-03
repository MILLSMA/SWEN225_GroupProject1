import java.util.Arrays;
import java.util.Collection;

public enum CharacterCard implements Card {
	MISS_SCARLETT,
	COLONEL_MUSTARD,
	MRS_WHITE,
	MR_GREEN,
	MRS_PEACOCK,
	PROFESSOR_PLUM;

	public static Collection<CharacterCard> getCharacters(){
		return Arrays.asList(values());
	}

	public static int size() {
		return values().length;
	}

	/**
	 * prints the name of a character with correct spacing can in title case
	 * @return String of character name
	 */
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
		String[] codes = {"s", "c", "w", "g", "k", "p"};
		return codes[ordinal()];
	}
}

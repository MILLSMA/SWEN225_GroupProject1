import java.util.Arrays;
import java.util.Collection;

public enum CharacterCard implements Card {
	MISS_SCARLETT,
	COLONEL_MUSTARD,
	MRS_WHITE,
	MR_GREEN,
	MRS_PEACOCK,
	PROFESSOR_PLUM;

	//color codes so that the players may have color in the ASCII display
	//may not work on all terminal environments (eg. windows console)
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

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

	public static Position characterStartPosition(CharacterCard character){
		switch(character){
			case MRS_WHITE:
				return new Position(0,9);
			case MR_GREEN:
				return new Position(17, 0);
			case MRS_PEACOCK:
				return new Position(24, 7);
			case MISS_SCARLETT:
				return new Position(19, 23);
			case PROFESSOR_PLUM:
				return new Position(7, 23);
			case COLONEL_MUSTARD:
				return new Position(7, 0);
			 default:
			 	return new Position(1, 1);
		}
	}

	@Override
	public String toString() {
		String[] codes = {ANSI_RED+"s", ANSI_YELLOW+"c", ANSI_WHITE+"w", ANSI_GREEN+"g", ANSI_CYAN+"k", ANSI_PURPLE + "p"};
		return codes[ordinal()] + ANSI_RESET;
	}
}

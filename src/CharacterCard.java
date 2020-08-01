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

	@Override
	public String getName() {
		return name().toLowerCase();
	}

	@Override
	public String toString() {
		String[] codes = {"s", "c", "w", "g", "k", "p"};
		return codes[ordinal()];
	}

	/**
	 * prints the name of a character with correct spacing can in title case
	 * @return String of character name
	 */
	public String getNameString(){
		StringBuilder sb = new StringBuilder();
		boolean capital = true;
		for (char c : getName().replace("_", " ").toCharArray()) {
			if(capital){c = Character.toUpperCase(c);}
			sb.append(c);
			capital = (" ".indexOf((int) c) >= 0);
		}
		return sb.toString();

	}
}

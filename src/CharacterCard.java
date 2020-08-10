import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

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

	private Cell cell;
	private static final String[] colours = {ANSI_RED, ANSI_YELLOW, ANSI_WHITE, ANSI_GREEN, ANSI_CYAN, ANSI_PURPLE};
	private static final String[] codes = {"s", "c", "w", "g", "k", "p"};

	public static Collection<CharacterCard> getCharacters(){
		return Arrays.asList(values());
	}

	public static int size() {
		return values().length;
	}

	public Cell getCell(){
		return cell;
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

	/**
	 * Get valid Character
	 * Match given string with characterCard enum
	 * @return enum CharacterCard value
	 */
	public static CharacterCard input(){
		Scanner sc = new Scanner(System.in);
		System.out.println("== Characters, enter code in [ ] ==");
		for (int i=0; i<size(); i++) {
			System.out.println(values()[i].getName() + " [" + colours[i] + codes[i] + ANSI_RESET +"]");
		}
		while(true) {
			System.out.print("Enter character: ");
			String guess = sc.next();
			for (int i=0; i<size(); i++) {
				if(codes[i].equalsIgnoreCase(guess)){
					return values()[i];
				}
			}
			System.out.println("Please use a given code in [ ].");
		}
	}

	/**
	 * Set a characters position give a preset value
	 * @param character: character to be set
	 * @return position of character
	 */
	public static Position characterStartPosition(CharacterCard character){
		switch(character){
			case MRS_WHITE:
				return new Position(0,9);
			case MR_GREEN:
				return new Position(0, 14);
			case MRS_PEACOCK:
				return new Position(6, 23);
			case MISS_SCARLETT:
				return new Position(24, 7);
			case PROFESSOR_PLUM:
				return new Position(19, 23);
			case COLONEL_MUSTARD:
				return new Position(17, 0);
			 default:
			 	return new Position(1, 1);
		}
	}

	/**
	 * change the call a character is in
	 * @param c: c to move to
	 */
	public void setCell(Cell c){
		if(cell != null) cell.setObject(null);
		cell = c;
		if (cell != null) c.setObject(this);
	}

	@Override
	public String toString() {
		return colours[ordinal()] + codes[ordinal()] + ANSI_RESET;
	}
}

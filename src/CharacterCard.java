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

	private Cell location; //TODO: change from player location to account for unplayed characters and access location without knowing player
	private static final String[] colours = {ANSI_RED, ANSI_YELLOW, ANSI_WHITE, ANSI_GREEN, ANSI_CYAN, ANSI_PURPLE};
	private static final String[] codes = {"s", "c", "w", "g", "k", "p"};

	public static Collection<CharacterCard> getCharacters(){
		return Arrays.asList(values());
	}

	public void changeLocation(Cell c){
		this.location = c;
	}

	public static int size() {
		return values().length;
	}

	public Cell getLocation(){
		return location;
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

	public void moveToCell(Cell c){
		if(this.location != null) this.location.setObject(null);
		this.location = c;
		c.setObject(this);
	}

	@Override
	public String toString() {
		return colours[ordinal()] + codes[ordinal()] + ANSI_RESET;
	}
}

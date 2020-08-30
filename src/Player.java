import java.util.*;

/**
 * Contains a player's details, such as its name, character, its hand and if it's out of the game
 */
public class Player
{

	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Player Attributes
	private final CharacterCard token;
	private final String name;
	private boolean isExcluded;

	//Player Associations
	private final List<Card> cards;
	private final List<Cell> cellsMovedTo = new ArrayList<>();

	//------------------------
	// CONSTRUCTOR
	//------------------------

	/**
	 * Construct a Player
	 * @param aToken chosen token
	 * @param n chosen name
	 */
	public Player(CharacterCard aToken, String n)
	{
		token = aToken;
		isExcluded = false;
		cards = new ArrayList<>();
		name = n;
	}

	//------------------------
	// INTERFACE
	//------------------------

	/**
	 * Exclude player from rest of game
	 */
	public void setPlayerOut(){
		isExcluded = true;
	}

	/**
	 * Get this player's name
	 * @return player name
	 */
	public String getName(){
		return this.name;
	}

	/**
	 * Get this player's token
	 * @return player token
	 */
	public CharacterCard getToken(){
		return token;
	}

	/**
	 * Get the cell this player is currently located in
	 * @return player cell
	 */
	public Cell getLocation(){
		return token.getCell();
	}

	/**
	 * Return if player is still in the game
	 * @return true if player still in
	 */
	public boolean isStillIn(){
		return !isExcluded;
	}

	/**
	 * Return player's hand
	 * @return cards on player's hand
	 */
	public List<Card> getCards(){
		return Collections.unmodifiableList(cards);
	}

	/**
	 * Move the player and associated character token to a new cell
	 * @param cell: cell to move to
	 */
	public void setCell(Cell cell){
		token.setCell(cell);
	}

	/**
	 * Reset the cells that the Player has moved to, should be done at end of round
	 * TODO: do a test on this
	 */
	public void clearCellsMovedTo() {
		for (Cell c : cellsMovedTo) {
			c.setUsedInRound(false);
		}
		cellsMovedTo.clear();
	}

	/**
	 * Add a cell that the Player has moved to
	 * @param c cell Player moved to
	 */
	public void addCellsMovedTo(Cell c) {
		cellsMovedTo.add(c);
	}

	/**
	 * add a card to the players held cards
	 * @param aCard: card to add
	 */
	public void addCard(Card aCard) {
		cards.add(aCard);
	}

	public String toString() {
		return token.toString();
	}
}
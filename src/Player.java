import java.util.*;

public class Player
{

	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Player Attributes
	private final CharacterCard token;
	private boolean isExcluded;

	//Player Associations
	private final List<Card> cards;

	//------------------------
	// CONSTRUCTOR
	//------------------------

	public Player(CharacterCard aToken, Cell aLocation, boolean aIsExcluded)
	{
		token = aToken;
		token.changeLocation(aLocation);
		isExcluded = aIsExcluded;
		cards = new ArrayList<>();
	}

	//------------------------
	// INTERFACE
	//------------------------

	public void setLocation(Cell aLocation) {
		token.changeLocation(aLocation);
	}

	public void setIsExcluded(boolean aIsExcluded){
		isExcluded = aIsExcluded;
	}

	public CharacterCard getToken(){
		return token;
	}

	public Cell getLocation(){
		return token.getLocation();
	}

	public boolean getExcluded(){
		return isExcluded;
	}

	public List<Card> getCards(){
		return Collections.unmodifiableList(cards);
	}

	/**
	 * move the player and associated character token to a new cell
	 * @param cell: cell to move to
	 */
	public void moveToCell(Cell cell){
		// removes the player from the cell
		token.getLocation().setObject(null);
		//tell the player which cell they are in
		token.changeLocation(cell);
		//place the player in the cell
		token.getLocation().setObject(getToken());
	}

	/**
	 * add a card to the players held cards
	 * @param aCard: card to add
	 */
	public void addCard(Card aCard) {
		cards.add(aCard);
	}

	/**
	 * print out the player's hand in a readable way
	 */
	public void displayHand(){
		System.out.println("Cards held by player: " + this.token.getName());
		for(Card c : cards){
			System.out.println(c.getName());
		}
	}

	public String toString() {
		return token.toString();
	}
}
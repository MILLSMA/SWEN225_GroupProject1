import java.util.*;

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

	//------------------------
	// CONSTRUCTOR
	//------------------------

	public Player(CharacterCard aToken, Cell aLocation, String n)
	{
		token = aToken;
		token.setCell(aLocation);
		isExcluded = false;
		cards = new ArrayList<>();
		name = n;
	}

	//------------------------
	// INTERFACE
	//------------------------

	public void setIsExcluded(boolean aIsExcluded){
		isExcluded = aIsExcluded;
	}

	public CharacterCard getToken(){
		return token;
	}

	public Cell getLocation(){
		return token.getCell();
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
	public void setCell(Cell cell){
		token.setCell(cell);
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
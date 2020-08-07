import java.util.*;

public class Player
{

	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Player Attributes
	private final CharacterCard token;
	private Cell location;
	private boolean isExcluded;

	//Player Associations
	private final List<Card> cards;

	//------------------------
	// CONSTRUCTOR
	//------------------------

	public Player(CharacterCard aToken, Cell aLocation, boolean aIsExcluded)
	{
		token = aToken;
		location = aLocation;
		isExcluded = aIsExcluded;
		cards = new ArrayList<>();
	}

	//------------------------
	// INTERFACE
	//------------------------

//	public void setToken(CharacterCard aToken)
//	{
//		token = aToken;
//	}

	public void setLocation(Cell aLocation)
	{
		location = aLocation;

	}

	public void setIsExcluded(boolean aIsExcluded)
	{
		isExcluded = aIsExcluded;
	}

	public CharacterCard getToken()
	{
		return token;
	}

	public Cell getLocation()
	{
		return location;
	}


	public boolean isExcluded()
	{
		return isExcluded;
	}

	public List<Card> getCards()
	{
		return Collections.unmodifiableList(cards);
	}

	public void updatePosition(Cell cell){
		// removes the player from the cell
		location.setObject(null);
		//tell the player which cell they are in
		location = cell;
		//place the player in the cell
		location.setObject(getToken());
	}

	public boolean addCard(Card aCard)
	{
		if (cards.contains(aCard)) { return false; }
		cards.add(aCard);
		return true;
	}

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
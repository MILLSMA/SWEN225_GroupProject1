import java.util.*;

public class Player
{

	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Player Attributes
	private final CharacterCard token;
	private Position position;
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
		if (aLocation != null) position = aLocation.getPosition();
		isExcluded = aIsExcluded;
		cards = new ArrayList<>();
	}

	//------------------------
	// INTERFACE
	//------------------------

	public void setLocation(Cell aLocation)
	{
		token.changeLocation(aLocation);
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
		return token.getLocation();
	}

	public boolean isExcluded()
	{
		return isExcluded;
	}

	public List<Card> getCards()
	{
		return Collections.unmodifiableList(cards);
	}

	public void moveToCell(Cell cell){
		// removes the player from the cell
		token.getLocation().setObject(null);
		//tell the player which cell they are in
		token.changeLocation(cell);
		//place the player in the cell
		token.getLocation().setObject(getToken());
		position = cell.getPosition();
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
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
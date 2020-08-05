import java.util.*;

public class Room {
	private RoomCard card;
	private String type = "";
	Room(RoomCard roomCard) {
		card = roomCard;
	}
	Room(String str){
		type = str;
	}

	public RoomCard getCard(){
		return card;
	}
	public Boolean setCard(RoomCard card) {
		if(this.card == null){
			this.card = card;
			return true;
		}
		return false;
	}

	//Room Associations
	private List<Cell> cells = new ArrayList<>();
	private List<Card> cards = new ArrayList<>();

	public boolean isProperRoom(){
		return card != null;
	}

	public String getType(){
		return type;
	}
	public List<Cell> getCells()
	{
		return Collections.unmodifiableList(cells);
	}
	public int numberOfCells()
	{
		return cells.size();
	}

	public List<Card> getCards()
	{
		return Collections.unmodifiableList(cards);
	}

	public boolean addCell(Cell aCell)
	{
		if (cells.contains(aCell)) { return false; }
		cells.add(aCell);
		return true;
	}

	/* Code from template association_RemoveMany */
	public boolean removeCell(Cell aCell)
	{
		cells.remove(aCell);
		return !cells.contains(aCell);
	}

	public boolean addCard(Card aCard)
	{
		if (cards.contains(aCard)) { return false; }
		cards.add(aCard);
		return true;
	}

	public boolean removeCard(Card aCard) {
		if (cards.contains(aCard)) {
			cards.remove(aCard);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		if(type.equals("Door")) return ")";
		else if(card != null) return card.toString();
		else if(type.equals("Hallway")) return "_";
		else return "/";
	}
}

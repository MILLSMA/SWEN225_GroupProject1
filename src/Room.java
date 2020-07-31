import java.util.*;

public class Room {

	private final RoomCard card;

	Room(RoomCard roomCard) {
		card = roomCard;
	}

	public RoomCard getCard(){
		return card;
	}

	//Room Associations
	private List<Cell> cells;
	private List<Card> cards;

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
}

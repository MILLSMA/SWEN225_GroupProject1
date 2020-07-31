import java.util.*;

public enum Room{
	KITCHEN(new RoomCard("Kitchen")),
	BALLROOM(new RoomCard("Ballroom")),
	CONSERVATORY(new RoomCard("Conservatory")),
	BILLIARD_ROOM(new RoomCard("Billiard Room")),
	DINING_ROOM(new RoomCard("Dining Room")),
	LOUNGE(new RoomCard("Lounge")),
	HALL(new RoomCard("Hall")),
	LIBRARY(new RoomCard("Library")),
	STUDY(new RoomCard("Study"));

	private final RoomCard card;

	/**
	 * gets a hashset of all the room cards
	 * @return - HashSet<Room>
	 */
	static Collection<RoomCard> getRoomCards() {
		return new HashSet<>(Arrays.asList(
				KITCHEN.getCard(),
				BALLROOM.getCard(),
				CONSERVATORY.getCard(),
				BILLIARD_ROOM.getCard(),
				DINING_ROOM.getCard(),
				LOUNGE.getCard(),
				HALL.getCard(),
				LIBRARY.getCard(),
				STUDY.getCard()
		));
	}

	Room(RoomCard roomCard) {
		card = roomCard;
	}
	public static Collection<Room> getRooms(){
		return Arrays.asList(values());
	}
	public String getName() {
		return name().toLowerCase();
	}
	@Override
	public String toString() {
		return getName();
	}

	public RoomCard getCard(){
		return card;
	}

	//Room Associations
	private List<Cell> cells;
	private List<Card> cards;

	public List<Cell> getCells()
	{
		List<Cell> newCells = Collections.unmodifiableList(cells);
		return newCells;
	}
	public int numberOfCells()
	{
		int number = cells.size();
		return number;
	}

	public List<Card> getCards()
	{
		List<Card> newCards = Collections.unmodifiableList(cards);
		return newCards;
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
		if(cells.contains(aCell)) return false;
		return true;
	}

	public boolean addCard(Card aCard)
	{
		boolean wasAdded = false;
		if (cards.contains(aCard)) { return false; }
		cards.add(aCard);
		wasAdded = true;
		return wasAdded;
	}

	public boolean removeCard(Card aCard)
	{
		boolean wasRemoved = false;
		if (cards.contains(aCard))
		{
			cards.remove(aCard);
			wasRemoved = true;
		}
		return wasRemoved;
	}

	public void delete()
	{
		while (cells.size() > 0)
		{
			Cell aCell = cells.get(cells.size() - 1);
			aCell.delete();
			cells.remove(aCell);
		}

		cards.clear();
	}
	
}

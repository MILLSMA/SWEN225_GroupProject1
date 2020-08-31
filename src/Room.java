import java.util.*;

/**
 * This Room class contains more information than the enumeration,
 * encapsulating extra information like cells and cards belonging to the room.
 */
public class Room {
	private RoomCard card;
	private final String type;

	/**
	 * Create room based on a RoomCard
	 * @param roomCard RoomCard
	 */
	Room(RoomCard roomCard) {
		card = roomCard;
		type = roomCard.getName();
	}

	/**
	 * Create room based on string, e.g. Door, Wall...
	 * @param str room type
	 */
	Room(String str){
		type = str;
	}

	/**
	 * Return RoomCard belonging to room
	 * @return card belonging to room
	 */
	public RoomCard getCard(){
		return card;
	}

	/**
	 * Assign a room card to the current room object
	 * @param c : card to assign
	 */
	public void setCard(RoomCard c) {
		if(card == null){
			card = c;
		}
	}

	//Room Associations
	private final List<Cell> cells = new ArrayList<>();
	private final List<Card> cards = new ArrayList<>();

	/**
	 * Return if room contains a card
	 * @return if room is actually a room
	 */
	public boolean isProperRoom(){
		return card != null;
	}

	/**
	 * Return if room is a door
	 * @return if room is door
	 */
	public boolean isDoor() { return type.equals("Door"); }

	/**
	 * Return if room is a hallway
	 * @return if room is hallway
	 */
	public boolean isHallway() { return type.equals("Hallway"); }

	/**
	 * Return if room is a wall
	 * @return if room is wall
	 */
	public boolean isWall() { return type.equals("Wall"); }

	/**
	 * Return collection of objects inside room
	 * @return list of cards/objects in room
	 */
	public List<Card> getCards(){
		return cards;
	}

	/**
	 * Add a cell to the room
	 * @param aCell: cell to add
	 */
	public void addCell(Cell aCell){
		cells.add(aCell);
	}

	/**
	 * add a card into a empty cell inside a room
	 * @param aCard: card to add
	 * @return the cell teh card is added to
	 */
	public Cell addCard(Card aCard){
		cards.add(aCard);
		Cell c = findEmptyCell();
		if(aCard instanceof WeaponCard){
			WeaponCard weapon = (WeaponCard) aCard;
			weapon.setCell(c);
		}
		if(aCard instanceof CharacterCard){
			((CharacterCard)aCard).setCell(c);
		}
		return c;
	}

	/**
	 * Finds the first empty cell in the room
	 * @return a cell within a room containing no items
	 * @throws RuntimeException if no empty cells are found
	 */
	public Cell findEmptyCell(){
		for(Cell c : this.cells){
			if(c.getObject() == null) {
				return c;
			}
		}
		throw new RuntimeException("The room should always have at least one empty cell");
	}

	/**
	 * Get how many cells are in the room
	 * @return size of
	 */
	public int getRoomSize(){
		return this.cells.size();
	}

	@Override
	public String toString() {
		if(isDoor()) return ")";
		else if(isHallway()) return "_";
		else if(card != null) return card.toString();
		else return "/";
	}

	/**
	 * Return room name
	 * @return room's name
	 */
	public String getName(){
		if (card == null) return type;
		return card.getName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Room room = (Room) o;
		return card == room.card;
	}

	@Override
	public int hashCode() {
		return Objects.hash(card);
	}
}

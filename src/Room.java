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
	private final List<Cell> cells = new ArrayList<>();
	private final List<Card> cards = new ArrayList<>();

	public boolean isProperRoom(){
		return card != null;
	}

	public String getType(){
		return type;
	}
//	public List<Cell> getCells()
//	{
//		return Collections.unmodifiableList(cells);
//	}
//	public int numberOfCells()
//	{
//		return cells.size();
//	}
//
//	public List<Card> getCards()
//	{
//		return Collections.unmodifiableList(cards);
//	}

	public boolean addCell(Cell aCell)
	{
		if (cells.contains(aCell)) { return false; }
		cells.add(aCell);
		return true;
	}

	public void addCard(Card aCard)
	{
		cards.add(aCard);
		Cell c = findEmptyCell();
		if(aCard instanceof WeaponCard){
			((WeaponCard)aCard).moveToRoom(this);
			((WeaponCard)aCard).moveToCell(c);
		}
		if(aCard instanceof CharacterCard){
			((CharacterCard)aCard).moveToCell(c);
		}
	}

	/**
	 * returns the first empty cell in the room
	 * @return c: a cell within a room containing no items
	 */
	public Cell findEmptyCell(){
		for(Cell c : this.cells){//TODO: populate room with cells - door should be in same room as cells
			if(c.getObject() == null) {
				return c;
			}
		}
		throw new RuntimeException("The room should always have at least one empty cell");
	}

	public int getRoomSize(){
		return this.cells.size();
	}
	@Override
	public String toString() {
		if(type.equals("Door")) return ")";
		else if(card != null) return card.toString();
		else if(type.equals("Hallway")) return "_";
		else return "/";
	}
}

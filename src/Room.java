import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Room {
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

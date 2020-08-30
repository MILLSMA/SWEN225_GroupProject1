import java.util.Objects;

/**
 * Represents a cell's Position on the board
 */
public class Position
{

	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Position Attributes
	private final int row;
	private final int col;

	/**
	 * Construct a position
	 * @param aRow row on Board
	 * @param aCol column on Board
	 */
	public Position(int aRow, int aCol){
		row = aRow;
		col = aCol;
	}

	//------------------------
	// INTERFACE
	//------------------------

	/**
	 * Get position's row
	 * @return row of position
	 */
	public int getRow(){
		return row;
	}

	/**
	 * Get position's column
	 * @return column of position
	 */
	public int getCol(){
		return col;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Position position = (Position) o;
		return row == position.row &&
				col == position.col;
	}

	@Override
	public int hashCode() {
		return Objects.hash(row, col);
	}
	public String toString(){
		return "(" + getRow()+ ", " + getCol()+ ")";
	}
}
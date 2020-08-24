import java.util.Objects;

public class Position
{

	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Position Attributes
	private int row;
	private int col;

	//------------------------
	// CONSTRUCTOR
	//------------------------

	public Position(int aRow, int aCol){
		row = aRow;
		col = aCol;
	}

	//------------------------
	// INTERFACE
	//------------------------

	public void setRow(int aRow){
		row = aRow;
	}

	public void setCol(int aCol){
		col = aCol;
	}

	public int getRow(){
		return row;
	}

	public int getCol(){
		return col;
	}

	public boolean equals(Position pos){
		return (this.row == pos.getRow() && this.col == pos.getCol());
	}
	@Override
	public int hashCode() {
		return Objects.hash(row, col);
	}
	public String toString(){
		return "(" + getRow()+ ", " + getCol()+ ")";
	}
}
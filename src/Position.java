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

	public Position(int aRow, int aCol)
	{
		row = aRow;
		col = aCol;
	}

	//------------------------
	// INTERFACE
	//------------------------

	public boolean setRow(int aRow)
	{
		row = aRow;
		return true;
	}

	public boolean setCol(int aCol)
	{
		col = aCol;
		return true;
	}

	public int getRow()
	{
		return row;
	}

	public int getCol()
	{
		return col;
	}


	public String toString()
	{
		return "(" + getRow()+ ", " + getCol()+ ")";
	}
}
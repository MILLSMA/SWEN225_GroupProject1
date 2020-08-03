/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.0.515.d9da8f6c modeling language!*/



// line 50 "model.ump"
// line 119 "model.ump"
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
		boolean wasSet = false;
		row = aRow;
		wasSet = true;
		return wasSet;
	}

	public boolean setCol(int aCol)
	{
		boolean wasSet = false;
		col = aCol;
		wasSet = true;
		return wasSet;
	}

	public int getRow()
	{
		return row;
	}

	public int getCol()
	{
		return col;
	}

	public void delete()
	{}


	public String toString()
	{
		return super.toString() + "["+
				"row" + ":" + getRow()+ "," +
				"col" + ":" + getCol()+ "]";
	}
}
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Board
{
     int ROWS = 26, COLS = 26;
	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Board Associations
	private final List<Cell> cells;
	private final List<Player> players;
	private final HashMap<String, Room> rooms;
	private HashMap<Player, Position> playerPositionMap;
	private Cell[][] board;
	//------------------------
	// CONSTRUCTOR
	//------------------------

	public Board(Collection<Player> allPlayers)
	{
		cells = new ArrayList<>();
		players = new ArrayList<>(allPlayers);
		playerPositionMap = new HashMap<>();
		rooms = new HashMap<>();
		rooms.put("Door", new Room("Door", true));
		rooms.put("Wall", new Room("Wall", false));
		rooms.put("Hallway", new Room("Hallway", true));
		for(RoomCard card : RoomCard.getRooms()){
			rooms.put(card.getName(), new Room(card));
		}
		board = new Cell[ROWS][COLS];
		createCells();
		for (Player player : players) {
			Position playerPos = CharacterCard.characterStartPosition(player.getToken());
			board[playerPos.getRow()][playerPos.getCol()].setObject(player.getToken());
			playerPositionMap.put(player, playerPos);
		}
	}


	//------------------------
	// INTERFACE
	//------------------------

	public Room getRoomFromCard(RoomCard card){
		return rooms.get(card.getName());
	}
	public Room getRoom(String name){
		return rooms.get(name);
	}
	private Room getRoomFromChar(char c) {
		for(RoomCard card : RoomCard.getRooms()) {
			if (card.toString().charAt(0) == c) return getRoomFromCard(card);
		}
		if(c == ')')return rooms.get("Door");
		if(c == '/')return rooms.get("Wall");
		if(c == '_')return rooms.get("Hallway");
		return null;
	}
	private void createCells(){
		try {
			Scanner sc = new Scanner(new File("CluedoBoard.txt")).useDelimiter("(\\b|\\B)");
			int xPosition = 0, yPosition = 0;
			String left, cell, right;
			left = sc.next();
			cell = sc.next();
			right = sc.next();
			while(sc.hasNext()){
				Position cellPosition = new Position(xPosition, yPosition);
				Room cellRoom = getRoomFromChar(cell.charAt(0));
				Cell newCell = new Cell(cellPosition, cellRoom);
				board[yPosition][xPosition] = newCell;
				if(left.charAt(0) != '/') newCell.setDirection(Cell.Directions.WEST, true);
				if(right.charAt(0) != '/') newCell.setDirection(Cell.Directions.EAST, true);

				if(sc.hasNext("\n")){
					sc.next();
					xPosition = 0;
					yPosition++;
					if(sc.hasNext()) left = sc.next();
				}else {
					xPosition++;
					left = right;
				}

				if (sc.hasNext()) cell = sc.next();
				if (sc.hasNext()) right = sc.next();
			}
		}catch(FileNotFoundException e){
			System.out.println("Cluedo board file not found.");
		}
		updateCellDirections();
	}
	private void updateCellDirections(){
		for (int xIndex = 0; xIndex < ROWS ; xIndex++) {
			for (int yIndex = 1; yIndex < COLS -3; yIndex++) {
				Cell currentCell = board[xIndex][yIndex];
				Cell cellAbove = board[xIndex][yIndex-1];
				Cell cellBelow = board[xIndex][yIndex+1];
				if(currentCell.getRoom() == rooms.get("Door")){
					currentCell.setDirection(Cell.Directions.NORTH, true);
					currentCell.setDirection(Cell.Directions.SOUTH, true);
					cellBelow.setDirection(Cell.Directions.NORTH, true);
					cellAbove.setDirection(Cell.Directions.SOUTH, true);
				}
				else if(currentCell.getRoom() == cellBelow.getRoom()){
					currentCell.setDirection(Cell.Directions.SOUTH, true);
					cellBelow.setDirection(Cell.Directions.NORTH, true);
				}
				else if(currentCell.getRoom() == cellAbove.getRoom()){
					currentCell.setDirection(Cell.Directions.NORTH, true);
					cellBelow.setDirection(Cell.Directions.SOUTH, true);
				}
			}
		}
	}
	/* Code from template association_GetMany */
	public Cell getCell(int index)
	{
		return cells.get(index);
	}

	public List<Cell> getCells()
	{
		return Collections.unmodifiableList(cells);
	}

	public int numberOfCells()
	{
		return cells.size();
	}

	/* Code from template association_GetMany */
	public Position getPlayerLocation(Player p)
	{
		return playerPositionMap.get(p);
	}

	public List<Player> getPlayers()
	{
		return Collections.unmodifiableList(players);
	}

	public int numberOfPlayers()
	{
		return players.size();
	}

	/* Code from template association_AddManyToOne */
	public Cell addCell(Position aPosition, Room room)
	{
		return new Cell(aPosition, room);
	}

	public boolean addCell(Cell aCell)
	{
		if (cells.contains(aCell)) { return false; }
		cells.add(aCell);
		return true;
	}
	/* Code from template association_RequiredNumberOfMethod */
	public static int requiredNumberOfPlayers()
	{
		return 6;
	}


	// line 34 "model.ump"
	public Position checkValidMove(Player p, String code, int steps){

		return null;
	}

	// line 36 "model.ump"
	public void move(Player p, Position old){

	}

	@Override
	public String toString() {
		StringBuilder boardLayout = new StringBuilder();
		for (int xIndex = 0; xIndex < ROWS; xIndex++) {
			for (int yIndex = 0; yIndex < COLS; yIndex++) {
				if(board[xIndex][yIndex] == null) continue;
				boardLayout.append(board[xIndex][yIndex].toString()).append(" ");
			}
			boardLayout.append("\n");
		}
		return boardLayout.toString();
	}
}
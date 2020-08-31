import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Board model storing where the
 */
public class Board
{
	public static final int ROWS = 25, COLS = 24;
	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Board Associations
	private final HashMap<String, Room> rooms;
 	public final Cell[][] board;

 	private static final String boardFile = "///////////////////_/O O O O/_///////////////////\n" +
			"/K K K K K K///_ _ _/O O O O/_ _ _///C C C C C C/\n" +
			"/K K K K K K/_ _/O O O O O O O O/_ _/C C C C C C/\n" +
			"/K K K K K K/_ _/O O O O O O O O/_ _/C C C C C C/\n" +
			"/K K K K K K/_ _/O O O O O O O O/_ _/) C C C C C/\n" +
			"/K K K K K K/_ _ ) O O O O O O ) _ _ _/C C C C C/\n" +
			"///K K K ) K/_ _/O O O O O O O O/_ _ _ _ _ _ _ _/\n" +
			"/_ _ _ _ _ _ _ _/O ) O O O O ) O)_ _ _ _ _ _ _///\n" +
			"///_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _/B B B B B B/\n" +
			"/D D D D D/_ _ _ _ _ _ _ _ _ _ _ _ _ ) B B B B B/\n" +
			"/D D D D D D D D/_ _///////////_ _ _/B B B B B B/\n" +
			"/D D D D D D D D/_ _///////////_ _ _/B B B B B B/\n" +
			"/D D D D D D D ) _ _///////////_ _ _/B B B B ) B/\n" +
			"/D D D D D D D D/_ _///////////_ _ _ _ _ _ _ _///\n" +
			"/D D D D D D D D/_ _///////////_ _ _/I I ) I I///\n" +
			"/D D D D D D ) D/_ _///////////_ _/I I I I I I I/\n" +
			"///_ _ _ _ _ _ _ _ _///////////_ _ ) I I I I I I/\n" +
			"/_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _/I I I I I I I/\n" +
			"///_ _ _ _ _ _ _ _/H H ) ) H H/_ _ _/I I I I I///\n" +
			"/L L L L L L )/_ _/H H H H H H/_ _ _ _ _ _ _ _ _/\n" +
			"/L L L L L L L/_ _/H H H H H ) _ _ _ _ _ _ _ _///\n" +
			"/L L L L L L L/_ _/H H H H H H/_ _/) S S S S S///\n" +
			"/L L L L L L L/_ _/H H H H H H/_ _/S S S S S S S/\n" +
			"/L L L L L L L/_ _/H H H H H H/_ _/S S S S S S S/\n" +
			"/L L L L L L///_/////H H H H/////_///S S S S S S/\n";
	//------------------------
	// CONSTRUCTOR
	//------------------------

	/**
	 * Construct board based on given players
	 * @param allPlayers players to add to board
	 */
	public Board(Collection<Player> allPlayers)
	{
		rooms = new HashMap<>();
		//creates the dummy rooms which are only needed for mechanics (not gameplay)
		rooms.put("Door", new Room("Door"));
		rooms.put("Wall", new Room("Wall"));
		rooms.put("Hallway", new Room("Hallway"));
		for(RoomCard card : RoomCard.getRooms()){
			rooms.put(card.getName(), new Room(card));
		}
		board = new Cell[ROWS][COLS];
		createCells();
		for (Player player : allPlayers) {
			//gets the characters start position
			Position playerPos = CharacterCard.characterStartPosition(player.getToken());
			//gets the cell the player is on (based on players position)
			Cell playerCell = board[playerPos.getRow()][playerPos.getCol()];
			//tells the cell which player is on them
			playerCell.setObject(player.getToken());
			//tell the player which cell they are in
			player.setCell(playerCell);
		}
	}


	//------------------------
	// INTERFACE
	//------------------------

	/**
	 * Return a Room based on a given RoomCard
	 * @param card RoomCard to find
	 * @return corresponding room
	 */
	public Room getRoom(RoomCard card){
		return rooms.get(card.getName());
	}

	/**
	 * for reading the text file, when a character is given return
	 * whichever room that character corresponds with
	 * @param c - character to check against
	 * @return - the Room which the character corresponds too (or null if not found)
	 */
	private Room getRoom(char c) {
		for(RoomCard card : RoomCard.getRooms()) {
			if (card.toString().charAt(0) == c) return getRoom(card);
		}
		if(c == ')')return new Room("Door");
		if(c == '/')return rooms.get("Wall");
		if(c == '_')return rooms.get("Hallway");
		return null;
	}

	/**
	 * Return a given room's colour (based on its type) on board
	 * @param room room to display
	 * @return given room's colour
	 */
	private Color getRoomColor(Room room){
		if (room.isProperRoom()) return Color.LIGHT_GRAY;
		else if(room.isHallway()) return Color.WHITE;
		else if(room.isDoor()) return Color.LIGHT_GRAY.darker();
		else return Color.DARK_GRAY;
	}

	/**
	 * Add weapons randomly to the board
	 */
	private void placeWeapons(){
		List<RoomCard> roomValues = new ArrayList<>(RoomCard.getRooms());
		List<WeaponCard> weapons = new ArrayList<>(WeaponCard.getWeapons());
		Random rand = new Random();
		RoomCard currentRoom;
		for(WeaponCard w: weapons){
			currentRoom = roomValues.remove(rand.nextInt(roomValues.size()));
			rooms.get(currentRoom.getName()).addCard(w);
		}
	}

	/**
	 * reads the input file and creates the board accordingly
	 */
	private void createCells(){
		//scanner to read the hardcoded CluedoBoard.txt file with a delimiter that reads all characters including whitespace
		Scanner sc = new Scanner(boardFile).useDelimiter("(\\b|\\B)");
		int xPosition = 0, yPosition = 0;
		String left, cell, right;
		//read whats on the left and right of the cell as well as the cell
		left = sc.next();
		cell = sc.next();
		right = sc.next();
		//while the board file still has characters to read
		while(sc.hasNext()){
			Position cellPosition = new Position(yPosition, xPosition);
			Room cellRoom = getRoom(cell.charAt(0));
			Cell newCell = new Cell(cellPosition, cellRoom);
			//add cell to room
			if(cellRoom != null){
				cellRoom.addCell(newCell);
				newCell.setColor(getRoomColor(cellRoom));
			}
			board[yPosition][xPosition] = newCell;
			if(left.charAt(0) != '/') newCell.setDirection(Cell.Direction.WEST, true);
			if(right.charAt(0) != '/') newCell.setDirection(Cell.Direction.EAST, true);

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
		updateCellDirections();
		placeWeapons();
	}

	/**
	 * goes through all the cells and checks what is above and
	 * beneath it and adds the correct directions to the available
	 * directions list in each cell.
	 */
	private void updateCellDirections(){
		for (int xIndex = 0; xIndex < ROWS - 1 ; xIndex++) {
			for (int yIndex = 0; yIndex < COLS - 2 ; yIndex++) {
				Cell currentCell = board[xIndex][yIndex];
				Cell belowCell = board[xIndex + 1][yIndex];
				if(currentCell.getRoom() == belowCell.getRoom()){
					currentCell.setDirection(Cell.Direction.SOUTH, true);
					belowCell.setDirection(Cell.Direction.NORTH, true);
				}
				if(currentCell.getRoom().isDoor()){
					Cell leftCell = board[xIndex][yIndex - 1];
					Cell rightCell = board[xIndex][yIndex + 1];
					RoomCard doorCard;
					//set the door to the room that it is closest too
					doorCard = (leftCell.getRoom().isProperRoom()) ? leftCell.getRoom().getCard() : rightCell.getRoom().getCard();
					doorCard.addDoor(currentCell);
					currentCell.getRoom().setCard(doorCard);
					//sets the cell above able to move down into the doorway
					board[xIndex - 1][yIndex].setDirection(Cell.Direction.SOUTH, true);
					currentCell.setDirection(Cell.Direction.SOUTH, true);
					currentCell.setDirection(Cell.Direction.NORTH, true);
					belowCell.setDirection(Cell.Direction.NORTH, true);
				}
				if(currentCell.getRoom() == rooms.get("Wall")){
					// All directions false
					for (Cell.Direction d : Cell.Direction.values()) {
						currentCell.setDirection(d, false);
					}
				}
			}
		}
	}

	/**
	 * Checks if the player has already used the Cell in the current round
	 * @param pos - position
	 * @param dir - direction they chose to move
	 * @return if move is illegal
	 */
	public boolean isCellUsed(Position pos, Cell.Direction dir) {
		int row = pos.getRow();
		int col = pos.getCol();
		switch(dir){
			case NORTH:
				row--;
				break;
			case SOUTH:
				row++;
				break;
			case EAST:
				col++;
				break;
			case WEST:
				col--;
				break;
		}
		if (row < 0 || col < 0 || row >= ROWS || col >= COLS) return true;
		return board[row][col].isUsedInRound() || board[row][col].getObject() != null;
	}

	/**
	 * Retrieve a cell's neighbour based on given direction
	 * @param cell cell to travel from
	 * @param dir direction to travel to
	 * @return neighbour cell
	 */
	public Cell getNeighbourCell(Cell cell, Cell.Direction dir){
		Position position = cell.getPosition();
		int row = position.getRow();
		int col = position.getCol();
		switch(dir){
			case NORTH:
				row--;
				break;
			case SOUTH:
				row++;
				break;
			case EAST:
				col++;
				break;
			case WEST:
				col--;
				break;
		}
		return board[row][col];
	}

	/**
	 * checks for a room in the four cells surround a players cell
	 * @param p: player
	 * @return a room that has been found, null if none exists
	 */
	public Room checkSurroundingCells(Player p){
		Position playerPos = p.getLocation().getPosition();
		int row = playerPos.getRow();
		int col = playerPos.getCol();
		Cell south = board[row + 1][col];
		if(south.getRoom().isProperRoom()){
			return south.getRoom();
		}
		Cell north = board[row - 1][col];
		if(north.getRoom().isProperRoom()){
			return north.getRoom();
		}
		Cell east = board[row][col + 1];
		if(east.getRoom().isProperRoom()){
			return east.getRoom();
		}
		Cell west = board[row][col - 1];
		if(west.getRoom().isProperRoom()){
			return west.getRoom();
		}
		return null;
	}

	/**
	 * prints the board to the terminal for benefit of the player
	 * @return graphical ascii representation of the board
	 */
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
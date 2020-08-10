import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Board
{
	private final int ROWS = 25, COLS = 24;
	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Board Associations
	private final HashMap<String, Room> rooms;
 	private final Cell[][] board;

 	private static final String boardFile = "///////////////////_/O O O O/_///////////////////\n" +
			"/K K K K K K///_ _ _/O O O O/_ _ _///C C C C C C/\n" +
			"/K K K K K K/_ _/O O O O O O O O/_ _/C C C C C C/\n" +
			"/K K K K K K/_ _/O O O O O O O O/_ _/C C C C C C/\n" +
			"/K K K K K K/_ _/O O O O O O O O/_ _/) C C C C C/\n" +
			"/K K K K K K/_ _)O O O O O O O O)_ _ _/C C C C C/\n" +
			"///K K K ) K/_ _/O O O O O O O O/_ _ _ _ _ _ _ _/\n" +
			"/_ _ _ _ _ _ _ _/O ) O O O O ) O)_ _ _ _ _ _ _ _/\n" +
			"///_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _/B B B B B B/\n" +
			"/D D D D D/_ _ _ _ _ _ _ _ _ _ _ _ _ ) B B B B B/\n" +
			"/D D D D D D D D/_ _///////////_ _ _/B B B B B B/\n" +
			"/D D D D D D D D/_ _///////////_ _ _/B B B B B B/\n" +
			"/D D D D D D D ) _ _///////////_ _ _/B B B B ) B/\n" +
			"/D D D D D D D D/_ _///////////_ _ _ _ _ _ _ _///\n" +
			"/D D D D D D D D/_ _///////////_ _ _/I I ) I I///\n" +
			"/D D D D D D D D/_ _///////////_ _/I I I I I I I/\n" +
			"///_ _ _ _ _ _ _ _ _///////////_ _ ) I I I I I I/\n" +
			"/_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _/I I I I I I I/\n" +
			"///_ _ _ _ _ _ _ _/H H ) ) H H/_ _ _/I I I I I///\n" +
			"/L L L L L L )/_ _/H H H H H H/_ _ _ _ _ _ _ _ _/\n" +
			"/L L L L L L L/_ _/H H H H H H/_ _ _ _ _ _ _ _///\n" +
			"/L L L L L L L/_ _/H H H H H H/_ _/) S S S S S///\n" +
			"/L L L L L L L/_ _/H H H H H H/_ _/S S S S S S S/\n" +
			"/L L L L L L L/_ _/H H H H H H/_ _/S S S S S S S/\n" +
			"/L L L L L L///_/////H H H H/////_///S S S S S S/\n";
	//------------------------
	// CONSTRUCTOR
	//------------------------

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

	private void weaponStartPoints(){
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
			if(cellRoom != null){cellRoom.addCell(newCell);}
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
		weaponStartPoints();
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
				if(currentCell.getRoom().getType().equals("Door")){
					Cell leftCell = board[xIndex][yIndex - 1];
					Cell rightCell = board[xIndex][yIndex + 1];
					RoomCard doorCard;
					//set the door to the room that it is closest too
					doorCard = (leftCell.getRoom().isProperRoom()) ? leftCell.getRoom().getCard() : rightCell.getRoom().getCard();
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
	 * moves the player on the board in which every direction is chosen
	 * @param p - player to be moved
	 * @param dir - relative direction for player to be moved
	 * @return - the cell the player was moved into
	 */
	public Cell move(Player p, Cell.Direction dir) {
		Position playerPos = p.getLocation().getPosition();
		switch(dir){
			case NORTH:
				playerPos.setRow(playerPos.getRow() - 1);
				break;
			case SOUTH:
				playerPos.setRow(playerPos.getRow() + 1);
				break;
			case EAST:
				playerPos.setCol(playerPos.getCol() + 1);
				break;
			case WEST:
				playerPos.setCol(playerPos.getCol() - 1);
				break;
		}
		return board[playerPos.getRow()][playerPos.getCol()];
	}

	/**
	 * Checks if the player has already used the Cell in the current round
	 * @param p - Player
	 * @param dir - direction they chose to move
	 * @return if move is illegal
	 */
	public boolean isCellUsed(Player p, Cell.Direction dir) {
		Position playerPos = p.getLocation().getPosition();
		int row = playerPos.getRow();
		int col = playerPos.getCol();
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
		if (row < 0 || col < 0 || row >= ROWS || col >= COLS) return false;
		return board[row][col].isUsedInRound() || board[row][col].getObject() != null;
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
		if(!south.getRoom().toString().equals("_") && south.getRoom().getRoomSize() > 1){
			return south.getRoom();
		}
		Cell north = board[row - 1][col];
		if(!north.getRoom().toString().equals("_") && north.getRoom().getRoomSize()> 1){
			return north.getRoom();
		}
		Cell east = board[row][col + 1];
		if(!east.getRoom().toString().equals("_") && east.getRoom().getRoomSize() > 1 ){
			return east.getRoom();
		}
		Cell west = board[row][col - 1];
		if(!west.getRoom().toString().equals("_") && west.getRoom().getRoomSize() > 1){
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
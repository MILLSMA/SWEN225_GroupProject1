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
	private final HashMap<Player, Position> playerPositionMap; //TODO: is this needed?
	private final Cell[][] board;
	//------------------------
	// CONSTRUCTOR
	//------------------------

	public Board(Collection<Player> allPlayers)
	{
		playerPositionMap = new HashMap<>();
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
			//puts the player and the position in the map (never has to be updated)
			playerPositionMap.put(player, playerPos);
			//tell the player which cell they are in
			player.setLocation(playerCell);
		}
	}


	//------------------------
	// INTERFACE
	//------------------------

	public Room getRoom(RoomCard card){
		return rooms.get(card.getName());
	}

//	public Room getRoom(String name){
//		return rooms.get(name);
//	}

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
		if(c == '|')return new Room("Door");
		if(c == '|')return rooms.get("Wall");
		if(c == '_')return rooms.get("Hallway");
		return null;
	}

	/**
	 * reads the input file and creates the board accordingly
	 */
	private void createCells(){
		try {
			//scanner to read the CluedoBoard.txt file with a delimiter that reads all characters including whitespace
			Scanner sc = new Scanner(new File("CluedoBoard.txt")).useDelimiter("(\\b|\\B)");
			int xPosition = 0, yPosition = 0;
			String left, cell, right;
			//read whats on the left and right of the cell aswell as the cell
			left = sc.next();
			cell = sc.next();
			right = sc.next();
			//while the board file still has characters to read
			while(sc.hasNext()){
				Position cellPosition = new Position(xPosition, yPosition);
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
		}catch(FileNotFoundException e){
			System.out.println("Please put a CluedoBoard.txt file in the root directory.");
		}
		updateCellDirections();
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
					currentCell.setDirection(Cell.Direction.SOUTH, false);
					currentCell.setDirection(Cell.Direction.NORTH, false);
					currentCell.setDirection(Cell.Direction.WEST, false);
					currentCell.setDirection(Cell.Direction.EAST, false);
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
		Position playerPos = playerPositionMap.get(p);
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
	 * Dummy version of the move method, to check to see if
	 * the player is going to be moving into an illegal space
	 * @param p - Player
	 * @param dir - direction they chose to move
	 * @return TODO
	 */
	public Cell checkMove(Player p, Cell.Direction dir) {
		Position playerPos = playerPositionMap.get(p);
		Position dummyPos = new Position(playerPos.getRow(), playerPos.getCol());
		switch(dir){
			case NORTH:
				dummyPos.setRow(dummyPos.getRow() - 1);
				break;
			case SOUTH:
				dummyPos.setRow(dummyPos.getRow() + 1);
				break;
			case EAST:
				dummyPos.setCol(dummyPos.getCol() + 1);
				break;
			case WEST:
				dummyPos.setCol(dummyPos.getCol() - 1);
				break;
		}
		return board[dummyPos.getRow()][dummyPos.getCol()];
	}



	public Room checkSurroundingCells(Player p){
		Position playerPos = playerPositionMap.get(p);
		if(!board[playerPos.getRow()+1][playerPos.getCol()].getRoom().toString().equals("_") && board[playerPos.getRow()+1][playerPos.getCol()].getRoom().getRoomSize() > 1){
			return board[playerPos.getRow()+1][playerPos.getCol()].getRoom();
		}
		if(!board[playerPos.getRow()-1][playerPos.getCol()].getRoom().toString().equals("_") && board[playerPos.getRow()-1][playerPos.getCol()].getRoom().getRoomSize()> 1){
			return board[playerPos.getRow()-1][playerPos.getCol()].getRoom();
		}
		if(!board[playerPos.getRow()][playerPos.getCol()+1].getRoom().toString().equals("_") && board[playerPos.getRow()][playerPos.getCol()+1].getRoom().getRoomSize() > 1 ){
			return board[playerPos.getRow()][playerPos.getCol()+1].getRoom();
		}
		if(!board[playerPos.getRow()][playerPos.getCol()-1].getRoom().toString().equals("_") && board[playerPos.getRow()][playerPos.getCol()-1].getRoom().getRoomSize()> 1){
			return board[playerPos.getRow()][playerPos.getCol()-1].getRoom();
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
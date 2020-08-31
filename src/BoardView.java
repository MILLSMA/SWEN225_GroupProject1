import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class Canvas extends JPanel implements MouseMotionListener {

	public static final HashMap<String, Integer> roomLabels = new HashMap<String, Integer>(){
		{
			put("Ballroom", new Position(3, 10).hashCode());
			put("Dining Room", new Position(12, 2).hashCode());
			put("Study", new Position(22, 19).hashCode());
			put("Hall", new Position(20, 10).hashCode());
			put("Library", new Position(15, 19).hashCode());
			put("Billiard Room", new Position(9, 19).hashCode());
			put("Lounge", new Position(21, 1).hashCode());
			put("Conservatory", new Position(2, 19).hashCode());
			put("Kitchen", new Position(3, 1).hashCode());
		}
	};
/**
 * Class that displays the Cluedo board visually
 */
public class BoardView extends JPanel implements MouseMotionListener {
	private Board board;
	protected int cellWidth, cellHeight;
	private DrawingTile lastHoveredTile;
	private Color lastColor;
	private final HashMap<Integer, DrawingTile> tilesToDraw = new HashMap<>();
	private CompletableFuture<Cell> promisedCell;

	/**
	 * Paints the board
	 */
	public void paint(Graphics g) {
		tilesToDraw.values().forEach(tile -> {
			g.drawImage(tile.image, tile.xPosition, tile.yPosition, null);
			if(tile.cell.getObject() instanceof CharacterCard || tile.cell.getObject() instanceof WeaponCard) {
				String CardName = tile.cell.getObject().getName();
				g.drawImage(playerImage(CardName), tile.xPosition, tile.yPosition, null);
			}
		});
	}

	/**
	 * Gets the image that corresponds to the given name
	 * @param playerName - name of image excluding extension
	 * @return - buffered image
	 */
	public BufferedImage playerImage(String playerName){
		BufferedImage image = null;
		String fileName = "Resources/" +  playerName.replace(' ', '_')+".png";
		try {
			image = ImageIO.read(new File(fileName));
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
		return image;
	}

	/**
	 * returns to the image that belongs to the given cell
	 * @param cell - Cell of image you want to receive
	 * @return - image of the cell to display
	 */
	private BufferedImage cellImage(Cell cell){
		String name;
		if(cell.getRoom().isWall()) name = "wall";
		else if(cell.getRoom().isHallway()) name = "hallway";
		else if(cell.getRoom().isDoor()) name = "door " + doorDirection(cell);
		else name = "floorboard";
		return playerImage(name);
	}

	/**
	 * Returns the direction the door is supposed to facing, for image purposes
	 * @param door - cell that is a door
	 * @return string representing door's direction
	 */
	private String doorDirection(Cell door){
		boolean northIsRoom = board.getNeighbourCell(door, Locatable.Direction.NORTH).getRoom().isProperRoom();
		boolean southIsRoom = board.getNeighbourCell(door, Locatable.Direction.SOUTH).getRoom().isProperRoom();
		boolean westIsRoom = board.getNeighbourCell(door, Locatable.Direction.WEST).getRoom().isProperRoom();
		boolean eastIsRoom = board.getNeighbourCell(door, Locatable.Direction.EAST).getRoom().isProperRoom();

		if(northIsRoom && southIsRoom){
			if(westIsRoom) return "right";
			if(eastIsRoom) return "left";
		}
		if(northIsRoom) return "down";
		return "up";
	}

	/**
	 * Sets all the data structures needed to paint the board based on current board
	 * @param b board model
	 */
	public void drawBoard(Board b){
		board = b;
		tilesToDraw.clear();
		cellWidth = this.getWidth() / Board.COLS;
		cellHeight = this.getHeight() / Board.ROWS -3;
		int widthCount = 0;
		int heightCount = 0;
		for (int xIndex = 0; xIndex < Board.ROWS; xIndex++) {
			for (int yIndex = 0; yIndex < Board.COLS; yIndex++) {
				if (board.board[xIndex][yIndex] == null) continue;
				Cell cellToDraw = board.board[xIndex][yIndex];
				DrawingTile newTile = new DrawingTile(cellToDraw.getColor(), widthCount, heightCount, cellToDraw, cellImage(cellToDraw));
				tilesToDraw.put(cellToDraw.position.hashCode(), newTile);
				widthCount += cellWidth;
			}
			heightCount += cellHeight;
			widthCount = 0;
		}

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (promisedCell == null) return;
				for (DrawingTile tile : tilesToDraw.values()) {
					if (tile.getRect().contains(e.getPoint())) {
						promisedCell.complete(tile.cell);
					}
				}
			}
		});
		addLabels();
		this.repaint();
	}

	private BufferedImage getLabelPart(String room, int index){
		BufferedImage image;
		String roomName = room.replace(' ', '_');
		String fileName = "Resources/Room_Labels/" + roomName + "/" + roomName + "_"+ index + ".png";
		try {
			image = ImageIO.read(new File(fileName));
		}catch(IOException ignore){
			//CluedoView.showDialog(e.getMessage() + "\n" + fileName);
			return getLabelPart("Ballroom", index);
		}
		return image;
	}

	private void addLabels(){
		for (Integer position : roomLabels.values()) {
			DrawingTile updateTile = tilesToDraw.get(position);
			for (int index = 1; index < 9; index++) {
				updateTile.setImage(getLabelPart(updateTile.cell.getRoom().getName(), index));
				updateTile = tilesToDraw.get(new Position(updateTile.cell.position.getRow(), updateTile.cell.position.getCol() + 1).hashCode());
				if(index == 4)  updateTile =  tilesToDraw.get(new Position(updateTile.cell.position.getRow() + 1, updateTile.cell.position.getCol() - 4).hashCode()); ;
			}
		}
	}


	/**
	 * updates the board, only slightly more efficient than draw board
	 */
	public void updateBoard(){
		for (int xIndex = 0; xIndex < Board.ROWS; xIndex++) {
			for (int yIndex = 0; yIndex < Board.COLS; yIndex++) {
				Position tilePos = new Position(xIndex,yIndex);
				DrawingTile tileToUpdate = tilesToDraw.get(tilePos.hashCode());
				Cell superCell = board.board[xIndex][yIndex];
				if(superCell.getColor() != tileToUpdate.tileColor)
					if(tileToUpdate != lastHoveredTile) tileToUpdate.tileColor = superCell.getColor();
			}
		}
		this.repaint();
	}


	/**
	 * Gets the cell the user clicks on - allows the game to wait
	 * for the uses response
	 * @return - A promise of the cell the player clicks on
	 */
	public CompletableFuture<Cell> getCell(){
		promisedCell = new CompletableFuture<>();
		return promisedCell;
	}

	/**
	 * Interrupt blocking waiting for a mouse click
	 */
	public void cancelPromise() {
		try {
			promisedCell.cancel(true);
		} catch (Exception ignored) {
		}
	}

	/**
	 * Convert mouse click coords into a board Position
	 * @param x click x-coordinate on BoardView
	 * @param y click y-coordinate on BoardView
	 * @return Position on Board
	 */
	private Position mouseCoordinatesToPos(int x, int y){
		int xPos = x / cellWidth;
		int yPos = y / cellHeight;
		return new Position(yPos, xPos);
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		try {
			if (board == null) return;
			if (lastColor != null && lastHoveredTile != null) lastHoveredTile.tileColor = lastColor;
			Position pos = mouseCoordinatesToPos(e.getX(), e.getY());
			DrawingTile hoverTile = tilesToDraw.get(pos.hashCode());
			Cell c = hoverTile.cell;
			String label = "";

			if (c.getRoom() != null && c.getRoom().isProperRoom()) {
				label = label + c.getRoom().getName();
			}
			if (c.getObject() != null) {
				if (!label.equals("")) label = label + " : ";
				label = label + c.getObject().getName();
			}

			this.setToolTipText(label);
			lastColor = hoverTile.tileColor;
			hoverTile.tileColor = new Color(57, 255, 20, 75);
			this.repaint();
			this.lastHoveredTile = hoverTile;
		}catch(NullPointerException ignored){}
	}
	/**
	 * Class for drawing tiles on the canvas
	 */
	private class DrawingTile {
		Color tileColor;
		final BufferedImage image;
		final int xPosition;
		final int yPosition;
		final Rectangle rect;
		final Cell cell;

		/**
		 * Create a new DrawingTile
		 * @param tc colour of tile
		 * @param x x-coordinate on BoardView
		 * @param y y-coordinate on BoardView
		 * @param c cell this tile represents
		 * @param image image to draw on tile
		 */
		DrawingTile(Color tc, int x, int y, Cell c, BufferedImage image) {
			this.tileColor = tc;
			this.xPosition = x;
			this.yPosition = y;
			this.cell = c;
			this.image = image;
			rect = new Rectangle(xPosition, yPosition, cellWidth, cellHeight);
		}

		/**
		 * Return rectangle drawn by DrawingTile
		 * @return rectangle Shape
		 */
		public Rectangle getRect() {
			return this.rect;
		}
	}
}



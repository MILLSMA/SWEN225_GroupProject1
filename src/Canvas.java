import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class Canvas extends JPanel implements MouseMotionListener {
	private Board board;
	protected int cellWidth, cellHeight;
	private DrawingTile lastHoveredTile;
	private Color lastColor;
	private final HashMap<Integer, DrawingTile> tilesToDraw = new HashMap<>();
	private CompletableFuture<Cell> promisedCell;

	//paints the board (not overly efficient)
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
	 * gets the image that corresponds to the name given
	 * @param playerName - name of image excluding extension
	 * @return - buffered image
	 */
	public BufferedImage playerImage(String playerName){
		BufferedImage image = null;
		String fileName = "Resources/" +  playerName.replace(' ', '_')+".png";
		try {
			image = ImageIO.read(new File(fileName));
		}catch(IOException e){
			CluedoView.showDialog(e.getMessage());
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
	 * gets the direction the door is supposed to facing, for viewing purposes
	 * @param door - cell that is a door
	 * @return
	 */
	private String doorDirection(Cell door){
		Cell above, left, right, below;
		above = board.getNeighbourCell(door, Locatable.Direction.NORTH);
		below = board.getNeighbourCell(door, Locatable.Direction.SOUTH);
		left = board.getNeighbourCell(door, Locatable.Direction.WEST);
		right = board.getNeighbourCell(door, Locatable.Direction.EAST);

		if(RoomCard.isProperRoom(above.getRoom()) && RoomCard.isProperRoom(below.getRoom())){
			if(RoomCard.isProperRoom(left.getRoom())) return "right";
			if(RoomCard.isProperRoom(right.getRoom())) return "left";
		}else if(RoomCard.isProperRoom(left.getRoom()) && RoomCard.isProperRoom(right.getRoom())){
			if(RoomCard.isProperRoom(above.getRoom())) return "down";
			if(RoomCard.isProperRoom(below.getRoom())) return "up";
		} else if(RoomCard.isProperRoom(below.getRoom())) return "up";
		return "down";

	}

	/**
	 * updates all the data structures needed to paint the board
	 * @param b
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
				DrawingTile newTile = new DrawingTile(cellToDraw.getColor(), widthCount, heightCount, cellToDraw);
				newTile.setImage(cellImage(cellToDraw));
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

		this.repaint();
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

	public void cancelPromise() {
		try {
			promisedCell.cancel(true);
		} catch (Exception ignored) {
		}
	}

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
			if (lastColor != null && lastHoveredTile != null) lastHoveredTile.tileColor = lastColor;
			Position pos = mouseCoordinatesToPos(e.getX(), e.getY());
			DrawingTile hoverTile = tilesToDraw.get(pos.hashCode());
			Cell c = hoverTile.cell;
			String label = "";

			if (c.getRoom() != null && c.getRoom().getCard() != null) {
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
		}catch(NullPointerException ex){}
	}
	/**
	 * Class for drawing tiles on the canvas
	 */
	private class DrawingTile {
		Color tileColor;
		BufferedImage image;
		final int xPosition;
		final int yPosition;
		final Rectangle rect;
		final Cell cell;

		DrawingTile(Color tc, int x, int y, Cell c) {
			this.tileColor = tc;
			this.xPosition = x;
			this.yPosition = y;
			this.cell = c;
			rect = new Rectangle(xPosition, yPosition, cellWidth, cellHeight);
		}

		public void setImage(BufferedImage image) {
			this.image = image;
		}

		public Rectangle getRect() {
			return this.rect;
		}
	}
}



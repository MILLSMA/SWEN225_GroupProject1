import java.util.List;

public abstract class Locatable {
	private Position position;
	public List<Cell.Direction> directionsAvailable;
	public Position getPosition() {
		return position;
	}
	public abstract boolean setPosition(Position aNewPosition);
}

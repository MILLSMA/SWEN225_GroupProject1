import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Generic class for Pathfinder, so it can avoid accessing unnecessary information in Cell,
 * only nodes and their neighbours.
 */
public abstract class Locatable {
	public enum Direction{
		NORTH,
		SOUTH,
		EAST,
		WEST;
		public String toString() {
			return "[" + name().charAt(0) + "]" + name().substring(1).toLowerCase();
		}
	}
	public Position position;
	public List<Direction> directionsAvailable;

	/**
	 * Get this object's position
	 * @return object's position
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Abstract method for setting object's position
	 * @param aNewPosition new position of object
	 * @return if position setting is successful
	 */
	public abstract boolean setPosition(Position aNewPosition);

	/**
	 * Returns which directions one can travel to from the current object
	 * @param b board model, used for checking for player/per-turn obstructions
	 * @return list of available directions
	 */
	public Collection<Direction> getDirectionsAvailable(Board b){
		List<Direction> ans = new ArrayList<>();
		for (Direction d : directionsAvailable) {
			if (!b.isCellUsed(position, d)) ans.add(d);
		}
		return ans;
	}
}

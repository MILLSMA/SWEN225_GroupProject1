import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * I created this to help with a more Generic pathfinder class and
 * to lessen the Cell class
 */
public abstract class Locatable {
	public enum Direction{
		NORTH,
		SOUTH,
		EAST,
		WEST;

//		/**
//		 *  retrieves and validates direction input from user
//		 * @param p: player making move
//		 * @return direction enum to move the player
//		 */
//		public static Direction input(Player p, Board board){
//			Scanner input = new Scanner(System.in);
//			//Stores the directions that the player can legally move
//			ArrayList<Direction> correctAnswers = new ArrayList<>(p.getLocation().directionsAvailable);
//			for (Direction direction : p.getLocation().directionsAvailable) {
//				if(!board.isCellUsed(p, direction)){
//					System.out.println(direction);
//				}else correctAnswers.remove(direction);
//
//			}
//			System.out.println("End Turn [X]");
//			while(true){
//				System.out.print("Move: ");
//				char answer = input.next().toUpperCase().charAt(0);
//				if (answer == 'X') return null;
//
//				for (Direction d : correctAnswers) {
//					if (answer == d.code()) return d;
//				}
//				System.out.println("Please use the given code in [ ].");
//				input.nextLine();
//			}
//		}

		public String toString() {
			return "[" + name().substring(0, 1) + "]" + name().substring(1).toLowerCase();
		}

		public char code() {
			return name().charAt(0);
		}
	}
	public Position position;
	public List<Direction> directionsAvailable;
	public Position getPosition() {
		return position;
	}
	public abstract boolean setPosition(Position aNewPosition);

	public Collection<Direction> getDirectionsAvailable(Board b){
		List<Direction> ans = new ArrayList<>();
		for (Direction d : directionsAvailable) {
			if (!b.isCellUsed(position, d)) ans.add(d);
		}
		return ans;
	}
}

import java.util.*;
import java.util.stream.Collectors;

/**
 * A star search algorithm
 * @param <K> object
 */
public class Pathfinder<K extends Locatable> {
	private K goal;
	private ArrayList<Node> pathway;
	private ArrayList<K> objectPath;
	private final Board board;

	/**
	 * Constructs pathfinder and connects it to board model
	 * @param board board model
	 */
	Pathfinder(Board board){
		this.board = board;
	}

	/**
	 * get the path from the start Cell to the end Cell
	 * @param start - the Cell the player is in
	 * @param goal - the Cell to move the player too
	 * @return - path from the the start Cell to the end Cell
	 */
	public ArrayList<K> findPath(K start, K goal){
		this.goal = goal;
		Node start1 = new Node(start, getEstimate(start), 0, null);
		Collection<K> visited = new HashSet<>();
		pathway = new ArrayList<>();

		//creates a priority queue bases on the distance of each locatable objects
		//distance from the goal node
		PriorityQueue<Node> fringe = new PriorityQueue<>((node1, node2) -> {
			if (node2.getF() + node2.getG() > node1.getF() + node1.getG()) {
				return -1;
			} else if (node2.getF() + node2.getG() < node1.getF() + node1.getG()) {
				return 1;
			}
			return 0;
		});

		//basic A* search algorithm
		fringe.add(start1);
		while(!fringe.isEmpty()){
			Node currentNode = fringe.poll();
			if(!visited.contains(currentNode.object)) {
				visited.add(currentNode.object);
				if (currentNode.equals(goal)) {
					pathway.add(currentNode);
					break;
				}

				for (Cell.Direction direction : currentNode.getObject().getDirectionsAvailable(board)) {
					K neighbourObject = (K) board.getNeighbourCell((Cell) currentNode.object, direction);
					Node neighbourNode = new Node(neighbourObject, currentNode.getF() + getEstimate(neighbourObject), currentNode.getG() + 1, currentNode);
					if (!visited.contains(neighbourNode.object)) fringe.add(neighbourNode);
				}
				pathway.add(currentNode);
			}
		}
		editPathway();
		return objectPath;
	}

	/**
	 * goes over the pathway, deletes duplicates
	 * and adds them to an array of their original object in order
	 */
	private void editPathway(){
		List<Node> newPath = new ArrayList<>();
		Node currentNode = pathway.get(pathway.size() -1);
		newPath.add(currentNode);
		pathway.remove(currentNode);

		while(currentNode.previousNode != null){
			newPath.add(currentNode);
			currentNode = currentNode.previousNode;
		}
		newPath.add(currentNode);
		objectPath = newPath.stream().distinct().map(node -> node.object).collect(Collectors.toCollection(ArrayList::new));
		Collections.reverse(objectPath);
	}

	/**
	 * Estimates the manhattan distance to the goal node
	 * @param node node to start at
	 * @return Manhattan distance from node to goal
	 */
	private double getEstimate(K node){
		Position nodePosition = node.getPosition();
		Position goalPosition = goal.getPosition();
		return Math.sqrt((goalPosition.getRow()-nodePosition.getRow())*(goalPosition.getRow()-nodePosition.getRow()) +
				(goalPosition.getCol()-nodePosition.getCol())*((goalPosition.getCol()-nodePosition.getCol())));
	}

	/**
	 * Helper node class for A* search
	 */
	private class Node{
		private final K object;
		private final Node previousNode;
		private final double f;
		private final double g;

		/**
		 * Construct A* node
		 * @param c Locatable object
		 * @param f current estimate
		 * @param g cost from start
		 * @param prev previous node
		 */
		Node(K c, double f, double g, Node prev){
			this.object = c;
			this.f = f;
			this.g = g;
			this.previousNode = prev;
		}

		/**
		 * Return equality based on if they encapsulate the same object
		 * @param object object to compare
		 * @return if both same
		 */
		public boolean equals(K object){
			return (this.object == object);
		}

		/**
		 * Return encapsulated object
		 * @return object
		 */
		public K getObject() {
			return object;
		}

		/**
		 * F is the current estimate to goal
		 * @return current estimate to goal
		 */
		public double getF() {
			return f;
		}

		/**
		 * G is the current cost from start
		 * @return current cost from start
		 */
		public double getG() {
			return g;
		}

	}
}

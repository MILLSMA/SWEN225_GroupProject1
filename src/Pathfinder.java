import java.util.*;
import java.util.stream.Collectors;

public class Pathfinder<K extends Locatable> {
	private PriorityQueue<Node> fringe;
	private Collection<Node> visited;
	private Node start;
	private K goal;
	private ArrayList<Node> pathway;
	private ArrayList<K> objectPath;
	private Board board;
	
	Pathfinder(Board board){
		this.board = board;
	}

	public ArrayList<K> findPath(K start, K goal){
		this.goal = goal;
		this.start = new Node(start, getEstimate(start), 0);
		visited = new HashSet<>();
		pathway = new ArrayList<>();

		fringe = new PriorityQueue<>((node1, node2) -> {
			if (node2.getF() + node2.getG()  > node1.getF()+ node1.getG()) {
				return -1;
			} else {
				return 1;
			}
		});

		fringe.add(this.start);
		while(!fringe.isEmpty()){
			Node currentNode = fringe.poll();
			if(!visited.contains(currentNode)) {
				visited.add(currentNode);
				if (currentNode.equals(goal)) {
					pathway.add(currentNode);
					break;
				}


				for (Cell.Direction direction : currentNode.getObject().directionsAvailable) {
					K neighbourObject = (K) board.getNeighbourCell((Cell) currentNode.object, direction);
					Node neighbourNode = new Node(neighbourObject, currentNode.getF() + getEstimate(neighbourObject), currentNode.getG() + 1);
					if (!visited.contains(neighbourNode)) fringe.add(neighbourNode);
				}
				pathway.add(currentNode);
			}
		}
		editPathway();
		return objectPath;
	}

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
		objectPath = new ArrayList(newPath.stream().distinct().map(node -> node.object).collect(Collectors.toList()));
	}

	private double getEstimate(K node){
		Position nodePosition = node.getPosition();
		Position goalPosition = goal.getPosition();
		return Math.sqrt((goalPosition.getRow()-nodePosition.getRow())*(goalPosition.getRow()-nodePosition.getRow()) +
				(goalPosition.getCol()-nodePosition.getCol())*((goalPosition.getCol()-nodePosition.getCol())));
	}

	private class Node{
		private K object;
		private Node previousNode = null;
		private double f, g;

		Node(K c, double f, double g){
			this.object = c;
			this.f = f;
			this.g = g;
		}

		public void setPreviousNode(Node previousNode) {
			this.previousNode = previousNode;
		}

		public Node getPreviousNode() {
			return previousNode;
		}

		public boolean equals(K object){
			return (this.object == object);
		}

		public K getObject() {
			return object;
		}

		public double getF() {
			return f;
		}

		public double getG() {
			return g;
		}

	}
}

public class RoomCard implements Card{

	String name;

	RoomCard(String name){
		this.name = name;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		return name;
	}



}

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class CardTriplet {
	//------------------------
	// MEMBER VARIABLES
	//------------------------

	private final CharacterCard character;
	private final WeaponCard weapon;
	private final RoomCard room;

	//------------------------
	// CONSTRUCTOR
	//------------------------

	public CardTriplet(CharacterCard c, WeaponCard w, RoomCard r) {
		character = c;
		weapon = w;
		room = r;
	}

	/**
	 * Construct card triplet during suggestion
	 * room must be room player is in
	 * @param r : room card player is in
	 */
	public CardTriplet(RoomCard r){
		character = CharacterCard.input();
		weapon = WeaponCard.input();
		room = r;
	}

	/**
	 * Construct card triplet using user input
	 */
	public CardTriplet(){
		character = CharacterCard.input();
		weapon = WeaponCard.input();
		room = RoomCard.input();
	}


	//------------------------
	// INTERFACE
	//------------------------

	public Collection<Card> getSet() {
		return new HashSet<>(Arrays.asList(
				weapon,
				character,
				room
		));
	}

	public CharacterCard getCharacter()
	{
		return character;
	}
	public WeaponCard getWeapon()
	{
		return weapon;
	}
	public RoomCard getRoom()
	{
		return room;
	}

	public String toString() {
		return "<" + character.getName() + ", " + weapon.getName() + ", " + room.getName() + ">";
	}
}
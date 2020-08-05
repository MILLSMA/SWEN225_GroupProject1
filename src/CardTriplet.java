import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class CardTriplet {
	//------------------------
	// MEMBER VARIABLES
	//------------------------

	private CharacterCard character;
	private WeaponCard weapon;
	private RoomCard room;

	//------------------------
	// CONSTRUCTOR
	//------------------------

	public CardTriplet(CharacterCard c, WeaponCard w, RoomCard r) {
		character = c;
		weapon = w;
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

//	public boolean setCharacter(CharacterCard aCharacter)
//	{
//		character = aCharacter;
//		return true;
//	}
//
//	public boolean setWeapon(WeaponCard aWeapon)
//	{
//		weapon = aWeapon;
//		return true;
//	}
//
//	public boolean setRoom(RoomCard aRoom)
//	{
//		room = aRoom;
//		return true;
//	}

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
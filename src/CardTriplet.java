import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CardTriplet that = (CardTriplet) o;
		return character == that.character &&
				weapon == that.weapon &&
				room == that.room;
	}

	@Override
	public int hashCode() {
		return Objects.hash(character, weapon, room);
	}

	public String toString() {
		return "<" + character.getName() + ", " + weapon.getName() + ", " + room.getName() + ">";
	}
}
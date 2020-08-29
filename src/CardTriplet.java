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

	public CardTriplet(String c, String w, RoomCard r){
		character = CharacterCard.valueOf(replaceSpace(c));
		weapon = WeaponCard.valueOf(replaceSpace(w));
		room = r;
	}

	public CardTriplet(String c, String w, String r){
		character = CharacterCard.valueOf(replaceSpace(c));
		weapon = WeaponCard.valueOf(replaceSpace(w));
		room = RoomCard.valueOf(replaceSpace(r));
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CardTriplet that = (CardTriplet) o;
		return character == that.character &&
				weapon == that.weapon &&
				room == that.room;
	}

	public String replaceSpace(String s){

		return s.replace(' ', '_').toUpperCase();
	}

	@Override
	public int hashCode() {
		return Objects.hash(character, weapon, room);
	}

	public String toString() {
		return "<" + character.getName() + ", " + weapon.getName() + ", " + room.getName() + ">";
	}
}
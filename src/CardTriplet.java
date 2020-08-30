import java.util.*;

/**
 * Tuple of statically typed cards guaranteeing one of each type
 */
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

	/**
	 * Create card triplet when we know all 3 card objects
	 * @param c character
	 * @param w weapon
	 * @param r room
	 */
	public CardTriplet(CharacterCard c, WeaponCard w, RoomCard r) {
		character = c;
		weapon = w;
		room = r;
	}

	/**
	 * Create card triplet when we know the Room object
	 * and the names of the other 2, e.g. from an input dialog
	 * @param c character name
	 * @param w weapon name
	 * @param r room
	 */
	public CardTriplet(String c, String w, RoomCard r){
		character = CharacterCard.valueOf(replaceSpace(c));
		weapon = WeaponCard.valueOf(replaceSpace(w));
		room = r;
	}

	/**
	 * Create card triplet when we know only their names, e.g. from an input dialog
	 * @param c character name
	 * @param w weapon name
	 * @param r room name
	 */
	public CardTriplet(String c, String w, String r){
		character = CharacterCard.valueOf(replaceSpace(c));
		weapon = WeaponCard.valueOf(replaceSpace(w));
		room = RoomCard.valueOf(replaceSpace(r));
	}

	//------------------------
	// INTERFACE
	//------------------------

	/**
	 * Convert tuple to unmodifiable collection
	 * @return collection of 3 cards
	 */
	public Collection<Card> getList() {
		return Collections.unmodifiableCollection(Arrays.asList(
				weapon,
				character,
				room
		));
	}

	/**
	 * Return character of this triplet
	 * @return character
	 */
	public CharacterCard getCharacter()
	{
		return character;
	}

	/**
	 * Return weapon of this triplet
	 * @return weapon
	 */
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

	/**
	 * Helper method that replaces underscore with space
	 * @param s string to format
	 * @return formatted string
	 */
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
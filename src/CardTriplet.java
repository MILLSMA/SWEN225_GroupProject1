/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.0.515.d9da8f6c modeling language!*/



// line 86 "model.ump"
// line 151 "model.ump"
public class CardTriplet
{



  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //CardTriplet Attributes
  private CharacterCard character;
  private WeaponCard weapon;
  private RoomCard room;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public CardTriplet(CharacterCard aCharacter, WeaponCard aWeapon, RoomCard aRoom)
  {
    character = aCharacter;
    weapon = aWeapon;
    room = aRoom;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setCharacter(CharacterCard aCharacter)
  {
    boolean wasSet = false;
    character = aCharacter;
    wasSet = true;
    return wasSet;
  }

  public boolean setWeapon(WeaponCard aWeapon)
  {
    boolean wasSet = false;
    weapon = aWeapon;
    wasSet = true;
    return wasSet;
  }

  public boolean setRoom(RoomCard aRoom)
  {
    boolean wasSet = false;
    room = aRoom;
    wasSet = true;
    return wasSet;
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

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "character" + "=" + (getCharacter() != null ? !getCharacter().equals(this)  ? getCharacter().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "weapon" + "=" + (getWeapon() != null ? !getWeapon().equals(this)  ? getWeapon().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "room" + "=" + (getRoom() != null ? !getRoom().equals(this)  ? getRoom().toString().replaceAll("  ","    ") : "this" : "null");
  }
}
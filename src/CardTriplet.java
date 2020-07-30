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
  private Character character;
  private Weapon weapon;
  private Room room;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public CardTriplet(Character aCharacter, Weapon aWeapon, Room aRoom)
  {
    character = aCharacter;
    weapon = aWeapon;
    room = aRoom;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setCharacter(Character aCharacter)
  {
    boolean wasSet = false;
    character = aCharacter;
    wasSet = true;
    return wasSet;
  }

  public boolean setWeapon(Weapon aWeapon)
  {
    boolean wasSet = false;
    weapon = aWeapon;
    wasSet = true;
    return wasSet;
  }

  public boolean setRoom(Room aRoom)
  {
    boolean wasSet = false;
    room = aRoom;
    wasSet = true;
    return wasSet;
  }

  public Character getCharacter()
  {
    return character;
  }

  public Weapon getWeapon()
  {
    return weapon;
  }

  public Room getRoom()
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
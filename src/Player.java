/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.0.515.d9da8f6c modeling language!*/


import java.util.*;

// line 56 "model.ump"
// line 124 "model.ump"
public class Player
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Player Attributes
  private CharacterCard token;
  private Cell location;
  private boolean isExcluded;

  //Player Associations
  private List<Card> cards;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Player(CharacterCard aToken, Cell aLocation, boolean aIsExcluded)
  {
    token = aToken;
    location = aLocation;
    isExcluded = aIsExcluded;
    cards = new ArrayList<Card>();
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setToken(CharacterCard aToken)
  {
    boolean wasSet = false;
    token = aToken;
    wasSet = true;
    return wasSet;
  }

  public boolean setLocation(Cell aLocation)
  {
    boolean wasSet = false;
    location = aLocation;
    wasSet = true;
    return wasSet;
  }

  public boolean setIsExcluded(boolean aIsExcluded)
  {
    boolean wasSet = false;
    isExcluded = aIsExcluded;
    wasSet = true;
    return wasSet;
  }

  public CharacterCard getToken()
  {
    return token;
  }

  public Cell getLocation()
  {
    return location;
  }

  public boolean getIsExcluded()
  {
    return isExcluded;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isIsExcluded()
  {
    return isExcluded;
  }
  /* Code from template association_GetMany */
  public Card getCard(int index)
  {
    Card aCard = cards.get(index);
    return aCard;
  }

  public List<Card> getCards()
  {
    List<Card> newCards = Collections.unmodifiableList(cards);
    return newCards;
  }

  public int numberOfCards()
  {
    int number = cards.size();
    return number;
  }

  public boolean hasCards()
  {
    boolean has = cards.size() > 0;
    return has;
  }

  public int indexOfCard(Card aCard)
  {
    int index = cards.indexOf(aCard);
    return index;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfCards()
  {
    return 0;
  }
  /* Code from template association_AddManyToManyMethod */
  public boolean addCard(Card aCard)
  {
    if (cards.contains(aCard)) { return false; }
    cards.add(aCard);
    return true;
  }
  /* Code from template association_RemoveMany */
  public boolean removeCard(Card aCard)
  {
   if(cards.contains(aCard)) {
     cards.remove(aCard);
     return true;
   }
   return false;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addCardAt(Card aCard, int index)
  {  
    boolean wasAdded = false;
    if(addCard(aCard))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfCards()) { index = numberOfCards() - 1; }
      cards.remove(aCard);
      cards.add(index, aCard);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveCardAt(Card aCard, int index)
  {
    boolean wasAdded = false;
    if(cards.contains(aCard))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfCards()) { index = numberOfCards() - 1; }
      cards.remove(aCard);
      cards.add(index, aCard);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addCardAt(aCard, index);
    }
    return wasAdded;
  }

  public void delete()
  {

    cards.clear();

  }

  // line 64 "model.ump"
   public Boolean checkHand(Card c){

    return true;
  }

  // line 67 "model.ump"
   public void displayHand(){
    System.out.println("Cards held by player: " + this.token.getName());
    for(Card c : cards){
      System.out.println(c);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "isExcluded" + ":" + getIsExcluded()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "token" + "=" + (getToken() != null ? !getToken().equals(this)  ? getToken().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "location" + "=" + (getLocation() != null ? !getLocation().equals(this)  ? getLocation().toString().replaceAll("  ","    ") : "this" : "null");
  }
}
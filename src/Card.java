public interface Card
{
  /**
   * Get full name of card
   * @return lower case name of card
   */
  String getName();

  /**
   * Get single-character representation of card on board
   * @return board code of card
   */
  String toString();
}
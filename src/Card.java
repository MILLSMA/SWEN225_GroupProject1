/**
 * Interface grouping each Card enumeration together.
 * Unable to factorise other common methods across all 3 as they are static methods.
 */
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
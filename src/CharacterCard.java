import java.util.Arrays;
import java.util.Collection;

public enum CharacterCard implements Card {
	MISS_SCARLETT,
	COLONEL_MUSTARD,
	MRS_WHITE,
	MR_GREEN,
	MRS_PEACOCK,
	PROFESSOR_PLUM;

	public static Collection<CharacterCard> getCharacters(){
		return Arrays.asList(values());
	}
	@Override
	public String getName() {
		return name().toLowerCase();
	}
	@Override
	public String toString() {
		return getName();
	}
}

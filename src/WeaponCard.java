import java.util.Arrays;
import java.util.Collection;

public enum WeaponCard implements Card{
	CANDLESTICK,
	DAGGER,
	LEAD_PIPE,
	REVOLVER,
	ROPE,
	SPANNER;

	public static Collection<WeaponCard> getWeapons(){
		return Arrays.asList(values());
	}

	public static int size() {
		return values().length;
	}

	@Override
	public String getName() {
		return name().toLowerCase();
	}

	@Override
	public String toString() {
		String[] codes = {"+", "#", "|", "%", "@", "?"};
		return codes[ordinal()];
	}
}

import java.util.Arrays;
import java.util.Collection;

public enum Weapon implements Card{
	CANDLESTICK,
	DAGGER,
	LEAD_PIPE,
	REVOLVER,
	ROPE,
	SPANNER;

	public static Collection<Weapon> getWeapons(){
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

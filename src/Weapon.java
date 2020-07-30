public enum Weapon implements Card{
	CANDLESTICK,
	DAGGER,
	LEAD_PIPE,
	REVOLVER,
	ROPE,
	SPANNER;

	@Override
	public String getName() {
		return name().toLowerCase();
	}
	@Override
	public String toString() {
		return getName();
	}
}

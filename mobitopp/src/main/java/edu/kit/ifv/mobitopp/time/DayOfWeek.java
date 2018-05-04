package edu.kit.ifv.mobitopp.time;

public enum DayOfWeek {

	MONDAY(0), TUESDAY(1), WEDNESDAY(2), THURSDAY(3), FRIDAY(4), SATURDAY(5), SUNDAY(6);

	private static final int daysPerWeek = 7;
	private final int numeric;

	private DayOfWeek(int numeric) {
		this.numeric = numeric;
	}

	public int getTypeAsInt() {
		return this.numeric;
	}

	public static DayOfWeek fromDay(int numeric) {
		int numericDay = inWeek(correctBeforeWeek(numeric));
		return values()[numericDay];
	}

	private static int correctBeforeWeek(int numeric) {
		return inWeek(numeric) + daysPerWeek;
	}

	private static int inWeek(int numeric) {
		return numeric % daysPerWeek;
	}

	public DayOfWeek next() {
		return fromDay(inWeek(numeric + 1));
	}

}

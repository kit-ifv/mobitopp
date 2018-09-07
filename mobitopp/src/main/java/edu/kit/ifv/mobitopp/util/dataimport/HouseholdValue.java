package edu.kit.ifv.mobitopp.util.dataimport;

class HouseholdValue extends Value {

	private final String size;

	public HouseholdValue(int zoneId, String size, String amount) {
		super(zoneId, amount);
		this.size = size;
	}

	static HouseholdValue from(String[] line) {
		int zoneId = Integer.parseInt(line[0]);
		String size = line[2];
		String amount = line[3];
		return new HouseholdValue(zoneId, size, amount);
	}

	protected String constraint() {
		return "HHTyp:" + size;
	}
}
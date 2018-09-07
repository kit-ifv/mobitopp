package edu.kit.ifv.mobitopp.util.dataimport;

public abstract class Value {

	private final int zoneId;
	private final String areaType;
	private final String amount;

	protected Value(int zoneId, String areaType, String amount) {
		super();
		this.zoneId = zoneId;
		this.areaType = areaType;
		this.amount = amount;
	}

	int zoneId() {
		return zoneId;
	}
	
	String areaType() {
		return areaType;
	}

	String amount() {
		return amount;
	}

	protected abstract String constraint();
}
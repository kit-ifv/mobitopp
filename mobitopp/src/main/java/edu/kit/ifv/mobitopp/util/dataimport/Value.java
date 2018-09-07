package edu.kit.ifv.mobitopp.util.dataimport;

public abstract class Value {

	private final int zoneId;
	private final String amount;

	protected Value(int zoneId, String amount) {
		super();
		this.zoneId = zoneId;
		this.amount = amount;
	}

	int zoneId() {
		return zoneId;
	}

	String amount() {
		return amount;
	}

	protected abstract String constraint();
}
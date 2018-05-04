package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.visum.VisumChargingPoint;

public class VisumChargingPointBuilder {

	private static final int defaultId = 1;
	private static final float defaultX = 1.0f;
	private static final float defaultY = 1.0f;
	private static final int defaultStationId = 2;
	private static final float defaultPower = 2.0f;

	private int id;
	private float x;
	private float y;
	private int stationId;
	private float power;

	public VisumChargingPointBuilder() {
		super();
		id = defaultId;
		x = defaultX;
		y = defaultY;
		stationId = defaultStationId;
		power = defaultPower;
	}

	public VisumChargingPoint build() {
		return new VisumChargingPoint(id, x, y, stationId, power);
	}

	public VisumChargingPointBuilder withId(int id) {
		this.id = id;
		return this;
	}

	public VisumChargingPointBuilder with(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public VisumChargingPointBuilder withPower(float power) {
		this.power = power;
		return this;
	}

}

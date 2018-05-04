package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;

public class VisumChargingPoint implements Serializable {

	private static final long serialVersionUID = 1L;

	public final int id;
	public final VisumPoint2 coord;
	public final int stationId;
	public final float power;

	public VisumChargingPoint(int id, float xCoord, float yCoord, int stationId, float power) {
		super();
		this.id = id;
		coord = new VisumPoint2(xCoord, yCoord);
		this.stationId = stationId;
		this.power = power;
	}

}

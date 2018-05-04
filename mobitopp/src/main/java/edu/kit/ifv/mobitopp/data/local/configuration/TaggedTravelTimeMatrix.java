package edu.kit.ifv.mobitopp.data.local.configuration;

import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;

public class TaggedTravelTimeMatrix {

	private TravelTimeMatrixId id;
	private TravelTimeMatrix travelTimeMatrix;

	public TaggedTravelTimeMatrix(TravelTimeMatrixId id, TravelTimeMatrix travelTimeMatrix) {
		super();
		this.id = id;
		this.travelTimeMatrix = travelTimeMatrix;
	}

	public TravelTimeMatrixId id() {
		return id;
	}

	public TravelTimeMatrix matrix() {
		return travelTimeMatrix;
	}

}

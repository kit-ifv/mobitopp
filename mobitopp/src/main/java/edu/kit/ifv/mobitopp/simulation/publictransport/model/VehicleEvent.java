package edu.kit.ifv.mobitopp.simulation.publictransport.model;

public enum VehicleEvent {

	departure("departure"), arrival("arrival");

	private final String type;

	private VehicleEvent(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	};
}

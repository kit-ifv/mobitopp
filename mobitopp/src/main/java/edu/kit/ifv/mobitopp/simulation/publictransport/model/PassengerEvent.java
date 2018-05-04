package edu.kit.ifv.mobitopp.simulation.publictransport.model;

public enum PassengerEvent {
	board("board"), getOff("getOff"), wait("wait");

	private final String type;

	private PassengerEvent(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}
}

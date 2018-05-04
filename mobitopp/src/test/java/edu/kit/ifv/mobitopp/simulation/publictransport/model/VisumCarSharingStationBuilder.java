package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.visum.VisumCarSharingStation;

public class VisumCarSharingStationBuilder {

	private int id;
	private String name;
	private float x;
	private float y;
	private int numberOfCars;
	private String parkingSpace;
	private String type;

	public VisumCarSharingStationBuilder() {
		super();
		id = 1;
		name = "name";
		x = 1.0f;
		y = 2.0f;
		numberOfCars = 2;
		parkingSpace = "parkingSpace";
		type = "normal";
	}

	public VisumCarSharingStation build() {
		return new VisumCarSharingStation(id, name, x, y, numberOfCars, parkingSpace, type);
	}

	public VisumCarSharingStationBuilder withId(int id) {
		this.id = id;
		return this;
	}

}

package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.simulation.publictransport.TransportSystemHelper.dummySet;

import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;
import edu.kit.ifv.mobitopp.visum.VisumVehicleUnit;

public class VisumVehicleUnitBuilder {

	private static final int defaultId = 0;
	private static final String defaultCode = "default vehicle unit";
	private static final String defaultName = "default vehicle unit";
	private static final VisumTransportSystemSet defaultSystem = dummySet();
	private static final int defaultCapacity = 0;
	private static final int defaultSeats = defaultCapacity;

	private final int id;
	private final String code;
	private final String name;
	private final VisumTransportSystemSet transportSystems;
	private int capacity;
	private int seats;

	public VisumVehicleUnitBuilder() {
		super();
		id = defaultId;
		code = defaultCode;
		name = defaultName;
		transportSystems = defaultSystem;
		capacity = defaultCapacity;
		seats = defaultSeats;
	}

	public VisumVehicleUnitBuilder seatsFor(int passengers) {
		capacity = passengers;
		seats = passengers;
		return this;
	}

	public VisumVehicleUnit build() {
		return new VisumVehicleUnit(id, code, name, transportSystems, capacity, seats);
	}

}

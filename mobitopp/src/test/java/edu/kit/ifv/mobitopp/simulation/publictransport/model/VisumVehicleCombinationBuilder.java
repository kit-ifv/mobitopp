package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumVehicleUnit;

import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.visum.VisumVehicleCombination;
import edu.kit.ifv.mobitopp.visum.VisumVehicleUnit;

public class VisumVehicleCombinationBuilder {

	private static final int oneTime = 1;
	private static final int defaultId = 0;
	private static final String defaultCode = "default combination";
	private static final String defaultName = "default combination";
	private final int id;
	private final String code;
	private final String name;
	private final Map<VisumVehicleUnit, Integer> vehicles;


	public VisumVehicleCombinationBuilder() {
		super();
		id = defaultId;
		code = defaultCode;
		name = defaultName;
		vehicles = new HashMap<>();
	}

	public VisumVehicleCombination build() {
		return new VisumVehicleCombination(id, code, name, vehicles);
	}

	public VisumVehicleCombinationBuilder seatsFor(int passengers) {
		vehicles.put(visumVehicleUnit().seatsFor(passengers).build(), oneTime);
		return this;
	}

}

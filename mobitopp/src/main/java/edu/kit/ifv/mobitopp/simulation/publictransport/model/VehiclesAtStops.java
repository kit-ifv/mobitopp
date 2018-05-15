package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static java.util.Collections.emptySet;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public class VehiclesAtStops {

	private final Map<Stop, Set<Vehicle>> vehicles;

	public VehiclesAtStops() {
		super();
		vehicles = new HashMap<>();
	}

	public void add(Vehicle vehicle, Stop atStop) {
		if (!vehicles.containsKey(atStop)) {
			vehicles.put(atStop, new LinkedHashSet<>());
		}
		vehicles.get(atStop).add(vehicle);
	}

	public boolean contains(Vehicle vehicle, Stop atStop) {
		return vehicles.getOrDefault(atStop, emptySet()).contains(vehicle);
	}

	public void remove(Vehicle vehicle, Stop atStop) {
		if(vehicles.containsKey(atStop)) {
			vehicles.get(atStop).remove(vehicle);
		}
	}

}

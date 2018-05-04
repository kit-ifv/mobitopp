package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Iterator;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public class VehicleLocation {

	private final Iterator<Stop> iterator;
	private Stop current;

	public VehicleLocation(Route route) {
		super();
		iterator = route.iterator();
		current = iterator.next();
	}

	public Stop current() {
		return current;
	}

	public void move() {
		current = iterator.next();
	}

}

package edu.kit.ifv.mobitopp.simulation.publictransport;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.simulation.Location;

class Distance {

	private final Location location;
	private double shortest = Double.MAX_VALUE;

	public Distance(Location location) {
		super();
		this.location = location;
	}

	public void ifNearer(Node node) {
		double distance = distanceTo(node);
		if (shortest >= distance) {
			shortest = distance;
		}
	}

	private double distanceTo(Node node) {
		return location.coordinate.distance(node.coordinate());
	}

	public double shortest() {
		return shortest;
	}

}

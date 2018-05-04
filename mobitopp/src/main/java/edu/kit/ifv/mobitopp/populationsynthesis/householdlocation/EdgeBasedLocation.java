package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.kit.ifv.mobitopp.network.Edge;
import edu.kit.ifv.mobitopp.network.ZoneArea;
import edu.kit.ifv.mobitopp.simulation.Location;

public class EdgeBasedLocation {

	private final Lateral roadBased;

	public EdgeBasedLocation(double maxDist) {
		super();
		roadBased = new Lateral(maxDist);
	}

	public Location randomLocationAt(Edge edge, int seed, ZoneArea area) {
		Random localRandom = new Random(seed);
		Point2D point = null;
		Location location = null;
		int cnt = 0;
		while (point == null || (cnt < 10 && !area.contains(point))) {
			double position = localRandom.nextDouble();
			double relDist = localRandom.nextDouble();
			point = roadBased.pointAt(edge, position, relDist);
			location = new Location(point, edge.id(), position);
			cnt++;
		}
		return location;
	}
}

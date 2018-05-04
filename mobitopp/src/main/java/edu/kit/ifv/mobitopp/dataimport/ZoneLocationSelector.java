package edu.kit.ifv.mobitopp.dataimport;

import java.awt.geom.Point2D;

import edu.kit.ifv.mobitopp.network.SimpleEdge;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.visum.VisumPoint2;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class ZoneLocationSelector {

	private final SimpleRoadNetwork network;

	public ZoneLocationSelector(SimpleRoadNetwork network) {
		super();
		this.network = network;
	}

	public Location selectLocation(VisumZone zone, VisumPoint2 coordinate) {
		Point2D point = coordinate.asPoint2D();
		SimpleEdge road = network.zones().get(zone.id).nearestEdge(point);
		double pos = road.nearestPositionOnEdge(point);
		return new Location(point, road.id(), pos);
	}
}

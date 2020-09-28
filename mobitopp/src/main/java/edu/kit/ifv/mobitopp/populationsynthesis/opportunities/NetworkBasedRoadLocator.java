package edu.kit.ifv.mobitopp.populationsynthesis.opportunities;

import java.awt.geom.Point2D;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.network.SimpleEdge;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;

public final class NetworkBasedRoadLocator implements RoadLocator {

	private SimpleRoadNetwork roadNetwork;

	public NetworkBasedRoadLocator(SimpleRoadNetwork roadNetwork) {
		this.roadNetwork = roadNetwork;
	}

	@Override
	public RoadPosition getRoadPosition(ZoneId zone, Point2D coordinate) {
		SimpleEdge nearestEdge = roadNetwork.zone(zone).nearestEdge(coordinate);
		double positionOnEdge = nearestEdge.nearestPositionOnEdge(coordinate);
		return new RoadPosition(nearestEdge.id(), positionOnEdge);
	}
}
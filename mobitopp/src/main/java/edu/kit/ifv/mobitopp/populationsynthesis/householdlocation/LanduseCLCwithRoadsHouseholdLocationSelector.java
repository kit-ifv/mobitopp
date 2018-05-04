package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import java.awt.geom.Point2D;

import edu.kit.ifv.mobitopp.network.SimpleEdge;
import edu.kit.ifv.mobitopp.network.Zone;
import edu.kit.ifv.mobitopp.network.ZoneArea;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.simulation.Location;

public class LanduseCLCwithRoadsHouseholdLocationSelector
		extends LanduseCLCHouseholdLocationSelector implements HouseholdLocationSelector {

	public LanduseCLCwithRoadsHouseholdLocationSelector(SynthesisContext context) {
		super(context);
	}

	@Override
	public Location selectLocation(Zone zone) {
		ZoneArea area = zoneArea(zone);
		Point2D point = area.randomPoint(random().nextInt());
		SimpleEdge road = zone.nearestEdge(point);
		double pos = road.nearestPositionOnEdge(point);
		return new Location(point, road.id(), pos);
	}

}

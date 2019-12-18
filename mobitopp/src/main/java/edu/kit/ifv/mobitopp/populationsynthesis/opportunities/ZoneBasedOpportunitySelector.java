package edu.kit.ifv.mobitopp.populationsynthesis.opportunities;

import java.awt.geom.Point2D;
import java.util.Map;

import edu.kit.ifv.mobitopp.network.SimpleEdge;
import edu.kit.ifv.mobitopp.network.ZoneArea;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.simulation.Location;

public class ZoneBasedOpportunitySelector extends DefaultOpportunityLocationSelector {

	public ZoneBasedOpportunitySelector(SynthesisContext context) {
		super(context);
	}

	@Override
	protected Location selectLocation(
			edu.kit.ifv.mobitopp.network.Zone zone, Map<String, Float> landtypeWeights) {
		ZoneArea area = zone.totalArea();
		Point2D point = area.randomPoint(random().nextInt());
		SimpleEdge road = zone.nearestEdge(point);
		double pos = road.nearestPositionOnEdge(point);
		return new Location(point, road.id(), pos);
	}

}

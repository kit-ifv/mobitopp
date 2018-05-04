package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import edu.kit.ifv.mobitopp.network.Edge;
import edu.kit.ifv.mobitopp.network.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.simulation.Location;

public class RoadBasedHouseholdLocationSelector extends RandomHouseholdLocationSelector
		implements HouseholdLocationSelector {

	private final EdgeBasedLocation randomLocation;
	private final EdgeDistributions edgeDistributions;

	public RoadBasedHouseholdLocationSelector(
			SynthesisContext context, double maxDist, EdgeFilter edgeFilter) {
		super(context);
		randomLocation = new EdgeBasedLocation(maxDist);
		edgeDistributions = new EdgeDistributions(context.roadNetwork(), edgeFilter);
	}

	@Override
	public Location selectLocation(Zone zone) {
		double random = random().nextDouble();
		Edge edge = edgeDistributions.selectEdgeIn(zone, random);
		return randomLocation.randomLocationAt(edge, random().nextInt(), zone.totalArea());
	}

}

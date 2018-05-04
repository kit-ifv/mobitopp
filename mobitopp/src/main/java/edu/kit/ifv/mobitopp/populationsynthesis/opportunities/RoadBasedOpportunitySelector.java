package edu.kit.ifv.mobitopp.populationsynthesis.opportunities;

import java.util.Map;
import java.util.Random;

import edu.kit.ifv.mobitopp.network.Edge;
import edu.kit.ifv.mobitopp.network.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.EdgeBasedLocation;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.EdgeDistributions;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.EdgeFilter;
import edu.kit.ifv.mobitopp.simulation.Location;

public class RoadBasedOpportunitySelector extends DefaultOpportunityLocationSelector {

	private final Random random;
	private final EdgeDistributions edgeDistributions;
	private final EdgeBasedLocation randomLocation;

	public RoadBasedOpportunitySelector(
			SynthesisContext context, EdgeFilter edgeFilter, double maximumDistance) {
		super(context);
		this.random = new Random(context.seed());
		edgeDistributions = new EdgeDistributions(context.roadNetwork(), edgeFilter);
		randomLocation = new EdgeBasedLocation(maximumDistance);
	}

	@Override
	protected Location selectLocation(Zone zone, Map<String, Float> landtypeWeights) {
		Edge edge = edgeDistributions.selectEdgeIn(zone, random.nextDouble());
		return randomLocation.randomLocationAt(edge, random.nextInt(), zone.totalArea());
	}

}

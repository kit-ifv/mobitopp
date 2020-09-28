package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Map;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;

public class FallBackOpportunityLocationSelector implements OpportunityLocationSelector {

	private final OpportunityLocationSelector selector;
	private final OpportunityLocationSelector defaultSelector;

	public FallBackOpportunityLocationSelector(
			OpportunityLocationSelector selector, OpportunityLocationSelector defaultSelector) {
		this.selector = selector;
		this.defaultSelector = defaultSelector;
	}

	@Override
	public Map<Location, Integer> createLocations(
			ZoneId zone, ActivityType activityType, Integer total_opportunities) {
		Map<Location, Integer> locations = selector
				.createLocations(zone, activityType, total_opportunities);
		if (locations.isEmpty()) {
			return defaultSelector.createLocations(zone, activityType, total_opportunities);
		}
		return locations;
	}

}

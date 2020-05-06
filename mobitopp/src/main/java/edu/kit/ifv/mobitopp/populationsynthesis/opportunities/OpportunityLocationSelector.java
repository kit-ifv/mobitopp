package edu.kit.ifv.mobitopp.populationsynthesis.opportunities;

import java.util.Map;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;

public interface OpportunityLocationSelector {

	Map<Location, Integer> createLocations(
			Zone dataZone, ActivityType activityType, Integer total_opportunities);

}
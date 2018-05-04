package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

public class LocationSelector implements OpportunityLocationSelector {

	private Map<Zone, Map<ActivityType, Map<Location, Integer>>> mapping;

	private LocationSelector(Map<Zone, Map<ActivityType, Map<Location, Integer>>> mapping) {
		super();
		this.mapping = mapping;
	}

	@Override
	public Map<Location, Integer> createLocations(
			Zone zone, ActivityType activityType, Integer total_opportunities) {
		if (mapping.containsKey(zone)) {
			return createLocations(mapping.get(zone), activityType);
		}
		return emptyMap();
	}

	private static Map<Location, Integer> createLocations(
			Map<ActivityType, Map<Location, Integer>> mapping, ActivityType activityType) {
		if (mapping.containsKey(activityType)) {
			return mapping.get(activityType);
		}
		return emptyMap();
	}

	public static OpportunityLocationSelector from(List<Opportunity> opportunities) {
		Map<Zone, Map<ActivityType, Map<Location, Integer>>> mapping = opportunities
				.stream()
				.collect(byZoneAndActivityType());
		return new LocationSelector(mapping);
	}

	private static Collector<Opportunity, ?, LinkedHashMap<Zone, Map<ActivityType, Map<Location, Integer>>>> byZoneAndActivityType() {
		return groupingBy(Opportunity::zone, LinkedHashMap::new, groupingByActivityType());
	}

	private static Collector<Opportunity, ?, Map<ActivityType, Map<Location, Integer>>> groupingByActivityType() {
		return groupingBy(Opportunity::activityType, LinkedHashMap::new, groupingByLocation());
	}

	private static Collector<Opportunity, ?, Map<Location, Integer>> groupingByLocation() {
		return toMap(Opportunity::location, Opportunity::attractivity, uniqueLocations(),
				LinkedHashMap::new);
	}

	/**
	 * @see Collectors#throwingMerger
	 */
	private static <T> BinaryOperator<T> uniqueLocations() {
		return (location, v) -> {
			throw new IllegalArgumentException(String.format("Several opportunities at same location", location));
		};
	}

}

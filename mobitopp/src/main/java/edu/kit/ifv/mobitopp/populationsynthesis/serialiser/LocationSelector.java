package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocationSelector implements OpportunityLocationSelector {

	private Map<ZoneId, Map<ActivityType, Map<Location, Integer>>> mapping;

	private LocationSelector(Map<ZoneId, Map<ActivityType, Map<Location, Integer>>> mapping) {
		super();
		this.mapping = mapping;
	}

	@Override
	public Map<Location, Integer> createLocations(
			ZoneId zone, ActivityType activityType, Integer total_opportunities) {
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
		Map<ZoneId, Map<ActivityType, Map<Location, Integer>>> mapping = opportunities
				.stream()
				.collect(byZoneAndActivityType());
		return new LocationSelector(mapping);
	}

	private static Collector<Opportunity, ?, LinkedHashMap<ZoneId, Map<ActivityType, Map<Location, Integer>>>> byZoneAndActivityType() {
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
			throw warn(new IllegalArgumentException(String.format("Several opportunities at same location: %s", location)), log);
		};
	}

}

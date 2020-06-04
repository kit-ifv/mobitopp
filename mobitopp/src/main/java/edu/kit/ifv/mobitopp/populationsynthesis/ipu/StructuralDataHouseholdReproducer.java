package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;

public class StructuralDataHouseholdReproducer implements HouseholdReproducer {

	private final DemandZone zone;
	private final AttributeType householdFilterType;
	private final WeightedHouseholdSelector householdSelector;
	private final List<Attribute> attributes;
	
	public StructuralDataHouseholdReproducer(
			final DemandZone zone, final AttributeType householdFilterType,
			final WeightedHouseholdSelector householdSelector,
			final List<Attribute> householdAttributes) {
		super();
		this.zone = zone;
		this.householdFilterType = householdFilterType;
		this.householdSelector = householdSelector;
		this.attributes = householdAttributes;
	}

	@Override
	public Stream<WeightedHousehold> getHouseholdsToCreate(final List<WeightedHousehold> households) {
		return householdDistribution().items().flatMap(item -> filter(households, item));
	}

	private RangeDistributionIfc householdDistribution() {
		return zone.nominalDemography().getDistribution(householdFilterType);
	}

	private Stream<WeightedHousehold> filter(
			final List<WeightedHousehold> households, final RangeDistributionItem item) {
		List<WeightedHousehold> householdsByType = households
				.stream()
				.filter(household -> filterType(household, item))
				.collect(toList());
		return householdSelector.selectFrom(householdsByType, item.amount()).stream();
	}

	private boolean filterType(final WeightedHousehold household, final RangeDistributionItem item) {
		return attributes
				.stream()
				.filter(attribute -> attribute.matches(item))
				.anyMatch(attribute -> 0 < household.attribute(attribute.name()));
	}

}

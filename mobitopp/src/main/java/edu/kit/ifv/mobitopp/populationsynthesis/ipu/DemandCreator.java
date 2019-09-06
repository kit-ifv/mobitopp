package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DemandCreator {

	private static final Predicate<HouseholdOfPanelData> acceptAll = h -> true;

	private final HouseholdBuilder householdBuilder;
	private final PanelDataRepository panelData;
	private final WeightedHouseholdSelector householdSelector;
	private final AttributeType attributeType;
	private final Predicate<HouseholdOfPanelData> householdFilter;

	public DemandCreator(
			final HouseholdBuilder householdBuilder, final PanelDataRepository panelDataRepository,
			final WeightedHouseholdSelector householdSelector, final AttributeType attributeType,
			final Predicate<HouseholdOfPanelData> householdFilter) {
		super();
		this.householdBuilder = householdBuilder;
		this.panelData = panelDataRepository;
		this.householdSelector = householdSelector;
		this.attributeType = attributeType;
		this.householdFilter = householdFilter;
	}

	public DemandCreator(
			final HouseholdBuilder householdBuilder, final WeightedHouseholdSelector householdSelector,
			final AttributeType attributeType, final PanelDataRepository panelData) {
		this(householdBuilder, panelData, householdSelector, attributeType, acceptAll);
	}

	public List<HouseholdForSetup> demandFor(
			List<WeightedHousehold> households, RangeDistributionIfc distribution) {
		return distribution
				.items()
				.flatMap(item -> filter(households, item))
				.map(WeightedHousehold::id)
				.map(panelData::getHousehold)
				.filter(householdFilter)
				.map(householdBuilder::householdFor)
				.collect(toList());
	}

	private Stream<WeightedHousehold> filter(
			List<WeightedHousehold> households, RangeDistributionItem item) {
		List<WeightedHousehold> householdsByType = households
				.stream()
				.filter(household -> filterType(household, item))
				.collect(toList());
		return householdSelector.selectFrom(householdsByType, item.amount()).stream();
	}

	private boolean filterType(WeightedHousehold household, RangeDistributionItem item) {
		String instanceName = attributeType.createInstanceName(item);
		return household.attribute(instanceName) > 0;
	}

}

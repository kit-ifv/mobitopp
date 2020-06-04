package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DemandCreator {

	private final HouseholdBuilder householdBuilder;
	private final PanelDataRepository panelData;
	private final Predicate<HouseholdOfPanelData> householdFilter;
	private final HouseholdReproducer householdMultiplier;

	public DemandCreator(
			final HouseholdBuilder householdBuilder, final PanelDataRepository panelDataRepository,
			final Predicate<HouseholdOfPanelData> householdFilter,
			final HouseholdReproducer householdMultiplier) {
		super();
		this.householdBuilder = householdBuilder;
		this.panelData = panelDataRepository;
		this.householdFilter = householdFilter;
		this.householdMultiplier = householdMultiplier;
	}

	public List<HouseholdForSetup> demandFor(final List<WeightedHousehold> households) {
		return householdMultiplier.getHouseholdsToCreate(households)
				.map(WeightedHousehold::id)
				.map(panelData::getHousehold)
				.filter(householdFilter)
				.map(householdBuilder::householdFor)
				.collect(toList());
	}

}

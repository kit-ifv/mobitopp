package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonCreator;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WeightDemandCreatorFactory implements DemandCreatorFactory {

	private final HouseholdCreator householdCreator;
	private final PersonCreator personCreator;
	private final PanelDataRepository panelData;
	private final AttributeType householdFilterType;
	private final Function<DemandZone, Predicate<HouseholdOfPanelData>> householdFilter;
	private final WeightedHouseholdSelector householdSelector;

	@Override
	public DemandCreator create(final DemandZone zone, final AttributeResolver attributeResolver) {
		HouseholdBuilder usingBuilder = new DefaultHouseholdBuilder(zone, householdCreator,
				personCreator, panelData);
		List<Attribute> householdAttributes = attributeResolver.attributesOf(householdFilterType);
		Predicate<HouseholdOfPanelData> householdForZoneFilter = householdFilter.apply(zone);
		return new DemandCreator(usingBuilder, panelData, householdForZoneFilter,
				new WeightedHouseholdReproducer(householdAttributes, householdSelector));
	}

}

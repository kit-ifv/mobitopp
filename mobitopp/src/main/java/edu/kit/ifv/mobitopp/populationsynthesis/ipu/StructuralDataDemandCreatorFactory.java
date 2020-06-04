package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonCreator;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class StructuralDataDemandCreatorFactory implements DemandCreatorFactory {

	private final HouseholdCreator householdCreator;
	private final PersonCreator personCreator;
	private final PanelDataRepository panelData;
	private final AttributeType householdFilterType;
	private final Function<DemandZone, Predicate<HouseholdOfPanelData>> householdFilter;
	private final WeightedHouseholdSelector householdSelector;

	public StructuralDataDemandCreatorFactory(
			final HouseholdCreator householdCreator, final PersonCreator personCreator,
			final PanelDataRepository panelData, final AttributeType householdFilterType,
			final Function<DemandZone, Predicate<HouseholdOfPanelData>> householdFilter,
			final WeightedHouseholdSelector householdSelector) {
				this.householdCreator = householdCreator;
				this.personCreator = personCreator;
				this.panelData = panelData;
				this.householdFilterType = householdFilterType;
				this.householdFilter = householdFilter;
				this.householdSelector = householdSelector;
	}

	@Override
	public DemandCreator create(final DemandZone zone, final AttributeResolver attributeResolver) {
		HouseholdBuilder usingBuilder = new DefaultHouseholdBuilder(zone, householdCreator,
				personCreator, panelData);
		List<Attribute> householdAttributes = attributeResolver.attributesOf(householdFilterType);
		Predicate<HouseholdOfPanelData> householdForZoneFilter = householdFilter.apply(zone);
		StructuralDataHouseholdReproducer multiplier = new StructuralDataHouseholdReproducer(zone,
				householdFilterType, householdSelector, householdAttributes);
		return new DemandCreator(usingBuilder, panelData, householdForZoneFilter, multiplier);
	}

}

package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.Attribute;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeResolver;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DefaultHouseholdBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DemandCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.HouseholdBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHousehold;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHouseholdSelector;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateAndSaveDemand {

	private final HouseholdCreator householdCreator;
	private final PersonCreator personCreator;
	private final PanelDataRepository panelData;
	private final AttributeType householdFilterType;
	private final Function<DemandZone, Predicate<HouseholdOfPanelData>> householdFilter;
	private final WeightedHouseholdSelector householdSelector;
	private final Results results;
	private final DemandCategories categories;

	public void createAndSave(List<WeightedHousehold> households, DemandZone forZone, AttributeResolver attributeResolver) {
		List<HouseholdForSetup> demand = createDemand(households, forZone, attributeResolver);
		save(demand, forZone);
	}
	private List<HouseholdForSetup> createDemand(
			List<WeightedHousehold> households, DemandZone zone, AttributeResolver attributeResolver) {
		HouseholdBuilder usingBuilder = new DefaultHouseholdBuilder(zone, householdCreator,
				personCreator, panelData);
		List<Attribute> householdAttributes = attributeResolver.attributesOf(householdFilterType);
		Predicate<HouseholdOfPanelData> householdForZoneFilter = householdFilter.apply(zone);
		DemandCreator create = new DemandCreator(usingBuilder, panelData, householdSelector,
				householdAttributes, householdForZoneFilter);
		// TODO find correct distribution of householdSize
		RangeDistributionIfc distribution = householdDistributionFor(zone);
		return create.demandFor(households, distribution);
	}

	private RangeDistributionIfc householdDistributionFor(DemandRegion forZone) {
		return forZone.nominalDemography().getDistribution(householdFilterType);
	}

	private void save(List<HouseholdForSetup> demand, DemandZone demandZone) {
		for (HouseholdForSetup newHousehold : demand) {
			log(newHousehold);
			demandZone.getPopulation().addHousehold(newHousehold);
		}
	}
	private void log(HouseholdForSetup newHousehold) {
		results.write(categories.demanddataResult, newHousehold.toString());
	}

}

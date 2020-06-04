package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeResolver;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DemandCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DemandCreatorFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHousehold;
import edu.kit.ifv.mobitopp.result.Results;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateAndSaveDemand {

	private final Results results;
	private final DemandCategories categories;
	private final DemandCreatorFactory demandCreatorFactory;

	public void createAndSave(
			final List<WeightedHousehold> households, final DemandZone forZone,
			final AttributeResolver attributeResolver) {
		List<HouseholdForSetup> demand = createDemand(households, forZone, attributeResolver);
		save(demand, forZone);
	}

	private List<HouseholdForSetup> createDemand(
			final List<WeightedHousehold> households, final DemandZone zone,
			final AttributeResolver attributeResolver) {
		DemandCreator create = createDemandCreator(zone, attributeResolver);
		return create.demandFor(households);
	}

	private DemandCreator createDemandCreator(
			final DemandZone zone, final AttributeResolver attributeResolver) {
		return demandCreatorFactory.create(zone, attributeResolver);
	}

	private void save(final List<HouseholdForSetup> demand, final DemandZone demandZone) {
		for (HouseholdForSetup newHousehold : demand) {
			log(newHousehold);
			demandZone.getPopulation().addHousehold(newHousehold);
		}
	}

	private void log(final HouseholdForSetup newHousehold) {
		results.write(categories.demanddataResult, newHousehold.toString());
	}

}

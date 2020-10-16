package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;

import edu.kit.ifv.mobitopp.data.DemandRegion;
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
			final List<WeightedHousehold> households, final DemandRegion region,
			final AttributeResolver attributeResolver) {
		List<HouseholdForSetup> demand = createDemand(households, region, attributeResolver);
		save(demand, region);
	}

	private List<HouseholdForSetup> createDemand(
			final List<WeightedHousehold> households, final DemandRegion region,
			final AttributeResolver attributeResolver) {
		DemandCreator create = createDemandCreator(region, attributeResolver);
		return create.demandFor(households);
	}

	private DemandCreator createDemandCreator(
			final DemandRegion region, final AttributeResolver attributeResolver) {
		return demandCreatorFactory.create(region, attributeResolver);
	}

	private void save(final List<HouseholdForSetup> demand, final DemandRegion region) {
	  region.zones().forEach(toZone -> addHouseholdsOf(demand, toZone));
	}

  private void addHouseholdsOf(List<HouseholdForSetup> demand, DemandZone zone) {
    demand
        .stream()
        .filter(household -> zone.getId().equals(household.homeZone().getId()))
        .forEach(household -> addHousehold(zone, household));
  }

  protected void addHousehold(DemandZone zone, HouseholdForSetup household) {
    zone.getPopulation().addHousehold(household);
    log(household);
  }

  private void log(final HouseholdForSetup newHousehold) {
		results.write(categories.demanddataResult, newHousehold.toString());
	}

}

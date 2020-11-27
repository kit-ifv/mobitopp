package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations;

import java.util.Collection;
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PopulationForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemandRegionOdPairSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.region.OdPair;
import edu.kit.ifv.mobitopp.populationsynthesis.region.PopulationSynthesisStep;

public class DemandRegionDestinationSelector implements PopulationSynthesisStep {

	private final DemandRegionOdPairSelector odPairSelector;
	private final ZoneSelector zoneSelector;
	private final Predicate<PersonBuilder> personFilter;
	private final DoubleSupplier random;

	public DemandRegionDestinationSelector(
			final DemandRegionOdPairSelector odPairSelector, final ZoneSelector zoneSelector,
			final Predicate<PersonBuilder> personFilter, final DoubleSupplier random) {
		super();
		this.odPairSelector = odPairSelector;
		this.zoneSelector = zoneSelector;
		this.personFilter = personFilter;
		this.random = random;
	}

	@Override
	public void process(final DemandRegion region) {
		int numberOfCommuters = Math
				.toIntExact(region
						.zones()
						.map(DemandZone::getPopulation)
						.flatMap(PopulationForSetup::households)
						.flatMap(HouseholdForSetup::persons)
						.filter(personFilter)
						.count());
		odPairSelector.scale(region, numberOfCommuters);
		region
				.zones()
				.map(DemandZone::getPopulation)
				.flatMap(PopulationForSetup::households)
				.flatMap(HouseholdForSetup::persons)
				.filter(personFilter)
				.forEach(this::assignZone);
	}

  private void assignZone(final PersonBuilder person) {
    try {
      final Collection<OdPair> relations = odPairSelector.select(person);
      double randomNumber = random.getAsDouble();
      zoneSelector.select(person, relations, randomNumber);
    } catch (IllegalArgumentException exception) {
      System.out
          .println(String
              .format("Could not assign a destination for person %s: %s", person.getId(),
                  exception));
      exception.printStackTrace();
    }
	}

}

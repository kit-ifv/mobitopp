package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.Collection;
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PopulationForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemandRegionOdPairSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.region.OdPair;
import edu.kit.ifv.mobitopp.populationsynthesis.region.PopulationSynthesisStep;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DemandRegionDestinationSelector implements PopulationSynthesisStep {

	private final DemandRegionOdPairSelector odPairSelector;
	private final ZoneSelector zoneSelector;
	private final Predicate<PersonBuilder> personFilter;
	private final DoubleSupplier random;

	public DemandRegionDestinationSelector(final DemandRegionOdPairSelector odPairSelector,
			final ZoneSelector zoneSelector, final Predicate<PersonBuilder> personFilter,
			final DoubleSupplier random) {
		super();
		this.odPairSelector = odPairSelector;
		this.zoneSelector = zoneSelector;
		this.personFilter = personFilter;
		this.random = random;
	}

	@Override
	public void process(final DemandRegion region) {
		int numberOfCommuters = Math.toIntExact(getAgentsOf(region).count());
		odPairSelector.scale(region, numberOfCommuters);
		getAgentsOf(region).forEach(this::assignZone);
	}

	private Stream<PersonBuilder> getAgentsOf(final DemandRegion region) {
		return region
				.zones()
				.map(DemandZone::getPopulation)
				.flatMap(PopulationForSetup::households)
				.flatMap(HouseholdForSetup::persons)
				.filter(personFilter);
	}

	private void assignZone(final PersonBuilder person) {
		try {
			final Collection<OdPair> relations = odPairSelector.select(person);
			double randomNumber = random.getAsDouble();
			zoneSelector.select(person, relations, randomNumber);
		} catch (IllegalArgumentException exception) {
			String message = String.format("Could not assign a destination for person %s:",
					person.getId());
			warn(exception, message, log);
		}
	}

}

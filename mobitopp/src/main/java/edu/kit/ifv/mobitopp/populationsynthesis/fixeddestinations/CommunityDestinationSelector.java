package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations;

import java.util.Collection;
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PopulationForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.community.Community;
import edu.kit.ifv.mobitopp.populationsynthesis.community.OdPair;
import edu.kit.ifv.mobitopp.populationsynthesis.community.OdPairSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.community.PopulationSynthesisStep;

public class CommunityDestinationSelector implements PopulationSynthesisStep {

	private final OdPairSelector odPairSelector;
	private final ZoneSelector zoneSelector;
	private final Predicate<PersonBuilder> personFilter;
	private final DoubleSupplier random;

	public CommunityDestinationSelector(
			final OdPairSelector odPairSelector, final ZoneSelector zoneSelector,
			final Predicate<PersonBuilder> personFilter, final DoubleSupplier random) {
		super();
		this.odPairSelector = odPairSelector;
		this.zoneSelector = zoneSelector;
		this.personFilter = personFilter;
		this.random = random;
	}

	@Override
	public void process(final Community community) {
		int numberOfCommuters = Math.toIntExact(community
				.zones()
				.map(DemandZone::getPopulation)
				.flatMap(PopulationForSetup::households)
				.flatMap(HouseholdForSetup::persons)
				.filter(personFilter).count());
		odPairSelector.scale(community, numberOfCommuters);
		community
				.zones()
				.map(DemandZone::getPopulation)
				.flatMap(PopulationForSetup::households)
				.flatMap(HouseholdForSetup::persons)
				.filter(personFilter)
				.forEach(this::assignZone);
	}

	private void assignZone(final PersonBuilder person) {
		final Collection<OdPair> relations = odPairSelector.select(person);
		double randomNumber = random.getAsDouble();
		zoneSelector.select(person, relations, randomNumber);
	}

}

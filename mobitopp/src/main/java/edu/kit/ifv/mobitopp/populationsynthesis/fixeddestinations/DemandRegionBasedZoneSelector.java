package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemandRegionRelationsObserver;
import edu.kit.ifv.mobitopp.populationsynthesis.region.OdPair;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.util.collections.StreamUtils;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

public class DemandRegionBasedZoneSelector implements ZoneSelector {

	private final DemandRegionRelationsObserver observer;
	private final DoubleSupplier random;
	private final ActivityType activityType;

	public DemandRegionBasedZoneSelector(DemandRegionRelationsObserver observer, DoubleSupplier random) {
		super();
		this.observer = observer;
		this.random = random;
		this.activityType = ActivityType.WORK;
	}

	@Override
	public void select(PersonBuilder person, Collection<OdPair> relations, double randomNumber) {
		Map<Zone, Integer> attractivities = collectPossibleZones(relations);
		if (attractivities.isEmpty()) {
			throw new IllegalArgumentException("Could not determine a location for " + person);
		}
		Zone zone = selectZone(randomNumber, attractivities);
		assignZone(person, zone);
		notifyAssignment(person.homeZone(), zone);
	}

	private void notifyAssignment(Zone homeZone, Zone destination) {
		observer.notifyAssignedRelation(homeZone, destination);
	}

	private void assignZone(PersonBuilder person, Zone zone) {
		Location location = zone.opportunities().selectRandomLocation(activityType, nextRandom());
		person.addFixedDestination(new FixedDestination(activityType, zone, location));
	}

	private double nextRandom() {
		return random.getAsDouble();
	}

	private Map<Zone, Integer> collectPossibleZones(final Collection<OdPair> relations) {
		return relations
				.stream()
				.map(OdPair::getPossibleDestination)
				.filter(zone -> zone.isDestinationFor(activityType))
				.collect(StreamUtils
						.toSortedMap(Function.identity(), z -> (int) z.getAttractivity(activityType),
								Comparator.comparing(Zone::getId)));
	}

	private Zone selectZone(double randomNumber, Map<Zone, Integer> attractivities) {
		return new DiscreteRandomVariable<>(attractivities).realization(randomNumber);
	}

}

package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.ForeignKeySerialiserFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PersonFixedDestination;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PopulationContext;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;
import edu.kit.ifv.mobitopp.simulation.Person;

public class DefaultFixedDestinationFormat
		implements ForeignKeySerialiserFormat<PersonFixedDestination> {

	private static final int personOidIndex = 0;
	private static final int activityTypeIndex = 1;
	private static final int zoneOidIndex = 2;
	private static final int locationIndex = 3;
	
	private final ZoneRepository zoneRepository;
	private final LocationParser locationParser;

	public DefaultFixedDestinationFormat(ZoneRepository zoneRepository) {
		super();
		this.zoneRepository = zoneRepository;
		locationParser = new LocationParser();
	}
	
	@Override
	public List<String> header() {
		return asList("personId", "activityType", "zoneId", "location");
	}

	@Override
	public List<String> prepare(PersonFixedDestination element, PopulationContext context) {
		String location = locationParser.serialise(element.fixedDestination().location());
		return asList(
				valueOf(element.person().getOid()),
				valueOf(element.fixedDestination().activityType()),
				valueOf(element.fixedDestination().zone().getOid()),
				location
		);
	}

	@Override
	public Optional<PersonFixedDestination> parse(List<String> data, PopulationContext context) {
		return destinationOf(data).flatMap(d -> createFixedDestination(data, context, d));
	}

	private Optional<PersonFixedDestination> createFixedDestination(
			List<String> data, PopulationContext context, FixedDestination destination) {
		Optional<Person> person = personOf(data, context);
		return person.map(p -> new PersonFixedDestination(p, destination));
	}

	private Optional<Person> personOf(List<String> data, PopulationContext context) {
		int oid = Integer.parseInt(data.get(personOidIndex));
		return context.getPersonByOid(oid);
	}

	private Optional<FixedDestination> destinationOf(List<String> data) {
		Optional<Zone> zone = zoneOF(data);
		return zone.map(z -> createFixedDestination(data, z));
	}

	private FixedDestination createFixedDestination(List<String> data, Zone zone) {
		ActivityType activityType = ActivityType.valueOf(data.get(activityTypeIndex));
		Location location = locationParser.parse(data.get(locationIndex));
		return new FixedDestination(activityType, zone, location);
	}

	private Optional<Zone> zoneOF(List<String> data) {
		int zoneOid = Integer.parseInt(data.get(zoneOidIndex));
		if (zoneRepository.hasZone(zoneOid)) {
			return Optional.of(zoneRepository.getZoneByOid(zoneOid));
		}
		return Optional.empty();
	}

}

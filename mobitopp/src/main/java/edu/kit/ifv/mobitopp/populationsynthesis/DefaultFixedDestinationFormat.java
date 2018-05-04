package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

import java.util.List;

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
	public PersonFixedDestination parse(List<String> data, PopulationContext context) {
		Person person = personOf(data, context);
		FixedDestination destination = destinationOf(data);
		return new PersonFixedDestination(person, destination);
	}

	private Person personOf(List<String> data, PopulationContext context) {
		int oid = Integer.parseInt(data.get(personOidIndex));
		return context.getPersonByOid(oid);
	}

	private FixedDestination destinationOf(List<String> data) {
		ActivityType activityType = ActivityType.valueOf(data.get(activityTypeIndex));
		int zoneOid = Integer.parseInt(data.get(zoneOidIndex));
		Zone zone = zoneRepository.getZoneByOid(zoneOid);
		Location location = locationParser.parse(data.get(locationIndex));
		return new FixedDestination(activityType, zone, location);
	}

}

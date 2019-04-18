package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

public class DefaultOpportunityFormat implements SerialiserFormat<Opportunity> {

	private static final int zoneOidIndex = 0;
	private static final int activityTypeIndex = 1;
	private static final int locationIndex = 2;
	private static final int attractivityIndex = 3;

	private final ZoneRepository zoneRepository;
	private final LocationParser locationParser;

	public DefaultOpportunityFormat(ZoneRepository zoneRepository) {
		super();
		this.zoneRepository = zoneRepository;
		locationParser = new LocationParser();
	}
	
	@Override
	public List<String> header() {
		return asList("zoneId", "activityType", "location", "attractivity");
	}

	@Override
	public List<String> prepare(Opportunity opportunity) {
		String location = locationParser.serialise(opportunity.location());
		ArrayList<String> attributes = new ArrayList<>();
		attributes.add(valueOf(opportunity.zone().getId().getMatrixColumn()));
		attributes.add(valueOf(opportunity.activityType()));
		attributes.add(location);
		attributes.add(valueOf(opportunity.attractivity()));
		return attributes;
	}

	@Override
	public Optional<Opportunity> parse(List<String> data) {
		return zoneOf(data).map(z -> createOpportunity(data, z));
	}

	private Opportunity createOpportunity(List<String> data, Zone zone) {
		ActivityType activityType = activityTypeOf(data);
		Location location = locationOf(data);
		Integer attractivity = attractivityOf(data);
		Opportunity opportunity = new Opportunity(zone, activityType, location, attractivity);
		return opportunity;
	}

	private Optional<Zone> zoneOf(List<String> data) {
		int zoneOid = Integer.parseInt(data.get(zoneOidIndex));
		if (zoneRepository.hasZone(zoneOid)) {
			return Optional.of(zoneRepository.getZoneByOid(zoneOid));
		}
		return Optional.empty();
	}

	private ActivityType activityTypeOf(List<String> data) {
		return ActivityType.valueOf(data.get(activityTypeIndex));
	}

	private Location locationOf(List<String> data) {
		return locationParser.parse(data.get(locationIndex));
	}

	private Integer attractivityOf(List<String> data) {
		return Integer.parseInt(data.get(attractivityIndex));
	}

}

package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toConcurrentMap;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.local.configuration.ZoneProfiles;
import edu.kit.ifv.mobitopp.publictransport.model.DefaultStation;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Profile;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.ProfileBuilder;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ZonesToStops {

	private static final Double defaultCoordinate = new Point2D.Double();
	private static final RelativeTime noChangeTime = RelativeTime.ZERO;
	private static final RelativeTime noWalkTime = RelativeTime.ZERO;
	
	private final Collection<Zone> zones;
	private final HashMap<Zone, Stop> assignedStop;
	private final HashMap<Integer, Stop> assignedStopId;

	public ZonesToStops(Zone... zones) {
		this(asList(zones));
	}

	public ZonesToStops(Collection<Zone> zones) {
		super();
		this.zones = zones;
		assignedStop = new HashMap<>();
		assignedStopId = new HashMap<>();
		initialiseAssignment();
	}

	private void initialiseAssignment() {
		for (Zone zone : zones) {
			initialiseStopFor(zone);
		}
	}

	private void initialiseStopFor(Zone zone) {
		Stop zoneStop = zoneStop(zone);
		assignedStop.put(zone, zoneStop);
		assignedStopId.put(zone.getOid(), zoneStop);
	}

	private Stop zoneStop(Zone zone) {
		int id = -zone.getOid();
		String name = zone.getId();
		Station station = new DefaultStation(id, emptyList());
		int externalId = id;
		return new Stop(id, name, defaultCoordinate, noChangeTime, station, externalId);
	}

	public Stop stopFor(int zoneId) {
		if (assignedStopId.containsKey(zoneId)) {
			return assignedStopId.get(zoneId);
		}
		throw new IllegalArgumentException("No stop assigned for zone: " + zoneId);
	}

	public Stop stopFor(Zone zone) {
		if (assignedStop.containsKey(zone)) {
			return assignedStop.get(zone);
		}
		throw new IllegalArgumentException("No stop assigned for zone: " + zone.getOid());
	}

	public void assign(Stop stop) {
		for (Zone zone : zones) {
			if (zone.getZonePolygon().contains(stop.coordinate())) {
				assign(stop, zone);
			}
		}
	}

	private void assign(Stop stop, Zone zone) {
		Stop zoneStop = assignedStop.get(zone);
		zoneStop.addNeighbour(stop, noWalkTime);
		assignedStop.put(zone, zoneStop);
		assignedStopId.put(zone.getOid(), zoneStop);
	}

	public Matrices calculateMatricesAt(Time day, ProfileBuilder builder) {
		return Matrices.from(day, builder, zones, this::stopFor);
	}

	public ZoneProfiles calculateProfiles(ProfileBuilder builder) {
		logStart();
		Function<Entry<Zone, Stop>, ZoneProfile> toProfile = entry -> toProfile(entry, builder);
		Map<Zone, Profile> profiles = assignedStop.entrySet().parallelStream().map(toProfile).collect(
				toConcurrentMap(ZoneProfile::zone, ZoneProfile::profile));
		logEnd();
		return new ZoneProfiles(this, profiles);
	}

	private void logStart() {
		System.out.println(LocalDateTime.now() + ": Starting to calculate profiles");
	}

	private void logEnd() {
		System.out.println(LocalDateTime.now() + ": Finishing to calculate profiles");
	}

	private ZoneProfile toProfile(Entry<Zone, Stop> entry, ProfileBuilder builder) {
		Zone zone = entry.getKey();
		Stop stop = entry.getValue();
		log(zone);
		Profile profile = builder.buildUpTo(stop);
		return new ZoneProfile(zone, profile);
	}

	private static void log(Zone target) {
		System.out.println(target.getOid());
	}
}

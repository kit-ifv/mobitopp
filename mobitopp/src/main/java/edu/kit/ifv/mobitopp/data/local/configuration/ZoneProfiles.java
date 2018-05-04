package edu.kit.ifv.mobitopp.data.local.configuration;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.matrix.ZonesToStops;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Profile;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ZoneProfiles {

	private final ZonesToStops zonesToStops;
	private final Map<Integer, Profile> profiles;

	public ZoneProfiles(ZonesToStops zonesToStops, Map<Zone, Profile> profiles) {
		this.zonesToStops = zonesToStops;
		this.profiles = profiles
				.entrySet()
				.stream()
				.collect(toMap(entry -> entry.getKey().getOid(), Entry::getValue));
	}

	/**
	 * @return Travel time in minutes between source and target.
	 */
	public float getTravelTime(int source, int target, Time date) {
		Profile profile = profiles.get(target);
		Stop start = zonesToStops.stopFor(source);
		Optional<Time> current = profile.from(start).arrivalFor(date);
		return current.map(arrival -> arrival.differenceTo(date)).map(RelativeTime::toMinutes).orElse(
				RelativeTime.INFINITE.toMinutes());
	}

}

package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Profile;

public class TravelTimes {

	private final Map<Integer, Matrix> matrices;
	private final List<Integer> oids;
	private final List<Profile> profiles;

	public TravelTimes(List<Integer> oids, long hours) {
		super();
		matrices = initialise(oids, hours);
		this.oids = new ArrayList<>(oids);
		profiles = new ArrayList<>();
	}

	private ConcurrentHashMap<Integer, Matrix> initialise(List<Integer> oids, long hours) {
		ConcurrentHashMap<Integer, Matrix> matrix = new ConcurrentHashMap<>();
		for (int hour = 0; hour < hours; hour++) {
			matrix.put(hour, new Matrix(oids));
		}
		return matrix;
	}

	public void update(int hour, Zone origin, Zone destination, Duration travelTime) {
		if (travelTime.isZero()) {
			return;
		}
		Matrix originDestination = matrices.get(hour);
		originDestination.set(origin, destination, travelTime);
		matrices.put(hour, originDestination);
	}

	Duration time(int hour, Zone someZone, Zone anotherZone) {
		return matrices.getOrDefault(hour, new Matrix(oids)).get(someZone, anotherZone);
	}

	public Collection<Integer> hours() {
		return matrices.keySet();
	}

	public Matrix matrixIn(int hour) {
		return matrices.get(hour);
	}

	public void fixInnerZoneTime(Zone zone, float seconds) {
		for (Matrix matrix : matrices.values()) {
			matrix.set(zone, zone, seconds);
		}
	}

	public void add(Profile profile) {
		profiles.add(profile);
	}
	
	public List<Profile> profiles() {
		return profiles;
	}

}

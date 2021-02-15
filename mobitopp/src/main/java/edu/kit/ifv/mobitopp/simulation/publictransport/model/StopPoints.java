package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.serializer.NeighbourhoodCoupler;
import edu.kit.ifv.mobitopp.publictransport.serializer.StopResolver;
import edu.kit.ifv.mobitopp.publictransport.serializer.StopSerializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StopPoints implements StopResolver, VisumStopResolver {

	private final Map<Integer, Stop> points;
	private final Map<Integer, Stop> internalPoints;

	public StopPoints() {
		super();
		points = new HashMap<>();
		internalPoints = new HashMap<>();
	}

	public void initialiseNeighbourhood(NeighbourhoodCoupler walkLinks) {
		for (Stop start : points.values()) {
			for (Stop end : points.values()) {
				walkLinks.addNeighboursshipBetween(start, end);
			}
		}
	}

	public void add(Stop stop) {
		points.put(stop.externalId(), stop);
		internalPoints.put(stop.id(), stop);
	}

	@Override
	public Stop get(int id) {
		if (points.containsKey(id)) {
			return points.get(id);
		}
		throw warn(new RuntimeException("Stop not available: " + id), log);
	}

	@Override
	public Stop resolve(int internal) {
		return internalPoints.get(internal);
	}

	/**
	 * Returns an unmodifiable collection of all {@link Stop}s
	 *
	 * @return
	 */
	public Collection<Stop> stops() {
		return Collections.unmodifiableCollection(points.values());
	}

	public void serializeTo(StopSerializer serializer) {
		points.values().forEach(serializer::serialize);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		StopPoints other = (StopPoints) obj;
		if (points == null) {
			if (other.points != null) {
				return false;
			}
		} else if (!points.equals(other.points)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "StopPoints [points=" + points + "]";
	}

}

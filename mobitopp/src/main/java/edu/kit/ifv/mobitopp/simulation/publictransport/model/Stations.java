package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.serializer.StationResolver;
import edu.kit.ifv.mobitopp.publictransport.serializer.StationSerializer;
import edu.kit.ifv.mobitopp.simulation.publictransport.ShortestPathSearch;

public class Stations implements StationResolver, Iterable<Station> {

	private final Map<Integer, Station> stations;

	public Stations(Map<Integer, Station> stations) {
		super();
		this.stations = new HashMap<>(stations);
	}

	public static Stations from(List<Station> stations) {
		Map<Integer, Station> stationMapping = stations
				.stream()
				.collect(toMap(Station::id, Function.identity()));
		return new Stations(stationMapping);
	}

	@Override
	public Iterator<Station> iterator() {
		return stations.values().iterator();
	}

	@Override
	public Station getStation(int stationId) {
		return stations.get(stationId);
	}

	public StationFinder finder(Function<Map<Node, Station>, ShortestPathSearch> factory) {
		HashMap<Node, Station> inNodeToStation = new HashMap<>();
		for (Station station : stations.values()) {
			Consumer<Node> consumer = linkTo(station, inNodeToStation);
			station.forEachNode(consumer);
		}
		return new ReachableStationsFinder(factory.apply(inNodeToStation), inNodeToStation);
	}

	private Consumer<Node> linkTo(Station station, HashMap<Node, Station> nodeToStation) {
		Consumer<Node> consumer = node -> nodeToStation.put(node, station);
		return consumer;
	}

	public void serializeStationsTo(StationSerializer serializer) {
		stations.values().forEach(serializer::serialize);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stations == null) ? 0 : stations.hashCode());
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
		Stations other = (Stations) obj;
		if (stations == null) {
			if (other.stations != null) {
				return false;
			}
		} else if (!stations.equals(other.stations)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Stations [stations=" + stations + "]";
	}

}

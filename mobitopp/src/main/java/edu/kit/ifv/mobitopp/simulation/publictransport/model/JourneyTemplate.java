package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.RoutePoints;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.TransportSystem;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumPtTimeProfile;
import edu.kit.ifv.mobitopp.visum.VisumPtTimeProfileElement;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourney;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourneySection;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JourneyTemplate {

	/** 500 seats + stance **/
	private static final int regionalTrain = 1200;
	private static final int defaultCapacity = 0;
	
	private final String name;
	private final Map<Integer, Stop> stops;
	private final Map<Integer, RelativeTime> arrivals;
	private final Map<Integer, RelativeTime> departures;

	JourneyTemplate(String name) {
		super();
		this.name = name;
		stops = new HashMap<>();
		arrivals = new HashMap<>();
		departures = new HashMap<>();
	}

	public static JourneyTemplate from(VisumPtTimeProfile visum, VisumStopResolver stopResolver) {
		JourneyTemplate timeProfile = new JourneyTemplate(visum.name);
		for (VisumPtTimeProfileElement element : visum.elements.values()) {
			timeProfile.add(element, stopResolver);
		}
		return timeProfile;
	}

	private void add(VisumPtTimeProfileElement value, VisumStopResolver stopResolver) {
		if (!value.lineRouteElement.isStop()) {
			return;
		}
		Stop stop = stopResolver.get(value.lineRouteElement.stopPoint.id);
		Integer index = value.index;
		RelativeTime arrival = RelativeTime.ofSeconds(value.arrival);
		RelativeTime departure = RelativeTime.ofSeconds(value.departure);
		add(stop, index, arrival, departure);
	}

	void add(Stop stop, int index, RelativeTime arrival, RelativeTime departure) {
		verify(index);
		stops.put(index, stop);
		arrivals.put(index, arrival);
		departures.put(index, departure);
	}

	private void verify(int index) {
		if (arrivals.containsKey(index)) {
			throw warn(new RuntimeException("Should never happen"), log);
		}
		if (departures.containsKey(index)) {
			throw warn(new RuntimeException("Should never happen"), log);
		}
	}

	Optional<ModifiableJourney> createJourney(
			VisumPtVehicleJourney visumJourney, PublicTransportFactory factory, Time day) {
		int capacity = capacityOf(visumJourney);
		TransportSystem system = transportSystemOf(visumJourney);
		ModifiableJourney created = factory.createJourney(visumJourney.id, day, capacity, system);
		// TODO Read "valid (day)" of journeySections
		// TODO Filter relevant journeySections Time##weekday
		// TODO Create Journey only if relevant journeySections are available
		for (int current = visumJourney.fromProfileIndex
				+ 1; current <= visumJourney.toProfileIndex; current++) {
			int previous = current - 1;
			Connection connection = connectionFrom(visumJourney, created, current, previous, factory,
					day);
			created.add(connection);
		}
		return Optional.of(created);
	}

	private static TransportSystem transportSystemOf(VisumPtVehicleJourney visumJourney) {
		String code = visumJourney.route.line.transportSystem.code;
		return new TransportSystem(code);
	}

	private static int capacityOf(VisumPtVehicleJourney visumJourney) {
		for (VisumPtVehicleJourneySection section : visumJourney.sections) {
			if (section.vehicle == null) {
				return defaultCapacity;
			}
			return correctRegionalTrain(section.vehicle.getCapacity());
		}
		return defaultCapacity;
	}

	private static int correctRegionalTrain(int capacity) {
		return capacity == 0 ? regionalTrain : capacity;
	}

	private Connection connectionFrom(
			VisumPtVehicleJourney visumJourney, Journey journey, int current, int previous,
			PublicTransportFactory factory, Time date) {
		Time departureOfJourney = departureOf(visumJourney, date);
		Time departure = departsFrom(previous, departureOfJourney);
		Time arrival = arrivesAt(current, departureOfJourney);
		Stop start = stops.get(previous);
		Stop end = stops.get(current);
		RoutePoints coordinates = factory.coordinates(visumJourney, previous, current, start, end);
		return factory.connectionFrom(start, end, departure, arrival, journey, coordinates);
	}

	private Time departureOf(VisumPtVehicleJourney visumJourney, Time date) {
		Time visumDeparture = date.plusSeconds(visumJourney.departure);
		RelativeTime relativeTime = departures.get(visumJourney.fromProfileIndex);
		return visumDeparture.minus(relativeTime);
	}

	Time departsFrom(Integer stopIndex, Time atTime) {
		RelativeTime relativeTime = departures.get(stopIndex);
		return atTime.plus(relativeTime);
	}

	Time arrivesAt(Integer stopIndex, Time atTime) {
		RelativeTime relativeTime = arrivals.get(stopIndex);
		return atTime.plus(relativeTime);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arrivals == null) ? 0 : arrivals.hashCode());
		result = prime * result + ((departures == null) ? 0 : departures.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((stops == null) ? 0 : stops.hashCode());
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
		JourneyTemplate other = (JourneyTemplate) obj;
		if (arrivals == null) {
			if (other.arrivals != null) {
				return false;
			}
		} else if (!arrivals.equals(other.arrivals)) {
			return false;
		}
		if (departures == null) {
			if (other.departures != null) {
				return false;
			}
		} else if (!departures.equals(other.departures)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (stops == null) {
			if (other.stops != null) {
				return false;
			}
		} else if (!stops.equals(other.stops)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "TimeProfile [name=" + name + ", stops=" + stops + ", arrivals=" + arrivals
				+ ", departures=" + departures + "]";
	}

}

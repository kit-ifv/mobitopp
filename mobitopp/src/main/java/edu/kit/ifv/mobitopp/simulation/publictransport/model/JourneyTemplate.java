package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.RoutePoints;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.TransportSystem;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumPtTimeProfile;
import edu.kit.ifv.mobitopp.visum.VisumPtTimeProfileElement;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourney;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourneySection;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode
@ToString
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

	/**
	 * Create a journey from visum for the specified day. The day will be considered
	 * as absolute anchor for relative travel times within the journey as well as to
	 * filter the journey section for this {@link DayOfWeek}.
	 * 
	 * @param visumJourney journey from visum
	 * @param factory      factory to create journey and connection objects
	 * @param atDay        day were the journey is served
	 * @return journey at the specified day
	 */
	Optional<ModifiableJourney> createJourney(VisumPtVehicleJourney visumJourney,
		PublicTransportFactory factory, Time atDay) {
		return streamSectionsOf(visumJourney, atDay)
			.findFirst()
			.map(firstSection -> createJourney(visumJourney, factory, atDay, firstSection));
	}

	private ModifiableJourney createJourney(VisumPtVehicleJourney visumJourney,
		PublicTransportFactory factory, Time atDay, VisumPtVehicleJourneySection firstSection) {
		int capacity = capacityOf(visumJourney);
		TransportSystem system = transportSystemOf(visumJourney);
		ModifiableJourney journey = factory.createJourney(visumJourney.id, atDay, capacity, system);
		Time departureOfJourney = departureOf(visumJourney, firstSection, atDay);
		Consumer<VisumPtVehicleJourneySection> consumer = section -> createConnectionFrom(section,
			visumJourney, factory, departureOfJourney, journey, journey::add);
		streamSectionsOf(visumJourney, atDay).forEach(consumer);
		return journey;
	}

	/**
	 * Stream sections served at the specified day
	 * 
	 * @param visumJourney
	 * @param day
	 * @return
	 */
	private Stream<VisumPtVehicleJourneySection> streamSectionsOf(
		VisumPtVehicleJourney visumJourney, Time day) {
		return visumJourney.sections.stream().filter(isValidAt(day));
	}

	/**
	 * Check whether a section is served at the given day or not.
	 * 
	 * @param day
	 * @return
	 */
	private Predicate<VisumPtVehicleJourneySection> isValidAt(Time day) {
		return section -> section.validDays.contains(day.weekDay());
	}

	private void createConnectionFrom(VisumPtVehicleJourneySection section,
		VisumPtVehicleJourney visumJourney, PublicTransportFactory factory, Time departureOfJourney,
		Journey journey, Consumer<Connection> consumer) {
		for (int current = section.fromElementIndex
			+ 1; current <= section.toElementIndex; current++) {
			int previous = current - 1;
			Connection connection = connectionFrom(visumJourney, journey, current, previous,
				factory, departureOfJourney);
			consumer.accept(connection);
		}
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

	private Connection connectionFrom(VisumPtVehicleJourney visumJourney, Journey journey,
		int current, int previous, PublicTransportFactory factory, Time departureOfJourney) {
		Time departure = departsFrom(previous, departureOfJourney);
		Time arrival = arrivesAt(current, departureOfJourney);
		Stop start = stops.get(previous);
		Stop end = stops.get(current);
		RoutePoints coordinates = factory.coordinates(visumJourney, previous, current, start, end);
		return factory.connectionFrom(start, end, departure, arrival, journey, coordinates);
	}

	private Time departureOf(VisumPtVehicleJourney visumJourney,
		VisumPtVehicleJourneySection section, Time date) {
		Time visumDeparture = date.plusSeconds(visumJourney.departure);
		RelativeTime relativeTime = departures.get(section.fromElementIndex);
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

}

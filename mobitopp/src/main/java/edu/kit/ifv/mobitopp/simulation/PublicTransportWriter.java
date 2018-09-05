package edu.kit.ifv.mobitopp.simulation;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent.board;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent.getOff;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent.wait;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VehicleEvent.arrival;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VehicleEvent.departure;
import static java.util.stream.Collectors.joining;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.result.CsvBuilder;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.PublicTransportResults;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.VehicleEvent;
import edu.kit.ifv.mobitopp.time.DateFormat;
import edu.kit.ifv.mobitopp.time.Time;

public class PublicTransportWriter implements PublicTransportResults {

	private final ResultWriter results;
	private final DateFormat format;
	private final PublicTransportCategories categories;

	public PublicTransportWriter(ResultWriter results) {
		super();
		this.results = results;
		categories = new PublicTransportCategories();
		this.format = results.dateFormat();
	}

	private ResultWriter results() {
		return results;
	}

	@Override
	public void arrive(Time time, Vehicle vehicle) {
		Stop currentStop = vehicle.currentStop();
		writeVehicleTrip(arrival, vehicle, currentStop, time);
	}

	@Override
	public void depart(Time time, Vehicle vehicle) {
		Stop currentStop = vehicle.currentStop();
		writeVehicleTrip(departure, vehicle, currentStop, time);
	}

	public void writeVehicleTrip(VehicleEvent event, Vehicle vehicle, Stop stop, Time currentDate) {
		int journeyId = vehicle.journeyId();
		int stopId = stop.externalId();
		String stopName = stop.name();

		String tripBeginDay = format.asDay(currentDate);
		String tripBeginTime = format.asTime(currentDate);

		CsvBuilder message = new CsvBuilder();
		message.append(event.toString());
		message.append(journeyId);
		message.append(stopId);
		message.append(stopName);
		message.append(tripBeginDay);
		message.append(tripBeginTime);
		message.append(vehicle.passengerCount());

		results().write(categories.vehicle, message.toString());
	}

	@Override
	public void board(SimulationPerson person, Time time, PublicTransportLeg part, TripIfc trip) {
		Stop stop = part.start();
		writePassenger(board, person, time, stop, part, trip);
	}

	@Override
	public void getOff(SimulationPerson person, Time time, PublicTransportLeg part, TripIfc trip) {
		Stop stop = part.end();
		writePassenger(getOff, person, time, stop, part, trip);
		writeRouteLeg(person, part);
	}

	@Override
	public void wait(SimulationPerson person, Time time, PublicTransportLeg part, TripIfc trip) {
		Stop stop = part.start();
		writePassenger(wait, person, time, stop, part, trip);
	}

	public void writePassenger(
			PassengerEvent event, Person person, Time time, Stop stop, PublicTransportLeg part,
			TripIfc trip) {
		int tripId = trip.getOid();
		int personOid = person.getOid();
		int journeyId = part.journeyId();
		int stopId = stop.externalId();
		String stopName = stop.name();

		String tripDay = format.asDay(time);
		String tripTime = format.asTime(time);

		Time departure = part.departure();

		CsvBuilder message = new CsvBuilder();
		message.append(event);
		message.append(tripId);
		message.append(personOid);
		message.append(journeyId);
		message.append(stopId);
		message.append(stopName);
		message.append(tripDay);
		message.append(tripTime);
		message.append(departure);

		results().write(categories.passenger, message.toString());
	}

	public void writeRouteLeg(SimulationPerson person, PublicTransportLeg part) {
		int personOid = person.getOid();
		int journeyId = part.journeyId();
		String connections = part
				.connections()
				.stream()
				.map(Connection::id)
				.map(String::valueOf)
				.collect(joining(","));

		Time tripStart = person.currentTrip().startDate();

		String tripDay = format.asDay(tripStart);
		String tripTime = format.asTime(tripStart);

		Time departure = part.departure();

		CsvBuilder message = new CsvBuilder();
		message.append(personOid);
		message.append(journeyId);
		message.append(tripDay);
		message.append(tripTime);
		message.append(departure);
		message.append(connections);

		results().write(categories.routeLeg, message.toString());
	}

	@Override
	public void waitingAt(Stop stop, Time date, int persons) {
		int stopId = stop.externalId();
		String stopName = stop.name();

		String tripDay = format.asDay(date);
		String tripTime = format.asTime(date);

		CsvBuilder message = new CsvBuilder();
		message.append(stopId);
		message.append(stopName);
		message.append(tripDay);
		message.append(tripTime);
		message.append(persons);

		results().write(categories.stop, message.toString());
	}

	@Override
	public void searchNewTrip(SimulationPerson person, Time time, PublicTransportTrip trip) {
		Optional<PublicTransportLeg> part = trip.currentLeg();
		int personId = person.getOid();
		int stopId = part.map(PublicTransportLeg::start).map(Stop::externalId).orElse(-1);
		String stopName = part.map(PublicTransportLeg::start).map(Stop::name).orElse("no part");
		int journeyId = part.map(PublicTransportLeg::journeyId).orElse(-1);

		String tripDay = format.asDay(time);
		String tripTime = format.asTime(time);

		CsvBuilder message = new CsvBuilder();
		message.append(personId);
		message.append(stopId);
		message.append(stopName);
		message.append(tripDay);
		message.append(tripTime);
		message.append(journeyId);

		results().write(categories.searchNewTrip, message.toString());
	}

	@Override
	public void vehicleCrowded(Vehicle vehicle, PublicTransportLeg leg) {
		String departure = format.asTime(leg.departure());
		int stopId = leg.start().externalId();
		String stopName = leg.start().name();
		int journeyId = leg.journeyId();
		int passengerCount = vehicle.passengerCount();
		int capacity = leg.journey().capacity();

		CsvBuilder message = new CsvBuilder();
		message.append(departure);
		message.append(stopId);
		message.append(stopName);
		message.append(journeyId);
		message.append(passengerCount);
		message.append(capacity);

		results().write(categories.vehicleCrowded, message.toString());
	}
}

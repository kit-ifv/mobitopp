package edu.kit.ifv.mobitopp.simulation.person;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class LegBuilder {

	private final List<Connection> connections;
	private Time arrival;
	private Stop start;
	private Stop end;
	private Time departure;
	private Journey journey;

	public LegBuilder() {
		connections = new ArrayList<>();
	}

	public void startWith(Connection connection) {
		start = connection.start();
		departure = connection.departure();
		journey = connection.journey();
		add(connection);
	}

	public void add(Connection connection) {
		end = connection.end();
		arrival = connection.arrival();
		connections.add(connection);
	}

	public void createLeg(Consumer<PublicTransportLeg> consumer) {
		if (notYetStarted()) {
			return;
		}
		PublicTransportLeg newLeg = createLeg();
		consumer.accept(newLeg);
	}

	private PublicTransportLeg createLeg() {
		return new VehicleLeg(start, end, journey, departure, arrival, connections);
	}

	public boolean needsToSplit(Connection connection) {
		return notYetStarted() || usesAnotherJourney(connection);
	}

	private boolean notYetStarted() {
		return journey == null;
	}

	private boolean usesAnotherJourney(Connection connection) {
		return !journey.equals(connection.journey());
	}


}

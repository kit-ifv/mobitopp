package edu.kit.ifv.mobitopp.simulation.publictransport.vehicle;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.fromSomeToAnother;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.RoutePoints;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;

public class DefaultVehicleFactoryTest {

	private Journey journey;
	private DefaultVehicleFactory factory;

	@Before
	public void initialise() {
		journey = journey().build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsOnEmptyJourney() {
		createVehicle();
	}

	private Vehicle createVehicle() {
		factory = new DefaultVehicleFactory();
		return factory.createFrom(journey);
	}

	@Test
	public void vehicleStartsAtDepot() {
		journey.connections().add(someConnection());
		Vehicle vehicle = createVehicle();

		Stop currentStop = vehicle.currentStop();
		Optional<ConnectionId> nextConnection = vehicle.nextConnection();

		assertThat(currentStop, is(equalTo(depot())));
		assertThat(nextConnection, hasValue(depotExit().id()));
	}

	private Connection someConnection() {
		return fromSomeToAnother();
	}

	private Stop depot() {
		return factory.depot();
	}

	private Connection depotExit() {
		return Connection.from(connectionId(), depot(), someConnection().start(),
				someConnection().departure(), someConnection().departure(), journey,
				RoutePoints.from(depot(), someConnection().start()));
	}

	private ConnectionId connectionId() {
		return ConnectionId.of(depot().id());
	}
}

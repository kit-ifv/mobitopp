package edu.kit.ifv.mobitopp.simulation.publictransport;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;

public class VehicleRouteTest {

	private VehicleRoute vehicleRoute;

	@Before
	public void initialise() {
		Journey journey = journey().build();
		VehicleLocation location = new VehicleLocation(Route.from(connections()));
		VehicleTimes connections = TimetableToVehicleConverter.from(connections());
		vehicleRoute = new VehicleRoute(journey, location, connections);
	}

	@After
	public void firstDepartureStaysSame() {
		assertThat(vehicleRoute.firstDeparture(), is(equalTo(firstConnection().departure())));
	}

	@Test
	public void startsAtBeginning() {
		assertThat(vehicleRoute.currentStop(), is(equalTo(firstConnection().start())));
		assertThat(vehicleRoute.nextConnection(), hasValue(firstConnection().id()));
		assertThat(vehicleRoute.nextDeparture(), hasValue(firstConnection().departure()));
		assertThat(vehicleRoute.nextArrival(), hasValue(firstConnection().arrival()));
	}

	@Test
	public void moveToNextStop() {
		vehicleRoute.moveToNextStop(firstConnection().arrival());

		assertThat(vehicleRoute.currentStop(), is(equalTo(firstConnection().end())));
		assertThat(vehicleRoute.nextConnection(), hasValue(secondConnection().id()));
		assertThat(vehicleRoute.nextDeparture(), hasValue(secondConnection().departure()));
		assertThat(vehicleRoute.nextArrival(), hasValue(secondConnection().arrival()));
	}

	@Test
	public void moveToLastStop() {
		vehicleRoute.moveToNextStop(firstConnection().arrival());
		vehicleRoute.moveToNextStop(secondConnection().arrival());

		assertThat(vehicleRoute.currentStop(), is(equalTo(secondConnection().end())));
		assertThat(vehicleRoute.nextConnection(), isEmpty());
		assertThat(vehicleRoute.nextDeparture(), isEmpty());
		assertThat(vehicleRoute.nextArrival(), isEmpty());
	}

	private List<Connection> connections() {
		return asList(firstConnection(), secondConnection());
	}

	private Connection firstConnection() {
		return Data.fromSomeToAnother();
	}

	private Connection secondConnection() {
		return Data.fromAnotherToOther();
	}
}

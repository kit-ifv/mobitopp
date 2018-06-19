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
		VehicleConnections connections = new VehicleConnections(connections());
		vehicleRoute = new VehicleRoute(journey, location, connections);
	}
	
	@After
	public void firstDepartureStaysSame() {
		assertThat(vehicleRoute.firstDeparture(), is(equalTo(someConnection().departure())));
	}

	@Test
	public void startsAtBeginning() {
		assertThat(vehicleRoute.currentStop(), is(equalTo(someConnection().start())));
		assertThat(vehicleRoute.nextConnection(), hasValue(someConnection()));
		assertThat(vehicleRoute.nextDeparture(), hasValue(someConnection().departure()));
		assertThat(vehicleRoute.nextArrival(), hasValue(someConnection().arrival()));
	}

	@Test
	public void moveToNextStop() {
		vehicleRoute.moveToNextStop();
		
		assertThat(vehicleRoute.currentStop(), is(equalTo(someConnection().end())));
		assertThat(vehicleRoute.nextConnection(), hasValue(anotherConnection()));
		assertThat(vehicleRoute.nextDeparture(), hasValue(anotherConnection().departure()));
		assertThat(vehicleRoute.nextArrival(), hasValue(anotherConnection().arrival()));
	}
	
	@Test
	public void moveToLastStop() {
		vehicleRoute.moveToNextStop();
		vehicleRoute.moveToNextStop();
		
		assertThat(vehicleRoute.currentStop(), is(equalTo(anotherConnection().end())));
		assertThat(vehicleRoute.nextConnection(), isEmpty());
		assertThat(vehicleRoute.nextDeparture(), isEmpty());
		assertThat(vehicleRoute.nextArrival(), isEmpty());
	}

	private List<Connection> connections() {
		return asList(someConnection(), anotherConnection());
	}

	private Connection anotherConnection() {
		return Data.fromAnotherToOther();
	}

	private Connection someConnection() {
		return Data.fromSomeToAnother();
	}
}

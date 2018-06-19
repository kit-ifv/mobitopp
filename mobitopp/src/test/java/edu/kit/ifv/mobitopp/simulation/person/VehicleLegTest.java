package edu.kit.ifv.mobitopp.simulation.person;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class VehicleLegTest {

	@Test
	public void findFirstConnection() {
		PublicTransportLeg leg = newLeg();

		ConnectionId firstConnection = leg.firstConnection();

		assertThat(firstConnection, is(equalTo(someConnection().id())));
	}

	@Test(expected = IllegalArgumentException.class)
	public void legWithoutConnectionIsNotAllowed() {
		Stop start = someConnection().start();
		Stop end = someConnection().end();
		Journey journey = someConnection().journey();
		Time departure = someConnection().departure();
		Time arrival = someConnection().arrival();
		new VehicleLeg(start, end, journey, departure, arrival, emptyList());
	}

	private PublicTransportLeg newLeg() {
		return Data.newLeg(someConnection());
	}

	private Connection someConnection() {
		return Data.fromSomeToAnother();
	}
}

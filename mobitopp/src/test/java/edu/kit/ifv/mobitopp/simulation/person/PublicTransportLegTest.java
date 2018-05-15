package edu.kit.ifv.mobitopp.simulation.person;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class PublicTransportLegTest {
	
	@Test
	public void findFirstConnection() {
		PublicTransportLeg leg = newLeg();

		Connection firstConnection = leg.firstConnection();

		assertThat(firstConnection, is(equalTo(someConnection())));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void legWithoutConnectionIsNotAllowed() {
		Connection connection = Data.fromSomeToAnother();
		Stop start = connection.start();
		Stop end = connection.end();
		Journey journey = connection.journey();
		Time departure = connection.departure();
		Time arrival = connection.arrival();
		new PublicTransportLeg(start, end, journey, departure, arrival, emptyList());
	}

	private PublicTransportLeg newLeg() {
		return Data.newLeg(someConnection());
	}

	private Connection someConnection() {
		return Data.fromSomeToAnother();
	}
}

package edu.kit.ifv.mobitopp.simulation.publictransport.vehicle;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import nl.jqno.equalsverifier.EqualsVerifier;

public class WaitTimeTest {

	@Test
	public void nextDeparture() {
		RelativeTime waitTime = RelativeTime.ofMinutes(5);
		WaitTime waiting = new WaitTime(waitTime);

		Time currentTime = Data.someTime();
		Time nextDeparture = waiting.nextDeparture(currentTime);

		assertThat(nextDeparture, is(equalTo(currentTime.plus(waitTime))));
	}
	
	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(WaitTime.class).usingGetClass().verify();
	}
}

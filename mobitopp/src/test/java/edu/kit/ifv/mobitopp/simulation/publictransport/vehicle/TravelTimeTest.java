package edu.kit.ifv.mobitopp.simulation.publictransport.vehicle;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import nl.jqno.equalsverifier.EqualsVerifier;

public class TravelTimeTest {

	@Test
	public void nextArrival() {
		ConnectionId id = ConnectionId.of(0);
		RelativeTime travelTime = RelativeTime.ofMinutes(4);
		TravelTime driving = new TravelTime(id, travelTime);

		Optional<Time> nextDeparture = Optional.of(someTime());
		Optional<Time> nextArrival = driving.nextArrival(nextDeparture);

		assertThat(nextArrival, hasValue(someTime().plus(travelTime)));
	}
	
	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(TravelTime.class).usingGetClass().verify();
	}
}

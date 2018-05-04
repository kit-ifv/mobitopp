package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.ArrivalTimeFunction;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Profile;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class EarliestTest {

	private static final Time departure = SimpleTime.ofSeconds(0);
	private static final Time someArrival = departure.plusMinutes(1);
	
	private Profile profile;
	private Stop someStop;
	private ArrivalTimeSupplier earliest;

	@Before
	public void initialise() {
		profile = mock(Profile.class);
		someStop = mock(Stop.class);

		arrival(someStop, departure, someArrival);

		earliest = new Earliest(profile, someStop);
	}

	@Test
	public void arrival() {
		Time earliestArrival = someArrival;
		assertThat(earliest.startingAt(departure), is(equalTo(earliestArrival)));
	}

	@Test
	public void arrivalStartingTooLateDeparte() {
		Time tooLateDeparture = departure.plusMinutes(1);

		assertThat(earliest.startingAt(tooLateDeparture), is(equalTo(Time.future)));
	}

	private void arrival(Stop stop, Time departure, Time arrival) {
		ArrivalTimeFunction function = mock(ArrivalTimeFunction.class);
		when(function.arrivalFor(departure)).thenReturn(Optional.of(arrival));
		when(profile.from(stop)).thenReturn(function);
	}
}

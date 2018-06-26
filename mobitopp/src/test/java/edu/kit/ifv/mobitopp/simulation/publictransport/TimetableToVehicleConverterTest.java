package edu.kit.ifv.mobitopp.simulation.publictransport;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class TimetableToVehicleConverterTest {

	@Test
	public void convertsDrivingBetweenStops() {
		VehicleTimesConverter converter = new TimetableToVehicleConverter();

		VehicleTimes times = converter.convert(singleConnection());

		assertThat(times.firstDeparture(), is(equalTo(firstConnection().departure())));
		assertThat(times.nextDeparture(), hasValue(firstConnection().departure()));
		assertThat(times.nextArrival(), hasValue(firstConnection().arrival()));
		assertThat(times.nextConnection(), hasValue(firstConnection().id()));
	}

	private Collection<Connection> singleConnection() {
		return asList(firstConnection());
	}

	private Connection firstConnection() {
		return Data.fromSomeToAnother();
	}
	
	@Test
	public void convertsSeveralConnections() {
		RelativeTime delay = RelativeTime.ofMinutes(5);
		VehicleTimesConverter converter = new TimetableToVehicleConverter();
		
		VehicleTimes times = converter.convert(severalConnections());
		Time current = secondConnection().departure().plus(delay);
		times.move(current);
		
		assertThat(times.firstDeparture(), is(equalTo(firstConnection().departure())));
		assertThat(times.nextDeparture(), hasValue(secondConnection().departure().plus(delay)));
		assertThat(times.nextArrival(), hasValue(secondConnection().arrival().plus(delay)));
		assertThat(times.nextConnection(), hasValue(secondConnection().id()));
	}

	private Collection<Connection> severalConnections() {
		return asList(firstConnection(), secondConnection());
	}

	private Connection secondConnection() {
		return Data.fromAnotherToOther();
	}
}

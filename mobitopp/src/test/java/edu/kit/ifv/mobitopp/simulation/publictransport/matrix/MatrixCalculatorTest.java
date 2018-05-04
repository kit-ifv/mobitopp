package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.ArrivalTimeFunction;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Profile;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.ProfileBuilder;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class MatrixCalculatorTest {

	private static final int firstHour = 0;
	private static final Time day = SimpleTime.ofSeconds(0);
	private static final int someOid = 1;
	private static final int anotherOid = 2;

	private ProfileBuilder builder;
	private Function<Zone, Stop> stop;
	private Zone someZone;
	private Stop someStop;
	private Profile someProfile;
	private Zone anotherZone;
	private Stop anotherStop;
	private Profile anotherProfile;

	@SuppressWarnings("unchecked")
	@Before
	public void initialise() {
		someZone = mock(Zone.class);
		anotherZone = mock(Zone.class);
		builder = mock(ProfileBuilder.class);
		someStop = mock(Stop.class);
		anotherStop = mock(Stop.class);
		stop = mock(Function.class);

		when(someZone.getOid()).thenReturn(someOid);
		when(anotherZone.getOid()).thenReturn(anotherOid);
		when(stop.apply(someZone)).thenReturn(someStop);
		when(stop.apply(anotherZone)).thenReturn(anotherStop);

		someProfile = mock(Profile.class);
		anotherProfile = mock(Profile.class);
		when(builder.buildUpTo(someStop)).thenReturn(someProfile);
		when(builder.buildUpTo(anotherStop)).thenReturn(anotherProfile);
	}

	@Test
	public void travelTimeBetweenZones() {
		RelativeTime someToSome = RelativeTime.of(1, MINUTES);
		RelativeTime anotherToSome = RelativeTime.of(2, MINUTES);
		RelativeTime someToAnother = RelativeTime.of(3, MINUTES);
		RelativeTime anotherToAnother = RelativeTime.of(4, MINUTES);
		arrivalAtSome(someStop, someToSome);
		arrivalAtSome(anotherStop, anotherToSome);
		arrivalAtAnother(someStop, someToAnother);
		arrivalAtAnother(anotherStop, anotherToAnother);

		MatrixCalculator calculator = newCalculator(zones());

		TravelTimes matrices = calculator.createMatrices(1);

		Matrix matrix = matrices.matrixIn(firstHour);
		assertThat(matrix.get(someZone, someZone), is(equalTo(someToSome.toDuration())));
		assertThat(matrix.get(someZone, anotherZone), is(equalTo(someToAnother.toDuration())));
		assertThat(matrix.get(anotherZone, someZone), is(equalTo(anotherToSome.toDuration())));
		assertThat(matrix.get(anotherZone, anotherZone), is(equalTo(anotherToAnother.toDuration())));
	}

	private List<Zone> zones() {
		return asList(someZone, anotherZone);
	}

	private void arrivalAtSome(Stop from, RelativeTime duration) {
		arrival(someProfile, from, duration);
	}

	private void arrivalAtAnother(Stop from, RelativeTime duration) {
		arrival(anotherProfile, from, duration);
	}

	private void arrival(Profile to, Stop from, RelativeTime duration) {
		ArrivalTimeFunction function = mock(ArrivalTimeFunction.class);
		when(function.arrivalFor(day)).thenReturn(Optional.of(day.plus(duration)));
		when(to.from(from)).thenReturn(function);
	}

	private MatrixCalculator newCalculator(Collection<Zone> zones) {
		return new MatrixCalculator(zones, builder, stop, day);
	}
}

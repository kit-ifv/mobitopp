package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import static edu.kit.ifv.mobitopp.simulation.publictransport.matrix.Matrix.infinite;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.FloatMatrix;
import edu.kit.ifv.mobitopp.data.Zone;

public class MatrixTest {

	private static final int someZoneId = 1;
	private static final int anotherZoneId = 2;

	private Zone someZone;
	private Zone anotherZone;
	private Matrix matrix;

	@Before
	public void initialise() throws Exception {
		someZone = mock(Zone.class);
		when(someZone.getOid()).thenReturn(someZoneId);
		anotherZone = mock(Zone.class);
		when(anotherZone.getOid()).thenReturn(anotherZoneId);
		matrix = new Matrix(oids());
	}

	@Test
	public void hasInfiniteTravelTimeWhenEmpty() throws Exception {
		assertThat(matrix.get(someZone, anotherZone), is(infinite));
	}

	@Test
	public void savesTravelTimeForGivenOriginDestinationPair() throws Exception {
		Duration travelTime = Duration.of(1, MINUTES);

		matrix.set(someZone, anotherZone, travelTime);

		assertThat(matrix.get(someZone, anotherZone), is(travelTime));
	}

	@Test
	public void hasInfiniteTravelTimeForUnknownOriginDestinationPair() throws Exception {
		matrix.set(anotherZone, someZone, Duration.of(2, MINUTES));

		assertThat(matrix.get(someZone, anotherZone), is(infinite));
	}

	@Test
	public void toFloatMatrix() {
		Duration someToAnother = Duration.of(1, MINUTES);
		Duration anotherToSome = Duration.of(2, MINUTES);
		matrix.set(someZone, anotherZone, someToAnother);
		matrix.set(anotherZone, someZone, anotherToSome);

		FloatMatrix floatMatrix = matrix.toFloatMatrix();

		assertThat(floatMatrix.get(someZoneId, anotherZoneId), is(equalTo(inMinutes(someToAnother))));
		assertThat(floatMatrix.get(anotherZoneId, someZoneId), is(equalTo(inMinutes(anotherToSome))));
	}

	private float inMinutes(Duration someToAnother) {
		return someToAnother.toMinutes();
	}

	private List<Integer> oids() {
		return asList(someZoneId, anotherZoneId);
	}
}

package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.coordinate;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.serializer.NeighbourhoodCoupler;
import edu.kit.ifv.mobitopp.publictransport.serializer.StopSerializer;
import edu.kit.ifv.mobitopp.util.ReflectionHelper;
import nl.jqno.equalsverifier.EqualsVerifier;

public class StopPointsTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private StopPoints stopPoints;
	private NeighbourhoodCoupler walkLink;

	@BeforeClass
	@AfterClass
	public static void clearTransportSystems()
			throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		ReflectionHelper.clearTransportSystemSetCache();
	}

	@Before
	public void initialise() throws Exception {
		walkLink = mock(NeighbourhoodCoupler.class);
		stopPoints = new StopPoints();
	}

	@Test
	public void whenStopDoesNotExist() throws Exception {
		int id = 1;

		thrown.expect(RuntimeException.class);
		thrown.expectMessage(endsWith(String.valueOf(id)));
		
		stopPoints.get(id);
	}
	
	@Test
	public void returnsStopForVisumStop() throws Exception {
		Stop savedStop = someStop();
		int id = savedStop.externalId();
		stopPoints.add(savedStop);
		
		Stop returned = stopPoints.get(id);
		
		assertThat(returned, is(someStop()));
	}

	@Test
	public void addsSingleNeighbourReachableViaWalkLinkToStop() throws Exception {
		Stop someStop = someStop();
		Stop anotherStop = anotherStop();
		stopPoints.add(someStop);
		stopPoints.add(anotherStop);

		stopPoints.initialiseNeighbourhood(walkLink);

		verify(walkLink).addNeighboursshipBetween(someStop, someStop);
		verify(walkLink).addNeighboursshipBetween(anotherStop, someStop);
		verify(walkLink).addNeighboursshipBetween(someStop, anotherStop);
		verify(walkLink).addNeighboursshipBetween(anotherStop, anotherStop);
	}

	@Test
	public void serializesAllStops() throws Exception {
		StopSerializer serializer = mock(StopSerializer.class);
		stopPoints.add(someStop());

		stopPoints.serializeTo(serializer);

		verify(serializer).serialize(someStop());
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		EqualsVerifier
				.forClass(StopPoints.class)
				.withPrefabValues(Point2D.class, coordinate(0, 0), coordinate(1, 1))
				.withPrefabValues(Stop.class, someStop(), anotherStop())
				.withIgnoredFields("internalPoints")
				.usingGetClass()
				.verify();
	}

}

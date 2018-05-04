package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public class MatrixGeneratorTest {

	private Stop someStop;
	private Stop anotherStop;
	private Zone someZone;
	private ZonesToStops zonesToStops;
	private File someFile;
	private File anotherFile;

	@Before
	public void initialise() throws Exception {
		someStop = mock(Stop.class);
		anotherStop = mock(Stop.class);
		someZone = mock(Zone.class);
		zonesToStops = mock(ZonesToStops.class);
	}

	@Test
	public void assignStopsToZones() throws Exception {
		MatrixGenerator generator = generator();

		generator.assignStopsToZones();

		verify(zonesToStops).assign(someStop);
		verify(zonesToStops).assign(anotherStop);
	}

	private MatrixGenerator generator() {
		return new MatrixGenerator(someFile, anotherFile) {

			@Override
			ZonesToStops zonesToStops() {
				return zonesToStops;
			}

			@Override
			List<Zone> zones() {
				return asList(someZone);
			}

			@Override
			Collection<Stop> stops() {
				return asList(someStop, anotherStop);
			}
		};
	}

}

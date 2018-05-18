package edu.kit.ifv.mobitopp.data.local.configuration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.DayType;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

public class StoredMatricesTest {

	private final String pathToMatrix = "path-to-matrix";
	private final TimeSpan timeSpan = new TimeSpan(0);
	
	private StoredMatrices matrices;
	private Consumer<StoredMatrix> consumer;

	@SuppressWarnings("unchecked")
	@Before
	public void initialise() throws IOException {
		consumer = mock(Consumer.class);
		matrices = new StoredMatrices();
	}


	@Test(expected = IllegalArgumentException.class)
	public void failsOnMissingTravelTimeType() {
		matrices.travelTimeMatrixFor(TravelTimeMatrixType.car);
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsOnMissingCostType() {
		matrices.costMatrixFor(CostMatrixType.car);
	}

	@Test
	public void processCostMatrices() {
		matrices.add(CostMatrixType.car, DayType.weekdays, timeSpan, pathToMatrix);
		
		matrices.forEach(consumer);
		
		verify(consumer).accept(new StoredMatrix(timeSpan, pathToMatrix));
	}
	
	@Test
	public void processTravelTimeMatrices() {
		matrices.add(TravelTimeMatrixType.car, DayType.weekdays, timeSpan, pathToMatrix);
		
		matrices.forEach(consumer);
		
		verify(consumer).accept(new StoredMatrix(timeSpan, pathToMatrix));
	}
	
	@Test
	public void processFixedDistributionMatrices() {
		matrices.add(ActivityType.WORK, pathToMatrix);
		
		matrices.forEach(consumer);
		
		verify(consumer).accept(new StoredMatrix(pathToMatrix));
	}
}

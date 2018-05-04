package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.BiConsumer;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import nl.jqno.equalsverifier.EqualsVerifier;

public class StopJourneyTest {

	private static final Journey someJourney = journey().build();
	private BiConsumer<Stop, Vehicle> consumer;
	private Vehicles vehicles;
	private Vehicle vehicle;

	@SuppressWarnings("unchecked")
	@Before
	public void initialise() throws Exception {
		consumer = mock(BiConsumer.class);
		vehicles = mock(Vehicles.class);
		vehicle = mock(Vehicle.class);
		when(vehicles.vehicleServing(someJourney)).thenReturn(vehicle);
	}

	@Test
	public void consumesStopAndJourney() throws Exception {
		StopJourney stopJourney = new StopJourney(someStop(), someJourney);

		stopJourney.apply(consumer, vehicles);

		verify(consumer).accept(someStop(), vehicle);
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		EqualsVerifier
				.forClass(StopJourney.class)
				.withPrefabValues(Stop.class, someStop(), anotherStop())
				.usingGetClass()
				.verify();
	}
}

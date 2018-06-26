package edu.kit.ifv.mobitopp.simulation.publictransport.vehicle;

import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.FootJourney;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.ModifiableJourneys;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicles;

public class VehiclesConverterTest {

	private ModifiableJourneys journeys;
	private ModifiableJourney journey;
	private Vehicle vehicle;

	@Before
	public void initialise() {
		journey = journey().withId(1).at(firstConnection().departure()).build();
		journey.add(firstConnection());
		journey.add(secondConnection());
		journeys = new ModifiableJourneys();
		journeys.add(journey);
		vehicle = mock(Vehicle.class);
		when(vehicle.firstDeparture()).thenReturn(Data.someTime());
	}

	@Test
	public void convertsJourney() {
		VehicleFactory factory = mock(VehicleFactory.class);
		when(factory.createFrom(journey)).thenReturn(vehicle);
		VehiclesConverter converter = new VehiclesConverter(factory);

		Vehicles converted = converter.convert(journeys);

		assertTrue(converted.hasNextUntil(firstConnection().departure()));
		Vehicle firstVehicle = converted.next();
		assertThat(firstVehicle, is(equalTo(vehicle)));
	}

	@Test
	public void convertsAddsFootJourney() {
		VehiclesConverter converter = new VehiclesConverter();

		Vehicles converted = converter.convert(journeys);
		Vehicle footVehicle = converted.vehicleServing(FootJourney.footJourney);

		assertThat(footVehicle, is(instanceOf(FootVehicle.class)));
	}

	private Connection firstConnection() {
		return Data.fromSomeToAnother();
	}

	private Connection secondConnection() {
		return Data.fromSomeToAnother();
	}
}

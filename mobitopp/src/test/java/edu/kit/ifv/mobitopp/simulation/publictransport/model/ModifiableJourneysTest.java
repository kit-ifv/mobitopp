package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.NoJourney.noJourney;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourney;
import nl.jqno.equalsverifier.EqualsVerifier;

public class ModifiableJourneysTest {

	private static final Time day = Data.someTime();
	private static final int manyPassengers = 10;
	private static final int journeyId = 1;

	private ModifiableJourneys journeys;
	private ModifiableJourney journey;

	@Before
	public void initialise() throws Exception {
		journeys = new ModifiableJourneys();
		journey = mock(ModifiableJourney.class);
		when(journey.id()).thenReturn(journeyId);
	}

	@Test
	public void usesTimeProfileToCreateJourney() throws Exception {
		JourneyTemplates timeProfiles = mock(JourneyTemplates.class);
		JourneyTemplate timeProfile = mock(JourneyTemplate.class);
		VisumPtVehicleJourney visum = mock(VisumPtVehicleJourney.class);
		PublicTransportFactory factory = mock(PublicTransportFactory.class);

		when(timeProfiles.from(visum)).thenReturn(timeProfile);
		when(timeProfile.createJourney(visum, factory, day)).thenReturn(journey);

		journeys.add(visum, timeProfiles, factory, day);

		assertThat(journeys.get(journeyId), is(equalTo(journey)));

		verify(timeProfile).createJourney(visum, factory, day);
	}

	@Test
	public void useDefaultJourneyWhenJourneyIsMissing() throws Exception {
		Journey journey = journeys.get(journeyId);

		assertThat(journey, is(equalTo(noJourney)));
	}

	@Test
	public void collectsAllCollections() throws Exception {
		Connections connections = new Connections();
		when(journey.id()).thenReturn(journeyId);
		when(journey.connections()).thenReturn(connections);

		journeys.add(journey);
		journeys.connections();

		verify(journey).connections();
	}

	@Test
	public void scalesVehiclesAccordingToMaximumCapacity() throws Exception {
		when(journey.id()).thenReturn(1);
		when(journey.capacity()).thenReturn(manyPassengers);

		journeys.add(journey);

		Function<Integer, Float> scaler = journeys.vehicleScaler();
		Float fullScale = scaler.apply(10);
		Float halfScale = scaler.apply(5);

		assertThat(fullScale, is(1.0f));
		assertThat(halfScale, is(0.5f));
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		EqualsVerifier.forClass(ModifiableJourneys.class).usingGetClass().verify();
	}

}

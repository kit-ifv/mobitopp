package edu.kit.ifv.mobitopp.simulation.bikesharing;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.simulation.ActivityType;

@ExtendWith(MockitoExtension.class)
public class BikeSharingTripTest {

	private TripSetup tripSetup;
	@Mock
	private BikeSharingDataForZone originData;
	@Mock
	private BikeSharingDataForZone destinationData;

	@BeforeEach
	void before() {
		tripSetup = TripSetup.create();
		tripSetup.origin.setBikeSharing(originData);
		tripSetup.destination.setBikeSharing(destinationData);
	}

	@Test
	void allocateBike() throws Exception {
		when(originData.isBikeAvailableFor(tripSetup.person)).thenReturn(true);
		BikeSharingTrip trip = new BikeSharingTrip(tripSetup.tripData, tripSetup.person);

		trip.prepareTrip(tripSetup.impedance, tripSetup.currentTime);

		verify(originData).isBikeAvailableFor(tripSetup.person);
		Bike bike = verify(originData).bookBike(tripSetup.person);
		verify(tripSetup.person).useBike(bike, tripSetup.currentTime);
	}

	@Test
	void takesParkedBike() throws Exception {
		when(tripSetup.person.hasParkedBike()).thenReturn(true);
		BikeSharingTrip trip = new BikeSharingTrip(tripSetup.tripData, tripSetup.person);

		trip.prepareTrip(tripSetup.impedance, tripSetup.currentTime);

		verify(tripSetup.person).takeBikeFromParking();
		verifyZeroInteractions(originData);
	}

	@Test
	void failsForMissingBike() throws Exception {
		BikeSharingTrip trip = new BikeSharingTrip(tripSetup.tripData, tripSetup.person);

		assertThrows(IllegalStateException.class,
				() -> trip.prepareTrip(tripSetup.impedance, tripSetup.currentTime));
	}

	@Test
	void returnBikeToMobilityProvier() throws Exception {
		Bike bike = mock(Bike.class);
		when(tripSetup.person.whichBike()).thenReturn(bike);
		when(destinationData.isBikeSharingAreaFor(bike)).thenReturn(true);
		when(tripSetup.person.releaseBike(tripSetup.currentTime)).thenReturn(bike);
		BikeSharingTrip trip = new BikeSharingTrip(tripSetup.tripData, tripSetup.person);

		trip.finish(tripSetup.currentTime, tripSetup.results);

		verify(destinationData).isBikeSharingAreaFor(bike);
		verify(tripSetup.person).releaseBike(tripSetup.currentTime);
		verify(bike).returnBike(tripSetup.destination.getId());
	}

	@Test
	void parkBike() throws Exception {
		tripSetup.configureNextActivity(ActivityType.WORK);
		Bike bike = mock(Bike.class);
		when(tripSetup.person.whichBike()).thenReturn(bike);
		when(destinationData.isBikeSharingAreaFor(bike)).thenReturn(false);
		when(tripSetup.person.parkBike(tripSetup.destination, tripSetup.end, tripSetup.currentTime))
				.thenReturn(bike);
		BikeSharingTrip trip = new BikeSharingTrip(tripSetup.tripData, tripSetup.person);

		trip.finish(tripSetup.currentTime, tripSetup.results);

		verify(destinationData).isBikeSharingAreaFor(bike);
		verify(tripSetup.person).parkBike(tripSetup.destination, tripSetup.end, tripSetup.currentTime);
	}
}

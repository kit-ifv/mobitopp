package edu.kit.ifv.mobitopp.simulation.bikesharing;

import static org.junit.jupiter.api.Assertions.*;
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
	private BikeSharingDataForZone bikeSharingData;

	@BeforeEach
	void before() {
		tripSetup = TripSetup.create();
		tripSetup.zone.setBikeSharing(bikeSharingData);
	}

	@Test
	void allocateBike() throws Exception {
		tripSetup.configureCurrentActivity(ActivityType.HOME);
		when(bikeSharingData.isBikeAvailableFor(tripSetup.person)).thenReturn(true);
		BikeSharingTrip trip = new BikeSharingTrip(tripSetup.tripData, tripSetup.person);

		trip.prepareTrip(tripSetup.impedance, tripSetup.currentTime);

		verify(bikeSharingData).isBikeAvailableFor(tripSetup.person);
		Bike bike = verify(bikeSharingData).bookBike(tripSetup.person);
		verify(tripSetup.person).useBike(bike, tripSetup.currentTime);
	}

	@Test
	void takesParkedBike() throws Exception {
		when(tripSetup.person.hasParkedBike()).thenReturn(true);
		BikeSharingTrip trip = new BikeSharingTrip(tripSetup.tripData, tripSetup.person);

		trip.prepareTrip(tripSetup.impedance, tripSetup.currentTime);

		verify(tripSetup.person).takeBikeFromParking();
		verifyZeroInteractions(bikeSharingData);
	}

	@Test
	void failsForMissingBike() throws Exception {
		tripSetup.configureCurrentActivity(ActivityType.HOME);
		BikeSharingTrip trip = new BikeSharingTrip(tripSetup.tripData, tripSetup.person);

		assertThrows(IllegalStateException.class,
				() -> trip.prepareTrip(tripSetup.impedance, tripSetup.currentTime));
	}

	@Test
	void returnBikeToMobilityProvier() throws Exception {
		tripSetup.configureNextActivity(ActivityType.HOME);
		Bike bike = mock(Bike.class);
		when(tripSetup.person.whichBike()).thenReturn(bike);
		when(bikeSharingData.isBikeSharingAreaFor(bike)).thenReturn(true);
		when(tripSetup.person.releaseBike(tripSetup.currentTime)).thenReturn(bike);
		BikeSharingTrip trip = new BikeSharingTrip(tripSetup.tripData, tripSetup.person);

		trip.finish(tripSetup.currentTime, tripSetup.results);

		verify(bikeSharingData).isBikeSharingAreaFor(bike);
		verify(tripSetup.person).releaseBike(tripSetup.currentTime);
		verify(bike).returnBike(tripSetup.zone.getId());
	}

	@Test
	void parkBike() throws Exception {
		tripSetup.configureNextActivity(ActivityType.WORK);
		Bike bike = mock(Bike.class);
		when(tripSetup.person.whichBike()).thenReturn(bike);
		when(bikeSharingData.isBikeSharingAreaFor(bike)).thenReturn(false);
		when(tripSetup.person.parkBike(tripSetup.zone, tripSetup.location, tripSetup.currentTime))
				.thenReturn(bike);
		BikeSharingTrip trip = new BikeSharingTrip(tripSetup.tripData, tripSetup.person);

		trip.finish(tripSetup.currentTime, tripSetup.results);

		verify(bikeSharingData).isBikeSharingAreaFor(bike);
		verify(tripSetup.person).parkBike(tripSetup.zone, tripSetup.location, tripSetup.currentTime);
	}
}

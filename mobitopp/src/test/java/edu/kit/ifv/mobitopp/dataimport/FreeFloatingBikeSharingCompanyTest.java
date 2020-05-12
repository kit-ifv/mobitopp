package edu.kit.ifv.mobitopp.dataimport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.bikesharing.Bike;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingCompany;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;

@ExtendWith(MockitoExtension.class)
public class FreeFloatingBikeSharingCompanyTest {

	private static final String companyName = "BikeRent";
	@Mock(lenient = true)
	private SimulationPerson person;
	@Mock(lenient = true)
	private BikeSharingBike bike;
	@Mock(lenient = true)
	private BikeSharingBike mule;
	@Mock(lenient = true)
	private Zone firstLocation;
	@Mock(lenient = true)
	private Zone secondLocation;
	@Mock(lenient = true)
	private ActivityIfc firstActivity;
	@Mock(lenient = true)
	private ActivityIfc secondActivity;
	private BikeSharingCompany muleCompany;
	private ZoneId bikeZone;
	private ZoneId firstZoneId;
	private ZoneId secondZoneId;

	@BeforeEach
	void before() {
		// muleCompany = new FreeFloatingBikeSharingCompany(companyName);
		// mule = new BikeSharingBike(0, bikeZone, muleCompany);
		bikeZone = new ZoneId("1", 0);
		firstZoneId = new ZoneId("2", 1);
		when(firstLocation.getId()).thenReturn(firstZoneId);
		when(firstActivity.zone()).thenReturn(firstLocation);
		when(secondLocation.getId()).thenReturn(secondZoneId);
		when(secondActivity.zone()).thenReturn(secondLocation);
		when(person.currentActivity()).thenReturn(firstActivity);
	}

	@Test
	void noBikeIsAvailableForCustomer() throws Exception {
		when(bike.startZone()).thenReturn(bikeZone);
		when(person.isMobilityProviderCustomer(companyName)).thenReturn(true);
		FreeFloatingBikeSharingCompany company = new FreeFloatingBikeSharingCompany(companyName);
		company.own(List.of(bike));
		boolean isBikeAvailable = company.isBikeAvailableFor(person);

		assertThat(isBikeAvailable).isFalse();
		verify(person).isMobilityProviderCustomer(companyName);
	}

	@Test
	void bikeIsAvailableForCustomer() throws Exception {
		when(bike.startZone()).thenReturn(firstZoneId);
		when(person.isMobilityProviderCustomer(companyName)).thenReturn(true);
		FreeFloatingBikeSharingCompany company = new FreeFloatingBikeSharingCompany(companyName);
		company.own(List.of(bike));
		boolean isBikeAvailable = company.isBikeAvailableFor(person);

		assertThat(isBikeAvailable).isTrue();
		verify(person).isMobilityProviderCustomer(companyName);
	}

	@Test
	void bookBike() throws Exception {
		when(bike.startZone()).thenReturn(firstZoneId);
		when(person.isMobilityProviderCustomer(companyName)).thenReturn(true);
		FreeFloatingBikeSharingCompany company = new FreeFloatingBikeSharingCompany(companyName);
		company.own(List.of(bike));
		Bike bookedBike = company.bookBikeFor(person);

		assertAll(() -> assertThat(bookedBike).isEqualTo(bike),
				() -> assertThat(company.isBikeAvailableFor(person)).isFalse());
	}

	@Test
	void returnsBike() throws Exception {
		when(bike.startZone()).thenReturn(firstZoneId);
		when(person.isMobilityProviderCustomer(companyName)).thenReturn(true);
		when(person.currentActivity()).thenReturn(firstActivity);
		FreeFloatingBikeSharingCompany company = new FreeFloatingBikeSharingCompany(companyName);
		when(bike.owner()).thenReturn(company);
		company.own(List.of(bike));
		company.bookBikeFor(person);

		company.returnBike(bike, secondZoneId);

		when(person.currentActivity()).thenReturn(secondActivity);
		assertThat(company.isBikeAvailableFor(person)).isTrue();
	}

	@Test
	void ensureBikeOwnerMatches() throws Exception {
		when(bike.startZone()).thenReturn(firstZoneId);
		when(person.isMobilityProviderCustomer(companyName)).thenReturn(true);
		FreeFloatingBikeSharingCompany company = new FreeFloatingBikeSharingCompany(companyName);
		when(mule.owner()).thenReturn(muleCompany);

		assertThrows(IllegalArgumentException.class, () -> company.returnBike(mule, bikeZone));
	}
}

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

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.bikesharing.Bike;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingCompany;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;

@ExtendWith(MockitoExtension.class)
public class FreeFloatingBikeSharingCompanyTest {

	private static final String companyName = "BikeRent";
	@Mock
	private SimulationPerson person;
	@Mock
	private BikeSharingBike bike;
	@Mock
	private BikeSharingBike mule;
	@Mock
	private BikeSharingCompany muleCompany;
	private ZoneId bikeZone;
	private ZoneId firstZoneId;
	private ZoneId secondZoneId;

	@BeforeEach
	void before() {
		bikeZone = new ZoneId("1", 0);
		firstZoneId = new ZoneId("2", 1);
	}

	@Test
	void noBikeIsAvailableForCustomer() throws Exception {
		when(bike.startZone()).thenReturn(bikeZone);
		when(person.isMobilityProviderCustomer(companyName)).thenReturn(true);
		FreeFloatingBikeSharingCompany company = new FreeFloatingBikeSharingCompany(companyName);
		company.own(List.of(bike));
		boolean isBikeAvailable = company.isBikeAvailableFor(person, firstZoneId);

		assertThat(isBikeAvailable).isFalse();
		verify(person).isMobilityProviderCustomer(companyName);
	}

	@Test
	void bikeIsAvailableForCustomer() throws Exception {
		when(bike.startZone()).thenReturn(firstZoneId);
		when(person.isMobilityProviderCustomer(companyName)).thenReturn(true);
		FreeFloatingBikeSharingCompany company = new FreeFloatingBikeSharingCompany(companyName);
		company.own(List.of(bike));
		boolean isBikeAvailable = company.isBikeAvailableFor(person, firstZoneId);

		assertThat(isBikeAvailable).isTrue();
		verify(person).isMobilityProviderCustomer(companyName);
	}

	@Test
	void bookBike() throws Exception {
		when(bike.startZone()).thenReturn(firstZoneId);
		when(person.isMobilityProviderCustomer(companyName)).thenReturn(true);
		FreeFloatingBikeSharingCompany company = new FreeFloatingBikeSharingCompany(companyName);
		company.own(List.of(bike));
		Bike bookedBike = company.bookBikeFor(person, firstZoneId);

		assertAll(() -> assertThat(bookedBike).isEqualTo(bike),
				() -> assertThat(company.isBikeAvailableFor(person, firstZoneId)).isFalse());
	}

	@Test
	void returnsBike() throws Exception {
		when(bike.startZone()).thenReturn(firstZoneId);
		when(person.isMobilityProviderCustomer(companyName)).thenReturn(true);
		FreeFloatingBikeSharingCompany company = new FreeFloatingBikeSharingCompany(companyName);
		when(bike.owner()).thenReturn(company);
		company.own(List.of(bike));
		company.bookBikeFor(person, firstZoneId);

		company.returnBike(bike, secondZoneId);

		assertThat(company.isBikeAvailableFor(person, secondZoneId)).isTrue();
	}

	@Test
	void ensureBikeOwnerMatches() throws Exception {
		FreeFloatingBikeSharingCompany company = new FreeFloatingBikeSharingCompany(companyName);
		when(mule.owner()).thenReturn(muleCompany);

		assertThrows(IllegalArgumentException.class, () -> company.returnBike(mule, bikeZone));
	}
}

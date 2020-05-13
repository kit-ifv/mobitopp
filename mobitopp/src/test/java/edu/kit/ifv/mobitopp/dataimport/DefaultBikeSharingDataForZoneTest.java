package edu.kit.ifv.mobitopp.dataimport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.bikesharing.Bike;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingCompany;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;

@ExtendWith(MockitoExtension.class)
public class DefaultBikeSharingDataForZoneTest {

	private static final String bikeCompany = "BikeRent";
	private static final String muleCompany = "MuleRent";
	@Mock
	private Bike bike;
	@Mock
	private Bike mule;
	@Mock
	private BikeSharingCompany bikeSharingCompany;
	@Mock
	private BikeSharingCompany muleSharingCompany;
	@Mock(lenient = true)
	private SimulationPerson person;
	private ZoneId zoneId = new ZoneId("1", 0);

	@Test
	void isBikeSharingArea() throws Exception {
		BikeSharingDataForZone data = buildAvailableBikeService();

		assertThat(data.isBikeSharingAreaFor(bike)).isTrue();
		verify(bike).owner();
	}

	@Test
	void isNoBikeSharingArea() throws Exception {
		when(bikeSharingCompany.name()).thenReturn(bikeCompany);
		when(muleSharingCompany.name()).thenReturn(muleCompany);
		when(bike.owner()).thenReturn(bikeSharingCompany);
		when(mule.owner()).thenReturn(muleSharingCompany);
		Map<String, Boolean> serviceArea = Map.of(bikeCompany, false);
		DefaultBikeSharingDataForZone data = new DefaultBikeSharingDataForZone(zoneId, serviceArea,
				companies());

		assertThat(data.isBikeSharingAreaFor(bike)).isFalse();
		assertThat(data.isBikeSharingAreaFor(mule)).isFalse();
		verify(bike).owner();
		verify(mule).owner();
	}

	@Test
	void isBikeAvailableForPerson() throws Exception {
		configureBikeAvailable();
		BikeSharingDataForZone data = buildAvailableBikeService();

		assertThat(data.isBikeAvailableFor(person)).isTrue();

		verify(person).isMobilityProviderCustomer(bikeCompany);
		verify(bikeSharingCompany).isBikeAvailableFor(person, zoneId);
	}

	private void configureBikeAvailable() {
		when(person.isMobilityProviderCustomer(bikeCompany)).thenReturn(true);
		when(bikeSharingCompany.isBikeAvailableFor(person, zoneId)).thenReturn(true);
		lenient().when(bikeSharingCompany.bookBikeFor(person, zoneId)).thenReturn(bike);
	}

	@Test
	void isNoBikeAvailableForPerson() throws Exception {
		when(person.isMobilityProviderCustomer(bikeCompany)).thenReturn(true);
		when(person.isMobilityProviderCustomer(muleCompany)).thenReturn(false);
		when(bikeSharingCompany.isBikeAvailableFor(person, zoneId)).thenReturn(false);
		BikeSharingDataForZone data = buildAvailableBikeService();

		assertThat(data.isBikeAvailableFor(person)).isFalse();

		verify(person).isMobilityProviderCustomer(bikeCompany);
		verify(person).isMobilityProviderCustomer(muleCompany);
		verify(bikeSharingCompany).isBikeAvailableFor(person, zoneId);
	}

	@Test
	void bookFreeBike() throws Exception {
		configureBikeAvailable();
		BikeSharingDataForZone data = buildAvailableBikeService();

		Bike bookedBike = data.bookBike(person);
		
		assertThat(bookedBike).isEqualTo(bike);
		verify(bikeSharingCompany).bookBikeFor(person, zoneId);
	}

	private BikeSharingDataForZone buildAvailableBikeService() {
		lenient().when(bikeSharingCompany.name()).thenReturn(bikeCompany);
		lenient().when(bike.owner()).thenReturn(bikeSharingCompany);
		Map<String, Boolean> serviceArea = Map.of(bikeCompany, true);
		return new DefaultBikeSharingDataForZone(zoneId, serviceArea, companies());
	}

	private Map<String, BikeSharingCompany> companies() {
		return Map.of(bikeCompany, bikeSharingCompany, muleCompany, muleSharingCompany);
	}
}

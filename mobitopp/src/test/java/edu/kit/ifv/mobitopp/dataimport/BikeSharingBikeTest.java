package edu.kit.ifv.mobitopp.dataimport;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingCompany;
import edu.kit.ifv.mobitopp.time.Time;

@ExtendWith(MockitoExtension.class)
public class BikeSharingBikeTest {

	@Mock
	private BikeSharingCompany company;
	@Mock
	private Person person;
	@Mock
	private Person otherPerson;
	private ZoneId zoneId;
	
	@BeforeEach
	public void beforeEach() {
		zoneId = new ZoneId("1", 0);
	}

	@Test
	void returnBikeToOwner() throws Exception {
		BikeSharingBike bike = createBike();
		
		bike.returnBike(zoneId);
		
		verify(company).returnBike(bike, zoneId);
	}
	
	@Test
	void userMustRelease() throws Exception {
		BikeSharingBike bike = createBike();
		
		bike.use(person, Time.start);
		assertThrows(IllegalArgumentException.class, () -> bike.release(otherPerson, Time.future));
	}
	
	private BikeSharingBike createBike() {
		int bikeId = 0;
		ZoneId startZone = new ZoneId("1", 0);
		BikeSharingBike bike = new BikeSharingBike(bikeId, startZone, company);
		return bike;
	}
}

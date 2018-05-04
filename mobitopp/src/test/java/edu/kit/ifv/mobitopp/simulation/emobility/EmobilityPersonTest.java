package edu.kit.ifv.mobitopp.simulation.emobility;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson.PublicChargingInfluencesDestinationChoice;

public class EmobilityPersonTest {

	@Test
	public void hasNoAssignedCarSharingCompanies() {
		Map<String, Boolean> carSharingCustomership = Collections.emptyMap();
		EmobilityPerson emobilityPerson = newPerson(carSharingCustomership);
		
		assertFalse(emobilityPerson.isCarSharingCustomer("Stadtmobil"));
	}
	
	@Test
	public void isCarSharingCustomer() {
		String company = "Stadtmobil";
		Map<String, Boolean> carSharingCustomership = Collections.singletonMap(company, true);
		EmobilityPerson emobilityPerson = newPerson(carSharingCustomership);
		
		assertTrue(emobilityPerson.isCarSharingCustomer(company));
	}
	
	@Test
	public void isNoCarSharingCustomer() {
		String company = "Stadtmobil";
		Map<String, Boolean> carSharingCustomership = Collections.singletonMap(company, false);
		EmobilityPerson emobilityPerson = newPerson(carSharingCustomership);
		
		assertFalse(emobilityPerson.isCarSharingCustomer(company));
	}

	private EmobilityPerson newPerson(Map<String, Boolean> carSharingCustomership) {
		Person person = mock(Person.class);
		float eMobilityAcceptance = 1.0f;
		PublicChargingInfluencesDestinationChoice chargingInfluencesDestinationChoice = PublicChargingInfluencesDestinationChoice.NEVER;
		EmobilityPerson emobilityPerson = new EmobilityPerson(person, eMobilityAcceptance, chargingInfluencesDestinationChoice,
				carSharingCustomership);
		return emobilityPerson;
	}
}

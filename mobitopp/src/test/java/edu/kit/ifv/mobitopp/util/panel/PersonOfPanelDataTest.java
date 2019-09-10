package edu.kit.ifv.mobitopp.util.panel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonOfPanelDataBuilder;

public class PersonOfPanelDataTest {

	@Test
	void ageToLow() throws Exception {
		PersonOfPanelDataBuilder builder = new PersonOfPanelDataBuilder().withAge(-1);
		assertThrows(IllegalArgumentException.class, () -> builder.build());
	}
	
	@Test
	void ageToHigh() throws Exception {
		PersonOfPanelDataBuilder builder = new PersonOfPanelDataBuilder().withAge(121);
		assertThrows(IllegalArgumentException.class, () -> builder.build());
	}
	
	@Test
	void idMustBeSet() throws Exception {
		PersonOfPanelDataBuilder builder = new PersonOfPanelDataBuilder().withId(null);

		assertThrows(NullPointerException.class, () -> builder.build());
	}
	
	@Test
	void genderIsMaleOrFemale() throws Exception {
		PersonOfPanelDataBuilder builder = new PersonOfPanelDataBuilder().withGender(-1);
		
		assertThrows(IllegalArgumentException.class, () -> builder.build());
	}
}

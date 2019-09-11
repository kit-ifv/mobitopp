package edu.kit.ifv.mobitopp.util.panel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonOfPanelDataBuilder;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

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

	@Test
	void getPoleDistancePerActivityType() throws Exception {
		float distanceWork = 2.0f;
		float distanceEducation = 3.0f;
		PersonOfPanelData person = new PersonOfPanelDataBuilder()
				.withDistanceWork(distanceWork)
				.withDistanceEducation(distanceEducation)
				.build();

		assertAll(() -> assertThat(person.getPoleDistance()).isEqualTo(distanceWork),
				() -> assertThat(person.getPoleDistance(ActivityType.WORK)).isEqualTo(distanceWork),
				() -> assertThat(person.getPoleDistance(ActivityType.EDUCATION))
						.isEqualTo(distanceEducation));
	}

	@Test
	void usesWorkDistanceAsPoleDistance() throws Exception {
		float distanceWork = 2.0f;
		PersonOfPanelData person = new PersonOfPanelDataBuilder()
				.withDistanceWork(distanceWork)
				.build();

		assertThat(person.getPoleDistance()).isEqualTo(distanceWork);
	}

	@Test
	void usesEducationDistanceAsPoleDistance() throws Exception {
		float distanceEducation = 2.0f;
		PersonOfPanelData person = new PersonOfPanelDataBuilder()
				.withDistanceEducation(distanceEducation)
				.build();

		assertThat(person.getPoleDistance()).isEqualTo(distanceEducation);
	}
}

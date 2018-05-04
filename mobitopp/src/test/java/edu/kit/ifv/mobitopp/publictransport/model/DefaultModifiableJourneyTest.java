package edu.kit.ifv.mobitopp.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.kit.ifv.mobitopp.time.Time;

public class DefaultModifiableJourneyTest {

	private static final int someId = 0;
	private static final int anotherId = 1;
	private static final Time someDay = Data.someTime();
	private static final Time anotherDay = someDay.plusDays(1);

	@Test
	public void equalsAndHashCode() throws Exception {
		Journey oneJourney = someJourney();
		Journey sameJourney = someJourney();
		Journey anotherJourney = anotherJourney();
		Journey oneJourneyAtAnotherDay = someJourneyAtAnotherDay();
		Journey anotherJourneyAtAnotherDay = anotherJourneyAtAnotherDay();

		assertThat(oneJourney, is(equalTo(sameJourney)));
		assertThat(sameJourney, is(equalTo(oneJourney)));
		assertThat(oneJourney.hashCode(), is(equalTo(sameJourney.hashCode())));

		assertThat(oneJourney, is(equalTo(oneJourney)));
		assertThat(oneJourney.hashCode(), is(equalTo(oneJourney.hashCode())));

		assertThat(oneJourney, is(not(equalTo(anotherJourney))));
		assertThat(anotherJourney, is(not(equalTo(oneJourney))));
		assertThat(oneJourney, is(not(equalTo(oneJourneyAtAnotherDay))));
		assertThat(oneJourney, is(not(equalTo(anotherJourneyAtAnotherDay))));
		assertThat(anotherJourney, is(not(equalTo(oneJourneyAtAnotherDay))));
		assertThat(anotherJourney, is(not(equalTo(anotherJourneyAtAnotherDay))));
	}

	private static Journey someJourney() {
		return journey().withId(someId).at(someDay).build();
	}

	private static Journey anotherJourney() {
		return journey().withId(anotherId).at(someDay).build();
	}

	private static Journey someJourneyAtAnotherDay() {
		return journey().withId(someId).at(anotherDay).build();
	}

	private static Journey anotherJourneyAtAnotherDay() {
		return journey().withId(anotherId).at(anotherDay).build();
	}

}

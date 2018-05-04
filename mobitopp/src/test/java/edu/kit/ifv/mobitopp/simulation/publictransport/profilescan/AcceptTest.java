package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.time.RelativeTime.of;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class AcceptTest {

	private static final RelativeTime endOfHour = of(59, MINUTES);
	private static final RelativeTime inHour = of(1, MINUTES);

	private Accept acceptFirstHour;
	private FunctionEntry atFirstHour;
	private FunctionEntry atEndOfFirstHour;
	private FunctionEntry atSecondHour;
	private FunctionEntry inFirstHour;
	private FunctionEntry inSecondHour;

	@Before
	public void initialise() {
		acceptFirstHour = Accept.perHour(someTime());
		atFirstHour = entry(atFirstHour());
		atEndOfFirstHour = entry(atEndOfFirstHour());
		atSecondHour = entry(atSecondHour());
		inFirstHour = entry(inFirstHour());
		inSecondHour = entry(inSecondHour());
	}
	
	@Test
	public void providesUniqueFilename() {
		verifyUniqueFileNameFor(someTime());
	}
	
	@Test
	public void providesAnotherUniqueFilename() {
		verifyUniqueFileNameFor(atSecondHour());
	}

	private void verifyUniqueFileNameFor(Time time) {
		Validity validity = Accept.perHour(time);
		
		String fileName = validity.asFileName();
		
		String dayOfMonth = String.valueOf(time.getDay());
		String hour = String.valueOf(time.getHour());
		assertThat(fileName, startsWith(dayOfMonth));
		assertThat(fileName, endsWith(hour));
	}

	@Test
	public void isNotTooLate() {
		boolean tooLate = acceptFirstHour.isTooLate(atFirstHour);

		assertFalse(tooLate);
	}

	@Test
	public void isNotTooLateAtEndOfHour() {
		boolean tooLate = acceptFirstHour.isTooLate(atEndOfFirstHour);

		assertFalse(tooLate);
	}

	@Test
	public void isTooLate() {
		boolean tooLate = acceptFirstHour.isTooLate(atSecondHour);

		assertTrue(tooLate);
	}

	@Test
	public void acceptMatchingHour() {
		boolean accept = acceptFirstHour.accept(atFirstHour);

		assertTrue(accept);
	}

	@Test
	public void notAcceptMatchingHour() {
		boolean accept = acceptFirstHour.accept(atSecondHour);

		assertFalse(accept);
	}

	@Test
	public void acceptInHour() {
		boolean accept = acceptFirstHour.accept(inFirstHour);

		assertTrue(accept);
	}

	@Test
	public void notAcceptInHour() {
		boolean accept = acceptFirstHour.accept(inSecondHour);

		assertFalse(accept);
	}

	private FunctionEntry entry(Time departure) {
		Connection notRelevant = mock(Connection.class);
		Time arrivalAtTarget = departure;
		return new FunctionEntry(departure, arrivalAtTarget, notRelevant);
	}

	private Time atFirstHour() {
		return someTime();
	}

	private Time atSecondHour() {
		return atFirstHour().plus(of(1, HOURS));
	}
	
	private Time atEndOfFirstHour() {
		return atFirstHour().plus(endOfHour);
	}

	private Time inFirstHour() {
		return atFirstHour().plus(inHour);
	}

	private Time inSecondHour() {
		return atSecondHour().plus(inHour);
	}
}

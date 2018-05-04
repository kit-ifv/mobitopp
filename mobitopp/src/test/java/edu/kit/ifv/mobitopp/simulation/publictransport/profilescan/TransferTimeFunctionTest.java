package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.fourMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.threeMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.twoMinutesLater;
import static edu.kit.ifv.mobitopp.time.Time.future;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.time.Time;

public class TransferTimeFunctionTest {

	private TransferTimeFunction function;

	@Before
	public void initialise() throws Exception {
		function = new TransferTimeFunction();
	}

	@Test
	public void emptyFunctionArrivesAtEthernity() throws Exception {
		assertThat(function.arrivalFor(someTime()), hasValue(future));
	}

	@Test
	public void addEntryToEmptyFunction() throws Exception {
		function.update(entryStartingAt(someTime(), oneMinuteLater()));

		assertThat(function.arrivalFor(someTime()), hasValue(oneMinuteLater()));
	}

	@Test
	public void addEarlierEntry() throws Exception {
		function.update(entryStartingAt(oneMinuteLater(), twoMinutesLater()));
		function.update(entryStartingAt(someTime(), oneMinuteLater()));

		assertThat(function.arrivalFor(someTime()), hasValue(oneMinuteLater()));
	}

	@Test
	public void preservesEarlierEntries() throws Exception {
		function.update(entryStartingAt(someTime(), oneMinuteLater()));
		function.update(entryStartingAt(oneMinuteLater(), twoMinutesLater()));
		function.update(entryStartingAt(twoMinutesLater(), threeMinutesLater()));

		assertThat(function.arrivalFor(someTime()), hasValue(oneMinuteLater()));
	}

	@Test
	public void findsArrivalForNextDepartureTime() throws Exception {
		function.update(entryStartingAt(oneMinuteLater(), twoMinutesLater()));

		assertThat(function.arrivalFor(someTime()), hasValue(twoMinutesLater()));
	}

	@Test
	public void changeEntryToEarlierArrivalTime() throws Exception {
		function.update(entryStartingAt(someTime(), twoMinutesLater()));
		function.update(entryStartingAt(someTime(), oneMinuteLater()));

		assertThat(function.arrivalFor(someTime()), hasValue(oneMinuteLater()));
	}

	@Test
	public void preserveEarlierArrivalTimes() throws Exception {
		function.update(entryStartingAt(someTime(), oneMinuteLater()));
		function.update(entryStartingAt(oneMinuteLater(), fourMinutesLater()));
		function.update(entryStartingAt(twoMinutesLater(), threeMinutesLater()));

		assertThat(function.arrivalFor(someTime()), hasValue(oneMinuteLater()));
		assertThat(function.arrivalFor(oneMinuteLater()), hasValue(threeMinutesLater()));
	}

	@Test
	public void updatesEntryWithSameDeparture() throws Exception {
		function.update(entryStartingAt(oneMinuteLater(), threeMinutesLater()));
		function.update(entryStartingAt(someTime(), twoMinutesLater()));
		function.update(entryStartingAt(someTime(), oneMinuteLater()));

		assertThat(function.arrivalFor(someTime()), hasValue(oneMinuteLater()));
		assertThat(function.arrivalFor(oneMinuteLater()), hasValue(threeMinutesLater()));
	}

	@Test
	public void rejectsLaterArrival() throws Exception {
		function.update(entryStartingAt(someTime(), oneMinuteLater()));
		function.update(entryStartingAt(someTime(), twoMinutesLater()));

		assertThat(function.arrivalFor(someTime()), hasValue(oneMinuteLater()));
	}

	private static TransferEntry entryStartingAt(Time departure, Time arrival) {
		return new TransferEntry(departure, arrival);
	}

}

package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.fourMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.fromOtherToAnother;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.fromSomeToAnother;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.threeMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.twoMinutesLater;
import static edu.kit.ifv.mobitopp.time.RelativeTime.of;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;
import nl.jqno.equalsverifier.EqualsVerifier;

public class ArrivalTimeFunctionTest {

	private ArrivalTimeFunction function;
	private ProfileWriter writer;

	@Before
	public void initialise() throws Exception {
		writer = mock(ProfileWriter.class);
		function = new ArrivalTimeFunction();
	}

	@Test
	public void arrivesAtEthernityWhenItIsEmpty() throws Exception {
		assertThat(function.arrivalFor(someTime()), isEmpty());
	}

	@Test
	public void arrivesAtSavedArrival() throws Exception {
		function.update(entry(someTime(), oneMinuteLater()));

		assertThat(function.arrivalFor(someTime()), hasValue(oneMinuteLater()));
	}

	@Test
	public void arrivesAtUpdatedArrival() throws Exception {
		function.update(entry(someTime(), twoMinutesLater()));
		function.update(entry(someTime(), oneMinuteLater()));

		assertThat(function.arrivalFor(someTime()), hasValue(oneMinuteLater()));
	}

	@Test
	public void arrivesAtEarlierArrival() throws Exception {
		function.update(entry(threeMinutesLater(), fourMinutesLater()));
		function.update(entry(twoMinutesLater(), threeMinutesLater()));
		function.update(entry(someTime(), oneMinuteLater()));

		assertThat(function.arrivalFor(someTime()), hasValue(oneMinuteLater()));
		assertThat(function.arrivalFor(oneMinuteLater()), hasValue(threeMinutesLater()));
		assertThat(function.arrivalFor(twoMinutesLater()), hasValue(threeMinutesLater()));
		assertThat(function.arrivalFor(threeMinutesLater()), hasValue(fourMinutesLater()));
	}

	@Test
	public void arrivesAtSavedArrivalWhenDepartureTimeIsBeforeSavedDepartureTime() throws Exception {
		function.update(entry(oneMinuteLater(), twoMinutesLater()));

		assertThat(function.arrivalFor(someTime()), hasValue(twoMinutesLater()));
	}

	@Test
	public void updatesFunctionWithSameArrivalButLaterDepartureTime() throws Exception {
		Connection byFoot = mock(Connection.class);
		Connection byVehicle = mock(Connection.class);
		function.update(entry(someTime(), twoMinutesLater(), byFoot));
		function.update(entry(oneMinuteLater(), twoMinutesLater(), byVehicle));

		assertThat(function.connectionFor(someTime()), hasValue(byVehicle));
	}

	@Test
	public void removesChangeTimeFromSingleDepartureAtStartStop() throws Exception {
		Stop someStop = mock(Stop.class);
		when(someStop.addChangeTimeTo(any())).thenReturn(oneMinuteLater());
		function.update(entry(someTime(), oneMinuteLater()));

		ArrivalTimeFunction clearedFunction = function.removeChangeTimeAt(someStop);

		assertThat(clearedFunction.arrivalFor(someTime()), hasValue(oneMinuteLater()));
		assertThat(clearedFunction.arrivalFor(oneMinuteLater()), hasValue(oneMinuteLater()));
		assertThat(clearedFunction.arrivalFor(twoMinutesLater()), isEmpty());
	}

	@Test
	public void removesChangeTimesFromEveryDepartureAtStartStop() throws Exception {
		Stop someStop = mock(Stop.class);
		when(someStop.addChangeTimeTo(twoMinutesLater())).thenReturn(threeMinutesLater());
		when(someStop.addChangeTimeTo(someTime())).thenReturn(oneMinuteLater());
		function.update(entry(twoMinutesLater(), threeMinutesLater()));
		function.update(entry(someTime(), oneMinuteLater()));

		ArrivalTimeFunction clearedFunction = function.removeChangeTimeAt(someStop);

		assertThat(clearedFunction.arrivalFor(someTime()), hasValue(oneMinuteLater()));
		assertThat(clearedFunction.arrivalFor(oneMinuteLater()), hasValue(oneMinuteLater()));
		assertThat(clearedFunction.arrivalFor(twoMinutesLater()), hasValue(threeMinutesLater()));
		assertThat(clearedFunction.arrivalFor(threeMinutesLater()), hasValue(threeMinutesLater()));
		assertThat(clearedFunction.arrivalFor(fourMinutesLater()), isEmpty());
	}

	@Test
	public void arrivesAtEthernityWhenDepartureIsAfterLatestDeparture() throws Exception {
		function.update(entry(someTime(), twoMinutesLater()));

		assertThat(function.arrivalFor(oneMinuteLater()), isEmpty());
	}

	@Test
	public void returnsInsertedInstanceOfArrival() throws Exception {
		Time arrival = oneMinuteLater();
		function.update(entry(someTime(), arrival));

		assertThat(function.arrivalFor(someTime()), hasValue(sameInstance(arrival)));
	}

	@Test
	public void doesNotInsertEntryWhenExistingDepartureIsLaterButArrivalIsSame() throws Exception {
		Time arrivalOfSomeDeparture = twoMinutesLater();
		Time arrivalOfOneMinuteLater = twoMinutesLater();
		function.update(entry(oneMinuteLater(), arrivalOfOneMinuteLater));
		function.update(entry(someTime(), arrivalOfSomeDeparture));

		assertThat(function.arrivalFor(oneMinuteLater()),
				hasValue(sameInstance(arrivalOfOneMinuteLater)));
		assertThat(function.arrivalFor(someTime()), hasValue(sameInstance(arrivalOfOneMinuteLater)));
	}

	@Test
	public void savedArrivalIsLater() throws Exception {
		assertThat(function.hasLaterArrivalThan(someTime()), is(true));

		function.update(entry(someTime(), oneMinuteLater()));

		assertThat(function.hasLaterArrivalThan(someTime()), is(true));
		assertThat(function.hasLaterArrivalThan(oneMinuteLater()), is(false));
		assertThat(function.hasLaterArrivalThan(twoMinutesLater()), is(false));
	}

	private FunctionEntry entry(Time departure, Time arrivalAtTarget) {
		Connection notRelevant = mock(Connection.class);
		return new FunctionEntry(departure, arrivalAtTarget, notRelevant);
	}

	private FunctionEntry entry(Time departure, Time arrivalAtTarget, Connection connection) {
		return new FunctionEntry(departure, arrivalAtTarget, connection);
	}

	@Test
	public void hasNoConnectionIfItIsEmpty() throws Exception {
		assertThat(function.connectionFor(someTime()), isEmpty());
	}

	@Test
	public void startsWithConnection() throws Exception {
		function.update(entry(someTime(), oneMinuteLater(), fromSomeToAnother()));

		assertThat(function.connectionFor(someTime()), hasValue(fromSomeToAnother()));
	}

	@Test
	public void processesNothingWhenEmpty() throws Exception {
		function.forEach(writer);

		verifyZeroInteractions(writer);
	}

	@Test
	public void processesEachEntry() throws Exception {
		FunctionEntry laterEntry = mock(FunctionEntry.class);
		FunctionEntry earlierEntry = mock(FunctionEntry.class);
		when(laterEntry.arrivalAtTarget()).thenReturn(oneMinuteLater());
		when(earlierEntry.arrivalAtTarget()).thenReturn(someTime());
		when(earlierEntry.departure()).thenReturn(someTime());
		function.update(laterEntry);
		function.update(earlierEntry);

		function.forEach(writer);

		verify(writer).write(earlierEntry);
		verify(writer).write(laterEntry);
	}

	@Test
	public void splitsWithValidator() {
		FunctionEntry firstHour = entry(firstHour(), firstHour());
		FunctionEntry secondHour = entry(secondHour(), secondHour());
		function.update(secondHour);
		function.update(firstHour);
		EntryAcceptor firstPart = mock(EntryAcceptor.class);
		EntrySplitter splitter = mock(EntrySplitter.class);
		List<EntryAcceptor> splitters = asList(firstPart, firstPart);
		when(splitter.parts()).thenReturn(splitters);
		when(firstPart.accept(firstHour)).thenReturn(true, false);
		when(firstPart.isTooLate(firstHour)).thenReturn(false);
		when(firstPart.accept(secondHour)).thenReturn(true, false);
		when(firstPart.isTooLate(secondHour)).thenReturn(true, false);

		List<ArrivalTimeFunction> parts = function.split(splitter);

		ArrivalTimeFunction partOne = new ArrivalTimeFunction();
		partOne.update(secondHour);
		partOne.update(firstHour);
		ArrivalTimeFunction partTwo = new ArrivalTimeFunction();
		partTwo.update(secondHour);
		assertThat(parts, hasSize(splitters.size()));
		assertThat(parts.get(0), is(equalTo(partOne)));
		assertThat(parts.get(1), is(equalTo(partTwo)));
		verify(splitter).parts();
		verify(firstPart, atLeastOnce()).accept(firstHour);
		verify(firstPart, atLeastOnce()).accept(secondHour);
		verify(firstPart, atLeastOnce()).isTooLate(firstHour);
		verify(firstPart, atLeastOnce()).isTooLate(secondHour);
	}

	private Time firstHour() {
		return someTime();
	}

	private Time secondHour() {
		return firstHour().plus(of(1, HOURS));
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		Connection someConnection = fromSomeToAnother();
		Connection anotherConnection = fromOtherToAnother();
		EqualsVerifier
				.forClass(ArrivalTimeFunction.class)
				.withPrefabValues(Time.class, someTime(), oneMinuteLater())
				.withPrefabValues(Connection.class, someConnection, anotherConnection)
				.usingGetClass()
				.verify();
	}
}

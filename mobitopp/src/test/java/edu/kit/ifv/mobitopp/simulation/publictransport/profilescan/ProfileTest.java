package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.yetAnotherStop;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import nl.jqno.equalsverifier.EqualsVerifier;

public class ProfileTest {

	private ArrivalTimeFunction someFunction;
	private ProfileWriter stream;
	private ProfileReader reader;
	private Profile profile;
	private Stop target;
	private ArrivalTimeFunction anotherFunction;
	private EntrySplitter splitter;
	private Time timetableStart;

	@Before
	public void initialise() throws Exception {
	  timetableStart = someTime();
		splitter = Split.hourly(timetableStart);
		someFunction = mock(ArrivalTimeFunction.class);
		anotherFunction = mock(ArrivalTimeFunction.class);
		stream = mock(ProfileWriter.class);
		reader = mock(ProfileReader.class);
		target = yetAnotherStop();
		profile = new Profile(target);
	}

	@Test
	public void getsEmptyFunctionWhenNothingHasBeenAdded() throws Exception {
		assertThat(profile.from(someStop()), is(new ArrivalTimeFunction()));
	}

	@Test
	public void getsFunctionForGivenStop() throws Exception {
		profile.update(someStop(), someFunction);

		assertThat(profile.from(someStop()), is(someFunction));
	}

	@Test
	public void savesArrivalTimeFunctionsWithTheirStops() throws Exception {
		ArrivalTimeFunction anotherFunction = mock(ArrivalTimeFunction.class);
		profile.update(someStop(), someFunction);
		profile.update(anotherStop(), anotherFunction);

		profile.saveTo(stream);

		verify(stream).write(someStop(), someFunction);
		verify(stream).write(anotherStop(), anotherFunction);
	}

	@Test
	public void loadsSingleArrivalTimeFunction() throws Exception {
		when(reader.next()).thenReturn(true, false);
		when(reader.readStop()).thenReturn(someStop());
		when(reader.readFunction()).thenReturn(someFunction);

		profile.loadFrom(reader);

		assertThat(profile.from(someStop()), is(someFunction));
		verify(reader, times(2)).next();
		verify(reader).readStop();
		verify(reader).readFunction();
	}

	@Test
	public void loadsSeveralFunctions() throws Exception {
		when(reader.readStop()).thenReturn(someStop(), anotherStop());
		when(reader.readFunction()).thenReturn(someFunction, anotherFunction);
		when(reader.next()).thenReturn(true, true, false);

		profile.loadFrom(reader);

		verify(reader, times(3)).next();
		verify(reader, times(2)).readStop();
		verify(reader, times(2)).readFunction();
	}

	@Test
	public void splitsEmptyProfileToSingleEmptyProfile() throws Exception {
		List<Profile> parts = profile.split(splitter);

		assertThat(parts, hasSize(1));
		Profile part = parts.get(0);
		assertThat(part.validity(), is(equalTo(validityOf(firstHour()))));
		assertThat(part, is(not(sameInstance(profile))));
	}

	@Test
	public void splitsContainedFunctions() throws Exception {
		ArrivalTimeFunction singlePart = mock(ArrivalTimeFunction.class);
		when(someFunction.split(splitter)).thenReturn(asList(singlePart));
		profile.update(someStop(), someFunction);

		List<Profile> parts = profile.split(splitter);

		assertThat(parts, hasSize(1));
		assertThat(parts.get(0).from(someStop()), is(singlePart));
		verify(someFunction).split(splitter);
	}

	@Test
	public void splitsIntoSeveralProfilesWhenFunctionIsSplittedIntoSeveralFunctions()
			throws Exception {
		ArrivalTimeFunction functionOne = mock(ArrivalTimeFunction.class);
		ArrivalTimeFunction functionTwo = mock(ArrivalTimeFunction.class);
		when(someFunction.split(splitter)).thenReturn(asList(functionOne, functionTwo));
		profile.update(someStop(), someFunction);

		List<Profile> profileParts = profile.split(splitter);

		Profile firstPart = profileParts.get(0);
		Profile secondPart = profileParts.get(1);
		assertThat(firstPart.from(someStop()), is(functionOne));
		assertThat(secondPart.from(someStop()), is(functionTwo));
		assertThat(firstPart.validity(), is(validityOf(firstHour())));
		assertThat(secondPart.validity(), is(validityOf(secondHour())));
	}

	private static Validity validityOf(Time time) {
		return Accept.perHour(time);
	}

	private Time firstHour() {
		return timetableStart;
	}

	private Time secondHour() {
		return firstHour().plus(RelativeTime.of(1, HOURS));
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		Connection oneConnection = connection().startsAt(someStop()).build();
		Connection anotherConnection = connection().startsAt(anotherStop()).build();
		EqualsVerifier
				.forClass(Profile.class)
				.withPrefabValues(Time.class, someTime(), oneMinuteLater())
				.withPrefabValues(Stop.class, someStop(), anotherStop())
				.withPrefabValues(Connection.class, oneConnection, anotherConnection)
				.usingGetClass()
				.verify();
	}
}

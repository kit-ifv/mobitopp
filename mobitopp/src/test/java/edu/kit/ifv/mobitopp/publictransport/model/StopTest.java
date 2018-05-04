package edu.kit.ifv.mobitopp.publictransport.model;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.coordinate;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteEarlier;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import nl.jqno.equalsverifier.EqualsVerifier;

public class StopTest {

	private static final RelativeTime noChangeTime = RelativeTime.of(0, MINUTES);
	private static final RelativeTime smallChangeTime = RelativeTime.of(1, MINUTES);
	private static final RelativeTime smallDuration = RelativeTime.of(1, MINUTES);

	private static final int id = 0;
	private static final int externalId = 0;

	private Station station;
	private Time currentTime;
	private Point2D someCoordinate;

	@Before
	public void initialise() throws Exception {
		currentTime = someTime();
		station = mock(Station.class);
		someCoordinate = someCoordinate();
	}

	@Test
	public void transferToItself() {
		Stop stop = stop();

		stop.addNeighbour(stop, smallDuration);
		Optional<Time> arrival = stop.arrivalAt(stop, currentTime);

		assertThat(arrival, isEmpty());
		assertThat(stop.neighbours(), not(contains(stop)));
	}

	private Stop stop() {
		return stopWith(noChangeTime);
	}

	@Test
	public void addsChangeTimeToGivenTimeWhenChangeTimeIsGreaterZero() throws Exception {
		Time timeIncludingChangeTime = stopWith(smallChangeTime).addChangeTimeTo(currentTime);

		assertThat(timeIncludingChangeTime, is(equalTo(oneMinuteLater())));
	}

	@Test
	public void addsNothingToGivenTimeWhenChangeTimeIsZero() throws Exception {
		Time timeIncludingChangeTime = stopWith(noChangeTime).addChangeTimeTo(currentTime);

		assertThat(timeIncludingChangeTime, is(equalTo(currentTime)));
	}

	@Test
	public void subtractsChangeTimeFromGivenTime() throws Exception {
		Time timeIncludingChangeTime = stopWith(smallChangeTime).subtractChangeTimeFrom(currentTime);

		assertThat(timeIncludingChangeTime, is(equalTo(oneMinuteEarlier())));
	}

	@Test
	public void subtractsNothingFromGivenTime() throws Exception {
		Time timeIncludingChangeTime = stopWith(noChangeTime).subtractChangeTimeFrom(currentTime);

		assertThat(timeIncludingChangeTime, is(equalTo(currentTime)));
	}

	private static Double someCoordinate() {
		return new Point2D.Double(0, 0);
	}

	private Stop stopWith(RelativeTime changeTime) {
		return new Stop(id, "name", someCoordinate, changeTime, station, externalId);
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		EqualsVerifier
				.forClass(Stop.class)
				.withPrefabValues(Stop.class, someStop(), anotherStop())
				.withPrefabValues(Point2D.class, coordinate(0, 0), coordinate(1, 1))
				.withOnlyTheseFields("id")
				.usingGetClass()
				.verify();
	}

}

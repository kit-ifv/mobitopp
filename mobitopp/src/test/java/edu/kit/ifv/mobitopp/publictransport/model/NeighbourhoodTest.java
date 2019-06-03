package edu.kit.ifv.mobitopp.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.time.RelativeTime;

public class NeighbourhoodTest {

	private static final RelativeTime someTime = RelativeTime.of(1, MINUTES);
	private Stop self;
	private Neighbourhood ownNeighbourhood;
	private Stop neighbour;

	@Before
	public void initialise() {
		self = someStop();
		ownNeighbourhood = self.neighbours();
		neighbour = anotherStop();
	}

	@Test
	public void returnsEmptyWalkTimeWhenNeighbourhoodIsEmpty() throws Exception {
		Optional<RelativeTime> walkTime = ownNeighbourhood.walkTimeTo(neighbour);

		assertThat(walkTime, isEmpty());
	}

	@Test
	public void returnsDurationForStopWhichIsInNeighbourhood() throws Exception {
		ownNeighbourhood.add(neighbour, someTime);

		Optional<RelativeTime> walkTime = ownNeighbourhood.walkTimeTo(neighbour);

		assertThat(walkTime, isPresent());
		assertThat(walkTime, hasValue(equalTo(someTime)));
	}

	@Test
	public void returnsEmptyWalkTimeWhenStopIsNotANeighbour() throws Exception {
		ownNeighbourhood.add(neighbour, someTime);

		Stop noNeighbour = otherStop();
		Optional<RelativeTime> walkTime = ownNeighbourhood.walkTimeTo(noNeighbour);

		assertThat(walkTime, isEmpty());
	}

	@Test
	public void ensureSymmetricWalkTimesShorterBeforeLonger() {
		RelativeTime anotherTime = someTime.plusMinutes(1);
		ownNeighbourhood.add(neighbour, someTime);
		neighbour.addNeighbour(self, anotherTime);

		Optional<RelativeTime> neighbourToMe = neighbour.neighbours().walkTimeTo(self);
		Optional<RelativeTime> meToNeighbour = ownNeighbourhood.walkTimeTo(neighbour);

		assertThat(neighbourToMe, isPresent());
		assertThat(meToNeighbour, is(equalTo(neighbourToMe)));
	}

	@Test
	public void ensureSymmetricWalkTimesLongerBeforeShorter() {
		RelativeTime anotherTime = someTime.plusMinutes(1);
		ownNeighbourhood.add(neighbour, anotherTime);
		neighbour.addNeighbour(self, someTime);

		Optional<RelativeTime> neighbourToMe = neighbour.neighbours().walkTimeTo(self);
		Optional<RelativeTime> meToNeighbour = ownNeighbourhood.walkTimeTo(neighbour);

		assertThat(neighbourToMe, isPresent());
		assertThat(meToNeighbour, is(equalTo(neighbourToMe)));
	}
}

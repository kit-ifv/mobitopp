package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.twoMinutesLater;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class ConnectionComparatorTest {

	@Test
	public void whenDepartureTimesAreDifferent() throws Exception {
		Time earlierDeparture = someTime();
		Time laterDeparture = oneMinuteLater();
		Connection earlier = connection().departsAt(earlierDeparture).build();
		Connection later = connection().departsAt(laterDeparture).build();

		ConnectionComparator comparator = new ConnectionComparator();
		int compare = comparator.compare(earlier, later);

		assertThat(compare < 0, is(true));
	}

	@Test
	public void whenDepartureTimesAreEqual() throws Exception {
		Time departure = someTime();
		Time earlierArrival = oneMinuteLater();
		Time laterArrival = twoMinutesLater();
		Connection earlier = connection().departsAt(departure).arrivesAt(earlierArrival).build();
		Connection later = connection().departsAt(departure).arrivesAt(laterArrival).build();

		ConnectionComparator comparator = new ConnectionComparator();
		int compare = comparator.compare(earlier, later);

		assertThat(compare < 0, is(true));
	}

	@Test
	public void firstConnectionIsLessThanSecondConnectionWhenDepartureAndArrivalTimesAreEqualAndFirstConnectionArrivesAtStopWhereSecondConnectionDeparts()
			throws Exception {
		Stop start = someStop();
		Stop intermediate = anotherStop();
		Stop end = otherStop();
		Time sameTime = someTime();
		Connection lessConnection = connection()
				.startsAt(start)
				.endsAt(intermediate)
				.departsAt(sameTime)
				.arrivesAt(sameTime)
				.build();
		Connection greaterConnection = connection()
				.startsAt(intermediate)
				.endsAt(end)
				.departsAt(sameTime)
				.arrivesAt(sameTime)
				.build();

		ConnectionComparator comparator = new ConnectionComparator();
		int compareLessToGreater = comparator.compare(lessConnection, greaterConnection);
		int compareGreaterToLess = comparator.compare(greaterConnection, lessConnection);
		int compareLessToLess = comparator.compare(lessConnection, lessConnection);

		assertThat("lesser to greater", compareLessToGreater, is(lessThan(0)));
		assertThat("greater to lesser", compareGreaterToLess, is(greaterThan(0)));
		assertThat("lesser to lesser", compareLessToLess, is(0));
	}
}

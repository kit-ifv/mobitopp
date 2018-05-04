package edu.kit.ifv.mobitopp.publictransport.serializer;

import static edu.kit.ifv.mobitopp.publictransport.matcher.StopMatcher.hasNeighbour;
import static edu.kit.ifv.mobitopp.publictransport.matcher.StopMatcher.hasNoNeighbour;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class TransferCouplerTest {

	private static final RelativeTime walkTime = RelativeTime.of(1, MINUTES);
	private Stop stop1;
	private Stop stop2;
	private Stop stop3;
	private List<StopTransfer> transfers;

	@Before
	public void initialise() {
		stop1 = someStop();
		stop2 = anotherStop();
		stop3 = otherStop();

		transfers = new ArrayList<>();
	}

	@Test
	public void createNeighbourshipForFirstStop() throws Exception {
		addTransfer();

		NeighbourhoodCoupler coupler = TransferCoupler.from(transfers);
		coupler.addNeighboursshipBetween(stop1, stop2);

		assertThat(stop1, hasNeighbour(stop2));
		assertThat(stop2, hasNeighbour(stop1));
	}

	@Test
	public void doesNothingWithoutWalkTimeBetweenStops() throws Exception {
		NeighbourhoodCoupler coupler = TransferCoupler.from(transfers);
		coupler.addNeighboursshipBetween(stop1, stop2);

		assertThat(stop1, hasNoNeighbour());
		assertThat(stop2, hasNoNeighbour());
	}

	@Test
	public void doesNotCreateNeighbourshipWithoutWalktime() throws Exception {
		addTransfer();

		NeighbourhoodCoupler coupler = TransferCoupler.from(transfers);
		coupler.addNeighboursshipBetween(stop1, stop3);

		assertThat(stop1, not(hasNeighbour(stop3)));
		assertThat(stop3, not(hasNeighbour(stop1)));
	}

	private void addTransfer() {
		transfers.add(new StopTransfer(stop1, stop2, walkTime));
	}
}

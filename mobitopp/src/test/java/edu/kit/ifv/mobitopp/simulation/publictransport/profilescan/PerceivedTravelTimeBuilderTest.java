package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class PerceivedTravelTimeBuilderTest {

	private static final RelativeTime transferPenalty = RelativeTime.ofMinutes(5);
	private ExampleProfileNetwork network;

	@Before
	public void initialise() {
		network = new ExampleProfileNetwork();
	}

	@Test
	public void addsTransferPenalty() {
		List<Connection> connections = asList(network.fromSomeToAnother, network.fromAnotherToOther);
		ProfileBuilder builder = createBuilderFor(connections);

		Profile profile = builder.buildUpTo(network.otherStop);
		Optional<Time> arrival = profile
				.from(network.someStop)
				.arrivalFor(network.fromSomeToAnother.departure());

		Time arrivalWithPenalty = network.fromAnotherToOther.arrival().plus(transferPenalty);
		assertThat(arrival, hasValue(arrivalWithPenalty));
	}

	private ProfileBuilder createBuilderFor(List<Connection> connections) {
		return PerceivedTravelTimeBuilder.from(transferPenalty, connections);
	}

	@Test
	public void addsNoTransferPenalty() {
		List<Connection> connections = asList(network.fromSomeToAnother, network.fromAnotherToOther,
				network.laterFromSomeToAnother, network.laterFromAnotherToOther);
		ProfileBuilder builder = createBuilderFor(connections);

		Profile profile = builder.buildUpTo(network.otherStop);
		Optional<Time> arrival = profile
				.from(network.someStop)
				.arrivalFor(network.fromSomeToAnother.departure());

		Time arrivalWithPenalty = network.laterFromAnotherToOther.arrival();
		assertThat(arrival, hasValue(arrivalWithPenalty));
	}

	@Test
	public void addsPenaltyForEachTransfer() {
		int numberOfTransfers = 2;
		List<Connection> connections = asList(network.fromSomeToAnother, network.fromAnotherToOther,
				network.fromOtherToYetAnother);
		ProfileBuilder builder = createBuilderFor(connections);

		Profile profile = builder.buildUpTo(network.yetAnotherStop);
		Optional<Time> arrival = profile
				.from(network.someStop)
				.arrivalFor(network.fromSomeToAnother.departure());

		Time arrivalWithPenalty = network.fromOtherToYetAnother
				.arrival()
				.plus(transferPenalty.multiplyBy(numberOfTransfers));
		assertThat(arrival, hasValue(arrivalWithPenalty));
	}

}

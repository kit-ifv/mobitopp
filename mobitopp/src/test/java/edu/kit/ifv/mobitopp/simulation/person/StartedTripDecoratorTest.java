package edu.kit.ifv.mobitopp.simulation.person;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class StartedTripDecoratorTest {

	private StartedTrip trip;
	private StartedTrip decorator;

	@BeforeEach
	public void initialise() {
		trip = mock(StartedTrip.class);
		decorator = new StartedTripDecorator(trip);
	}

	@MethodSource("functions")
	@ParameterizedTest
	void getter(Function<StartedTrip, ?> method) throws Exception {
		method.apply(decorator);

		method.apply(verify(trip));
	}

	static Stream<Function<StartedTrip, ?>> functions() {
		return Stream.of(StartedTrip::getOid, StartedTrip::origin, StartedTrip::destination, StartedTrip::mode,
				StartedTrip::startDate, StartedTrip::plannedEndDate, StartedTrip::plannedDuration,
				StartedTrip::nextActivity, StartedTrip::previousActivity, StartedTrip::vehicleId);
	}
}

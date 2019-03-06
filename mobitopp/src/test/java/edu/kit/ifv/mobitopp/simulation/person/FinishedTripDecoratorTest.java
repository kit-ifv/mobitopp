package edu.kit.ifv.mobitopp.simulation.person;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class FinishedTripDecoratorTest {

  private FinishedTrip trip;
  private FinishedTrip decorator;

  @BeforeEach
  public void initialise() {
    trip = mock(FinishedTrip.class);
    decorator = new FinishedTripDecorator(trip);
  }

  @MethodSource("functions")
  @ParameterizedTest
  void getter(Function<FinishedTrip, ?> method) throws Exception {
    method.apply(decorator);

    method.apply(verify(trip));
  }

  static Stream<Function<FinishedTrip, ?>> functions() {
    return Stream
        .of(FinishedTrip::getOid, FinishedTrip::origin, FinishedTrip::destination,
            FinishedTrip::mode, FinishedTrip::startDate, FinishedTrip::endDate,
            FinishedTrip::plannedEndDate, FinishedTrip::plannedDuration, FinishedTrip::nextActivity,
            FinishedTrip::nextActivity, FinishedTrip::statistic, FinishedTrip::vehicleId);
  }
}

package edu.kit.ifv.mobitopp.simulation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class BaseTripTest {

  private TripData trip;
  private BaseTrip base;
  private PersonResults results;

  @BeforeEach
  public void initialise() {
    trip = mock(TripData.class);
    results = mock(PersonResults.class);
    SimulationPerson person = mock(SimulationPerson.class);
    base = new BaseTrip(trip, person);
  }
  
  @MethodSource("functions")
  @ParameterizedTest
  void getter(Function<TripData, ?> method) throws Exception {
    method.apply(base);

    method.apply(verify(trip));
  }

  static Stream<Function<TripData, ?>> functions() {
    return Stream
        .of(TripData::getOid, TripData::calculatePlannedEndDate, TripData::origin,
            TripData::destination, TripData::mode, TripData::startDate, TripData::plannedDuration,
            TripData::previousActivity, TripData::nextActivity);
  }

  @Test
  void finish() throws Exception {
    Time currentTime = mock(Time.class);
    FinishedTrip finishedTrip = base.finish(currentTime, results);
    
    assertThat(finishedTrip.endDate(), is(equalTo(currentTime)));
  }
}

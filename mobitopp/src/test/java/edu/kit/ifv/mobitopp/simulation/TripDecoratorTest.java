package edu.kit.ifv.mobitopp.simulation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class TripDecoratorTest {

  private TripIfc trip;
  private TripIfc decorator;
  private PersonResults results;

  @BeforeEach
  public void initialise() {
    trip = mock(TripIfc.class);
    results = mock(PersonResults.class);
    SimulationPerson person = mock(SimulationPerson.class);
    decorator = new TripDecorator(trip, person);
  }
  
  @MethodSource("functions")
  @ParameterizedTest
  void getter(Function<TripIfc, ?> method) throws Exception {
    method.apply(decorator);

    method.apply(verify(trip));
  }

  static Stream<Function<TripIfc, ?>> functions() {
    return Stream
        .of(TripIfc::getOid, TripIfc::calculatePlannedEndDate, TripIfc::origin,
            TripIfc::destination, TripIfc::mode, TripIfc::startDate, TripIfc::plannedDuration,
            TripIfc::previousActivity, TripIfc::nextActivity, TripIfc::timeOfNextChange);
  }

  @Test
  void startTrip() throws Exception {
    ImpedanceIfc impedance = mock(ImpedanceIfc.class);
    Time currentTime = mock(Time.class);
    decorator.allocateVehicle(impedance, currentTime);
    
    verify(trip).allocateVehicle(impedance, currentTime);
  }
  
  @Test
  void finish() throws Exception {
    Time currentTime = mock(Time.class);
    decorator.finish(currentTime, results);
    
    verify(trip).finish(currentTime, results);
  }
}

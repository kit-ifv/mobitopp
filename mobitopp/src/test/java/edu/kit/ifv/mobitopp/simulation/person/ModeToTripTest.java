package edu.kit.ifv.mobitopp.simulation.person;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.function.BiFunction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.Trip;

public class ModeToTripTest {

  private TripData tripData;
  private SimulationPerson person;
  
  @BeforeEach
  public void initialise() {
    tripData = mock(TripData.class);
    person = mock(SimulationPerson.class);
  }

  @SuppressWarnings("unchecked")
  @Test
  void useDefault() throws Exception {
    BiFunction<TripData, SimulationPerson, Trip> defaultTrip = mock(BiFunction.class);
    ModeToTrip modeToTrip = new ModeToTrip(defaultTrip);
    
    modeToTrip.create(StandardMode.CAR, tripData, person);
    
    verify(defaultTrip).apply(tripData, person);
  }
  
  @SuppressWarnings("unchecked")
  @Test
  void createsTripForMatchingMode() throws Exception {
    BiFunction<TripData, SimulationPerson, Trip> defaultTrip = mock(BiFunction.class);
    BiFunction<TripData, SimulationPerson, Trip> carTrip = mock(BiFunction.class);
    BiFunction<TripData, SimulationPerson, Trip> passengerTrip = mock(BiFunction.class);
    ModeToTrip modeToTrip = new ModeToTrip(defaultTrip);
    modeToTrip.add(StandardMode.CAR, carTrip);
    modeToTrip.add(StandardMode.PASSENGER, passengerTrip);
    
    modeToTrip.create(StandardMode.CAR, tripData, person);
    
    verify(carTrip).apply(tripData, person);
    verifyZeroInteractions(carTrip);
    verifyZeroInteractions(passengerTrip);
  }
}

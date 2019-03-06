package edu.kit.ifv.mobitopp.simulation.person;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.function.BiFunction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.TripIfc;

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
    BiFunction<TripData, SimulationPerson, TripIfc> defaultTrip = mock(BiFunction.class);
    ModeToTrip modeToTrip = new ModeToTrip(defaultTrip);
    
    modeToTrip.create(Mode.CAR, tripData, person);
    
    verify(defaultTrip).apply(tripData, person);
  }
  
  @SuppressWarnings("unchecked")
  @Test
  void createsTripForMatchingMode() throws Exception {
    BiFunction<TripData, SimulationPerson, TripIfc> defaultTrip = mock(BiFunction.class);
    BiFunction<TripData, SimulationPerson, TripIfc> carTrip = mock(BiFunction.class);
    BiFunction<TripData, SimulationPerson, TripIfc> passengerTrip = mock(BiFunction.class);
    ModeToTrip modeToTrip = new ModeToTrip(defaultTrip);
    modeToTrip.add(Mode.CAR, carTrip);
    modeToTrip.add(Mode.PASSENGER, passengerTrip);
    
    modeToTrip.create(Mode.CAR, tripData, person);
    
    verify(carTrip).apply(tripData, person);
    verifyZeroInteractions(carTrip);
    verifyZeroInteractions(passengerTrip);
  }
}

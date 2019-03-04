package edu.kit.ifv.mobitopp.simulation.person;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.time.Time;

public class PrivateCarTripTest {

  private TripSetup setup;
  private ImpedanceIfc impedance;
  private SimulationPerson person;
  private Time currentTime;
  private PrivateCar car;
  private TripIfc trip;

  @BeforeEach
  public void initialise() {
    setup = TripSetup.create();
    impedance = setup.impedance;
    person = setup.person;
    car = setup.car;
    trip = setup.trip;
    currentTime = setup.currentTime;
  }

  @Test
  void startsTrip() throws Exception {
    setup.configureActivity(ActivityType.HOME);
    PrivateCarTrip carTrip = new PrivateCarTrip(trip, person);

    carTrip.startTrip(impedance, currentTime);

    verify(person).useCar(car, currentTime);
  }

  @Test
  void usesParkedCar() throws Exception {
    when(person.hasParkedCar()).thenReturn(true);
    when(person.isCarDriver()).thenReturn(false);
    setup.configureActivity(ActivityType.WORK);
    PrivateCarTrip carTrip = new PrivateCarTrip(trip, person);

    carTrip.startTrip(impedance, currentTime);

    verify(person).takeCarFromParking();
  }
}

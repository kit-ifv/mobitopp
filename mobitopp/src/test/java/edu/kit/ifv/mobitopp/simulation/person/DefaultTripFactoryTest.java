package edu.kit.ifv.mobitopp.simulation.person;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class DefaultTripFactoryTest {

  private static final int oid = 1;
  private static final Time startDate = someTime();
  private static final int plannedDuration = 1;
  private SimulationPerson person;
  private ImpedanceIfc impedance;
  private ActivityIfc previousActivity;
  private ActivityIfc nextActivity;
  private Zone zone;
  private Location originLocation;
  private Location destinationLocation;

  @BeforeEach
  public void initialise() {
    person = mock(SimulationPerson.class);
    impedance = mock(ImpedanceIfc.class);
    previousActivity = mock(ActivityIfc.class);
    nextActivity = mock(ActivityIfc.class);
    zone = mock(Zone.class);
    originLocation = someLocation();
    destinationLocation = otherLocation();
    
    when(previousActivity.zone()).thenReturn(zone);
    when(nextActivity.zone()).thenReturn(zone);
    when(previousActivity.calculatePlannedEndDate()).thenReturn(startDate);
    when(previousActivity.location()).thenReturn(originLocation);
    when(nextActivity.location()).thenReturn(destinationLocation);
    when(previousActivity.isLocationSet()).thenReturn(true);
    when(nextActivity.isLocationSet()).thenReturn(true);
  }
  
  @Test
  void createPassengerTrip() throws Exception {
    Mode mode = StandardMode.PASSENGER;
    use(mode);

    TripFactory factory = newTripFactory();
    Trip trip = factory.createTrip(person, impedance, mode, previousActivity, nextActivity);

    assertThat(trip, is(instanceOf(PassengerTrip.class)));
    verifyStandardAttributes(mode, trip);
  }

  private TripFactory newTripFactory() {
    return new DefaultTripFactory();
  }

  @Test
  public void createCarTrip() {
    Mode mode = StandardMode.CAR;
    use(mode);

    TripFactory factory = newTripFactory();
    Trip trip = factory.createTrip(person, impedance, mode, previousActivity, nextActivity);

    assertThat(trip, is(instanceOf(PrivateCarTrip.class)));
    verifyStandardAttributes(mode, trip);
  }

  @Test
  public void createCarSharingStationTrip() {
    Mode mode = StandardMode.CARSHARING_STATION;
    use(mode);

    TripFactory factory = newTripFactory();
    Trip trip = factory.createTrip(person, impedance, mode, previousActivity, nextActivity);

    assertThat(trip, is(instanceOf(CarSharingStationTrip.class)));
    verifyStandardAttributes(mode, trip);
  }
  
  @Test
  public void createCarSharingFreeFloatingTrip() {
    Mode mode = StandardMode.CARSHARING_FREE;
    use(mode);

    TripFactory factory = newTripFactory();
    Trip trip = factory.createTrip(person, impedance, mode, previousActivity, nextActivity);

    assertThat(trip, is(instanceOf(CarSharingFreeFloatingTrip.class)));
    verifyStandardAttributes(mode, trip);
  }

  @Test
  public void createPublicTransportTrip() {
    PublicTransportRoute route = mock(PublicTransportRoute.class);
    Mode mode = StandardMode.PUBLICTRANSPORT;
    use(mode);
    when(impedance.getPublicTransportRoute(originLocation, destinationLocation, mode, startDate))
        .thenReturn(Optional.of(route));
    when(route.duration()).thenReturn(RelativeTime.ofMinutes(plannedDuration));

    TripFactory factory = newTripFactory();
    Trip trip = factory.createTrip(person, impedance, mode, previousActivity, nextActivity);

    assertThat(trip, is(instanceOf(NoActionTrip.class)));
    verifyStandardAttributes(mode, trip);
  }

  private void verifyStandardAttributes(Mode mode, Trip trip) {
    assertEquals(oid, trip.getOid());
    assertEquals(previousActivity, trip.previousActivity());
    assertEquals(nextActivity, trip.nextActivity());
    assertEquals(mode, trip.mode());
    assertEquals(startDate, trip.startDate());
    assertEquals(plannedDuration, trip.plannedDuration());
  }

  private void use(Mode mode) {
    when(impedance.getTravelTime(zone.getId(), zone.getId(), mode, startDate))
        .thenReturn((float) plannedDuration);
  }

  private static Location someLocation() {
    return new Location(new Point2D.Double(0.0d, 0.0d), 0, 0.0);
  }

  private static Location otherLocation() {
    return new Location(new Point2D.Double(1.0d, 1.0d), 0, 0.0);
  }
}

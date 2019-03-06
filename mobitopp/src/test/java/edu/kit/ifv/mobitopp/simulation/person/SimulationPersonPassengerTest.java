package edu.kit.ifv.mobitopp.simulation.person;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.simulation.person.DummyStates.another;
import static edu.kit.ifv.mobitopp.simulation.person.DummyStates.some;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.ModifiableActivityScheduleWithState;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.PersonResults;
import edu.kit.ifv.mobitopp.simulation.ReschedulingStrategy;
import edu.kit.ifv.mobitopp.simulation.StateChange;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.time.Time;

public class SimulationPersonPassengerTest {

  private static final Employment someEmploymentType = Employment.FULLTIME;
  private static final ActivityType defaultActivity = ActivityType.HOME;
  private static final int seed = 0;

  @Rule
  public final TemporaryFolder baseFolder = new TemporaryFolder();
  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  private PublicTransportBehaviour boarder;
  private Person person;
  private SimulationOptions options;
  private ModifiableActivityScheduleWithState schedule;
  private ActivityIfc firstActivity;
  private DestinationChoiceModel destinationChoice;
  private Household household;
  private Zone zone;
  private ZoneRepository zoneRepository;
  private EventQueue queue;
  private TripData tripData;
  private TripIfc mockedTrip;
  private PublicTransportRoute route;
  private PublicTransportLeg singleLeg;
  private ImpedanceIfc impedance;
  private PersonId personId;
  private ReschedulingStrategy rescheduling;
  private PersonResults results;
  private TripFactory tripFactory;

  @Before
  public void initialise() throws Exception {
    createElements();
    initializeBehaviour();
  }

  private void createElements() throws IOException {
    boarder = mock(PublicTransportBehaviour.class);
    person = mock(Person.class);
    options = mock(SimulationOptions.class);
    schedule = mock(ModifiableActivityScheduleWithState.class);
    firstActivity = mock(ActivityIfc.class);
    destinationChoice = mock(DestinationChoiceModel.class);
    household = mock(Household.class);
    zone = mock(Zone.class);
    zoneRepository = mock(ZoneRepository.class);
    queue = mock(EventQueue.class);
    tripData = mock(TripData.class);
    mockedTrip = mock(TripIfc.class);
    route = mock(PublicTransportRoute.class);
    singleLeg = mock(PublicTransportLeg.class);
    impedance = mock(ImpedanceIfc.class);
    personId = mock(PersonId.class);
    rescheduling = mock(ReschedulingStrategy.class);
    results = mock(PersonResults.class);
    tripFactory = mock(TripFactory.class);
  }

  private PublicTransportTrip createPublicTransportTrip(SimulationPerson person) {
    PublicTransportTrip trip = new PublicTransportTrip(tripData, person, boarder,
        Optional.of(route), asList(singleLeg));
    when(person.currentTrip()).thenReturn(trip);
    when(schedule.currentTrip()).thenReturn(trip);
    return trip;
  }

  private void initializeBehaviour() {
    when(person.activitySchedule()).thenReturn(schedule);
    when(schedule.firstActivity()).thenReturn(firstActivity);
    when(schedule.prevActivity(firstActivity)).thenReturn(firstActivity);
    when(options.destinationChoiceModel()).thenReturn(destinationChoice);
    when(firstActivity.activityType()).thenReturn(defaultActivity);
    when(person.household()).thenReturn(household);
    when(destinationChoice
        .selectDestination(eq(person), any(), eq(firstActivity), eq(firstActivity), anyDouble()))
            .thenReturn(zone);
    when(zoneRepository.getZoneByOid(anyInt())).thenReturn(zone);
    when(firstActivity.startDate()).thenReturn(someTime());
    when(firstActivity.location()).thenReturn(someLocation());
    when(firstActivity.zone()).thenReturn(someZone());
    when(firstActivity.isLocationSet()).thenReturn(true);
    when(person.currentActivity()).thenReturn(firstActivity);
    when(mockedTrip.startDate()).thenReturn(someTime());
    when(mockedTrip.calculatePlannedEndDate()).thenReturn(someTime());
    when(tripData.startDate()).thenReturn(someTime());
    when(tripData.calculatePlannedEndDate()).thenReturn(someTime());
    when(options.simulationStart()).thenReturn(someTime());
    when(options.simulationEnd()).thenReturn(someTime());
    when(schedule.nextActivity(firstActivity)).thenReturn(firstActivity);
    when(mockedTrip.previousActivity()).thenReturn(firstActivity);
    when(mockedTrip.nextActivity()).thenReturn(firstActivity);
    when(tripData.previousActivity()).thenReturn(firstActivity);
    when(tripData.nextActivity()).thenReturn(firstActivity);
    when(options.impedance()).thenReturn(impedance);
    when(household.homeZone()).thenReturn(zone);
    when(household.homeLocation()).thenReturn(someLocation());
    when(tripData.mode()).thenReturn(Mode.PUBLICTRANSPORT);
    when(person.employment()).thenReturn(someEmploymentType);
    when(person.getId()).thenReturn(personId);
    when(firstActivity.calculatePlannedEndDate()).thenReturn(someTime());
    when(options.rescheduling()).thenReturn(rescheduling);
    when(person.gender()).thenReturn(Gender.MALE);
    when(mockedTrip.origin()).thenReturn(someZoneAndLocation());
    when(mockedTrip.destination()).thenReturn(someZoneAndLocation());
  }

  private static Location someLocation() {
    return new Location(new Point2D.Double(0.0d, 0.0d), 0, 0.0);
  }

  private static Location otherLocation() {
    return new Location(new Point2D.Double(1.0d, 1.0d), 0, 0.0);
  }

  private static Zone someZone() {
    return mock(Zone.class);
  }

  private static ZoneAndLocation someZoneAndLocation() {
    return new ZoneAndLocation(someZone(), someLocation());
  }
  
  private void configurePublicTransportTrip(SimulationPerson person) {
    PublicTransportTrip trip = createPublicTransportTrip(person);
    when(tripFactory
        .createTrip(person, impedance, Mode.PUBLICTRANSPORT, firstActivity, firstActivity))
            .thenReturn(trip);
  }

  @Test
  public void boardsAndGetsOffTheFirstVehicleOnItsTrip() throws Exception {
    SimulationPerson simulationPerson = newPerson();
    TripIfc trip = createPublicTransportTrip(simulationPerson);

    simulationPerson.boardPublicTransportVehicle(someTime());
    simulationPerson.getOffPublicTransportVehicle(someTime());

    verify(boarder).board(simulationPerson, someTime(), singleLeg, trip);
    verify(boarder).getOff(simulationPerson, someTime(), singleLeg, trip);
  }

  @Test
  public void hasArrivedAtNextActivityWhenNoPartsAreLeft() throws Exception {
    SimulationPerson simulationPerson = newPerson();
    configurePublicTransportTrip(simulationPerson);

    assertThat(simulationPerson.hasArrivedAtNextActivity(), is(false));

    simulationPerson.boardPublicTransportVehicle(someTime());
    simulationPerson.getOffPublicTransportVehicle(someTime());

    assertThat(simulationPerson.hasArrivedAtNextActivity(), is(true));
  }

  @Test
  public void allowsVehicleBoardingWhenVehicleIsAvailableAtCurrentStop() throws Exception {
    boolean available = true;
    when(boarder.isVehicleAvailable(singleLeg)).thenReturn(available);
    SimulationPerson simulationPerson = newPerson();
    configurePublicTransportTrip(simulationPerson);

    assertThat(simulationPerson.isPublicTransportVehicleAvailable(someTime()), is(available));

    verify(boarder).isVehicleAvailable(singleLeg);
  }

  @Test
  public void doesNotAllowVehicleBoardingWhenVehicleIsNotAvailableAtCurrentStop() throws Exception {
    boolean notAvailable = false;
    when(boarder.isVehicleAvailable(singleLeg)).thenReturn(notAvailable);
    SimulationPerson simulationPerson = newPerson();
    configurePublicTransportTrip(simulationPerson);

    assertThat(simulationPerson.isPublicTransportVehicleAvailable(someTime()), is(notAvailable));

    verify(boarder).isVehicleAvailable(singleLeg);
  }

  @Test
  public void failToEnterFirstStopWithoutPublicTransportTrip() {
    when(person.currentTrip()).thenReturn(mockedTrip);
    when(mockedTrip.mode()).thenReturn(Mode.CAR);
    SimulationPersonPassenger simulationPerson = newPerson();

    thrown.expect(IllegalArgumentException.class);
    simulationPerson.enterFirstStop(someTime());
  }

  @Test
  public void entersWaitingAreaAtStartStop() throws Exception {
    when(singleLeg.start()).thenReturn(someStop());
    SimulationPerson simulationPerson = newPerson();
    configurePublicTransportTrip(simulationPerson);

    simulationPerson.enterFirstStop(someTime());

    verify(boarder).enterWaitingArea(simulationPerson, someStop());
  }

  @Test
  public void leavesWaitingAreaAtEndStop() throws Exception {
    when(singleLeg.end()).thenReturn(anotherStop());
    SimulationPerson simulationPerson = newPerson();
    configurePublicTransportTrip(simulationPerson);

    simulationPerson.endTrip(impedance, rescheduling, someTime());

    verify(boarder).leaveWaitingArea(simulationPerson, anotherStop());
  }

  @Test
  public void canBoardVehicleWhenVehicleIsNotFull() throws Exception {
    boolean placeAvailable = true;
    when(boarder.hasPlaceInVehicle(singleLeg)).thenReturn(placeAvailable);
    SimulationPerson person = newPerson();
    configurePublicTransportTrip(person);

    assertThat(person.hasPlaceInPublicTransportVehicle(), is(true));
    verify(boarder).hasPlaceInVehicle(singleLeg);
    verifyZeroInteractions(singleLeg);
  }

  @Test
  public void canNotBoardVehicleWhenVehicleIsFull() throws Exception {
    boolean placeUnavailable = false;
    when(boarder.hasPlaceInVehicle(singleLeg)).thenReturn(placeUnavailable);
    SimulationPerson person = newPerson();
    configurePublicTransportTrip(person);

    assertThat(person.hasPlaceInPublicTransportVehicle(), is(false));
    verify(boarder).hasPlaceInVehicle(singleLeg);
    verifyZeroInteractions(singleLeg);
  }

  @Test
  public void searchesANewTripWhenAsked() throws Exception {
    SimulationPerson person = newPerson();
    PublicTransportTrip trip = createPublicTransportTrip(person);
    TripIfc newTrip = mock(TripIfc.class);
    when(boarder.searchNewTrip(person, someTime(), trip)).thenReturn(newTrip);

    person.changeToNewTrip(someTime());

    verify(boarder).searchNewTrip(person, someTime(), trip);
    verify(this.person).currentTrip(newTrip);
  }

  @Test
  public void notifiesAboutStateChange() {
    SimulationPersonPassenger simulatedPerson = newPerson();
    DemandSimulationEventIfc event = mock(DemandSimulationEventIfc.class);
    when(event.getPerson()).thenReturn(simulatedPerson);
    when(event.getSimulationDate()).thenReturn(oneMinuteLater());

    simulatedPerson.notify(queue, event, oneMinuteLater());

    verify(results).notifyStateChanged(new StateChange(simulatedPerson, someTime(), some, another));
    verify(results)
        .notifyStateChanged(new StateChange(simulatedPerson, oneMinuteLater(), another, some));
  }

  @Test
  public void createTrip() {
    SimulationPersonPassenger person = newPerson();
    Mode mode = Mode.CAR;
    Time startDate = someTime();
    float plannedDuration = 1.0f;
    ActivityIfc nextActivity = mock(ActivityIfc.class);
    when(nextActivity.zone()).thenReturn(zone);
    Location destinationLocation = otherLocation();
    when(nextActivity.location()).thenReturn(destinationLocation);
    when(nextActivity.isLocationSet()).thenReturn(true);
    when(impedance.getTravelTime(zone, zone, mode, startDate)).thenReturn(plannedDuration);
    when(tripFactory.createTrip(person, impedance, mode, firstActivity, nextActivity))
        .thenReturn(mockedTrip);

    TripIfc trip = person.createTrip(impedance, mode, firstActivity, nextActivity);

    assertEquals(mockedTrip, trip);
    
    verify(tripFactory).createTrip(person, impedance, mode, firstActivity, nextActivity);
  }
  
  @Test
  public void endTrip() {
    Time currentDate = Time.start;
    FinishedTrip finishedTrip = mock(FinishedTrip.class);
    SimulationPersonPassenger person = newPerson();
    when(person.currentTrip()).thenReturn(mockedTrip);
    when(mockedTrip.mode()).thenReturn(Mode.CAR);
    when(mockedTrip.finish(currentDate, results)).thenReturn(finishedTrip);
    
    person.endTrip(impedance, rescheduling, currentDate);
    
    verify(mockedTrip).finish(currentDate, results);
    verify(results).notifyEndTrip(this.person, finishedTrip, firstActivity);
  }
  
  @Test
  public void changeStartOfPassengerTrip() {
    Time newTime = Data.oneMinuteLater();
    TripIfc changedTrip = newPerson().changeStartTimeOfTrip(mockedTrip, newTime);
    
    assertThat(changedTrip.startDate(), is(equalTo(newTime)));
  }
  
  private SimulationPersonPassenger newPerson() {
    PersonState initialState = DummyStates.some;
    return new SimulationPersonPassenger(person, zoneRepository, queue, options, null, null, null,
        tripFactory, initialState, boarder, seed, results) {

      private static final long serialVersionUID = 1L;

      @Override
      public boolean hasNextActivity() {
        return true;
      }
    };
  }
}

package edu.kit.ifv.mobitopp.simulation.person;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.simulation.person.DummyStates.another;
import static edu.kit.ifv.mobitopp.simulation.person.DummyStates.some;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
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
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import edu.kit.ifv.mobitopp.data.MaasDataForZone;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Car;
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
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;

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
	private TripIfc mockedTrip;
	private PublicTransportRoute route;
	private PublicTransportLeg singleLeg;
	private PublicTransportTrip trip;
	private ImpedanceIfc impedance;
	private PersonId personId;
	private ReschedulingStrategy rescheduling;
	private PersonResults results;

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
		mockedTrip = mock(TripIfc.class);
		route = mock(PublicTransportRoute.class);
		singleLeg = mock(PublicTransportLeg.class);
		List<PublicTransportLeg> legs = asList(singleLeg);
		trip = new PublicTransportTrip(mockedTrip, Optional.of(route), legs);
		impedance = mock(ImpedanceIfc.class);
		personId = mock(PersonId.class);
		rescheduling = mock(ReschedulingStrategy.class);
		results = mock(PersonResults.class);
	}

	private void initializeBehaviour() {
		when(person.activitySchedule()).thenReturn(schedule);
		when(schedule.firstActivity()).thenReturn(firstActivity);
		when(schedule.prevActivity(firstActivity)).thenReturn(firstActivity);
		when(options.destinationChoiceModel()).thenReturn(destinationChoice);
		when(firstActivity.activityType()).thenReturn(defaultActivity);
		when(person.household()).thenReturn(household);
		when(destinationChoice.selectDestination(eq(person), any(), eq(firstActivity), eq(firstActivity),
				anyDouble())).thenReturn(zone);
		when(zoneRepository.getZoneByOid(anyInt())).thenReturn(zone);
		when(firstActivity.startDate()).thenReturn(someTime());
		when(firstActivity.location()).thenReturn(someLocation());
		when(firstActivity.zone()).thenReturn(someZone());
		when(person.currentActivity()).thenReturn(firstActivity);
		when(mockedTrip.startDate()).thenReturn(someTime());
		when(mockedTrip.calculatePlannedEndDate()).thenReturn(someTime());
		when(person.currentTrip()).thenReturn(trip);
		when(options.simulationStart()).thenReturn(someTime());
		when(options.simulationEnd()).thenReturn(someTime());
		when(schedule.nextActivity(firstActivity)).thenReturn(firstActivity);
		when(schedule.currentTrip()).thenReturn(trip);
		when(mockedTrip.previousActivity()).thenReturn(firstActivity);
		when(mockedTrip.nextActivity()).thenReturn(firstActivity);
		when(options.impedance()).thenReturn(impedance);
		when(household.homeZone()).thenReturn(zone);
		when(household.homeLocation()).thenReturn(someLocation());
		when(mockedTrip.mode()).thenReturn(Mode.PUBLICTRANSPORT);
		when(person.employment()).thenReturn(someEmploymentType);
		when(person.getId()).thenReturn(personId);
		when(firstActivity.calculatePlannedEndDate()).thenReturn(someTime());
		when(options.rescheduling()).thenReturn(rescheduling);
		when(person.gender()).thenReturn(Gender.MALE);
		when(mockedTrip.origin()).thenReturn(someZoneAndLocation());
		when(mockedTrip.destination()).thenReturn(someZoneAndLocation());
	}

	private static Location someLocation() {
		return new Location(new Point2D.Double(), 0, 0.0);
	}
	
	private static Zone someZone() {
		return mock(Zone.class);
	}

	private static ZoneAndLocation someZoneAndLocation() {
		return new ZoneAndLocation(someZone(), someLocation());
	}

	@Test
	public void boardsAndGetsOffTheFirstVehicleOnItsTrip() throws Exception {
		SimulationPerson simulationPerson = person();

		simulationPerson.boardPublicTransportVehicle(someTime());
		simulationPerson.getOffPublicTransportVehicle(someTime());

		verify(boarder).board(simulationPerson, someTime(), singleLeg, trip);
		verify(boarder).getOff(simulationPerson, someTime(), singleLeg, trip);
	}

	@Test
	public void hasArrivedAtNextActivityWhenNoPartsAreLeft() throws Exception {
		SimulationPerson simulationPerson = person();

		assertThat(simulationPerson.hasArrivedAtNextActivity(), is(false));

		simulationPerson.boardPublicTransportVehicle(someTime());
		simulationPerson.getOffPublicTransportVehicle(someTime());

		assertThat(simulationPerson.hasArrivedAtNextActivity(), is(true));
	}

	@Test
	public void allowsVehicleBoardingWhenVehicleIsAvailableAtCurrentStop() throws Exception {
		boolean available = true;
		when(boarder.isVehicleAvailable(singleLeg)).thenReturn(available);
		SimulationPerson simulationPerson = person();

		assertThat(simulationPerson.isPublicTransportVehicleAvailable(someTime()), is(available));

		verify(boarder).isVehicleAvailable(singleLeg);
	}

	@Test
	public void doesNotAllowVehicleBoardingWhenVehicleIsNotAvailableAtCurrentStop() throws Exception {
		boolean notAvailable = false;
		when(boarder.isVehicleAvailable(singleLeg)).thenReturn(notAvailable);
		SimulationPerson simulationPerson = person();

		assertThat(simulationPerson.isPublicTransportVehicleAvailable(someTime()), is(notAvailable));

		verify(boarder).isVehicleAvailable(singleLeg);
	}

	@Test
	public void failToEnterFirstStopWithoutPublicTransportTrip() {
		when(person.currentTrip()).thenReturn(mockedTrip);
		when(mockedTrip.mode()).thenReturn(Mode.CAR);
		SimulationPersonPassenger simulationPerson = person();
		
		thrown.expect(IllegalArgumentException.class);
		simulationPerson.enterFirstStop(someTime());
	}

	@Test
	public void entersWaitingAreaAtStartStop() throws Exception {
		when(singleLeg.start()).thenReturn(someStop());
		SimulationPerson simulationPerson = person();

		simulationPerson.enterFirstStop(someTime());

		verify(boarder).enterWaitingArea(simulationPerson, someStop());
	}

	@Test
	public void leavesWaitingAreaAtEndStop() throws Exception {
		when(singleLeg.end()).thenReturn(anotherStop());
		SimulationPerson simulationPerson = person();

		simulationPerson.endTrip(impedance, rescheduling, someTime());

		verify(boarder).leaveWaitingArea(simulationPerson, anotherStop());
	}

	@Test
	public void canBoardVehicleWhenVehicleIsNotFull() throws Exception {
		boolean placeAvailable = true;
		when(boarder.hasPlaceInVehicle(singleLeg)).thenReturn(placeAvailable);
		SimulationPerson person = person();

		assertThat(person.hasPlaceInPublicTransportVehicle(), is(true));
		verify(boarder).hasPlaceInVehicle(singleLeg);
		verifyZeroInteractions(singleLeg);
	}

	@Test
	public void canNotBoardVehicleWhenVehicleIsFull() throws Exception {
		boolean placeUnavailable = false;
		when(boarder.hasPlaceInVehicle(singleLeg)).thenReturn(placeUnavailable);
		SimulationPerson person = person();

		assertThat(person.hasPlaceInPublicTransportVehicle(), is(false));
		verify(boarder).hasPlaceInVehicle(singleLeg);
		verifyZeroInteractions(singleLeg);
	}

	@Test
	public void searchesANewTripWhenAsked() throws Exception {
		SimulationPerson person = person();
		TripIfc newTrip = mock(TripIfc.class);
		when(boarder.searchNewTrip(person, someTime(), trip)).thenReturn(newTrip);

		person.changeToNewTrip(someTime());

		verify(boarder).searchNewTrip(person, someTime(), trip);
		verify(this.person).currentTrip(newTrip);
	}

	@Test
	public void notifiesAboutStateChange() {
		SimulationPersonPassenger simulatedPerson = person();
		DemandSimulationEventIfc event = mock(DemandSimulationEventIfc.class);
		when(event.getPerson()).thenReturn(simulatedPerson);
		when(event.getSimulationDate()).thenReturn(oneMinuteLater());
		
		simulatedPerson.notify(queue, event, oneMinuteLater());
		
		verify(results).notifyStateChanged(new StateChange(simulatedPerson, someTime(), some, another));
		verify(results)
				.notifyStateChanged(new StateChange(simulatedPerson, oneMinuteLater(), another, some));
	}
	
	@Test
  public void returnOwnCar() {
    SimulationPersonPassenger person = person();
    Car car = mock(Car.class);
    when(mockedTrip.mode()).thenReturn(Mode.CAR);
    when(firstActivity.zone()).thenReturn(zone);
    when(firstActivity.activityType()).thenReturn(ActivityType.HOME);
    when(this.person.releaseCar(someTime())).thenReturn(car);
    
    person.asDriverReturnOrParkCar(mockedTrip, firstActivity, someTime());
    
    verify(car).returnCar(zone);
  }
	
	@Test
	public void returnStationBasedCarSharingCar() {
	  SimulationPersonPassenger person = person();
	  Car car = mock(Car.class);
	  when(mockedTrip.mode()).thenReturn(Mode.CARSHARING_STATION);
	  when(firstActivity.zone()).thenReturn(zone);
	  when(firstActivity.activityType()).thenReturn(ActivityType.HOME);
	  when(this.person.releaseCar(someTime())).thenReturn(car);
	  
	  person.asDriverReturnOrParkCar(mockedTrip, firstActivity, someTime());
	  
	  verify(car).returnCar(zone);
	}
	
	@Test
	public void returnMaasCar() {
	  SimulationPersonPassenger person = person();
	  Car car = mock(Car.class);
	  MaasDataForZone maasData = mock(MaasDataForZone.class);
	  when(mockedTrip.mode()).thenReturn(Mode.AUTONOMOUS_TAXI);
	  when(firstActivity.zone()).thenReturn(zone);
	  when(firstActivity.activityType()).thenReturn(ActivityType.HOME);
    when(zone.maas()).thenReturn(maasData);
    when(maasData.isInMaasZone(car)).thenReturn(true);
    when(this.person.isCarDriver()).thenReturn(true);
    when(this.person.whichCar()).thenReturn(car);
	  when(this.person.releaseCar(someTime())).thenReturn(car);
	  
	  person.asDriverReturnOrParkCar(mockedTrip, firstActivity, someTime());
	  
	  verify(car).returnCar(zone);
	}
	
	
	private SimulationPersonPassenger person() {
		PersonState initialState = DummyStates.some;
		return new SimulationPersonPassenger(person, zoneRepository, queue, options, null, null,
				null, initialState, boarder, seed, results) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean hasNextActivity() {
				return true;
			}
		};
	}
}

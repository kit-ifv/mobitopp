package edu.kit.ifv.mobitopp.simulation.person;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.simulation.person.PersonStatePublicTransport.EXECUTE_ACTIVITY;
import static edu.kit.ifv.mobitopp.simulation.person.PersonStatePublicTransport.FINISHED;
import static edu.kit.ifv.mobitopp.simulation.person.PersonStatePublicTransport.FINISH_PUBLIC_TRANSPORT;
import static edu.kit.ifv.mobitopp.simulation.person.PersonStatePublicTransport.MAKE_TRIP;
import static edu.kit.ifv.mobitopp.simulation.person.PersonStatePublicTransport.RIDE_VEHICLE;
import static edu.kit.ifv.mobitopp.simulation.person.PersonStatePublicTransport.SEARCH_VEHICLE;
import static edu.kit.ifv.mobitopp.simulation.person.PersonStatePublicTransport.SELECT_MODE;
import static edu.kit.ifv.mobitopp.simulation.person.PersonStatePublicTransport.TRY_BOARDING;
import static edu.kit.ifv.mobitopp.simulation.person.PersonStatePublicTransport.UNINITIALIZED;
import static edu.kit.ifv.mobitopp.simulation.person.PersonStatePublicTransport.USE_OTHER_MODE;
import static edu.kit.ifv.mobitopp.simulation.person.PersonStatePublicTransport.USE_PUBLIC_TRANSPORT;
import static edu.kit.ifv.mobitopp.simulation.person.PersonStatePublicTransport.WAIT_FOR_VEHICLE;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.FootJourney;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.activityschedule.Activity;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.simulation.events.Event;
import edu.kit.ifv.mobitopp.time.Time;

public class PersonStatePublicTransportTest {

	private SimulationPerson person;
	private SimulationOptions options;
	private Trip trip;
	private Optional<PublicTransportRoute> existingRoute;
	private Optional<PublicTransportRoute> noRoute;
  private PublicTransportBehaviour publicTransportBehaviour;

	@Before
	public void initialise() throws Exception {
		person = mock(SimulationPerson.class);
		options = mock(SimulationOptions.class);
		publicTransportBehaviour = mock(PublicTransportBehaviour.class);
		trip = mock(Trip.class);
		PublicTransportRoute route = mock(PublicTransportRoute.class);
		existingRoute = Optional.of(route);
		noRoute = Optional.empty();
	}

	@Test
	public void switchesFromUninitializedToExecuteAcitivity() throws Exception {
		PersonState nextState = UNINITIALIZED.nextState(person, someDate());

		assertThat(nextState, is(EXECUTE_ACTIVITY));
	}

	@Test
	public void switchesToNothingFromFinished() throws Exception {
		try {
			FINISHED.nextState(person, someDate());
			fail("Should throw an AssertionError");
		} catch (AssertionError error) {
		}
	}

	@Test
	public void selectsModeAfterActivityIsFinished() throws Exception {
		PersonState nextState = EXECUTE_ACTIVITY.nextState(person, someDate());

		assertThat(nextState, is(SELECT_MODE));
	}

	@Test
	public void driveCarAfterModeSelectionWhenModeIsCar() throws Exception {
		when(trip.mode()).thenReturn(Mode.CAR);
		when(person.currentTrip()).thenReturn(trip);

		PersonState nextState = SELECT_MODE.nextState(person, someDate());

		assertThat(nextState, is(USE_OTHER_MODE));
	}

	@Test
	public void usePublicTransportAfterModeSelectionWhenModeIsPublicTransport() throws Exception {
		when(trip.mode()).thenReturn(Mode.PUBLICTRANSPORT);
		when(person.currentTrip()).thenReturn(trip);

		PersonState nextState = SELECT_MODE.nextState(person, someDate());

		assertThat(nextState, is(USE_PUBLIC_TRANSPORT));
	}

	@Test
	public void switchesFromUsePublicTransportToSearchVehicle() throws Exception {
		PersonState nextState = USE_PUBLIC_TRANSPORT.nextState(person, someDate());

		assertThat(nextState, is(SEARCH_VEHICLE));
	}

	@Test
	public void switchesFromDriveCarToMakeTrip() throws Exception {
		PersonState nextState = USE_OTHER_MODE.nextState(person, someDate());

		assertThat(nextState, is(MAKE_TRIP));
	}

	@Test
	public void switchesFromFinishPublicTransportToExecuteActivityWhenThereIsAtLeastOneActivity()
			throws Exception {
		when(person.hasNextActivity()).thenReturn(true);

		PersonState nextState = FINISH_PUBLIC_TRANSPORT.nextState(person, someDate());

		assertThat(nextState, is(EXECUTE_ACTIVITY));
	}

	@Test
	public void switchesFromFinishPublicTransportToFinishedWhenThereIsNoActivityLeft()
			throws Exception {
		when(person.hasNextActivity()).thenReturn(false);

		PersonState nextState = FINISH_PUBLIC_TRANSPORT.nextState(person, someDate());

		assertThat(nextState, is(FINISHED));
	}

	@Test
	public void switchesFromRideVehicleToFinishPublicTransportWhenPersonIsAtTheEndOfTheTrip()
			throws Exception {
		when(person.hasArrivedAtNextActivity()).thenReturn(true);

		PersonState nextState = RIDE_VEHICLE.nextState(person, someDate());

		assertThat(nextState, is(FINISH_PUBLIC_TRANSPORT));
	}

	@Test
	public void switchesFromRideVehicleToSearchVehicleWhenThePersonIsNotAtTheEndOfTheTrip()
			throws Exception {
		when(person.hasArrivedAtNextActivity()).thenReturn(false);

		PersonState nextState = RIDE_VEHICLE.nextState(person, someDate());

		assertThat(nextState, is(SEARCH_VEHICLE));
	}

	@Test
	public void switchesFromWaitForVehicleToSearchVehicle() throws Exception {
		PersonState nextState = WAIT_FOR_VEHICLE.nextState(person, someDate());

		assertThat(nextState, is(SEARCH_VEHICLE));
	}

	@Test
	public void triesToEnterVehicleWhenVehicleIsAvailable() throws Exception {
		boolean ableToBoard = true;
		when(person.isPublicTransportVehicleAvailable(someDate())).thenReturn(ableToBoard);

		PersonState nextState = SEARCH_VEHICLE.nextState(person, someDate());

		assertThat(nextState, is(TRY_BOARDING));
	}

	@Test
	public void ridesVehicleWhenPlaceIsAvailableInVehicle() throws Exception {
		boolean placeAvailable = true;
		when(person.hasPlaceInPublicTransportVehicle()).thenReturn(placeAvailable);

		PersonState nextState = TRY_BOARDING.nextState(person, someDate());

		assertThat(nextState, is(RIDE_VEHICLE));
	}

	@Test
	public void switchesFromTryBoardingToSearchVehicle() throws Exception {
		boolean ableToBoard = false;
		when(person.hasPlaceInPublicTransportVehicle()).thenReturn(ableToBoard);

		PersonState nextState = TRY_BOARDING.nextState(person, someDate());

		assertThat(nextState, is(SEARCH_VEHICLE));
	}

	@Test
	public void switchesFromSearchVehicleToWaitForVehicleWhenNoVehicleIsAvailableAtCurrentStop()
			throws Exception {
		boolean notAbleToBoard = false;
		boolean notYetDeparted = false;
		when(person.hasPublicTransportVehicleDeparted(someDate())).thenReturn(notYetDeparted);
		when(person.isPublicTransportVehicleAvailable(someDate())).thenReturn(notAbleToBoard);

		PersonState nextState = SEARCH_VEHICLE.nextState(person, someDate());

		assertThat(nextState, is(WAIT_FOR_VEHICLE));
	}

	@Test
	public void switchesFromSearchVehicleToSearchVehicleWhenVehicleHasAlreadyDepartedAtTheStop() {
		boolean vehicleAlreadyDeparted = true;
		when(person.hasPublicTransportVehicleDeparted(someDate())).thenReturn(vehicleAlreadyDeparted);

		PersonState nextState = SEARCH_VEHICLE.nextState(person, someDate());

		assertThat(nextState, is(SEARCH_VEHICLE));
	}

	@Test
	public void switchesFromMakeTripToExecuteActivityWhenThereIsAtLeastOneActivity()
			throws Exception {
		when(person.hasNextActivity()).thenReturn(true);

		PersonState nextState = MAKE_TRIP.nextState(person, someDate());

		assertThat(nextState, is(EXECUTE_ACTIVITY));
	}

	@Test
	public void switchesFromMakeTripToFinishedWhenThereIsNoActivityLeft() throws Exception {
		when(person.hasNextActivity()).thenReturn(false);

		PersonState nextState = MAKE_TRIP.nextState(person, someDate());

		assertThat(nextState, is(FINISHED));
	}

	@Test
	public void isInstantanous() throws Exception {
		assertThat(FINISHED, isNotInstantaneous());
		assertThat(MAKE_TRIP, isNotInstantaneous());
		assertThat(EXECUTE_ACTIVITY, isNotInstantaneous());
		assertThat(SELECT_MODE, isInstantaneous());
		assertThat(USE_OTHER_MODE, isInstantaneous());
		assertThat(USE_PUBLIC_TRANSPORT, isNotInstantaneous());
		assertThat(SEARCH_VEHICLE, isInstantaneous());
		assertThat(TRY_BOARDING, isInstantaneous());
		assertThat(RIDE_VEHICLE, isNotInstantaneous());
		assertThat(WAIT_FOR_VEHICLE, isNotInstantaneous());
		assertThat(FINISH_PUBLIC_TRANSPORT, isNotInstantaneous());
		assertThat(UNINITIALIZED, isNotInstantaneous());
	}

	@Test
	public void startsNoActionInFinishedState() throws Exception {
		FINISHED.doActionAtStart(person, someDate());

		verifyZeroInteractions(person);
	}

	@Test
	public void startsNoActionInUninitializedState() throws Exception {
		UNINITIALIZED.doActionAtStart(person, someDate());

		verifyZeroInteractions(person);
	}

	@Test
	public void selectsRouteAndStartsTripInMakeTripState() throws Exception {
		optionsAreNeeded();

		MAKE_TRIP.doActionAtStart(person, someDate());

		verify(person).selectRoute(any(), any(), eq(someDate()));
		verify(person).startTrip(any(), any(), eq(someDate()));
	}

	private void optionsAreNeeded() {
		when(person.options()).thenReturn(options);
	}

	@Test
	public void selectsRouteAndStartsTripInUsePublicTransportState() throws Exception {
		optionsAreNeeded();

		USE_PUBLIC_TRANSPORT.doActionAtStart(person, someDate());

		verify(person).selectRoute(any(), any(), eq(someDate()));
		verify(person).startTrip(any(), any(), eq(someDate()));
	}

	@Test
	public void entersWaitingAreaAfterAccessPath() {
		USE_PUBLIC_TRANSPORT.doActionAtEnd(person, someDate());
		
		verify(person).enterFirstStop(eq(someDate()));
	}

	@Test
	public void doesNothingWhenPersonHasPlaceInVehicle() throws Exception {
		boolean hasPlace = true;
		when(person.hasPlaceInPublicTransportVehicle()).thenReturn(hasPlace);

		TRY_BOARDING.doActionAtStart(person, someDate());

		verify(person).hasPlaceInPublicTransportVehicle();
		verifyNoMoreInteractions(person);
	}

	@Test
	public void searchesNewTripWhenVehicleIsFull() throws Exception {
		boolean vehicleFull = false;
		when(person.hasPlaceInPublicTransportVehicle()).thenReturn(vehicleFull);

		TRY_BOARDING.doActionAtStart(person, someDate());

		verify(person).hasPlaceInPublicTransportVehicle();
		verify(person).changeToNewTrip(someDate());
	}

	@Test
	public void searchesNewTripWhenVehicleAlreadyDeparted() throws Exception {
		boolean vehicleDeparted = true;
		when(person.hasPublicTransportVehicleDeparted(someDate())).thenReturn(vehicleDeparted);
		
		SEARCH_VEHICLE.doActionAtStart(person, someDate());
		
		verify(person).hasPublicTransportVehicleDeparted(someDate());
		verify(person).changeToNewTrip(someDate());
	}

	@Test
	public void selectsRouteAndStartsTripInRideVehicleState() throws Exception {
		RIDE_VEHICLE.doActionAtStart(person, someDate());

		verify(person).boardPublicTransportVehicle(someDate());
	}

	@Test
	public void waitsAtStartOfWaitingState() throws Exception {
		WAIT_FOR_VEHICLE.doActionAtStart(person, someDate());

		verify(person).wait(someDate());
	}

	@Test
	public void startsActivityInExecuteActivityState() throws Exception {
		optionsAreNeeded();

		EXECUTE_ACTIVITY.doActionAtStart(person, someDate());

		verify(person).startActivity(any(), any(), eq(someDate()));
	}

	@Test
	public void selectsDestinationAndModeDuringModeSelection() throws Exception {
		optionsAreNeeded();

		SELECT_MODE.doActionAtStart(person, someDate());

		verify(person).selectDestinationAndMode(any(), any(), any(), anyBoolean());
	}

	@Test
	public void allocatesCarAtStartOfDriveCarState() throws Exception {
		optionsAreNeeded();
		when(person.currentTrip()).thenReturn(trip);

		USE_OTHER_MODE.doActionAtStart(person, someDate());

		verify(person).allocateCar(any(), any(), any());
	}

	@Test
	public void doesNothingAfterFinishedState() throws Exception {
		FINISHED.doActionAtEnd(person, someDate());

		verifyZeroInteractions(person);
	}

	@Test
	public void doesNothingAfterUninitializedState() throws Exception {
		UNINITIALIZED.doActionAtEnd(person, someDate());

		verifyZeroInteractions(person);
	}

	@Test
	public void endsTripAfterMakeTripState() throws Exception {
		optionsAreNeeded();

		MAKE_TRIP.doActionAtEnd(person, someDate());

		verify(person).endTrip(any(), any(), eq(someDate()));
	}

	@Test
	public void endsTripAfterFinishPublicTransportState() throws Exception {
		optionsAreNeeded();

		FINISH_PUBLIC_TRANSPORT.doActionAtEnd(person, someDate());

		verify(person).endTrip(any(), any(), eq(someDate()));
	}

	@Test
	public void getOffTheVehicleAfterRideVehicleState() throws Exception {
		RIDE_VEHICLE.doActionAtEnd(person, someDate());

		verify(person).getOffPublicTransportVehicle(someDate());
	}

	@Test
	public void endsActivityAfterExecuteActivityState() throws Exception {
		EXECUTE_ACTIVITY.doActionAtEnd(person, someDate());

		verify(person).endActivity();
	}

	@Test
	public void doesNotCreateEventInFinishedState() throws Exception {
		Optional<DemandSimulationEventIfc> nextEvent = FINISHED.nextEvent(person, someDate());
		
		assertThat(nextEvent, isEmpty());
	}

	@Test
	public void doesNotCreateEventInUninitializedState() throws Exception {
		Optional<DemandSimulationEventIfc> nextEvent = UNINITIALIZED.nextEvent(person, someDate());
		assertThat(nextEvent, isEmpty());
	}

	@Test
	public void createsTripEndingEventInMakeTripState() throws Exception {
		when(person.currentTrip()).thenReturn(trip);
		when(trip.calculatePlannedEndDate()).thenReturn(someDate());

		Optional<DemandSimulationEventIfc> nextEvent = MAKE_TRIP.nextEvent(person, someDate());

		assertThat(nextEvent, hasValue(Event.tripEnding(person, trip)));
	}

	@Test
	public void createsTripEndingEventInFinishPublicTransportState() throws Exception {
		when(person.currentTrip()).thenReturn(trip);
		when(trip.calculatePlannedEndDate()).thenReturn(someDate());

		Optional<DemandSimulationEventIfc> nextEvent = FINISH_PUBLIC_TRANSPORT.nextEvent(person,
				someDate());

		assertThat(nextEvent, hasValue(Event.tripEnding(person, trip)));
	}

	@Test
	public void createsNewEventInExecuteActivityState() throws Exception {
		ActivityIfc activity = mock(Activity.class);
		when(activity.calculatePlannedEndDate()).thenReturn(anotherDate());
		when(person.currentActivity()).thenReturn(activity);

		Optional<DemandSimulationEventIfc> nextEvent = EXECUTE_ACTIVITY.nextEvent(person,
				someDate());

		assertThat(nextEvent, hasValue(Event.activityEnding(person, activity)));
	}

	@Test
	public void passengerWillBeNotifiedToLeaveTheVehicle() throws Exception {
		Time arrival = oneMinuteLater();
		Trip publicTransportTrip = newTrip(existingRoute, singlePart(arrival));
		when(person.currentTrip()).thenReturn(publicTransportTrip);

		Optional<DemandSimulationEventIfc> nextEvent = RIDE_VEHICLE.nextEvent(person, someDate());

		assertThat(nextEvent, isEmpty());
	}

	@Test
	public void createsRideEndingEventWithCurrentTimeWhenNoPartIsLeft() throws Exception {
		Trip publicTransportTrip = PublicTransportTrip.of(trip, person, publicTransportBehaviour, noRoute);
		when(person.currentTrip()).thenReturn(publicTransportTrip);

		Optional<DemandSimulationEventIfc> nextEvent = RIDE_VEHICLE.nextEvent(person, someDate());

		assertThat(nextEvent, isEmpty());
	}

	@Test
	public void createsEventWhenTransfering() {
		Trip publicTransportTrip = PublicTransportTrip.of(trip, person, publicTransportBehaviour, singleLegRoute());
		when(person.currentTrip()).thenReturn(publicTransportTrip);

		Optional<DemandSimulationEventIfc> nextEvent = RIDE_VEHICLE.nextEvent(person, someDate());

		assertThat(nextEvent, hasValue(Event.exitVehicle(person, publicTransportTrip, someDate())));
	}

	private Optional<PublicTransportRoute> singleLegRoute() {
		Connection footConnection = connection()
				.departsAndArrivesAt(someDate())
				.partOf(FootJourney.footJourney)
				.build();
		PublicTransportRoute route = mock(PublicTransportRoute.class);
		when(route.connections()).thenReturn(asList(footConnection));
		return Optional.of(route);
	}

	@Test
	public void passengerWillBeNotifiedByVehicleAtWaitingStop() throws Exception {
		Optional<DemandSimulationEventIfc> nextEvent = WAIT_FOR_VEHICLE.nextEvent(person,
				someDate());

		assertThat(nextEvent, isEmpty());
	}

	@Test
	public void createsEnterStartStopEvent() {
		Time departure = oneMinuteLater();
		Connection connection = connection().departsAndArrivesAt(departure).build();
		List<PublicTransportLeg> part = legsFor(connection);
		Trip publicTransportTrip = newTrip(existingRoute, part);
		Time nextTrigger = departure;
		when(trip.timeOfNextChange()).thenReturn(Optional.of(nextTrigger));
		when(person.currentTrip()).thenReturn(publicTransportTrip);
		Optional<DemandSimulationEventIfc> nextEvent = USE_PUBLIC_TRANSPORT.nextEvent(person, someDate());
		
		DemandSimulationEventIfc event = Event.enterStartStop(person, publicTransportTrip, nextTrigger);
		assertThat(nextEvent, hasValue(event));
	}

	private Trip newTrip(Optional<PublicTransportRoute> route, List<PublicTransportLeg> parts) {
		return new PublicTransportTrip(trip, person, publicTransportBehaviour, route, parts);
	}

	private static List<PublicTransportLeg> singlePart(Time arrival) {
		Connection connection = connection().arrivesAt(arrival).build();
		return legsFor(connection);
	}

	private static List<PublicTransportLeg> legsFor(Connection connection) {
		PublicTransportLeg part = Data.newLeg(connection);
		return Collections.singletonList(part);
	}

	private static Time someDate() {
		return Data.someTime();
	}

	private static Time anotherDate() {
		return someDate().plusMinutes(1);
	}

	private static Time oneMinuteLater() {
		return someDate().plusHours(0).plusMinutes(1);
	}

	private static Matcher<PersonState> isInstantaneous() {
		return new TypeSafeMatcher<PersonState>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("is instantaneous");
			}

			@Override
			protected boolean matchesSafely(PersonState state) {
				return state.instantaneous();
			}

			@Override
			protected void describeMismatchSafely(PersonState state, Description mismatchDescription) {
				mismatchDescription.appendValue(state);
				mismatchDescription.appendText(" is not instantaneous ");
			}
		};
	}

	private static Matcher<PersonState> isNotInstantaneous() {
		return new TypeSafeMatcher<PersonState>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("is not instantaneous");
			}

			@Override
			protected boolean matchesSafely(PersonState state) {
				return !state.instantaneous();
			}

			@Override
			protected void describeMismatchSafely(PersonState state, Description mismatchDescription) {
				mismatchDescription.appendValue(state);
				mismatchDescription.appendText(" is instantaneous ");
			}
		};
	}

}

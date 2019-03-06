package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.PersonResults;
import edu.kit.ifv.mobitopp.simulation.ReschedulingStrategy;
import edu.kit.ifv.mobitopp.simulation.RideSharingOffer;
import edu.kit.ifv.mobitopp.simulation.RideSharingOffers;
import edu.kit.ifv.mobitopp.simulation.StateChange;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.BaseData;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.ZoneBasedRouteChoice;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingPerson;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

public class SimulationPersonPassenger extends PersonDecorator
		implements SimulationPerson, CarSharingPerson {

	private static final long serialVersionUID = 1L;
	private final SimulationOptions options;
	private final ZoneRepository zoneRepository;
	protected final PersonResults results;
	private final PublicTransportBehaviour publicTransportBehaviour;
	private final Random random;
	protected final Set<Mode> modesInSimulation;
	private final TripFactory tripFactory;
	private boolean rideOfferAccepted = false;
	private transient PersonState state;
	private Events events;

	public SimulationPersonPassenger(
		Person person,
		ZoneRepository zoneRepository,
		EventQueue queue,
		SimulationOptions options,
		List<Time> simulationDays,
		Set<Mode> modesInSimulation,
		TourFactory tourFactory,
		TripFactory tripFactory,
		PersonState initialState,
		PublicTransportBehaviour publicTransportBehaviour,
		long seed, 
		PersonResults results
	) {
		super(person);
		this.options = options;
		this.zoneRepository = zoneRepository;
		this.random = new Random(person.getOid() + seed);
		person.initSchedule(tourFactory, options.activityDurationRandomizer(), simulationDays);
		this.tripFactory = tripFactory;

		this.state = initialState;
		this.modesInSimulation = modesInSimulation;
		this.results = results;
		this.publicTransportBehaviour = publicTransportBehaviour;
		events = new Events();

		initFirstActivity(queue);
	}

	protected void setState(PersonState state) {
		this.state = state;
	}

	public PersonState currentState() {
		return this.state;
	}
	public SimulationOptions options() {
		return this.options;
	}


	public boolean rideOfferAccepted() { return this.rideOfferAccepted; }
	public void acceptRideOffer() { this.rideOfferAccepted=true; }

	protected void initFirstActivity(EventQueue queue) {

		assert this.state != null;
		assert person() != null;

		ActivityIfc firstActivity = person().activitySchedule().firstActivity();

		assert firstActivity != null;
		assert !firstActivity.isRunning();
		assert firstActivity.activityType().isHomeActivity();

		
    firstActivity
        .setLocation(new ZoneAndLocation(person().household().homeZone(),
            person().household().homeLocation()));

		firstActivity.setRunning(true);
		notifyStartActivity(person(), firstActivity);

		updateState(queue, options.simulationStart());
	}

	private void notifyStartActivity(Person person, ActivityIfc firstActivity) {
		results.notifyStartActivity(person, firstActivity);
	}

	public void startActivity(
		Trip previousTrip,
  	ReschedulingStrategy rescheduling,
  	Time currentDate
	) {
	}

	public void endActivity() {

		person().currentActivity().setRunning(false);
	}

	protected void driveCar(
		Car car, 
		Trip trip,
		ImpedanceIfc impedance
	) {

		assert person().isCarDriver();

		float distance_meter = impedance.getDistance(trip.origin().zone().getOid(),
				trip.destination().zone().getOid());

		float distance = distance_meter / 1000.0f;

		car.driveDistance(distance);
	}

	public void notify(
			EventQueue queue, DemandSimulationEventIfc event, Time currentDate) {
		verifyEvent(event, currentDate);
		updateState(queue, currentDate);
	}

	private void verifyEvent(DemandSimulationEventIfc event, Time currentDate) {
		assert event.getPerson() == this;
		assert !event.getSimulationDate().isAfter(currentDate);
	}

	private void updateState(EventQueue queue, Time notificationTime) {
		do {
			changeState(notificationTime);
		} while (currentState().instantaneous());

		Optional<DemandSimulationEventIfc> nextEvent = currentState().nextEvent(this, notificationTime);
		nextEvent.ifPresent(queue::add);
	}

	private void changeState(Time currentTime) {
		PersonState previous = currentState();
		currentState().doActionAtEnd(this, currentTime);
		setState(currentState().nextState(this, currentTime));
		currentState().doActionAtStart(this, currentTime);
		results.notifyStateChanged(new StateChange(this, currentTime, previous, currentState()));
	}

	public boolean hasNextActivity() {
		return activitySchedule().hasNextActivity(currentActivity());
	}

	public ActivityIfc nextActivity() {
		return activitySchedule().nextActivity(currentActivity());
	}

	public boolean nextActivityStartsAfterSimulationEnd() {
		return nextActivity().startDate().isAfter( options.simulationEnd());
	}



	public void offerRide(
		Time currentDate,
		SimulationOptions options
	) {
			Trip trip = currentTrip();

		assert trip != null;

		if (trip.mode() == Mode.CAR) {
	  	assert person().isCarDriver();

			offerRide(options.rideSharingOffers(), this, trip);
		}
	}

	protected static void offerRide(
			RideSharingOffers rideOffers, SimulationPerson person, Trip trip) {
		rideOffers.add(trip, person);
	}


	public void selectDestinationAndMode(
		DestinationChoiceModel destinationChoiceModel,
		TourBasedModeChoiceModel modeChoiceModel,
		ImpedanceIfc impedance,
		boolean passengerAsOption
	) {
		ActivityIfc previousActivity = currentActivity();
		ActivityIfc nextActivity = nextActivity();
		
		Optional<Mode> tourMode = Optional.empty();
														
		ZoneAndLocation destination = selectAndSetDestinationOfActivity(destinationChoiceModel, previousActivity, nextActivity, tourMode);
		
		assert nextActivity.isLocationSet();
		
		selectModeAndCreateTrip(modeChoiceModel, impedance, passengerAsOption, previousActivity, nextActivity, destination.zone);
	}

	private ZoneAndLocation selectAndSetDestinationOfActivity(
		DestinationChoiceModel destinationChoiceModel,
		ActivityIfc previousActivity, 
		ActivityIfc nextActivity, 
		Optional<Mode> tourMode 
	) {
		
		if (nextActivity.isLocationSet()) {
			return nextActivity.zoneAndLocation();
		}
		
		assert !nextActivity.isLocationSet();

		Zone destination = destinationChoiceModel.selectDestination(person(), 
																																tourMode,
																																previousActivity, 
																																nextActivity,
																																this.getNextRandom()
																																);  

		Location location = selectLocation(person(), nextActivity, destination);

		nextActivity.setLocation(new ZoneAndLocation(destination, location));
		

		assert nextActivity.isLocationSet();
		return nextActivity.zoneAndLocation();
	}


	private void selectModeAndCreateTrip(
		TourBasedModeChoiceModel modeChoiceModel, 
		ImpedanceIfc impedance, 
		boolean passengerAsOption,
		ActivityIfc previousActivity, 
		ActivityIfc nextActivity, 
		Zone destination
	) {
		
		Set<Mode> choiceSet = new LinkedHashSet<Mode>(this.modesInSimulation);
		if (!passengerAsOption) {
			choiceSet.remove(Mode.PASSENGER);
		}

		Zone origin = previousActivity.zone();

		Mode mode = modeChoiceModel.selectMode( 
									null,
									null,
									person(), 
									origin,
									destination,
									previousActivity, 
									nextActivity,
									choiceSet,
									this.getNextRandom()
							);
		
		assert mode != null;
		

		Trip trip = createTrip(
													    impedance,
													    mode, 
													    previousActivity, 
													    nextActivity
													  );

		currentTrip(trip);
	}


	protected Location selectLocation(
		Person person,
		ActivityIfc activity,
		Zone zone
	) {

		ActivityType activityType = activity.activityType();

		Location location;

		if(activityType.isHomeActivity()) {

			return person.household().homeLocation();
		} 
		else if(activityType.isWorkActivity()) {
			
			return person.fixedDestinationFor(activityType);
		} 
		else if(activityType.isFixedActivity()) {
		
			return person.fixedDestinationFor(activityType);

		} else {
			
			if (zone.hasDemandData()
					&& zone.getDemandData().opportunities().locationsAvailable(activityType)) {
				double randomNumber = this.random.nextDouble();


			if (activityType == ActivityType.PRIVATE_VISIT) {
  			location = privateVisit(zone, activityType, randomNumber);
			} else {
				location = zone.getDemandData().opportunities().selectRandomLocation(activityType, randomNumber);
			}
			} else {
				consumeRandomNumber();
				location = zone.centroidLocation();
			}
		}


		return location;
	}

	private void consumeRandomNumber() {
		int randomNumber = this.random.nextInt();
		System.out.println("Consumed random integer: " + randomNumber);
	}

	private Location privateVisit(Zone zone, ActivityType activityType, double randomNumber) {
		Location location;
		List<Household> households = zone.getDemandData().getPopulationData().getHouseholds();

		if (!households.isEmpty()) {

			Household hh = new DiscreteRandomVariable<Household>(households).realization(randomNumber);
			location = hh.homeLocation();

		} else {
			location = zone.getDemandData().opportunities().selectRandomLocation(activityType, randomNumber);
		}
		return location;
	}


	public void allocateCar(
		ImpedanceIfc impedance,
		Trip trip,
		Time time
	) {
		assert currentActivity().zone().getOid() == trip.origin().zone().getOid();
    trip.prepareTrip(impedance, time);
	}

  protected Trip createTrip(
      ImpedanceIfc impedance, Mode modeType, ActivityIfc previousActivity,
      ActivityIfc nextActivity) {
    return tripFactory.createTrip(this, impedance, modeType, previousActivity, nextActivity);
  }

	protected Optional<PublicTransportRoute> findRoute(
			ImpedanceIfc impedance, Mode modeType, ActivityIfc previousActivity, ActivityIfc nextActivity,
			Time currentTime) {
		return impedance.getPublicTransportRoute(previousActivity.location(), nextActivity.location(),
				modeType, currentTime);
	}

	public boolean findAndAcceptBestMatchingRideOffer(
		RideSharingOffers rideOffers,
		Trip trip,
		int max_difference_minutes
	) {

		RideSharingOffer offer = findBestMatchingRideOffer(rideOffers,  trip,  max_difference_minutes);

		if (offer == null) { return false; }

		acceptOffer(offer);
		return true;
	}

	protected RideSharingOffer findBestMatchingRideOffer(
		RideSharingOffers rideOffers,
		Trip trip,
		int max_minutes_late
	) {

		int max_minutes_early = (int) 0.1*person().currentActivity().duration();

		List<RideSharingOffer> offers = rideOffers.matchingTrips(trip, max_minutes_early, max_minutes_late);

		if (offers.isEmpty()) { return null; }

		Collections.sort(offers, RideSharingOffer.comparator(currentActivity().calculatePlannedEndDate()));
		return offers.get(0);
	}


	protected void acceptOffer(RideSharingOffer offer) 
	{

		Time tripStart = offer.trip.startDate();
		Time activityStart = person().currentActivity().startDate();
		int adjustedDuration = Math.toIntExact(tripStart.differenceTo(activityStart).toMinutes());

		currentActivity().changeDuration(adjustedDuration);

		Trip modifiedTrip = changeStartTimeOfTrip(currentTrip(), currentActivity().calculatePlannedEndDate());

		person().currentTrip(modifiedTrip);

		useCarAsPassenger(offer.car);

		acceptRideOffer();

	}

	public void revokeRideOffer(
		RideSharingOffers rideOffers,
		Trip trip,
		Time currentTime
	) {
		
		if (trip.mode() == Mode.CAR) {
			rideOffers.remove(trip, this);
		}
	}

	protected Trip changeStartTimeOfTrip(Trip trip, Time newStartTime) {

		TripData modifiedTrip = new BaseData(
																	trip.getOid(),
																	trip.previousActivity(),
																	trip.nextActivity(),
																	trip.mode(),
																	newStartTime,
																	(short) trip.plannedDuration()
																);

		return new PassengerTrip(modifiedTrip, this);
	}


	public void endTrip(
		ImpedanceIfc impedance,
		ReschedulingStrategy rescheduling,
		Time currentDate
	) {
			Trip trip = person().currentTrip();

			assert trip != null;

			ActivityIfc activity = trip.nextActivity();

			assert activity != null : person().activitySchedule().toString();
			
			FinishedTrip finishedTrip = finish(currentDate, trip);

			notifyEndTrip(finishedTrip, activity);

			startActivityInternal(rescheduling, activity, currentDate, trip);
	}

	private FinishedTrip finish(Time currentDate, Trip trip) {
		if (trip instanceof PublicTransportTrip) {
			return ((PublicTransportTrip) trip).finish(currentDate, events);
		}
		return trip.finish(currentDate, results);
	}
	
	private void notifyEndTrip(FinishedTrip trip, ActivityIfc activity) {
	  Objects.requireNonNull(trip, "Missing finished trip.");
		results.notifyEndTrip(person(), trip, activity);
	}

	private void startActivityInternal(
		ReschedulingStrategy rescheduling,
		ActivityIfc activity,
		Time currentDate,
		Trip precedingTrip
	) {
		person().startActivity(currentDate, activity, precedingTrip, rescheduling);
		rideOfferAccepted = false;

		results.notifyStartActivity(person(), activity);
	}

	public void selectRoute(
		ZoneBasedRouteChoice routeChoice,
		Trip trip,
		Time date
	) {

		Mode mode = trip.mode();

		if (mode.usesCarAsDriver()) {

			assert isCarDriver() : ( "parked: " + person().hasParkedCar() + "\n"
															+ "driver: " + person().isCarDriver());

			Path route = routeChoice.selectRoute(date, 
																					getZoneId(trip.origin().zone().getOid()), 
																					getZoneId(trip.destination().zone().getOid())
																				);

			results.notifySelectCarRoute(person(), person().whichCar(), trip, route);
		}
	}

	public Integer getZoneId(int zoneOid) {

		return Integer.valueOf(this.zoneRepository.getZoneByOid(zoneOid).getId().substring(1));
	}

	public void startTrip(
		ImpedanceIfc impedance,
		Trip trip,
		Time date
	) {
		assert trip != null;
		events = new Events();
		person().currentTrip(trip);
		if (trip.mode().usesCarAsDriver()) {
			assert whichCar() != null;
			assert isCarDriver();
		}

		if (person().isCarDriver()) { 
			person().whichCar().start(date);
			driveCar(person().whichCar(), trip, impedance); 
		}
	}
	
	@Override
	public void enterFirstStop(Time time) {
		if (notPublicTransport(currentTrip())) {
			throw new IllegalArgumentException("Trip does not use public transport: " + currentTrip());
		}
		currentPart().ifPresent(part -> publicTransportBehaviour.enterWaitingArea(this, part.start()));
	}


	private static boolean notPublicTransport(Trip trip) {
		return !isPublicTransportTrip(trip);
	}

	private static boolean isPublicTransportTrip(Trip trip) {
		return Mode.PUBLICTRANSPORT.equals(trip.mode());
	}

	public boolean isCarSharingCustomer(String company) {

		if (!(person() instanceof CarSharingPerson)) {
			return false;
		}

		return ((CarSharingPerson)person()). isCarSharingCustomer(company);
	}

	@Override
	public boolean hasPublicTransportVehicleDeparted(Time time) {
		return currentPart().map(leg -> publicTransportBehaviour.hasVehicleDeparted(leg)).orElse(false);
	}

	@Override
	public boolean isPublicTransportVehicleAvailable(Time time) {
		return currentPart()
				.map(part -> publicTransportBehaviour.isVehicleAvailable(part))
				.orElse(true);
	}
	
	@Override
	public boolean hasPlaceInPublicTransportVehicle() {
		return currentPart().map(publicTransportBehaviour::hasPlaceInVehicle).orElse(true);
	}
	
	@Override
	public void changeToNewTrip(Time time) {
		if (notPublicTransport(currentTrip())) {
			return;
		}
		Trip newTrip = publicTransportBehaviour.searchNewTrip(this, time, publicTransportTrip());
		currentTrip(newTrip);
	}

	@Override
	public void boardPublicTransportVehicle(Time time) {
		currentPart().ifPresent(part -> board(time, part));
	}

	private void board(Time time, PublicTransportLeg part) {
		publicTransportBehaviour.board(this, time, part, currentTrip());
		events.add(new Event(PassengerEvent.board, time, part.journey()));
	}

	@Override
	public void getOffPublicTransportVehicle(Time time) {
		currentPart().ifPresent(part -> getOff(time, part));
		publicTransportTrip().nextLeg();
	}
	
	private void getOff(Time time, PublicTransportLeg part) {
		publicTransportBehaviour.getOff(this, time, part, currentTrip());
		events.add(new Event(PassengerEvent.getOff, time, part.journey()));
	}
	
	@Override
	public boolean hasArrivedAtNextActivity() {
		return !currentPart().isPresent();
	}
	
	@Override
	public void wait(Time time) {
		currentPart().ifPresent(part -> wait(time, part));
	}

	private void wait(Time time, PublicTransportLeg part) {
		publicTransportBehaviour.wait(this, time, part, currentTrip());
		events.add(new Event(PassengerEvent.wait, time, part.journey()));
	}

	@Override
	public void arriveAtStop(EventQueue queue, Time currentDate) {
		updateState(queue, currentDate);
	}
	
	@Override
	public void vehicleArriving(EventQueue queue, Vehicle vehicle, Time currentDate) {
		Consumer<Integer> enqueue = id -> updateState(queue, currentDate);
		currentPart()
				.map(PublicTransportLeg::journeyId)
				.filter(id -> id == vehicle.journeyId())
				.ifPresent(enqueue);
	}
	
	private Optional<PublicTransportLeg> currentPart() {
		return publicTransportTrip().currentLeg();
	}

	private PublicTransportTrip publicTransportTrip() {
		return (PublicTransportTrip) currentTrip();
	}
	
	protected double getNextRandom() {
		return this.random.nextDouble();
	}

}

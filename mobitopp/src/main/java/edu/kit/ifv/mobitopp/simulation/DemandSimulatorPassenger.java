package edu.kit.ifv.mobitopp.simulation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.PersonLoader;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.events.SimpleEventQueue;
import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportBehaviour;
import edu.kit.ifv.mobitopp.simulation.person.SimulationOptions;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPersonPassenger;
import edu.kit.ifv.mobitopp.simulation.person.TripFactory;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.simulation.tour.TourWithWalkAsSubtourFactory;
import edu.kit.ifv.mobitopp.time.DateFormat;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class DemandSimulatorPassenger
	implements DemandSimulator, SimulationOptions
{

	private static final int defaultMaxDifferenceInMinutes = 30;
	
	private final SimulationContext context;
  private final DestinationChoiceModel destinationChoiceModel;
	private final TourBasedModeChoiceModel modeChoice;
	private final ActivityStartAndDurationRandomizer activityDurationRandomizer;
	private final TripFactory tripFactory;
	private final ReschedulingStrategy rescheduling;
	private final ZoneBasedRouteChoice routeChoice;

	private final EventQueue queue;
	private final VehicleBehaviour vehicleBehaviour;
	protected final Set<Mode> modesInSimulation;
	protected final PersonState initialState;
	private final RideSharingOffers rideOffers;
	private final int max_difference_minutes;
	private final Hooks beforeTimeSlice;
	private final Hooks afterTimeSlice;

	protected final TourFactory tourFactory = new TourWithWalkAsSubtourFactory();

	
  public DemandSimulatorPassenger(
      final DestinationChoiceModel destinationChoiceModel,
			final TourBasedModeChoiceModel modeChoiceModel,
			final ZoneBasedRouteChoice routeChoice,
			final ActivityStartAndDurationRandomizer activityDurationRandomizer,
			final TripFactory tripFactory,
      final ReschedulingStrategy rescheduling,
			final Set<Mode> modesInSimulation,
			final PersonState initialState, 
			final SimulationContext context 
	)
  {
		this.context = context;
		this.destinationChoiceModel = destinationChoiceModel;
		this.modeChoice = modeChoiceModel;
		this.routeChoice = routeChoice;
		this.activityDurationRandomizer = activityDurationRandomizer;
		this.tripFactory = tripFactory;

		this.queue = new SimpleEventQueue();
		this.vehicleBehaviour = context.vehicleBehaviour();
		this.rescheduling = rescheduling;

		this.modesInSimulation = Collections.unmodifiableSet(modesInSimulation);

		this.initialState = initialState;
		max_difference_minutes = defaultMaxDifferenceInMinutes;
		rideOffers = new RideSharingOffers();
		beforeTimeSlice = new Hooks();
		afterTimeSlice = new Hooks();
		registerStandardHooks();
  }
  
  public DemandSimulatorPassenger(
      final DestinationChoiceModel destinationChoiceModelForDemandSimulation_,
			final TourBasedModeChoiceModel modeSelector_,
			final ZoneBasedRouteChoice routeChoice,
			final ActivityStartAndDurationRandomizer activityDurationRandomizer,
			final TripFactory tripFactory,
      final ReschedulingStrategy rescheduling,
			final PersonState initialState,
			final SimulationContext context
	)
  {
		this(destinationChoiceModelForDemandSimulation_,
					modeSelector_,
					routeChoice,
					activityDurationRandomizer,
					tripFactory,
					rescheduling,
					Mode.CHOICE_SET_FULL,
					initialState, 
					context
			);
	}

	public DestinationChoiceModel destinationChoiceModel() { return this.destinationChoiceModel; }
	public TourBasedModeChoiceModel modeChoiceModel() { return this.modeChoice; }
	public ReschedulingStrategy rescheduling() { return this.rescheduling; }
	public ZoneBasedRouteChoice routeChoice() { return this.routeChoice; }

	protected SimulationContext context() {
		return context;
	}
	
	public ImpedanceIfc impedance() {
		return context.impedance();
	}
	
	protected TripFactory tripFactory() {
	  return tripFactory;
	}
	
	public int maxDifferenceMinutes() { return this.max_difference_minutes; }
	public RideSharingOffers rideSharingOffers() { return this.rideOffers; }

	public Time simulationStart() {
		return this.context.simulationDays().startDate();
	}

	public Time simulationEnd() {
		return this.context.simulationDays().endDate();
	}

	private void registerStandardHooks() {
		addBeforeTimeSliceHook(DemandSimulatorPassenger::printCurrentTime);
		addAfterTimeSliceHook(DemandSimulatorPassenger::executeGc);
	}

	private static void executeGc(Time currentTime) {
		if (Time.start.equals(currentTime)) {
			String formattedTime = new DateFormat().asFullDate(currentTime);
			System.out.println("GC (Simulation): " + formattedTime);
			System.gc();
		}
	}

	private static void printCurrentTime(Time currentTime) {
		LocalDateTime realTime = LocalDateTime.now();
		String simulationTime = new DateFormat().asWeekdayTime(currentTime);
		System.out.println(realTime + ": Aktuelle Simulationszeit: " + simulationTime);
	}

	public void startSimulation() {
		initFractionOfHouseholds(queue, this.vehicleBehaviour, context.seed(), listener(), modesInSimulation, initialState);

		simulate();
	}

	private void simulate() {
		SimulationDays simulationDays = context.simulationDays();
		int timeIncrement = context.configuration().getTimeStepLength();
		try {
			for (Time currentTime=simulationDays.startDate();
						currentTime.isBefore(simulationDays.endDate()); 
						currentTime=currentTime.plusSeconds(timeIncrement)) {

				handle(currentTime);
			}

			System.out.println("Noch " + this.queue.size() + " Eintraege in events");

			Time future = SimpleTime.future;
			writeRemainingTripsToFile(this.queue, future);
			writeRemainingChargingsToFile(future);

		}
		catch(java.lang.AbstractMethodError e) {
			System.out.println("ABSTRACT METHOD ERROR:");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			e.printStackTrace();
		}
	}

	private PersonListener listener() {
		return context.personResults();
	}

	void handle(Time currentTime) {
		beforeTimeSlice.process(currentTime);
		handleEvents(currentTime);
		afterTimeSlice.process(currentTime);
	}

	protected void initFractionOfHouseholds(
		EventQueue queue,
		PublicTransportBehaviour boarder,
		long seed,
		PersonListener listener, 
		Set<Mode> modesInSimulation, 
		PersonState initialState
	) {

		float fraction = context.fractionOfPopulation();
		float remainder = 0.0f;

		List<Integer> householdsToBeRemoved = new ArrayList<Integer>();

		int instantiatedHouseholds = 0;

		for (int aHouseholdOid: personLoader().getHouseholdOids() ) {

			remainder += fraction;

			if (remainder >= 1.0) {
      
      	Household household = personLoader().getHouseholdByOid(aHouseholdOid);      

				for(Person p: household.getPersons()) {
					createSimulatedPerson(queue, boarder, seed, p, listener, modesInSimulation, initialState);
				} 

				remainder -= Math.floor(remainder);
				instantiatedHouseholds++;
			} else {
				householdsToBeRemoved.add(aHouseholdOid);
			}

		}
		for (Integer oid : householdsToBeRemoved) {
			personLoader().removeHousehold(oid);
		}
		
		System.out.println(instantiatedHouseholds + " households instantiated");

	}

	private PersonLoader personLoader() {
		return context.personLoader();
	}

	protected SimulationPersonPassenger createSimulatedPerson(
			EventQueue queue, PublicTransportBehaviour boarder, long seed, Person p,
			PersonListener listener, Set<Mode> modesInSimulation, PersonState initialState) {
		return new SimulationPersonPassenger(p, 
																					zoneRepository(),
																					queue,
																					simulationOptions(), 
																					simulationDays(),
																					modesInSimulation,
																					tourFactory,
																					tripFactory, 
																					initialState,
																					boarder,
																					seed,
																					listener
																				);
	}

	protected SimulationOptions simulationOptions() {
		return this;
	}

	protected ZoneRepository zoneRepository() {
		return context.zoneRepository();
	}

	protected List<Time> simulationDays() {
		return context.simulationDays().simulationDates();
	}

	private void handleEvents(Time currentDate) {
		assert currentDate != null;

		this.vehicleBehaviour.letVehiclesArriveAt(currentDate, queue);

		while (this.queue.hasEventsUntil(currentDate)) {
			DemandSimulationEventIfc simulationEvent = this.queue.nextEvent();
			simulationEvent.getPerson().notify(this.queue, simulationEvent, currentDate);
		}

		this.vehicleBehaviour.letVehiclesDepartAt(currentDate);
	}

	private void writeRemainingTripsToFile(EventQueue queue, Time currentDate) {
		while (queue.hasEventsUntil(currentDate)) {
			DemandSimulationEventIfc simulationEvent = queue.nextEvent();
			simulationEvent.writeRemaining(listener());
		}
	}

  protected void writeRemainingChargingsToFile(Time time) {

		for (int aHouseholdOid: personLoader().getHouseholdOids() ) {

     	Household household = personLoader().getHouseholdByOid(aHouseholdOid);      

			for (PrivateCar car :  household.whichCars()) {

				if (car.isStopped()) {
					car.start(time);
				}
			}
		}
	}

	public void addBeforeTimeSliceHook(Hook beforeHour) {
		this.beforeTimeSlice.add(beforeHour);
	}
	
	public void addAfterTimeSliceHook(Hook afterHour) {
		this.afterTimeSlice.add(afterHour);
	}

	@Override
	public ActivityStartAndDurationRandomizer activityDurationRandomizer() {
		return this.activityDurationRandomizer;
	}

}

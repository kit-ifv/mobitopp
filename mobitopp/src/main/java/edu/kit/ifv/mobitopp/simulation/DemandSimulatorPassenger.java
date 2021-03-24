package edu.kit.ifv.mobitopp.simulation;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.communication.JsonResource;
import edu.kit.ifv.mobitopp.communication.SimulationProgressData;
import edu.kit.ifv.mobitopp.data.PersonLoader;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityPeriodFixer;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.events.SimpleEventQueue;
import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportBehaviour;
import edu.kit.ifv.mobitopp.simulation.person.SimulationOptions;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPersonPassenger;
import edu.kit.ifv.mobitopp.simulation.person.TripFactory;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.simulation.tour.TourWithWalkAsSubtourFactory;
import edu.kit.ifv.mobitopp.time.DateFormat;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DemandSimulatorPassenger
	implements DemandSimulator, SimulationOptions
{

	private static final int defaultMaxDifferenceInMinutes = 30;
	
	private final SimulationPersonFactory personFactory;
	private final SimulationContext context;
    private final DestinationChoiceModel destinationChoiceModel;
	private final TourBasedModeChoiceModel modeChoice;
	private final ActivityPeriodFixer activityPeriodFixer;
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
			final ActivityPeriodFixer activityPeriodFixer,
			final ActivityStartAndDurationRandomizer activityDurationRandomizer,
			final TripFactory tripFactory,
      final ReschedulingStrategy rescheduling,
			final Set<Mode> modesInSimulation,
			final PersonState initialState, 
			final SimulationContext context,
			final SimulationPersonFactory personFactory
	)
  {
  	this.personFactory = personFactory;
		this.context = context;
		this.destinationChoiceModel = destinationChoiceModel;
		this.modeChoice = modeChoiceModel;
		this.routeChoice = routeChoice;
		this.activityPeriodFixer = activityPeriodFixer;
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
		registerProgressResource();
  }
  
  public DemandSimulatorPassenger(
  		final DestinationChoiceModel destinationChoiceModel,
			final TourBasedModeChoiceModel modeChoiceModel,
			final ZoneBasedRouteChoice routeChoice,
			final ActivityPeriodFixer activityPeriodFixer,
			final ActivityStartAndDurationRandomizer activityDurationRandomizer,
			final TripFactory tripFactory,
      final ReschedulingStrategy rescheduling,
			final Set<Mode> modesInSimulation,
			final PersonState initialState, 
			final SimulationContext context)
  {
		this(destinationChoiceModel, 
				modeChoiceModel, 
				routeChoice, 
				activityPeriodFixer,
				activityDurationRandomizer,
				tripFactory, 
				rescheduling,
				modesInSimulation,
				initialState, 
				context,
				SimulationPersonPassenger::new);
  }
  
  public DemandSimulatorPassenger(
      final DestinationChoiceModel destinationChoiceModelForDemandSimulation_,
			final TourBasedModeChoiceModel modeSelector_,
			final ZoneBasedRouteChoice routeChoice,
			final ActivityPeriodFixer activityPeriodFixer,
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
					activityPeriodFixer,
					activityDurationRandomizer,
					tripFactory,
					rescheduling,
					StandardChoiceSet.CHOICE_SET_FULL,
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
	
	protected SimulationPersonFactory personFactory() {
		return personFactory;
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
			log.info("GC (Simulation): " + formattedTime);
			System.gc();
		}
	}

	private static void printCurrentTime(Time currentTime) {
		String simulationTime = new DateFormat().asWeekdayTime(currentTime);
		log.info("Aktuelle Simulationszeit: " + simulationTime);
		System.out.println("Aktuelle Simulationszeit: " + simulationTime);
	}
	
	private void registerProgressResource() {
		if (context.restServer() != null) {
			SimulationProgressData data = new SimulationProgressData(0, simulationEnd().toSeconds());
		
			Hook update = time -> data.setSimulation_second(time.toSeconds());
			addBeforeTimeSliceHook(update);
		
			JsonResource resource = new JsonResource(data, "/rest/simulation/progress");
			context().restServer().registerResource(resource);
		} else  {
			System.err.println("Server is null");
		}
		
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

			log.info("Noch " + this.queue.size() + " Eintraege in events");

			Time future = SimpleTime.future;
			writeRemainingTripsToFile(this.queue, future);
			writeRemainingChargingsToFile(future);

		}
		catch(java.lang.AbstractMethodError e) {
			log.error("ABSTRACT METHOD ERROR:");
			log.error(e.getMessage(), e);
			log.error(e.getCause().toString());
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
		Consumer<Person> createAgent = p -> createSimulatedPerson(queue, boarder, seed, p, listener,
				modesInSimulation, initialState);
		personLoader().households().flatMap(Household::persons).forEach(createAgent);
		personLoader().clearInput();
	}

	protected PersonLoader personLoader() {
		return context.personLoader();
	}

	protected SimulationPerson createSimulatedPerson(
			EventQueue queue, PublicTransportBehaviour boarder, long seed, Person p,
			PersonListener listener, Set<Mode> modesInSimulation, PersonState initialState) {
		return personFactory.create(p, 
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
	
	public void removeBeforeTimeSliceHook(Hook beforeHour) {
		this.beforeTimeSlice.remove(beforeHour);
	}
	
	public void removeAfterTimeSliceHook(Hook afterHour) {
		this.afterTimeSlice.remove(afterHour);
	}

	@Override
	public ActivityStartAndDurationRandomizer activityDurationRandomizer() {
		return this.activityDurationRandomizer;
	}

	@Override
	public ActivityPeriodFixer activityPeriodFixer() {
		return this.activityPeriodFixer;
	}

}

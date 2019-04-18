package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.DefaultActivityDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.destinationAndModeChoice.DestinationAndModeChoiceSchaufenster;
import edu.kit.ifv.mobitopp.simulation.destinationAndModeChoice.DestinationAndModeChoiceUtility;
import edu.kit.ifv.mobitopp.simulation.destinationAndModeChoice.DestinationAndModeChoiceUtilitySchaufenster;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceWithFixedLocations;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.SimpleRepeatedDestinationChoice;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModelAddingCarsharing;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.modeChoice.stuttgart.ModeChoiceStuttgart;
import edu.kit.ifv.mobitopp.simulation.modeChoice.stuttgart.ModeSelectorParameterFirstTrip;
import edu.kit.ifv.mobitopp.simulation.modeChoice.stuttgart.ModeSelectorParameterOtherTrip;
import edu.kit.ifv.mobitopp.simulation.person.DefaultTripFactory;
import edu.kit.ifv.mobitopp.simulation.person.PersonStateSimple;
import edu.kit.ifv.mobitopp.simulation.person.TripFactory;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModelDummy;

public class SimulationSLIS extends Simulation {

	public SimulationSLIS(SimulationContext context) {
		super(context);
	}

	@Override
	protected DemandSimulator simulator() {
		ModeAvailabilityModel modeAvailabilityModel = new ModeAvailabilityModelAddingCarsharing(
				impedance());
    DestinationChoiceModel targetSelector = destinationChoiceModel(impedance(), modeAvailabilityModel);
		ModeChoiceModel modeSelector = modeChoiceModel(impedance(), modeAvailabilityModel);
		ZoneBasedRouteChoice routeChoice = new NoRouteChoice();
		ReschedulingStrategy rescheduling = new ReschedulingSkipTillHome(context().simulationDays());
		TripFactory tripFactory = new DefaultTripFactory();
		System.out.println("Initializing simulator...");
		return new DemandSimulatorPassenger(targetSelector, 
				new TourBasedModeChoiceModelDummy(modeSelector), routeChoice,
				new DefaultActivityDurationRandomizer(context().seed()), tripFactory, rescheduling,
				PersonStateSimple.UNINITIALIZED, context());
	}

	private ModeChoiceModel modeChoiceModel(
			ImpedanceIfc impedance, ModeAvailabilityModel modeAvailabilityModel) {
		ModeChoiceModel modeSelectorFirst = new ModeChoiceStuttgart(impedance,
				new ModeSelectorParameterFirstTrip());
		ModeChoiceModel modeSelectorOther = new ModeChoiceStuttgart(impedance,
				new ModeSelectorParameterOtherTrip());
		return new ModeSelectorFirstOther(modeAvailabilityModel, modeSelectorFirst, modeSelectorOther);
	}

	private DestinationChoiceModel destinationChoiceModel(
			ImpedanceIfc impedance, ModeAvailabilityModel modeAvailabilityModel) {
		Map<String, String> destinationChoiceFiles = context().configuration().getDestinationChoice();
		DestinationAndModeChoiceUtility utility1 = new DestinationAndModeChoiceUtilitySchaufenster(
				impedance, destinationChoiceFiles.get("work"));

		DestinationAndModeChoiceUtility utility2 = new DestinationAndModeChoiceUtilitySchaufenster(
				impedance, destinationChoiceFiles.get("business"));

		DestinationAndModeChoiceUtility utility3 = new DestinationAndModeChoiceUtilitySchaufenster(
				impedance, destinationChoiceFiles.get("education"));

		DestinationAndModeChoiceUtility utility6 = new DestinationAndModeChoiceUtilitySchaufenster(
				impedance, destinationChoiceFiles.get("service"));

		DestinationAndModeChoiceUtility utility7 = new DestinationAndModeChoiceUtilitySchaufenster(
				impedance, destinationChoiceFiles.get("home"));

		DestinationAndModeChoiceUtility utility8 = new DestinationAndModeChoiceUtilitySchaufenster(
				impedance, destinationChoiceFiles.get("undefined"));

		DestinationAndModeChoiceUtility utility9 = new DestinationAndModeChoiceUtilitySchaufenster(
				impedance, destinationChoiceFiles.get("otherhome"));

		DestinationAndModeChoiceUtility utility11 = new DestinationAndModeChoiceUtilitySchaufenster(
				impedance, destinationChoiceFiles.get("privateBusiness"));

		DestinationAndModeChoiceUtility utility12 = new DestinationAndModeChoiceUtilitySchaufenster(
				impedance, destinationChoiceFiles.get("privateVisit"));

		DestinationAndModeChoiceUtility utility41 = new DestinationAndModeChoiceUtilitySchaufenster(
				impedance, destinationChoiceFiles.get("shoppingDaily"));

		DestinationAndModeChoiceUtility utility42 = new DestinationAndModeChoiceUtilitySchaufenster(
				impedance, destinationChoiceFiles.get("shoppingOther"));

		DestinationAndModeChoiceUtility utility51 = new DestinationAndModeChoiceUtilitySchaufenster(
				impedance, destinationChoiceFiles.get("leisureIndoor"));

		DestinationAndModeChoiceUtility utility52 = new DestinationAndModeChoiceUtilitySchaufenster(
				impedance, destinationChoiceFiles.get("leisureOutdoor"));

		DestinationAndModeChoiceUtility utility53 = new DestinationAndModeChoiceUtilitySchaufenster(
				impedance, destinationChoiceFiles.get("leisureOther"));

		DestinationAndModeChoiceUtility utility77 = new DestinationAndModeChoiceUtilitySchaufenster(
				impedance, destinationChoiceFiles.get("leisureWalk"));

		Map<ActivityType, DestinationAndModeChoiceUtility> utilityFunctions = new HashMap<ActivityType, DestinationAndModeChoiceUtility>();

		utilityFunctions.put(ActivityType.WORK, utility1);
		utilityFunctions.put(ActivityType.BUSINESS, utility2);
		utilityFunctions.put(ActivityType.EDUCATION, utility3);
		utilityFunctions.put(ActivityType.SERVICE, utility6);
		utilityFunctions.put(ActivityType.HOME, utility7);
		utilityFunctions.put(ActivityType.UNDEFINED, utility8);
		utilityFunctions.put(ActivityType.OTHERHOME, utility9);

		utilityFunctions.put(ActivityType.PRIVATE_BUSINESS, utility11);
		utilityFunctions.put(ActivityType.PRIVATE_VISIT, utility12);
		utilityFunctions.put(ActivityType.SHOPPING_DAILY, utility41);
		utilityFunctions.put(ActivityType.SHOPPING_OTHER, utility42);
		utilityFunctions.put(ActivityType.LEISURE_INDOOR, utility51);
		utilityFunctions.put(ActivityType.LEISURE_OUTDOOR, utility52);
		utilityFunctions.put(ActivityType.LEISURE_OTHER, utility53);
		utilityFunctions.put(ActivityType.LEISURE_WALK, utility77);

    DestinationAndModeChoiceSchaufenster destinationModeModel = new DestinationAndModeChoiceSchaufenster(
        modeAvailabilityModel, utilityFunctions);

		return new DestinationChoiceWithFixedLocations(zoneRepository().zones(),
				new SimpleRepeatedDestinationChoice(zoneRepository().zones(), destinationModeModel,
						destinationChoiceFiles.get("repetition")));
	}

	public static void main(String... args) throws IOException {
		if (1 > args.length) {
			System.out.println("Usage: ... <configuration file>");
			System.exit(-1);
		}

		File configurationFile = new File(args[0]);
		LocalDateTime start = LocalDateTime.now();
		startSimulation(configurationFile);
		LocalDateTime end = LocalDateTime.now();
		Duration runtime = Duration.between(start, end);
		System.out.println("Simulation took " + runtime);
	}

	public static void startSimulation(File configurationFile) throws IOException {
		SimulationContext context = new ContextBuilder().buildFrom(configurationFile);
		new SimulationSLIS(context).simulate();
	}
}

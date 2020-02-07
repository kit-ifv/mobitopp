package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.IntegerMatrix;
import edu.kit.ifv.mobitopp.data.OutputHandler;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.DefaultActivityDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.CarRangeReachableZonesFilter;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceForFlexibleActivity;
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
import edu.kit.ifv.mobitopp.util.dataexport.MatrixPrinter;

public class SimulationExample extends Simulation {

	public SimulationExample(SimulationContext context) {
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
		Set<Mode> choiceSet = 	Collections
				.unmodifiableSet(EnumSet
						.of(StandardMode.CAR, StandardMode.PASSENGER, StandardMode.BIKE, StandardMode.PEDESTRIAN,
								StandardMode.PUBLICTRANSPORT));
    context().personResults().addListener(createMatrixListener(choiceSet));
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

		return new DestinationChoiceWithFixedLocations(
				zoneRepository().zones(),
				new SimpleRepeatedDestinationChoice(
						zoneRepository().zones(),
						new DestinationChoiceForFlexibleActivity(
								modeAvailabilityModel, 
								new CarRangeReachableZonesFilter(impedance),
								new AttractivityCalculatorCostNextPole(zoneRepository().zones(),
										impedance, destinationChoiceFiles.get("cost"), 0.5f)),
						destinationChoiceFiles.get("repetition")));
	}

  private PersonListener createMatrixListener(Set<Mode> choiceSet ) {
  	Set<OutputHandler> outputhandlers = new HashSet<>();
    List<ZoneId> ids = Collections
        .unmodifiableList(
            context().dataRepository().zoneRepository().getZoneIds());
  	for (Mode mode : choiceSet) {		
  		outputhandlers.add(createOutputHandler(mode, 0, 5, ids, "_100p", (int)(1/context().fractionOfPopulation())));
  		outputhandlers.add(createOutputHandler(mode, 6, 8, ids, "_100p", (int)(1/context().fractionOfPopulation())));
  		outputhandlers.add(createOutputHandler(mode, 9, 14, ids, "_100p", (int)(1/context().fractionOfPopulation())));
  		outputhandlers.add(createOutputHandler(mode, 15, 17, ids, "_100p",(int)(1/context().fractionOfPopulation())));
  		outputhandlers.add(createOutputHandler(mode, 18, 23, ids,"_100p", (int)(1/context().fractionOfPopulation())));
    }
    
    return new AggregateDemandPerMode(outputhandlers);
  }

  private OutputHandler createOutputHandler(Mode mode, int from, int to, List<ZoneId> ids, String suffix, int scalingfactor) {
    IntegerMatrix matrix = new IntegerMatrix(ids);
    return new OutputHandler(createMatrixWriter(mode, from, to, suffix), matrix, mode,
        from, to, scalingfactor);
  }

  private Consumer<IntegerMatrix> createMatrixWriter(Mode mode, int from, int to, String suffix) {
    MatrixPrinter printer = new MatrixPrinter();
    String filenamemodedescriptor = mode.toString();
    File toOutputFile = new File(context().configuration().getResultFolder(), filenamemodedescriptor + "_" + from + "_" + to + suffix + "_demand.mtx");
    return matrix -> {
      printer.writeMatrixToFile(matrix, Integer.toString(from), Integer.toString(to), toOutputFile);
    };
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
		startSimulation(context);
	}
		
	public static void startSimulation(SimulationContext context) {
		new SimulationExample(context).simulate();
	}
}

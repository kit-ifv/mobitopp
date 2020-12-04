package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimulationConfiguration extends Simulation {

	public SimulationConfiguration(SimulationContext context) {
		super(context);
	}

	@Override
	protected DemandSimulator simulator() {
		ModeAvailabilityModel modeAvailabilityModel = new ModeAvailabilityModelAddingCarsharing(
				impedance());
		DestinationChoiceModel targetSelector = destinationChoiceModel(modeAvailabilityModel);
		ModeChoiceModel modeSelector = modeChoiceModel(modeAvailabilityModel);
		ZoneBasedRouteChoice routeChoice = new NoRouteChoice();
		ReschedulingStrategy rescheduling = new ReschedulingSkipTillHome(context().simulationDays());
		TripFactory tripFactory = new DefaultTripFactory();
		log.info("Initializing simulator...");
		return new DemandSimulatorPassenger(targetSelector, 
				new TourBasedModeChoiceModelDummy(modeSelector), routeChoice,
				new DefaultActivityDurationRandomizer(context().seed()), tripFactory, rescheduling,
				PersonStateSimple.UNINITIALIZED, context());
	}

  private ModeChoiceModel modeChoiceModel(ModeAvailabilityModel modeAvailabilityModel) {
	  File firstTripFile = getModeChoiceFile("firstTrip");
    File otherTripFile = getModeChoiceFile("otherTrip");
    ModeChoiceModel modeSelectorFirst = new ModeChoiceStuttgart(impedance(),
        new ModeSelectorParameterFirstTrip(firstTripFile));
    ModeChoiceModel modeSelectorOther = new ModeChoiceStuttgart(impedance(),
        new ModeSelectorParameterOtherTrip(otherTripFile));
		return new ModeSelectorFirstOther(modeAvailabilityModel, modeSelectorFirst, modeSelectorOther);
	}

	private DestinationChoiceModel destinationChoiceModel(
			ModeAvailabilityModel modeAvailabilityModel) {
		return new DestinationChoiceWithFixedLocations(zoneRepository().zones(),
				new SimpleRepeatedDestinationChoice(zoneRepository().zones(),
						new DestinationChoiceForFlexibleActivity(modeAvailabilityModel,
								new CarRangeReachableZonesFilter(impedance()),
								new AttractivityCalculatorCostNextPole(zoneRepository().zones(), impedance(),
										getDestinationChoiceFileFor("cost"), 0.5f)),
						getDestinationChoiceFileFor("repetition")));
	}
	
  private String getDestinationChoiceFileFor(String name) {
    return context().configuration().getDestinationChoice().get(name);
  }

  private File getModeChoiceFile(String fileName) {
    return context().modeChoiceParameters().valueAsFile(fileName);
  }

	public static void main(String... args) throws IOException {
		if (1 > args.length) {
			log.error("Usage: ... <configuration file>");
			System.exit(-1);
		}

		File configurationFile = new File(args[0]);
		LocalDateTime start = LocalDateTime.now();
		startSimulation(configurationFile);
		LocalDateTime end = LocalDateTime.now();
		Duration runtime = Duration.between(start, end);
		log.info("Simulation took " + runtime);
	}

	private static void startSimulation(File configurationFile) throws IOException {
		SimulationContext context = new ContextBuilder().buildFrom(configurationFile);
		startSimulation(context);
	}
		
	public static void startSimulation(SimulationContext context) {
		new SimulationConfiguration(context).simulate();
	}
}

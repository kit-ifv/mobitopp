package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.destinationChoice.CarRangeReachableZonesFilter;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceForFlexibleActivity;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceWithFixedLocations;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.SimpleRepeatedDestinationChoice;
import edu.kit.ifv.mobitopp.simulation.modeChoice.BasicModeAvailabilityModel;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.modeChoice.stuttgart.ModeChoiceStuttgart;
import edu.kit.ifv.mobitopp.simulation.modeChoice.stuttgart.ModeSelectorParameterFirstTrip;
import edu.kit.ifv.mobitopp.simulation.modeChoice.stuttgart.ModeSelectorParameterOtherTrip;
import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.simulation.person.PersonStateSimple;

public class SimulationPublicTransport extends Simulation {

	private final PersonState initialState;
	private final Optional<Hook> cleanCache;

	public SimulationPublicTransport(
			SimulationContext context, PersonState initialState, Optional<Hook> cleanCache) {
		super(context);
		this.initialState = initialState;
		this.cleanCache = cleanCache;
	}

	@Override
	protected DemandSimulator simulator() {
		ModeAvailabilityModel modeAvailabilityModel = new BasicModeAvailabilityModel(impedance());

		Map<String, String> destinationChoiceFiles = context().configuration().getDestinationChoice();
		DestinationChoiceModel targetSelector = new DestinationChoiceWithFixedLocations(
				zoneRepository().zones(),
				new SimpleRepeatedDestinationChoice(zoneRepository().zones(),
						new DestinationChoiceForFlexibleActivity(modeAvailabilityModel,
								new CarRangeReachableZonesFilter(impedance()),
								new AttractivityCalculatorCostNextPole(zoneRepository().zones(), impedance(),
										"./" + destinationChoiceFiles.get("cost"), 0.5f)),
						"./" + destinationChoiceFiles.get("repetition")));

		ModeChoiceModel modeSelectorFirst = new ModeChoiceStuttgart(impedance(),
				new ModeSelectorParameterFirstTrip());
		ModeChoiceModel modeSelectorOther = new ModeChoiceStuttgart(impedance(),
				new ModeSelectorParameterOtherTrip());

		ModeChoiceModel modeSelector = new ModeSelectorFirstOther(modeAvailabilityModel,
				modeSelectorFirst, modeSelectorOther);

		ZoneBasedRouteChoice routeChoice = new NoRouteChoice();

		ReschedulingStrategy rescheduling = new ReschedulingSkipTillHome(context().simulationDays());

		System.out.println("Initializing simulator...");

		DemandSimulatorPassenger simulator = new DemandSimulatorPassenger(targetSelector, modeSelector,
				routeChoice, rescheduling, initialState, context());
		applyHooksTo(simulator);
		return simulator;
	}

	private void applyHooksTo(DemandSimulatorPassenger simulator) {
		cleanCache.ifPresent(simulator::addBeforeTimeSliceHook);
	}

	public static void main(String[] args) throws IOException {
		if (1 > args.length) {
			System.out.println("Arguments: " + args.length + ": " + args.toString());
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

	private static void startSimulation(File configurationFile) throws IOException {
		SimulationContext context = new ContextBuilder().buildFrom(configurationFile);
		startSimulation(context);
	}

	public static void startSimulation(SimulationContext context) {
		PersonState initialState = context.configuration().getPublicTransport().initialState(
				PersonStateSimple.UNINITIALIZED);
		Optional<Hook> cleanCache = context.configuration().getPublicTransport().cleanCacheHook();
		new SimulationPublicTransport(context, initialState, cleanCache).simulate();
	}

}

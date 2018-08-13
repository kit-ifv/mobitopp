package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceWithFixedLocations;
import edu.kit.ifv.mobitopp.simulation.impedance.FreePublicTransport;
import edu.kit.ifv.mobitopp.simulation.impedance.ImprovedPublicTransport;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import edu.kit.ifv.mobitopp.result.ResultWriter;
// import edu.kit.ifv.mobitopp.data.database.ProjectConfig;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.DefaultActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceSimple;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModelEmployment;
import edu.kit.ifv.mobitopp.simulation.person.PersonStateSimple;
import edu.kit.ifv.mobitopp.simulation.tour.ConstraintBasedDestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.tour.DefaultSubtourModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.tour.DefaultSubtourModeChoiceUtilityFunction;
import edu.kit.ifv.mobitopp.simulation.tour.DefaultTourModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.tour.DefaultTourModeChoiceUtilityFunction;
import edu.kit.ifv.mobitopp.simulation.tour.DefaultWithinTourModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.tour.DefaultWithinTourModeChoiceUtilityFunction;
import edu.kit.ifv.mobitopp.simulation.tour.DemandSimulatorTour;
import edu.kit.ifv.mobitopp.simulation.tour.FeasibleModesNoRestrictions;
import edu.kit.ifv.mobitopp.simulation.tour.FeasibleModesWithTimeRestrictions;
import edu.kit.ifv.mobitopp.simulation.tour.NoModeChangeWithinTour;
import edu.kit.ifv.mobitopp.simulation.tour.PreferenceOnlyUtilityFunction;
import edu.kit.ifv.mobitopp.simulation.tour.RandomModeChangeWithinTour;
import edu.kit.ifv.mobitopp.simulation.tour.SubtourModeChoiceParameterTimeCostTourmodePurpose;
import edu.kit.ifv.mobitopp.simulation.tour.SubtourModeChoiceParameterTimeCostTourmodePurposeIntrazonal;
import edu.kit.ifv.mobitopp.simulation.tour.SubtourModeSameAsTour;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceModelWithTimeRestrictions;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterOnlyConstants;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByAgeHhtSex;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByAgeSex;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByAgeSexIz;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByEmpAgeSex;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByEmpAgeSexOnlyConst;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByEmpHhtSex;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByEmpSex;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByEmpSexModeShares;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByEmpSexModeSharesPref;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByEmpSexModeSharesPrefScaled;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByEmpSexNumUsedBefore;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByEmpSexPreferenceML;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByEmpSexPreferenceMLScaled;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByEmpSexPreviousMode;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByEmpSexSameMode;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByEmpSexSameModeScaled;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByEmpSexUsedBefore;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceParameterTimeCostByEmpSexUsedBeforeScaled;
import edu.kit.ifv.mobitopp.simulation.tour.WithinTourModeChoiceParameterOnlyFlexibleModesPurposeTourmodeWithintour;
import edu.kit.ifv.mobitopp.simulation.modeChoice.BasicModeAvailabilityModel;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoiceModelUsingAvailableModes;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;

public class SimulationExperimental 
	extends Simulation
{ 

	private static final int scenario = 0;

	public SimulationExperimental(SimulationContext context) {
		super(context);
	}
	
	private DestinationChoiceModel createSimpleDestinationChoice(
			ModeAvailabilityModel modeAvailabilityModel, 
			ImpedanceIfc impedance
	){
		 return	new DestinationChoiceWithFixedLocations(
				 	zoneRepository().zones(),
					 		new DestinationChoiceSimple(
					 			new DestinationChoiceModelEmployment(impedance),
								modeAvailabilityModel,
								impedance, true
							)
					);
	}
	
	private DestinationChoiceModel createTimeConstraintDestinationChoice(
			ModeAvailabilityModel modeAvailabilityModel, 
			ImpedanceIfc impedance
	){
		 return	new DestinationChoiceWithFixedLocations(
				 	zoneRepository().zones(),
					 		new ConstraintBasedDestinationChoiceModel (
					 			new DestinationChoiceModelEmployment(impedance),
								modeAvailabilityModel,
								impedance, 1.5, 30
							)
					);
	}

	protected DemandSimulator simulator() {
		
		SimulationContext context = context();
		ImpedanceIfc basicImpedance = context().impedance();
		
		 ImpedanceIfc impedance = basicImpedance;
		// ImpedanceIfc impedance = new ImprovedPublicTransport(basicImpedance, 0.8f);
		// ImpedanceIfc impedance = new FreePublicTransport(basicImpedance);

		// ModeAvailabilityModel modeAvailabilityModel = new ModeAvailabilityModelAddingCarsharing(impedance);
		ModeAvailabilityModel modeAvailabilityModel = new BasicModeAvailabilityModel(impedance);


		DestinationChoiceModel destinationChoiceModel 
			// = createSimpleDestinationChoice(modeAvailabilityModel, impedance);
			= createTimeConstraintDestinationChoice(modeAvailabilityModel, impedance);

		TourBasedModeChoiceModel modeChoiceSimple; // = new ModeChoiceSimple(impedance);
		
		modeChoiceSimple = new TourModeChoiceModelWithTimeRestrictions(
				new DefaultTourModeChoiceModel(
					// new PreferenceOnlyUtilityFunction()
					new DefaultTourModeChoiceUtilityFunction(
					//	new TourModeChoiceParameterTimeCostByEmpAgeSex(),				// ok 
					//		new TourModeChoiceParameterTimeCostByEmpSexSameMode(), // ok 
					//		new TourModeChoiceParameterTimeCostByEmpSexSameModeScaled(), // ok 
					//		new TourModeChoiceParameterTimeCostByEmpSexPreviousMode(), // ok
					//		new TourModeChoiceParameterTimeCostByEmpSexUsedBefore(),  // ok
					//		new TourModeChoiceParameterTimeCostByEmpSexUsedBeforeScaled(),  // ok
					// 		new TourModeChoiceParameterTimeCostByEmpSexNumUsedBefore(),  // ok
					// 		new TourModeChoiceParameterTimeCostByEmpSexModeSharesPref(), // ok
					//		new TourModeChoiceParameterTimeCostByEmpSexModeSharesPrefScaled(), // ok
					//		new TourModeChoiceParameterTimeCostByEmpSexModeShares(),     // ok
					//	 new TourModeChoiceParameterTimeCostByEmpSexPreferenceML(), // ok 
						 new TourModeChoiceParameterTimeCostByEmpSexPreferenceMLScaled(), 
					impedance)
				), 
				// new RandomModeChangeWithinTour(), 
//				new NoModeChangeWithinTour(),

				new DefaultWithinTourModeChoiceModel(
					new DefaultWithinTourModeChoiceUtilityFunction(
						new WithinTourModeChoiceParameterOnlyFlexibleModesPurposeTourmodeWithintour(),
						// new WithinTourModeChoiceParameterOnlyFlexibleModesEmpAgeSexPurposeTourmodeWithintour(),
						impedance
					)
				),
				
//				 new SubtourModeSameAsTour(),

				new DefaultSubtourModeChoiceModel(
						new DefaultSubtourModeChoiceUtilityFunction(
								new SubtourModeChoiceParameterTimeCostTourmodePurposeIntrazonal(), impedance
								//new SubtourModeChoiceParameterTimeCostTourmodePurpose(), impedance
						)
				),
				
				// new FeasibleModesWithTimeRestrictions(impedance, 1.0,30),
				new FeasibleModesNoRestrictions(),
				impedance,
				context
			);
		
		ResultWriter results = ResultWriter.createDefaultWriter();
		

		TourBasedModeChoiceModel modeChoiceModel
			= new ModeChoiceModelUsingAvailableModes(modeAvailabilityModel, modeChoiceSimple, results);
		


		ZoneBasedRouteChoice routeChoice = new NoRouteChoice();
		// ZoneBasedRouteChoice routeChoice = new ZoneBasedDijkstraRouteChoice("../visum/data/stuttgart_netz.net");

		// ReschedulingStrategy rescheduling = new ReschedulingOriginal(); 
		// ReschedulingStrategy rescheduling = new ReschedulingTruncate();
		//ReschedulingStrategy rescheduling = new ReschedulingSkipTillHome(simconfig); 
		ReschedulingStrategy rescheduling = new ReschedulingAdjustHomeActivity(context);
		// ReschedulingStrategy rescheduling = new ReschedulingIgnore();
		
System.out.println("Initializing simulator...");


		DemandSimulator simulator = new DemandSimulatorTour(
					destinationChoiceModel,
					modeChoiceModel,
					routeChoice,
					new DefaultActivityStartAndDurationRandomizer(1234, 60.0, 0.0),
					rescheduling,
					PersonStateSimple.UNINITIALIZED
					, context
				);

		return simulator;
	}


	/*
	public static void main(String[] args) throws IOException {

		float fract = 1.0f;
		long seed = 1234L;
		int days = 1;
		Integer numberOfZones = null;

		if (args.length >= 1) {
			days = Integer.valueOf(args[0]);
		}

		if (args.length >= 2) {
			float tmp = Float.valueOf(args[1]);

			if (tmp > 0.0f && tmp < 1.0f) {
				fract = tmp;
			}
		}

		if (args.length>=3) {
			seed = Long.valueOf(args[2]);
		}

		if (args.length>=4) {
			numberOfZones = Integer.valueOf(args[3]);
		}


		System.out.println("\n\nUsing " + (100*fract) + " percent of the population for simulation\n\n");

		System.out.println("Simulating " + (numberOfZones!=null ? numberOfZones : "all") + " zones");

		ProjectConfig config = new ProjectConfig(10,"Stuttgart",scenario,1,2009,2011,0, 1, 4, 3);
		StartUpSimulation configuration = new DatabaseStartUpConfiguration(scenario, days, fract, seed,
				numberOfZones, config);
		InputSpecification input = new InputSpecificationExperimental();
		DataRepositoryForSimulation dataRepository = configuration.createDataRepository(input);
		SimulationExperimental simulation = new SimulationExperimental(dataRepository);
		simulation.simulate(days, fract, seed, numberOfZones);
	}
	*/

	
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
		new SimulationExperimental(context).simulate();
	}

}

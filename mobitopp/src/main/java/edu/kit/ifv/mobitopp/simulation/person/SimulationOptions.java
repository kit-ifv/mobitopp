package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.ReschedulingStrategy;
import edu.kit.ifv.mobitopp.simulation.RideSharingOffers;
import edu.kit.ifv.mobitopp.simulation.ZoneBasedRouteChoice;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityPeriodFixer;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.time.Time;

public interface SimulationOptions {

	public DestinationChoiceModel destinationChoiceModel();
	public TourBasedModeChoiceModel modeChoiceModel();
	public ReschedulingStrategy rescheduling();
	public ZoneBasedRouteChoice routeChoice();
	public ImpedanceIfc impedance();
	public int maxDifferenceMinutes();
	public RideSharingOffers rideSharingOffers();
	public Time simulationStart();
	public Time simulationEnd();
	public ActivityStartAndDurationRandomizer activityDurationRandomizer();
	
	public ActivityPeriodFixer activityPeriodFixer();

}

package edu.kit.ifv.mobitopp.simulation.tour;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivitySchedule;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivitySequence;

public interface TourAwareActivitySchedule 
	extends ActivitySequence, ActivitySchedule
{
	
	
	int numberOfTours();
	
	boolean isStartOfTour(ActivityIfc activity);
	
	Tour correspondingTour(ActivityIfc activity);
	
	Tour firstTour();
	
	ActivityIfc startOfFirstTour();

}

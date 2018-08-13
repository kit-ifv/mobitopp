package edu.kit.ifv.mobitopp.simulation.tour;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public interface TourFactory {

	public Tour createTour(
		ActivityIfc first, 
		ActivityIfc last,
		TourAwareActivitySchedule schedule);
}

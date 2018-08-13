package edu.kit.ifv.mobitopp.simulation.tour;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public class DefaultTourFactory implements TourFactory {

	@Override
	public Tour createTour(ActivityIfc first, ActivityIfc last, TourAwareActivitySchedule schedule) {
		
		return new DefaultTour(first, last, schedule);
	}
}

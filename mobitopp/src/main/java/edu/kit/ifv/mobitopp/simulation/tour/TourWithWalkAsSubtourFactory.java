package edu.kit.ifv.mobitopp.simulation.tour;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public class TourWithWalkAsSubtourFactory implements TourFactory {

	@Override
	public Tour createTour(ActivityIfc first, ActivityIfc last, TourAwareActivitySchedule schedule) {
		
		return new TourWithWalkAsSubtour(first, last, schedule);
	}

}

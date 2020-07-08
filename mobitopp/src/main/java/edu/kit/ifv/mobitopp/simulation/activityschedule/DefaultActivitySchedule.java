package edu.kit.ifv.mobitopp.simulation.activityschedule;




import java.util.List;
import java.io.Serializable;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ModifiableActivityScheduleWithState;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.tour.TourAwareActivitySchedule;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;


public class DefaultActivitySchedule 
	extends ActivityPeriodWithExtendedFirstAndLastActivity
	implements ActivitySchedule, ActivityScheduleWithState
		, ModifiableActivityScheduleWithState 
		, TourAwareActivitySchedule
		, Serializable
{

	private static final long serialVersionUID = -6409449269157250182L;
	
	
	private OccupationIfc currentOccupation;
	
	public DefaultActivitySchedule(	
		TourFactory tourFactory,
		PatternActivityWeek activityPattern,
		ActivityStartAndDurationRandomizer activityDurationRandomizer, 
		List<Time> dates
	) {
		super(tourFactory, activityDurationRandomizer, activityPattern, dates);
		
		this.startActivity(this.firstActivity());
	}


	public void setCurrentTrip(Trip trip) {

		this.currentOccupation = trip;		
	}

	public Trip currentTrip() {

		assert this.currentOccupation instanceof Trip : this.currentOccupation;

		return (Trip) this.currentOccupation;
	}

	public void startActivity(ActivityIfc activity) {
		this.currentOccupation = activity;
	}

	public ActivityIfc currentActivity() {

		assert this.currentOccupation instanceof ActivityIfc;

		return (ActivityIfc) this.currentOccupation;
	}

	public void insertActivityAfter(ActivityIfc previous, ActivityIfc toBeInserted) {
		
		this.insertAfter(previous, toBeInserted);
		
	}



}

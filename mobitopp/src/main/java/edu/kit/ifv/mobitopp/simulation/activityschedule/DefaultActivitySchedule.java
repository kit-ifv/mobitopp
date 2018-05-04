package edu.kit.ifv.mobitopp.simulation.activityschedule;




import java.io.Serializable;
import java.util.List;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ModifiableActivityScheduleWithState;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.tour.TourAwareActivitySchedule;
import edu.kit.ifv.mobitopp.time.Time;


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
		PatternActivityWeek activityPattern,
		ActivityStartAndDurationRandomizer activityDurationRandomizer, 
		List<Time> dates
	) {
		super(activityDurationRandomizer, activityPattern, dates);
		
		this.startActivity(this.firstActivity());
	}


	public void setCurrentTrip(TripIfc trip) {

		this.currentOccupation = trip;		
	}

	public TripIfc currentTrip() {

		assert this.currentOccupation instanceof TripIfc : this.currentOccupation;

		return (TripIfc) this.currentOccupation;
	}

	public void startActivity(ActivityIfc activity) {
		this.currentOccupation = activity;
	}

	public ActivityIfc currentActivity() {

		assert this.currentOccupation instanceof ActivityIfc;

		return (ActivityIfc) this.currentOccupation;
	}



}

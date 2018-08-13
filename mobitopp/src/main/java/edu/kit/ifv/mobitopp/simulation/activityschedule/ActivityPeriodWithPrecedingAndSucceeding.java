package edu.kit.ifv.mobitopp.simulation.activityschedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.tour.TourAwareActivitySchedule;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;


public class ActivityPeriodWithPrecedingAndSucceeding 
	extends ActivityPeriod 
	implements Serializable
	, ActivitySequence
	, TourAwareActivitySchedule
{

	private static final long serialVersionUID = 1L;
	
	protected ActivityIfc lastActivityOfPrevPeriod;
	protected ActivityIfc firstActivityOfNextPeriod;


	public ActivityPeriodWithPrecedingAndSucceeding(	
		TourFactory tourFactory,
		ActivityStartAndDurationRandomizer durationRandomizer,
		PatternActivityWeek activityPattern,
		List<Time> dates
	) {
		super(tourFactory, durationRandomizer, activityPattern, dates);
		
		Time firstDay = dates.get(0);
		Time lastDay = dates.get(dates.size()-1);
		
		assert firstDay.weekDay() == DayOfWeek.MONDAY;
		
		this.lastActivityOfPrevPeriod = instantiateLastActivity(durationRandomizer,activityPattern, firstDay.previousDay());
		this.firstActivityOfNextPeriod = instantiateFirstActivity(durationRandomizer,activityPattern, lastDay.nextDay());
		
	}
	
	



	public ActivityIfc nextActivity(ActivityIfc activity) {
	
		if (activity == firstActivityOfNextPeriod) { return null; }
		if (activity == lastActivityOfPrevPeriod) { return super.firstActivity(); }
	
		ActivityIfc next = super.nextActivity(activity);
	
		return next != null ? next : firstActivityOfNextPeriod;
	}

	public ActivityIfc prevActivity(ActivityIfc activity) {
	
		if (activity == lastActivityOfPrevPeriod) { return null; }
		if (activity == firstActivityOfNextPeriod) { return super.lastActivity(); }
	
		ActivityIfc prev = super.prevActivity(activity);
	
		return prev != null ? prev : lastActivityOfPrevPeriod;
	}

	public boolean hasNextActivity(ActivityIfc activity) {
	
		return nextActivity(activity) != null;
	}

	public int activityNrByType(ActivityIfc activity) {
	
		ActivityType activityType = activity.activityType();
	
		int cnt = 0;
	
		for (ActivityIfc act = firstActivity(); act != activity; act = nextActivity(act)) {
	
			if ( act.activityType() == activityType && act.isLocationSet() )  { cnt++; }
		
		}
	
		return 1+cnt;
	}


	@Override
	public ActivityIfc nextHomeActivity(ActivityIfc currentActivity) {
		ActivityIfc act = super.nextHomeActivity(currentActivity);
		
		return act != null ? act : firstActivityOfNextPeriod;
	}

	public boolean contains(ActivityIfc activity) {
	
		return activity == lastActivityOfPrevPeriod
					|| activity == firstActivityOfNextPeriod
					|| super.contains(activity);
	}

	@Override
	public List<ActivityIfc> activitiesBetween(ActivityIfc activityBefore, ActivityIfc activityAfter) {
		
		assert contains(activityBefore);
		assert contains(activityAfter);
		
		List<ActivityIfc> result = new ArrayList<ActivityIfc>();
		
		ActivityIfc current = activityBefore;
		
		while(current != activityAfter) {
			current = nextActivity(current);
			
			if (current != activityAfter) {
				result.add(current);
			}
		}
		
		
		return result;
	}

}
package edu.kit.ifv.mobitopp.simulation.activityschedule;

import java.io.Serializable;
import java.util.List;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.ActivityAsLinkedListElement;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.tour.TourAwareActivitySchedule;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;


public class ActivityPeriodWithExtendedFirstAndLastActivity 
	extends ActivityPeriod 
	implements Serializable
	, ActivitySequence
	, TourAwareActivitySchedule
{

	private static final long serialVersionUID = 1L;
	


	public ActivityPeriodWithExtendedFirstAndLastActivity(	
		TourFactory tourFactory,
		ActivityStartAndDurationRandomizer durationRandomizer,
		PatternActivityWeek activityPattern,
		List<Time> dates
	) {
		super(tourFactory,durationRandomizer, activityPattern, dates);
		
		Time firstDay = dates.get(0);
		assert firstDay.weekDay() == DayOfWeek.MONDAY;
	}
	
	@Override
	protected void init(
			final ActivityStartAndDurationRandomizer durationRandomizer,
			final PatternActivityWeek origPatternWeek,
			final List<Time> dates
		) {
		
			super.init(durationRandomizer, origPatternWeek, dates);
		
			adjustFirstActivity(origPatternWeek);
			adjustLastActivity(origPatternWeek, dates);
		

			if (!this.checkOrderInvariant()) {
				this.fixStartTimeOfActivities();
			}

			if (!this.checkNonOverlappingInvariant()) {
				this.fixStartTimeOfActivities();
			}

			assert this.checkOrderInvariant();
			assert this.checkNonOverlappingInvariant();
			
		}
	
	
	private ActivityIfc createActivity(
		ActivityIfc activity,
		Time startDate,
		int duration,
		float startFlexibility,
		float endFlexibility,
		float durationFlexibiliy
	) {
		return new ActivityAsLinkedListElement(
				activity.getOid(),
				activity.getActivityNrOfWeek(),
				activity.activityType(),
				startDate,
				duration,
				activity.observedTripDuration(),
				startFlexibility,
				endFlexibility,
				durationFlexibiliy
			);
	}

	private void adjustFirstActivity(PatternActivityWeek origPatternWeek) {
		
		ActivityIfc firstActivity = firstActivity();
			
		PatternActivity lastOfWeek = origPatternWeek.last();
		
		Time startDate = firstActivity.startDate();
		int duration = firstActivity.duration();
		
		if (firstActivity.activityType().isHomeActivity() && lastOfWeek.getActivityType().isHomeActivity()) {
			startDate = startDate.plusMinutes(-lastOfWeek.getDuration());
			duration = duration + lastOfWeek.getDuration();
		}
		
		assert firstActivity.activityType().isHomeActivity() : firstActivity.activityType();

		ActivityIfc adjustedFirstActivity = createActivity(firstActivity,startDate,duration,0.0f,0.0f,0.9f);
		
		removeActivity(firstActivity);
		addAsFirstActivity(adjustedFirstActivity);		
	}

	private void adjustLastActivity(PatternActivityWeek origPatternWeek, List<Time> dates) {
		
		ActivityIfc lastActivity = lastActivity();
		
		Time lastDay = dates.get(dates.size()-1);
		Time startOfFirstDayOfNextWeek = lastDay.nextDay().startOfDay();
		
		PatternActivity firstOfNextDay = origPatternWeek.first();
		
		assert firstOfNextDay.getActivityType().isHomeActivity();
		
		// If both are home activities, change startDate and duration
		// Always for new activities: start and duration are flexibel, but the end is fixed
		
		Time startDate = lastActivity.startDate();
		int duration = lastActivity.duration();
		
		if (!lastActivity.activityType().isHomeActivity()) {
			ActivityIfc lastHomeActivity = createHomeActivityAsLastActivity(lastDay,lastActivity.calculatePlannedEndDate(),
					0.7f,0.7f,0.0f);
			addAsLastActivity(lastHomeActivity);
		} else {
      if (lastActivity.calculatePlannedEndDate().isBefore(startOfFirstDayOfNextWeek)) {
        Time calculatePlannedEndDate = lastActivity.calculatePlannedEndDate();
        RelativeTime difference = startOfFirstDayOfNextWeek
            .plusMinutes(3)
            .differenceTo(calculatePlannedEndDate);
        duration = lastActivity.duration() + Math.toIntExact(difference.toMinutes());
				
				assert duration > 0;
			}
			
			ActivityIfc adjustedLastActivity = createActivity(lastActivity,startDate,duration,0.7f,0.7f,0.0f);
			
			removeActivity(lastActivity);
			addAsLastActivity(adjustedLastActivity);
		}
		
	}

	private ActivityIfc createHomeActivityAsLastActivity(
		Time lastDay, 
		Time endOfPreviousActivity,
		float startFlexibility, float endFlexibility, float durationFlexibiliy
	) {
		
		Time startDate = endOfPreviousActivity.plusMinutes(60);
		Time endDate = lastDay.plusDays(1).plusHours(8);
		
		int duration = (int) Math.max(endDate.differenceTo(startDate).toMinutes(), 4*60);
		
		return new ActivityAsLinkedListElement(
				1, //oid
				1, // activityNr
				ActivityType.HOME,
				startDate,
				duration,
				60, // observedTripDuration()
				startFlexibility,
				endFlexibility,
				durationFlexibiliy
			);
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


}
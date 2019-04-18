package edu.kit.ifv.mobitopp.simulation.activityschedule;

import static java.util.Collections.emptyList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.ActivitySequenceAsLinkedList;
import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.LinkedListElement;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;
import edu.kit.ifv.mobitopp.simulation.tour.TourAwareActivitySchedule;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

public class ActivityPeriod extends ActivitySequenceAsLinkedList
	implements Serializable
	, ActivitySequence
	, TourAwareActivitySchedule

{
	private static final long serialVersionUID = 3468272442319865155L;

	
	private static int occupationCounter = 1;

	private final TourFactory tourFactory;
	
	
	public ActivityPeriod(TourFactory tourFactory) {
		super();
		
		this.tourFactory = tourFactory;
	}
	
	public ActivityPeriod(
		TourFactory tourFactory,
		ActivityStartAndDurationRandomizer durationRandomizer,
		PatternActivityWeek patternWeek,
		List<Time> dates
	) {
		this(tourFactory);
		init(durationRandomizer, patternWeek, dates);
	}
	
	protected void init(
			final ActivityStartAndDurationRandomizer durationRandomizer,
			final PatternActivityWeek origPatternWeek,
			final List<Time> dates
		) {
		
		PatternActivityWeek patternWeek = durationRandomizer.randomizeStartAndDuration(origPatternWeek);
		
		assert patternWeek.getPatternActivities().size() == origPatternWeek.getPatternActivities().size();
		
			for (Time currentDay : dates) {
			List<PatternActivity> patternActivities = patternWeek.getPatternActivities(currentDay);

			for (int i = 0; i < patternActivities.size(); i++) {
		      PatternActivity aPatternActivity = patternActivities.get(i);
		      
		      
		      if (aPatternActivity.getActivityType() != ActivityType.LEISURE_WALK) {
		      	createAndAddActivity(currentDay, aPatternActivity, i);
		      } else {
		      	
		      	if (i == patternActivities.size()-1) {
		      		createAndAddWalkActivityWithHomeActivity(currentDay, i, aPatternActivity);
		      	} else {
		      		PatternActivity nextPatternActivity = patternActivities.get(i+1);
		      		
		      		if (nextPatternActivity.getActivityType().isHomeActivity()) {
		      			createAndAddActivity(currentDay, aPatternActivity, i);
		      		} else {
		      			createAndAddWalkActivityWithHomeActivity(currentDay, i, aPatternActivity);
		      		}
		      	}
		      }
					
		    }
			}

			if (!this.checkOrderInvariant()) {
				this.fixStartTimeOfActivities();
			}

			if (!this.checkNonOverlappingInvariant()) {
				this.fixStartTimeOfActivities();
			}

			assert this.checkOrderInvariant();
			assert this.checkNonOverlappingInvariant();
	}

	protected void createAndAddWalkActivityWithHomeActivity(Time currentDay, int i,
			PatternActivity aPatternActivity) {
		List<ActivityIfc> acts = createWalkActivityWithHomeActivity(currentDay, aPatternActivity, i+1);
		assert acts.size() == 2;
		
		this.addAsLastActivity(acts.get(0));
		this.addAsLastActivity(acts.get(1));
	}

	protected void createAndAddActivity(Time currentDay, PatternActivity aPatternActivity, int i) {
		ActivityIfc activity = createActivity(currentDay, aPatternActivity, i+1);
		this.addAsLastActivity(activity);
	}



	public List<ActivityIfc> getSucceedingActivitiesUntilDate(ActivityIfc occupation, Time endDate) {

		List<ActivityIfc> l = new ArrayList<>();

		LinkedListElement tmp = nextActivity((LinkedListElement) occupation);
		if (tmp == null) {
			return emptyList();
		}

		while (tmp != tail && ((ActivityIfc) tmp).startDate().isBefore(endDate)) {

			l.add((ActivityIfc) tmp);
			tmp = tmp.next();
		}

		return l;
	}


	public void print() {
		System.out.println(toString());
	}

	public String toString() {

		LinkedListElement tmp = head;

		String s = "";

		while(tmp!=tail && next(tmp) != null) {

			tmp = next(tmp);

			s+= ((ActivityIfc) tmp).toString() + "\n";
		}

		s += "\n";

		return s;
	}


	public boolean checkOrderInvariant() {

		boolean ok = true;

		if (isEmpty()) { return true; }

		LinkedListElement prev = nextActivity(head);
		LinkedListElement curr = nextActivity(prev);

		while (curr != tail) {

			if (!((ActivityIfc)prev).startDate().isBefore(((ActivityIfc)curr).startDate())) {
			
				ok = false;
				break;
			}
		
			prev = curr;
			curr = nextActivity(curr);
		}

		return ok;
	}

	public boolean checkNonOverlappingInvariant() {

		boolean ok = true;

		if (isEmpty()) { return true; }

		LinkedListElement prev = nextActivity(head);
		LinkedListElement curr = nextActivity(prev);

		while (curr != tail) {


			if (((ActivityIfc)prev).calculatePlannedEndDate().isAfter(((ActivityIfc)curr).startDate())) {
			
				ok = false;
				break;
			}
		
			prev = curr;
			curr = nextActivity(curr);
		}

		return ok;
	}


	public void fixStartTimeOfActivities() {

		if (isEmpty()) { return; }

		LinkedListElement prev = nextActivity(head);
		LinkedListElement curr = nextActivity(prev);

		while (curr != tail) {

			ActivityIfc previousActivity = (ActivityIfc)prev;
      ActivityIfc currentActivity = (ActivityIfc)curr;
      if (!previousActivity.startDate().isBefore(currentActivity.startDate())
					|| previousActivity.calculatePlannedEndDate().isAfter(currentActivity.startDate())) {

        int observedTripDuration = Math.max(0, currentActivity.observedTripDuration());
        int increment = previousActivity.duration() + observedTripDuration;
        Time fixedDate = previousActivity.startDate().plusMinutes(increment);

				currentActivity.setStartDate(fixedDate);
			}

			prev = curr;
			curr = nextActivity(curr);
		}
	}


	

  protected static ActivityIfc instantiateLastActivity(
  		ActivityStartAndDurationRandomizer durationRandomizer,
			PatternActivityWeek patternWeek,
			Time day
	) {

		List<PatternActivity> patternActivities = patternWeek.getPatternActivities();
		PatternActivity patternActivity = patternActivities.get(patternActivities.size()-1);

		patternActivity = durationRandomizer.randomizeStartAndDuration(patternActivity);
		
    ActivityIfc activity = createActivity(day, patternActivity, 0);

		return activity;
	}
  
 

  protected static ActivityIfc instantiateFirstActivity(
  		ActivityStartAndDurationRandomizer durationRandomizer,
			PatternActivityWeek patternWeek,
			Time day
	) {

		PatternActivity patternActivity = getFirstPatternActivityOfDay(patternWeek, day.weekDay());

		patternActivity = durationRandomizer.randomizeStartAndDuration(patternActivity);
		
		ActivityIfc activity = createActivity(day, patternActivity, 0);

		return activity;
	}

 




	private static PatternActivity getFirstPatternActivityOfDay(
		PatternActivityWeek patternWeek,
		DayOfWeek weekDay
	) {

			for (PatternActivity act : 
							patternWeek.getPatternActivities()) {

				if (act.getWeekDayTypeAsInt() >= weekDay.getTypeAsInt()) {

					return act;
				}
			}

		return patternWeek.getPatternActivities().get(0);
	}

	private static ActivityIfc createActivity(
			Time date,
      PatternActivity patternActivity_,
      int activityNumber
  ) {

		assert patternActivity_ != null;

    int hour   = patternActivity_.startTime().getHour();
    int minute = patternActivity_.startTime().getMinute();  

		Time startTime = date.newTime(hour, minute, 0);
		
		assert date.weekDay() == startTime.weekDay() : (date.weekDay() + " " + startTime.weekDay()
				+ "\n" + date + " : " + startTime);


    ActivityType activityType = patternActivity_.getActivityType();
		int duration = patternActivity_.getDuration();
		int tripDuration = patternActivity_.getObservedTripDuration();
		
		ActivityIfc activity = ActivitySequenceAsLinkedList.newActivity(occupationCounter++, activityNumber, 
																																		startTime, activityType, duration, tripDuration);

    return activity;
  }
	
	private List<ActivityIfc> createWalkActivityWithHomeActivity(
			Time currentDay,
			PatternActivity patternActivity, 
			int activityNumber
	) {
		
		int hour   = patternActivity.startTime().getHour();
    int minute = patternActivity.startTime().getMinute();  

		Time startTimeWalk = currentDay.newTime(hour, minute, 0);
		
		assert currentDay.weekDay() == startTimeWalk.weekDay() : (currentDay.weekDay() + " : " + startTimeWalk.weekDay() );
			
		int durationHome = patternActivity.getDuration();
		int durationWalk = patternActivity.getObservedTripDuration();
		
		Time startTimeHome = startTimeWalk.plusMinutes(durationWalk+1);
		
		List<ActivityIfc> activities = new ArrayList<ActivityIfc>();
			
		ActivityIfc walk = ActivitySequenceAsLinkedList.newActivity(occupationCounter++, activityNumber, 
				startTimeWalk, ActivityType.LEISURE_WALK, durationWalk, 1);

		ActivityIfc home = ActivitySequenceAsLinkedList.newActivity(occupationCounter++, activityNumber+1, 
				startTimeHome, ActivityType.HOME, durationHome, 1);

		activities.add(walk);
		activities.add(home);
			
		return activities;
	}

	@Override
	public int numberOfTours() {
			
		ActivityIfc current = firstActivity();
		
		int cnt = current.activityType().isHomeActivity() ? 0 : 1;
		
		while(current != lastActivity()) {
			
			if (current.activityType().isHomeActivity()) {
				cnt++;
			}
			current = nextActivity(current);
		}
		
		return cnt;
	}

	@Override
	public boolean isStartOfTour(ActivityIfc activity) {

		ActivityIfc prev = prevActivity(activity);
			
		return prev == null ? true : prev.activityType().isHomeActivity();
	}

	@Override
	public Tour correspondingTour(ActivityIfc activity) {
		
		ActivityIfc endOfTour = activity;
		ActivityIfc beginOfTour = activity;
		
		while(endOfTour != lastActivity()
					&& !endOfTour.activityType().isHomeActivity()) {
			endOfTour = nextActivity(endOfTour);
		}
		
		while(beginOfTour != firstActivity()
						&& !isStartOfTour(beginOfTour)) {
			beginOfTour = prevActivity(beginOfTour);
		}
		
		return this.tourFactory.createTour(beginOfTour, endOfTour, this);
	}
	
	@Override
	public Tour firstTour() {
		
		ActivityIfc beginOfTour = firstActivity();
		
		while(beginOfTour.activityType().isHomeActivity() &&  beginOfTour!= lastActivity()) {
			beginOfTour = nextActivity(beginOfTour);
		}
		
		return correspondingTour(beginOfTour);
	}

	@Override
	public ActivityIfc startOfFirstTour() {
	
		ActivityIfc first = firstActivity();
		
		assert nextActivity(first) != null;
		
		return !first.activityType().isHomeActivity() ? first : nextActivity(first);
	}

	public boolean hasNextActivity(ActivityIfc activity) {

		return nextActivity(activity) != null;
	}
	
	public boolean hasPrevActivity(ActivityIfc activity) {

		return prevActivity(activity) != null;
	}

	public int activityNrByType(ActivityIfc activity) {

		ActivityType activityType = activity.activityType();

		int cnt = 0;

		for (ActivityIfc act = firstActivity(); act != activity; act = nextActivity(act)) {

			if ( act.activityType() == activityType && act.isLocationSet() )  { cnt++; }
		
		}

		return 1+cnt;
	}

	public Set<ZoneId> alreadyVisitedZonesByActivityType(ActivityIfc activity) {

		TreeSet<ZoneId> zones = new TreeSet<>();

		for (ActivityIfc act = firstActivity(); act != activity; act = nextActivity(act)) {

      if ( act.activityType() == activity.activityType() && act.isLocationSet() ) {

        zones.add(act.zone().getInternalId());
      }

		}

		return zones;
	}

	public ActivityIfc nextHomeActivity(ActivityIfc currentActivity) {
		
		assert hasNextActivity(currentActivity) : ("\n" + this + "\ncurrent:" + currentActivity);
		
		ActivityIfc act = nextActivity(currentActivity);
		if (act.activityType().isHomeActivity()) { return act; }
		
		while(hasNextActivity(act)) { 
			act = nextActivity(act);
			if (act.activityType().isHomeActivity()) { return act; }
		}
		
		System.out.println("\n" + this + "\ncurrent:" + currentActivity + "\nlasttested: " + act
												+ "\nisHomeActivity? " + act.activityType().isHomeActivity());
		throw new AssertionError();
	}

	@Override
	public Map<Mode, Integer> alreadyUsedTourmodes(ActivityIfc activity) {
		
		LinkedHashMap<Mode,Integer> modes = new LinkedHashMap<Mode,Integer>();

		for (ActivityIfc act = firstActivity(); act != activity; act = nextActivity(act)) {

      if ( isStartOfTour(act) && act.isModeSet() ) {
      	
      	Mode mode = act.mode();
      	
      	if (!modes.containsKey(mode)) {
      		modes.put(mode, 0);
      	}
      	modes.put(mode, 1+modes.get(mode));
      }
		}

		return modes;
	}
	


 
}

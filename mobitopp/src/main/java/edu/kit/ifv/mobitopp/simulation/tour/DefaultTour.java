package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public class DefaultTour 
	implements Tour 
{
	
	private final ActivityIfc first;
	private final ActivityIfc last;
	private final TourAwareActivitySchedule schedule;
	
	public DefaultTour(
		ActivityIfc first, 
		ActivityIfc last,
		TourAwareActivitySchedule schedule
	) {
		assert first != null;
		assert last != null;
		
		this.first = first;
		this.last = last;
		this.schedule=schedule;
		
		List<ActivityIfc> tourList = asList();
		
		assert tourList.size() >= 1 : (tourList.size() + "\n" + this.first + "\n" + this.last);
		
		assert tourList.get(0) == first;
		assert tourList.get(tourList.size()-1) == last;
	}

	@Override
	public int numberOfTrips() {
		
		ActivityIfc current = first;
		
		int cnt = 1;
		
		while(current != last) {
			current = schedule.nextActivity(current);
			cnt++;
		}
		return cnt;
	}

	@Override
	public ActivityIfc firstActivity() {
		
		return first;
	}

	@Override
	public ActivityIfc lastActivity() {
		
		return last;
	}
	
	@Override
	public ActivityIfc mainActivity() {
		
		ActivityType purpose = purpose();
	
		Optional<ActivityIfc> mainActivity = activityWithMaxDuration(purpose);
		
		assert mainActivity.isPresent();
	
		return mainActivity.get();
	}
	

	
	@Override
	public ActivityType purpose() {
		
		if (containsActivityOf(ActivityType.WORK)) { return ActivityType.WORK; }
		if (containsActivityOf(ActivityType.EDUCATION)) { return ActivityType.EDUCATION; }
		if (containsActivityOf(ActivityType.SERVICE)) { return ActivityType.SERVICE; }

		return activityWithMaxDuration().activityType();
	}

	protected ActivityIfc activityWithMaxDuration() {
		
		ActivityIfc current = first;
		
		ActivityIfc maxDurationActivity = first;
		int maxDuration = current.duration();
		
		while(current != last) {
			
			if (current.duration() > maxDuration && !current.activityType().isHomeActivity()) {
				maxDuration = current.duration();
				maxDurationActivity = current;
			}
			
			current = schedule.nextActivity(current);
		}
		return maxDurationActivity;
	}
	
	protected Optional<ActivityIfc> activityWithMaxDuration(ActivityType activityType) {
	
		Optional<ActivityIfc> first = firstActivityOf(activityType);
		
		if (!first.isPresent()) {
				return Optional.empty();
		}
		
		ActivityIfc current =  first.get();
		
		ActivityIfc maxDurationActivity = current;
		int maxDuration = current.duration();
		
		while(current != last) {
			current = schedule.nextActivity(current);
			
			if (current.activityType() == activityType && current.duration() > maxDuration) {
				maxDuration = current.duration();
				maxDurationActivity = current;
			}
		}
		return Optional.of(maxDurationActivity);
	}
	
	

	@Override
	public boolean containsActivityOf(ActivityType type) {
		
		ActivityIfc current = first;
		
		if (current.activityType() == type) return true;
		
		while(current != last) {
			current = schedule.nextActivity(current);
			
			if (current.activityType() == type) return true;
		}

		return false;
	}
	
	@Override
	public boolean contains(ActivityIfc activity) {

		ActivityIfc current = first;
		
		if (current == activity) return true;
		
		while(current != last) {
			current = schedule.nextActivity(current);
			
			if (current == activity) return true;
		}

		return false;
	}
	
	public boolean equals(Object o) {
		
		if (!(o instanceof DefaultTour)) return false;
		
		DefaultTour other = (DefaultTour) o;

		return first==other.first && last==other.last;
	}
	
	public int hashCode() {
		
		return first.hashCode() + last.hashCode();
	}
	
	public String toString() {
		
		return  "(" + asList().stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
	}

	@Override
	public boolean isFirstActivity(ActivityIfc activity) {
		return activity == first;
	}

	@Override
	public Mode mode() {
		return first.mode();
	}

	@Override
	public List<ActivityIfc> asList() {
		
		List<ActivityIfc> list = new ArrayList<ActivityIfc>();
		
		list.add(first);
				
		ActivityIfc current = first;
		
		assert current != null;
				
		while(current != last) {
			ActivityIfc prev = current;
			current = schedule.nextActivity(current);
			assert current != null : (first + "\n" + last + "\n" + prev + "\n" + list);
			
			list.add(current);
		}
		
		return list;
	}
	
	@Override
	public List<ActivityIfc> activitiesBetween(ActivityIfc previousActivity, ActivityIfc mainActivity){
		return schedule.activitiesBetween(previousActivity, mainActivity);
	}

	@Override
	public boolean hasPreviousTour() {
		
		return schedule.hasPrevActivity(first) 
				&& schedule.hasPrevActivity(schedule.prevActivity(first));
	}

	@Override
	public Tour previousTour() {
		
		if (!hasPreviousTour()) { 
			throw new AssertionError("no previous Tour found -- use 'hasPreviousTour' to check");
		}
		
		ActivityIfc prev = schedule.prevActivity(first);
	
		assert prev != null;
		
		return schedule.correspondingTour(prev);
	}

	@Override
	public int tourNumber() {
		
		return 1 + (hasPreviousTour() ? previousTour().tourNumber() : 0);
	}
	
	
	private Map<ActivityType, Integer> numberOfActivitiesByType() {
		
		Map<ActivityType, Integer> activitiesByType = new LinkedHashMap<ActivityType, Integer>();
		
		ActivityIfc current = first;
		
		while(current != last) {
			ActivityType type = current.activityType();
			
			Integer cnt = activitiesByType.getOrDefault(type, 0);
			
			activitiesByType.put(type, 1+cnt);
			
			current = schedule.nextActivity(current);
		}
		
		return activitiesByType;
	}
	
	private boolean canHaveSubtours(ActivityType purpose) {
	
		return purpose == ActivityType.WORK
					|| purpose == ActivityType.EDUCATION
					|| purpose == ActivityType.SERVICE
					|| purpose == ActivityType.PRIVATE_VISIT
			;
	}

	@Override
	public boolean containsSubtour() {
		
		return numberOfSubtours() > 0;
	}

	@Override
	public int numberOfSubtours() {
	
		final ActivityType purpose = purpose();
	
		Map<ActivityType, Integer> activitiesByType = numberOfActivitiesByType();
		
		if (activitiesByType.getOrDefault(purpose, 0) >= 2 && canHaveSubtours(purpose)) {
			
			int numSubtours = 0;
			
			int numActivities = activitiesByType.get(purpose);
			assert numActivities >= 2;
			
			ActivityIfc current = firstActivityOf(purpose).get();
			ActivityIfc next 		=  nextActivityOf(purpose,current).get();
			
			for(int i=0;i<numActivities-1;i++) {
				
				if (!activitiesBetween(current, next).isEmpty()) {
					numSubtours++;
				
					current = next;
					next 		=  nextActivityOf(purpose,current).orElse(next);
					
				} else {
					
					next 		=  nextActivityOf(purpose,next).orElse(next);
				}
			}
			
			return numSubtours;
		}
		
		return 0;
	}
	
	

	@Override
	public Subtour nthSubtour(int n) {
		
		if (n >= numberOfSubtours())  {
			throw new IndexOutOfBoundsException();
		}
		
		ActivityType purpose = purpose();
		
		ActivityIfc firstOccurance = firstActivityOf(purpose).get();
		
		assert nextActivityOf(purpose,firstOccurance).isPresent() : (purpose + "\n" + firstOccurance + "\n" + this);
		
		ActivityIfc secondOccurance = nextActivityOf(purpose,firstOccurance).get();
		
		
		
		for(int cnt=0; cnt<n; cnt++) {
			
			firstOccurance = secondOccurance;
			
			assert nextActivityOf(purpose,firstOccurance).isPresent() : (purpose + "\n" + firstOccurance + "\n" + this);
			
			secondOccurance = nextActivityOf(purpose,firstOccurance).get();
		}
		
		
		assert schedule.hasNextActivity(firstOccurance);
		
		return new Subtour(1+n, schedule.nextActivity(firstOccurance),secondOccurance,schedule);
	}

	
	protected Optional<ActivityIfc> firstActivityOf(ActivityType type, ActivityIfc start) {
		
		ActivityIfc current = start;
		
		if (current.activityType() == type) return Optional.of(current);
		
		while(current != last) {
			current = schedule.nextActivity(current);
			
			if (current.activityType() == type) return Optional.of(current);
		}

		return Optional.empty();
	}
	
	@Override
	public Optional<ActivityIfc> firstActivityOf(ActivityType type) {
	
		return firstActivityOf(type,first);
	}

	@Override
	public Optional<ActivityIfc> nextActivityOf(ActivityType type, ActivityIfc currentActivity) {
		
		assert schedule.hasNextActivity(currentActivity);
		
		return firstActivityOf(type, schedule.nextActivity(currentActivity));
	}

	@Override
	public boolean hasNextActivityOf(ActivityType type, ActivityIfc currentActivity) {
	
		return nextActivityOf(type,currentActivity).isPresent();
	}

	@Override
	public boolean isStartOfSubtour(ActivityIfc activity) {
		
		for(int i=0; i<numberOfSubtours();i++) {
			Subtour st = nthSubtour(i);
				
			if(st.firstActivity() == activity) { return true; }
		}
		
		return false;
	}

	@Override
	public boolean isEndOfSubtour(ActivityIfc activity) {
		
		for(int i=0; i<numberOfSubtours();i++) {
			Subtour st = nthSubtour(i);
				
			if(st.lastActivity() == activity) { return true; }
		}
		
		return false;
	}

	@Override
	public boolean isInSubtour(ActivityIfc activity) {
		
		return correspondingSubtour(activity).isPresent();
	}

	@Override
	public Optional<Subtour> correspondingSubtour(ActivityIfc activity) {
		
		if (!contains(activity)) {
			throw new NoSuchElementException();
		}
		
		for(int i=0; i<numberOfSubtours();i++) {
			Subtour st = nthSubtour(i);
				
			if(st.contains(activity)) { return Optional.of(st); }
		}
	
		return Optional.empty();
	}

	@Override
	public String forLogging() {
		
		return "Tour("
							+ tourNumber() + "," 
							+ purpose() + "," 
							+ mode() + "," 
							+ numberOfTrips() + "," 
							+ containsSubtour() + "," 
							+ numberOfSubtours() + ","
							+ firstActivity()
					+ ")";
	}
	


}

package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public class Subtour 
	extends DefaultTour
	implements Tour 
{
	
	final int number;
	
	public Subtour(
		int number,
		ActivityIfc first, 
		ActivityIfc last,
		TourAwareActivitySchedule schedule
	) {
		super(first, last, schedule);
		
		this.number = number;
	}


	/*
	@Override
	public boolean hasPreviousTour() {
		
		return schedule.hasPrevActivity(first) 
				&& schedule.hasPrevActivity(schedule.prevActivity(first));
	}
	*/

	/*
	@Override
	public Tour previousTour() {
		
		if (!hasPreviousTour()) { 
			throw new AssertionError("no previous Tour found -- use 'hasPreviousTour' to check");
		}
		
		ActivityIfc prev = schedule.prevActivity(first);
	
		assert prev != null;
		
		return schedule.correspondingTour(prev);
	}
	*/

	@Override
	public int tourNumber() {

		return this.number;
	}
	
	@Override
	public boolean containsSubtour() {
		
		return false;
	}

	@Override
	public int numberOfSubtours() {
	
		return 0;
	}

	@Override
	public Subtour nthSubtour(int n) {
		
		throw new UnsupportedOperationException();
	}

	@Override
	public ActivityIfc mainActivity() {
		
		ActivityType purpose = purpose();
		
		// Optional<ActivityIfc> mainActivity = firstActivity(purpose);
		Optional<ActivityIfc> mainActivity = activityWithMaxDuration(purpose);
		
		assert mainActivity.isPresent();
	
		return mainActivity.get();
	}
	

	
	@Override
	public ActivityType purpose() {
		

		return activityWithMaxDuration().activityType();
	}
	
	@Override
	public String forLogging() {
			return "Sub" + super.forLogging();
	}

	


}

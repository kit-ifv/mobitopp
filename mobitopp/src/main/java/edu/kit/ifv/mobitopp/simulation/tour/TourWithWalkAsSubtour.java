package edu.kit.ifv.mobitopp.simulation.tour;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TourWithWalkAsSubtour extends DefaultTour 
	implements Tour 
{
	
	public TourWithWalkAsSubtour(
		ActivityIfc first, 
		ActivityIfc last,
		TourAwareActivitySchedule schedule
	) {
		super(first, last, schedule);
	}


	@Override
	public int numberOfSubtours() {
		
		final ActivityType tourPurpose = purpose();
		
		boolean canHaveRegularSubtour = numberOfActivitiesByType().getOrDefault(tourPurpose, 0) >= 2 && canHaveSubtours(tourPurpose);
		
		int numSubtours = 0;
	
		for(ActivityIfc current = firstActivity(); current!=lastActivity(); current=nextActivity(current)) {
			
			if (current != firstActivity() && current.activityType() == ActivityType.LEISURE_WALK) {
				numSubtours++;
			}
		
			if (canHaveRegularSubtour) {
				if(current.activityType() == tourPurpose && nextActivityOf(tourPurpose,current).isPresent()) {
					assert nextActivityOf(tourPurpose,current).isPresent() : (current + "\n" + purpose() + "\n" + this);
					current 		=  nextActivityOf(tourPurpose,current).get();	// skip activities in subtour
					
					numSubtours++;
				}
			}
		}
			
		return numSubtours;
	}
	
	

	@Override
	public Subtour nthSubtour(int n) {
		
		if (n >= numberOfSubtours())  {
			throw warn(new IndexOutOfBoundsException(), log);
		}
		
		
		final ActivityType tourPurpose = purpose();
		
		boolean canHaveRegularSubtour = numberOfActivitiesByType().getOrDefault(tourPurpose, 0) >= 2 && canHaveSubtours(tourPurpose);
		
		int numSubtours = 0;
		
	
		for(ActivityIfc current = firstActivity(); current!=lastActivity(); current=nextActivity(current)) {
			
			if (current != firstActivity() && current.activityType() == ActivityType.LEISURE_WALK) {
				if (n == numSubtours) {
					return createSubtour(1+n, current, current);
				}
				numSubtours++;
			}
		
			if (canHaveRegularSubtour) {
				if(current.activityType() == tourPurpose && nextActivityOf(tourPurpose,current).isPresent()) {
					ActivityIfc next =  nextActivityOf(tourPurpose,current).get();	
				
					if (n == numSubtours) {
						return createSubtour(1+n, nextActivity(current), next);
					}
					current 		=  next;	// skip activities in subtour
					numSubtours++;
				}
			}
		}

		throw new AssertionError();
	}



}

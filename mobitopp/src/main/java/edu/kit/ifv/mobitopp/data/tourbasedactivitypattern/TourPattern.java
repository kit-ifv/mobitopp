package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class TourPattern implements TourBasedActivityPatternElement {
	
	public final DayOfWeek day;
	public final Activity mainActivity;
	public final List<Activity> outboundTripActivities;
	public final List<Activity> inboundTripActivities;
	public final List<List<Activity>> subtourActivities;
	
	private static final RelativeTime TRIP_OFFSET = RelativeTime.ofMinutes(30);
	
	public TourPattern(
		DayOfWeek day,
		Activity mainActivity, 
		List<Activity> outboundTripActivities,
		List<Activity> inboundTripActivities,
		List<List<Activity>> subtourActivities
	) {
		this.day = day;
		this.mainActivity = mainActivity;
		this.outboundTripActivities = Collections.unmodifiableList(outboundTripActivities);
		this.inboundTripActivities = Collections.unmodifiableList(inboundTripActivities);
		this.subtourActivities = Collections.unmodifiableList(subtourActivities);
	}

	@Override
	public DayOfWeek startDay() {
		return day;
	}
	
	@Override
	public String toString() {
		return "TourPattern( " + day + ", " + mainActivity 
	//			+ ", " + expectedEnd 
				+ ", ["  
					+  outboundTripActivities.size() + "," 
					+  inboundTripActivities.size() + "," 
					+  subtourActivities.size() 
//					+ "\n" +  outboundTripActivities
//					+ "\n" +  inboundTripActivities
//					+ "\n" +  subtourActivities
				+ "])";
	}

	@Override
	public Time start() {
		
		if (!outboundTripActivities.isEmpty()) {
			Activity next = outboundTripActivities.get(0);
			return next.plannedStart().minus(next.expectedTripDuration());
		}
		
		return mainActivity.plannedStart().minus(mainActivity.expectedTripDuration());
	}
	
	@Override
	public List<Activity> asActivities() {
		List<Activity> activities = new ArrayList<Activity>();
		
		activities.addAll(outboundTripActivities);
		
		if (numberOfSubtours() == 0) {
			activities.add(mainActivity);
		} else {
			
			assert mainActivity instanceof SplitActivity;
			
			assert numberOfSubtours()+1 == ((SplitActivity)mainActivity).parts().size();

			List<SimpleActivity> parts =  ((SplitActivity)mainActivity).parts();
			
			for(int i=0; i<numberOfSubtours(); i++) {
				activities.add(parts.get(i));
				
				for(Activity subtourActivity :  subtourActivities.get(i)) {
					activities.add(subtourActivity);
				}
				
			}
			activities.add(parts.get(numberOfSubtours()+1));
			
		}
		
		activities.addAll(inboundTripActivities);
	
		return activities;
	}
	
	private int numberOfSubtours() {
		return subtourActivities.size();
	}

	@Override
	public Collection<ExtendedPatternActivity> asPatternActivities(int tournr) {
		List<ExtendedPatternActivity> activities = new ArrayList<ExtendedPatternActivity>();
		
		for(Activity act : outboundTripActivities) {
			activities.add(ExtendedPatternActivity.fromActivity(tournr, false, act));
		}
		
		if (numberOfSubtours() == 0) {
			activities.add(ExtendedPatternActivity.fromActivity(tournr, true, mainActivity));
		} else {
			
			assert mainActivity instanceof SplitActivity;
			
			assert numberOfSubtours()+1 == ((SplitActivity)mainActivity).parts().size();

			List<SimpleActivity> parts =  ((SplitActivity)mainActivity).parts();
			
			for(int i=0; i<numberOfSubtours(); i++) {
				activities.add(ExtendedPatternActivity.fromActivity(tournr, true, parts.get(i)));
				// activities.add(parts.get(i));
				
				for(Activity subtourActivity :  subtourActivities.get(i)) {
					activities.add(ExtendedPatternActivity.fromActivity(tournr, false, subtourActivity));
					// activities.add(subtourActivity);
				}
				
			}
			activities.add(ExtendedPatternActivity.fromActivity(tournr, true, parts.get(numberOfSubtours()+1)));
//			activities.add(parts.get(numberOfSubtours()+1));
			
		}
		
		for(Activity act : inboundTripActivities) {
			activities.add(ExtendedPatternActivity.fromActivity(tournr, false, act));
		}
	
		return activities;
	}
	
	//TODO: testen !!!!!!!!
	public static TourPattern fromExtendedPatternActivities(List<ExtendedPatternActivity> activities) {
	
		List<ExtendedPatternActivity> partsOfMainActivity = findPartsOfMainActivity(activities);
		assert !partsOfMainActivity.isEmpty();
		
		Activity mainActivity =  partsOfMainActivity.size()==1 
					? SimpleActivity.fromPatternActivity(partsOfMainActivity.get(0))
					: SplitActivity.fromPatternActivities(partsOfMainActivity.get(0).getActivityType(),partsOfMainActivity) ;
		DayOfWeek day 	= mainActivity.plannedStart().weekDay();
		
		List<Activity> outboundActivities = activitiesBefore(partsOfMainActivity.get(0), activities);
		List<Activity> inboundActivities = activitiesAfter(partsOfMainActivity.get(partsOfMainActivity.size()-1), activities);
		List<List<Activity>> subtourActivities = activitiesOnSubtours(partsOfMainActivity, activities);
		
		return new TourPattern(day, mainActivity, outboundActivities, inboundActivities, subtourActivities);
		
	}

	private static List<List<Activity>> activitiesOnSubtours(List<ExtendedPatternActivity> partsOfMainActivity,
			List<ExtendedPatternActivity> activities) {
		assert !partsOfMainActivity.isEmpty();
		
		if (partsOfMainActivity.size()==1) {
			return new ArrayList<List<Activity>>();
		}
		
		assert partsOfMainActivity.size() >= 2;
		
		List<List<Activity>> results = new ArrayList<List<Activity>>();
		
		for(int i=0; i< partsOfMainActivity.size()-1; i++) {
			
			ExtendedPatternActivity first = partsOfMainActivity.get(i);
			ExtendedPatternActivity next = partsOfMainActivity.get(i+1);
			
			List<ExtendedPatternActivity> subtour = activities.subList(1+activities.indexOf(first), activities.indexOf(next));
			
			results.add(
					subtour.stream().map(x -> SimpleActivity.fromPatternActivity(x)).collect(Collectors.toList())
				);
		}
		
		return results;
	}

	private static List<Activity> activitiesAfter(ExtendedPatternActivity mainActivity,
			Collection<ExtendedPatternActivity> activities) {
		
		List<ExtendedPatternActivity> reversed = new LinkedList<>(activities);
		Collections.reverse(reversed);
		
		List<Activity> result = new ArrayList<Activity>();
		
		for (ExtendedPatternActivity act : reversed) {
			if(act==mainActivity) { break; }
			
			result.add(SimpleActivity.fromPatternActivity(act));
		}
		
		Collections.reverse(result);
		
		return result;
	}

	private static List<Activity> activitiesBefore(ExtendedPatternActivity mainActivity,
		Collection<ExtendedPatternActivity> activities) {
		
		List<Activity> result = new ArrayList<Activity>();
		
		for (ExtendedPatternActivity act : activities) {
			if(act==mainActivity) { break; }
			
			result.add(SimpleActivity.fromPatternActivity(act));
		}
		
		return result;
	}

	private static List<ExtendedPatternActivity> findPartsOfMainActivity(Collection<ExtendedPatternActivity> activities) {

		return activities.stream().filter(x -> x.isMainActivity()).collect(Collectors.toList());
	}

}

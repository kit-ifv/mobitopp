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
		
		assert mainActivity instanceof SimpleActivity && subtourActivities.isEmpty()
				|| mainActivity instanceof SplitActivity && !subtourActivities.isEmpty();
		
		assert day == mainActivity.plannedStart().weekDay() : (day + " + " + mainActivity.plannedStart().weekDay() ) ;
		assert day.equals(mainActivity.plannedStart().weekDay());
		
		assert mainActivity instanceof SimpleActivity ||  day == subtourActivities.get(0).get(0).plannedStart().weekDay();
		
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((inboundTripActivities == null) ? 0 : inboundTripActivities.hashCode());
		result = prime * result + ((mainActivity == null) ? 0 : mainActivity.hashCode());
		result = prime * result + ((outboundTripActivities == null) ? 0 : outboundTripActivities.hashCode());
		result = prime * result + ((subtourActivities == null) ? 0 : subtourActivities.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TourPattern other = (TourPattern) obj;
		if (day != other.day)
			return false;
		if (inboundTripActivities == null) {
			if (other.inboundTripActivities != null)
				return false;
		} else if (!inboundTripActivities.equals(other.inboundTripActivities))
			return false;
		if (mainActivity == null) {
			if (other.mainActivity != null)
				return false;
		} else if (!mainActivity.equals(other.mainActivity))
			return false;
		if (outboundTripActivities == null) {
			if (other.outboundTripActivities != null)
				return false;
		} else if (!outboundTripActivities.equals(other.outboundTripActivities))
			return false;
		if (subtourActivities == null) {
			if (other.subtourActivities != null)
				return false;
		} else if (!subtourActivities.equals(other.subtourActivities))
			return false;
		return true;
	}

	@Override
	public List<ExtendedPatternActivity> asPatternActivities(int tournr) {
		List<ExtendedPatternActivity> activities = new ArrayList<ExtendedPatternActivity>();
		
		for(Activity act : outboundTripActivities) {
			activities.add(ExtendedPatternActivity.fromActivity(tournr, false, act));
		}
		
		if (numberOfSubtours() == 0) {
			activities.add(ExtendedPatternActivity.fromActivity(tournr, true, mainActivity));
		} else {
			
			assert mainActivity instanceof SplitActivity;
			
			assert numberOfSubtours()+1 == ((SplitActivity)mainActivity).parts().size() : 
				(numberOfSubtours() + " : " + ((SplitActivity)mainActivity).parts().size() 
						+ "\n" + this );

			List<SimpleActivity> parts =  ((SplitActivity)mainActivity).parts();
			
			for(int i=0; i<numberOfSubtours(); i++) {
				activities.add(ExtendedPatternActivity.fromActivity(tournr, true, parts.get(i)));
				// activities.add(parts.get(i));
				
				for(Activity subtourActivity :  subtourActivities.get(i)) {
					activities.add(ExtendedPatternActivity.fromActivity(tournr, false, subtourActivity));
					// activities.add(subtourActivity);
				}
				
			}
			activities.add(ExtendedPatternActivity.fromActivity(tournr, true, parts.get(parts.size()-1)));
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
		assert !partsOfMainActivity.isEmpty() : activities;
		
		assert partsOfMainActivity.size() == 1 || !partsOfMainActivity.get(0).equals(partsOfMainActivity.get(1)) : activities;
		
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
		
		assert partsOfMainActivity.size() == 1 || !partsOfMainActivity.get(0).equals(partsOfMainActivity.get(1)) : partsOfMainActivity;
		
		if (partsOfMainActivity.size()==1) {
			return new ArrayList<List<Activity>>();
		}
		
		assert partsOfMainActivity.size() >= 2;
		
		List<List<Activity>> results = new ArrayList<List<Activity>>();
		
		for(int i=0; i< partsOfMainActivity.size()-1; i++) {
			
			ExtendedPatternActivity first = partsOfMainActivity.get(i);
			ExtendedPatternActivity next = partsOfMainActivity.get(i+1);
			
			assert !first.equals(next) : partsOfMainActivity;
	
			/*
			assert activities.indexOf(first) < activities.indexOf(next) 
				: (activities.indexOf(first) + " : " + activities.indexOf(next)
						+ "\n same?" + (first==next)
						+ "\n equals?" + (first.equals(next))
						+ "\n first=" + first
						+ "\n next=" + next
						+ "\n first_idx=" + partsOfMainActivity.indexOf(first)
						+ "\n next_idx=" + partsOfMainActivity.indexOf(next)
						);
						*/
			
			assert activities.indexOf(first) < activities.indexOf(next) 
				: (activities.indexOf(first) + " : " + activities.indexOf(next)
						+ "\n" + first + "\n" + next + "\n" + partsOfMainActivity + "\n" + activities);
			
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

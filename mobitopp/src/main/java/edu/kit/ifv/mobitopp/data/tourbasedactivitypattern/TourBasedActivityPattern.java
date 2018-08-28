package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.SimpleTime;

public class TourBasedActivityPattern {
	
	private List<TourBasedActivityPatternElement>  elements;
	
	public TourBasedActivityPattern(List<TourBasedActivityPatternElement> elements) {
		this.elements  = new ArrayList<TourBasedActivityPatternElement>(elements);
	}
	
	@Override
	public String toString() {
		String result = "TourBasedActivityPattern(";
			
		for(TourBasedActivityPatternElement elem :elements ) {
			result += "\n\t" + elem.toString();
		}
			result += "\n)\n";
			
		return result;
	}
	
	public List<Activity> asActivities() {
		
		List<Activity> activities = new ArrayList<Activity>();
		
		if (elements.isEmpty()) {
			activities.add(new SimpleActivity(ActivityType.HOME, SimpleTime.ofHours(0), RelativeTime.INFINITE, RelativeTime.INFINITE));
			return activities;
		}
		

		for(TourBasedActivityPatternElement elem : elements) {
			
			List<Activity> elemActivities = elem.asActivities();
			
			activities.addAll(elemActivities);
		}
		
		return activities;
	}
	
	
	public List<ExtendedPatternActivity> asPatternActivities() {
		
		List<ExtendedPatternActivity> activities = new ArrayList<ExtendedPatternActivity>();
		
		if (elements.isEmpty()) {
			activities.add(ExtendedPatternActivity.STAYATHOME_ACTIVITY);
			return activities;
		}
		

		for(int i=0; i<elements.size(); i++) {
		
			TourBasedActivityPatternElement elem = elements.get(i);
			
			activities.addAll(elem.asPatternActivities(i));
		}
		
		return activities;
	}
	
	

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
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
		TourBasedActivityPattern other = (TourBasedActivityPattern) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}

	public static TourBasedActivityPattern fromExtendedPatternActivities(List<ExtendedPatternActivity> activities) {
		
		Map<Integer,List<ExtendedPatternActivity>> tours = new LinkedHashMap<Integer,List<ExtendedPatternActivity>>();
		
		for(ExtendedPatternActivity activity : activities) {
			int tournr = activity.tourNumber();
		
			tours.putIfAbsent(tournr,new ArrayList<ExtendedPatternActivity>());
			
			tours.get(tournr).add(activity);
		}
		
		// TODO: Sonderfall Stay-At-Home
		// TODO: Sonderfall Multi-Day-Tour
		
		List<TourBasedActivityPatternElement> elements = new ArrayList<TourBasedActivityPatternElement>();
		
		for(List<ExtendedPatternActivity> tour : tours.values())  {
			if (tour.size()==1 && tour.get(0).getActivityType() == ActivityType.HOME) {
				
				elements.add(HomeActivity.fromExtendedPatternActivities(tour));
			} else {
				elements.add(TourPattern.fromExtendedPatternActivities(tour));
			}
		}
	
		return new TourBasedActivityPattern(elements);
	}
	
	
		
}

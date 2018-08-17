package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class SplitActivity implements Activity {
	
	
	private ActivityType  activityType;
	private List<Map.Entry<Time,RelativeTime>>  plannedStartsAndDurations;
	
	private SplitActivity(ActivityType activityType,  List<Map.Entry<Time,RelativeTime>> plannedStartsAndDurations) {
		this.activityType = activityType;
		this.plannedStartsAndDurations = plannedStartsAndDurations;
	}

	public static SplitActivity fromPatternActivities(ActivityType typeOfMainActivity, List<PatternActivity> activities) {
		
		List<Map.Entry<Time,RelativeTime>> tmp = 
											activities.stream()
											.filter(x -> x.getActivityType() == typeOfMainActivity)
											.map(x -> new AbstractMap.SimpleImmutableEntry<Time,RelativeTime>(SimpleTime.ofMinutes(x.getStarttime()),RelativeTime.ofMinutes(x.getDuration())))
											.collect(Collectors.toList());
		 
		assert tmp.size() >= 2;
		 
		return new SplitActivity(typeOfMainActivity,tmp);
	}

}

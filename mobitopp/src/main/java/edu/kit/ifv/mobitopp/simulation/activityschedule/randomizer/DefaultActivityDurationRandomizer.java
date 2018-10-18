package edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer;


import java.util.Random;

import edu.kit.ifv.mobitopp.data.PatternActivity;


public class DefaultActivityDurationRandomizer 
implements ActivityStartAndDurationRandomizer 
{
	
	private final Random randomizer;
	
	public DefaultActivityDurationRandomizer(long seed) {
		randomizer = new Random(seed);
	}

	
	public PatternActivity randomizeStartAndDuration(PatternActivity activity) {
		
		double random = randomizer.nextGaussian();
	
		int duration = activity.getDuration();
	
		int deviation = (int) Math.round(random*duration/20.0);
	
		int newduration = Math.min(Math.max(1,duration+deviation),10080);
	

		return new  PatternActivity(
	  		activity.getActivityType(),
	      activity.getObservedTripDuration(),
	      activity.startTime(),
	      newduration
	     );
	}
	

}

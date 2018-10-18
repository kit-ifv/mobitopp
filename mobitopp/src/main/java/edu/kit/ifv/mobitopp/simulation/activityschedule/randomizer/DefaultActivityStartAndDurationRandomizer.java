package edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer;

import java.util.Random;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class DefaultActivityStartAndDurationRandomizer 
implements ActivityStartAndDurationRandomizer 
{
	
	private final Random randomizer;
	private final double startDeviationRange;
	private final double durationDeviationFactor;
	
	public DefaultActivityStartAndDurationRandomizer (
		long seed, 
		double startDeviationRange, 
		double durationDeviationFactor
	) {
		randomizer = new Random(seed);
		this.startDeviationRange = startDeviationRange;
		this.durationDeviationFactor = durationDeviationFactor;
	}

	
	public PatternActivity randomizeStartAndDuration(PatternActivity activity) {
		
		double random = randomizer.nextDouble()-0.5;
	
		int duration = activity.getDuration();
	
		int durationDeviation = (int) Math.round(random*duration*durationDeviationFactor);
	
		int newduration = Math.min(Math.max(1,duration+durationDeviation),10080);
		
		random = randomizer.nextDouble()-0.5;
		

		int origStartTime = activity.getStarttime();
		int minStartTime = (int) SimpleTime.ofMinutes(activity.getStarttime()).startOfDay().toMinutes();
		
		int startDeviation = (int) (startDeviationRange*random);

		int newStartTime = Math.min(Math.max(minStartTime, origStartTime+startDeviation), 100080-1);
		Time newStart = SimpleTime.ofMinutes(newStartTime);
		
		return new  PatternActivity(
	  		activity.getActivityType(),
	  		activity.getObservedTripDuration(),
	      newStart,
	      newduration
	     );
	}
	
	

}

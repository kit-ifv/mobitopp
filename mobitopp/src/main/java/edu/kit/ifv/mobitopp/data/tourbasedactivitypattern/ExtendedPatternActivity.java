package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

@SuppressWarnings("serial")
public class ExtendedPatternActivity extends PatternActivity {
	
	public final int tournr;
	public final boolean isMainActivity;
	
	public static final ExtendedPatternActivity STAYATHOME_ACTIVITY = new ExtendedPatternActivity(-1, false, ActivityType.HOME, DayOfWeek.SUNDAY, -1, 0, 9*24*60);

	private ExtendedPatternActivity(
		int tournr,
		boolean isMainActivity,
		ActivityType activityType, 
		DayOfWeek weekDayType, 
		int observedTripDuration,
		int starttime, 
		int duration
	) {
		super(activityType, weekDayType, observedTripDuration, starttime, duration);
		
		this.tournr=tournr;
		this.isMainActivity=isMainActivity;
	}
	
	public ExtendedPatternActivity(
		int tournr,
		boolean isMainActivity,
		ActivityType activityType, 
		long observedTripDuration,
		Time starttime, 
		long duration
	) {
		this(tournr, isMainActivity,
				activityType, 
				starttime.weekDay(), 
				(int) observedTripDuration, 
				(int) starttime.differenceTo(starttime.startOfDay()).toMinutes(), 
				(int) duration);
	}
	
	public static ExtendedPatternActivity fromActivity(int tournr, boolean isMainActivity, Activity activity) {
		
				return new ExtendedPatternActivity(tournr, isMainActivity,
						activity.activityType(),
						activity.expectedTripDuration().toMinutes(),
						activity.plannedStart(),
						activity.plannedDuration().toMinutes()
					);
	}
	
	public int tourNumber() {
		return tournr;
	}
	
	public boolean isMainActivity() {
		return isMainActivity;
	}


}

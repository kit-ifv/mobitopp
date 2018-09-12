package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class DefaultActivityFormat implements SerialiserFormat<PersonPatternActivity> {

	private static final int personIdIndex = 0;
	private static final int activityTypeIndex = 1;
	private static final int tripDurationIndex = 2;
	private static final int startTimeIndex = 3;
	private static final int durationIndex = 4;
	private static final int tournrIndex = 5;
	private static final int mainactivityIndex = 6;
	private static final int supertourIndex = 7;

	@Override
	public List<String> header() {
		return asList("personId", "activityType", "observedTripDuration", "startTime",
				"duration", "tournr", "isMainActivity");
	}
	
	@Override
	public List<String> prepare(PersonPatternActivity activity) {
		int personOid = activity.personOid();
		int activityType = activity.activityTypeAsInt();
		int duration = activity.duration();
		int observedTripDuration = activity.observedTripDuration();
		long starttime = activity.startTime().toMinutes();
		int tournr = activity.tourNumber();
		boolean isMainActivity = activity.isMainActivity();
		boolean isInSupertour = activity.isInSupertour();
		return asList( 
				valueOf(personOid), 
				valueOf(activityType),
				valueOf(observedTripDuration), 
				valueOf(starttime),
				valueOf(duration), 
				valueOf(tournr), 
				valueOf(isMainActivity),
				valueOf(isInSupertour)
			);
	}

	@Override
	public Optional<PersonPatternActivity> parse(List<String> data) {
		int personOid = personOidOf(data);
		int activityType = activityTypeOf(data);
		int observedTripDuration = tripDurationOf(data);
		Time startTime = startTimeOf(data);
		int duration = durationOf(data);
		
		int tournr = parseTournr(data);
		boolean isMainActivity = parseMainActivity(data);
		boolean isInSupertour = parseSupertour(data);
		
		ExtendedPatternActivity patternActivity = new ExtendedPatternActivity(
				tournr, isMainActivity, isInSupertour,
				ActivityType.getTypeFromInt(activityType),
				observedTripDuration, startTime, duration);
		return Optional.of(new PersonPatternActivity(personOid, patternActivity));
	}
	

	private int personOidOf(List<String> data) {
		return Integer.parseInt(data.get(personIdIndex));
	}

	private int activityTypeOf(List<String> data) {
		return Integer.parseInt(data.get(activityTypeIndex));
	}

	private int tripDurationOf(List<String> data) {
		return Integer.parseInt(data.get(tripDurationIndex));
	}

	private Time startTimeOf(List<String> data) {
		long secondsFromStart = Long.parseLong(data.get(startTimeIndex));
		return SimpleTime.ofMinutes(secondsFromStart);
	}

	private int durationOf(List<String> data) {
		return Integer.parseInt(data.get(durationIndex));
	}

	private int parseTournr(List<String> data) {
		return Integer.parseInt(data.get(tournrIndex));
	}
	
	private boolean parseMainActivity(List<String> data) {
		return Boolean.parseBoolean(data.get(mainactivityIndex));
	}
	
	private boolean parseSupertour(List<String> data) {
		return Boolean.parseBoolean(data.get(supertourIndex));
	}


}

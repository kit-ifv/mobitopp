package edu.kit.ifv.mobitopp.simulation.opportunities;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
public class Opportunity {

	private final ZoneId zoneId;
	private final ActivityType activityType;
	private final Location location;
	private final Integer attractivity;

	public ZoneId zone() {
		return zoneId;
	}

	public ActivityType activityType() {
		return activityType;
	}

	public Location location() {
		return location;
	}

	public Integer attractivity() {
		return attractivity;
	}

}

package edu.kit.ifv.mobitopp.simulation.modeChoice;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

import java.util.Collection;
import java.util.Set;

public interface ModeAvailabilityModel {

	public Set<Mode> filterAvailableModes(
		Person person,
		Zone source,
		Zone target,
		ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		Collection<Mode> allModes
	);

	public Set<Mode> availableModes(
		Person person,
		Zone zone,
		ActivityIfc previousActivity
	);

	public Set<Mode> availableModes(
		Person person,
		Zone zone,
		ActivityIfc previousActivity,
		Collection<Mode> allModes
	);

	public Set<Mode> modesWithReasonableTravelTime(
		Person person,
		Zone source,
		Zone target,
		ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		Collection<Mode> possibleModes,	
		boolean keepAtLeastOne
	);

}

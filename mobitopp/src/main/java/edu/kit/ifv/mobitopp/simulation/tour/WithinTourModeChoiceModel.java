package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public interface WithinTourModeChoiceModel {

	
	public Mode selectMode(
		Tour tour, 
		Mode tourMode,
		Person person,
		Zone source, 
		Zone destination, 
		ActivityIfc previousActivity,
		ActivityIfc nextActivity, 
		Set<Mode> choiceSet, 
		double randomNumber
		);
}

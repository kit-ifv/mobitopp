package edu.kit.ifv.mobitopp.simulation.destinationChoice;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public interface DestinationChoiceUtilityFunction {


	public double calculateUtility(
		Person person,
		ActivityIfc nextActivity,
		Zone sourceZone, 
		Zone targetZone,
		ActivityType activityType,
		Set<Mode> choiceSetForModes 
	);

	default Map<Zone, Double> calculateUtilities(
		Person person, 
		ActivityIfc nextActivity, 
		Zone sourceZone, 
		Map<Zone,Set<Mode>> possibleTargetZonesWithModes, 
		ActivityType activityType
	) {
		Map<Zone, Double> result = new LinkedHashMap<Zone, Double>();
	
		for (Zone zone : possibleTargetZonesWithModes.keySet()) {
			
			Set<Mode> availableModes = possibleTargetZonesWithModes.get(zone);
	
			Double utility = calculateUtility( 
																						person,
																						nextActivity,
																						sourceZone, 
																						zone, 
																						activityType, 
																						availableModes
																					);
	
			result.put(zone, utility);
	
		}
	
		return result;
	}


}

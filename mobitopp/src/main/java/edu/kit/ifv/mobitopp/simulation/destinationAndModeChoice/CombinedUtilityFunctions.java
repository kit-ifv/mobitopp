package edu.kit.ifv.mobitopp.simulation.destinationAndModeChoice;

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;

public class CombinedUtilityFunctions {

	private final SimulationContext context;

	public CombinedUtilityFunctions(SimulationContext context) {
		this.context = context;
	}

	public Map<ActivityType, DestinationAndModeChoiceUtility> load() {
		HashMap<ActivityType, String> activityToConfiguration = new HashMap<>();
		activityToConfiguration.put(ActivityType.WORK, "work");
		activityToConfiguration.put(ActivityType.BUSINESS, "business");
		activityToConfiguration.put(ActivityType.EDUCATION, "education");
		activityToConfiguration.put(ActivityType.SERVICE, "service");
		activityToConfiguration.put(ActivityType.HOME, "home");
		activityToConfiguration.put(ActivityType.UNDEFINED, "undefined");
		activityToConfiguration.put(ActivityType.OTHERHOME, "otherhome");
		activityToConfiguration.put(ActivityType.PRIVATE_BUSINESS, "privateBusiness");
		activityToConfiguration.put(ActivityType.PRIVATE_VISIT, "privateVisit");
		activityToConfiguration.put(ActivityType.SHOPPING_DAILY, "shoppingDaily");
		activityToConfiguration.put(ActivityType.SHOPPING_OTHER, "shoppingOther");
		activityToConfiguration.put(ActivityType.LEISURE_INDOOR, "leisureIndoor");
		activityToConfiguration.put(ActivityType.LEISURE_OUTDOOR, "leisureOutdoor");
		activityToConfiguration.put(ActivityType.LEISURE_OTHER, "leisureOther");
		activityToConfiguration.put(ActivityType.LEISURE_WALK, "leisureWalk");
		return activityToConfiguration.entrySet().stream().collect(
				toMap(Entry::getKey, this::createUtilityFunction));
	}

	private DestinationAndModeChoiceUtilitySchaufenster createUtilityFunction(
			Entry<ActivityType, String> entry) {
		return new DestinationAndModeChoiceUtilitySchaufenster(impedance(),
				getDestinationChoiceFileFor(entry.getValue()));
	}

	private String getDestinationChoiceFileFor(String name) {
		return context.configuration().getDestinationChoice().get(name);
	}

	private ImpedanceIfc impedance() {
		return context.impedance();
	}

}

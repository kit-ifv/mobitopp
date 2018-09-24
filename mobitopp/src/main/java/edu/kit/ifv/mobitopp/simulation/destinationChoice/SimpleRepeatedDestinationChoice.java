package edu.kit.ifv.mobitopp.simulation.destinationChoice;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public class SimpleRepeatedDestinationChoice
	implements DestinationChoiceModelChoiceSet 
{

	protected TargetChoiceRepetitionParameter2 targetParameter;
	protected DestinationChoiceModelChoiceSet destinationChoiceModel;
	protected final Map<Integer,Zone> zones;
	private final CommunityTypeMapping typeMapping;

	public SimpleRepeatedDestinationChoice(
		Map<Integer,Zone> zones,
		DestinationChoiceModelChoiceSet destinationChoiceModel,
		String parameterFile 
	) {

		this.targetParameter = new TargetChoiceRepetitionParameter2(parameterFile);
		this.destinationChoiceModel = destinationChoiceModel;

		this.zones = Collections.unmodifiableMap(zones);
		typeMapping = new CommunityTypeMapping();
	}




	private Set<Zone> zonesFromIds(Collection<Integer> ids) {
		Set<Zone> result = new LinkedHashSet<Zone>();

		for(Integer id : ids) {
			result.add(this.zones.get(id));
		}

		return result;
	}


	public Zone selectDestination(
		Person person,
		Optional<Mode> tourMode, 
		ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		Set<Zone> choiceSet, double randomNumber
	) {
		assert person != null;
		assert previousActivity != null;
		assert nextActivity != null;
		assert !nextActivity.activityType().isFixedActivity();
		assert !nextActivity.activityType().isHomeActivity();


		boolean selectNewDestination = selectNewDestination(person,previousActivity,nextActivity, randomNumber);

		Zone zone;

		Set<Integer> visitedZonesOids = person.activitySchedule().alreadyVisitedZonesByActivityType(nextActivity);
		Set<Zone> visitedZones = zonesFromIds(visitedZonesOids);

		if (selectNewDestination) {

			Set<Zone> zones = new LinkedHashSet<Zone>(choiceSet);
			zones.removeAll(visitedZones);

			zone = destinationChoiceModel.selectDestination(
																															person, 
																															tourMode, 
																															previousActivity,
																													 		nextActivity,
																															zones, randomNumber
																														);
		} else {

			zone = destinationChoiceModel.selectDestination(
																															person, 
																															tourMode, 
																															previousActivity,
																													 		nextActivity,
																															visitedZones, randomNumber
																														);
		}

		return zone;
	}


	private boolean selectNewDestination(
		Person person, 
		ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		double randomNumber
	) {

		Integer activityNumber = person.activitySchedule().activityNrByType(nextActivity);

		if (activityNumber < 2) { return true; }

		ActivityType activityType = nextActivity.activityType();

		Integer sourceIsFixedActivity = previousActivity.activityType().isFixedActivity() ? 1 : 0;

		Household hh = person.household();
		Zone zone = hh.homeZone();
		AreaType areaType = zone.getAreaType();

		int community_type = communityTypeFor(areaType);

		float p = this.targetParameter.getParameter(activityType,
				sourceIsFixedActivity, activityNumber, community_type);

		return randomNumber < p;
	}

	int communityTypeFor(AreaType areaType) {
		return typeMapping.getCommunityTypeFor(areaType);
	}

}

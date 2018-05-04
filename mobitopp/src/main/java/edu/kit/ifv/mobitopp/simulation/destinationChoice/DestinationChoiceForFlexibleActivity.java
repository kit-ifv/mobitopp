package edu.kit.ifv.mobitopp.simulation.destinationChoice;

import java.util.Collection;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.AttractivityCalculatorIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;


public class DestinationChoiceForFlexibleActivity
	implements DestinationChoiceModelChoiceSet
{


	protected final AttractivityCalculatorIfc attractivityCalculator;

	protected final ModeAvailabilityModel modeAvailabilityModel;
	protected final ReachableZonesFilter reachableZonesModel;


	public DestinationChoiceForFlexibleActivity(
		ModeAvailabilityModel modeAvailabilityModel,
		ReachableZonesFilter reachableZonesModel,
		AttractivityCalculatorIfc attractivityCalculator
	) {

		this.attractivityCalculator = attractivityCalculator;
		this.modeAvailabilityModel = modeAvailabilityModel;
		this.reachableZonesModel = reachableZonesModel;
	}

	public Zone selectDestination(
		Person person,
		Optional<Mode> tourMode, 
		ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		Set<Zone> possibleTargetZones, double randomNumber
	) {
		assert person != null;
		assert previousActivity != null;
		assert nextActivity != null;
		assert !nextActivity.activityType().isFixedActivity();
		assert possibleTargetZones.size()>0;

		final int MAX_TRAVELTIME =  24*60;

		ActivityType activityType = nextActivity.activityType();

		Zone currentZone = previousActivity.zone();
		
		Zone nextFixedZone = person.nextFixedActivityZone(person.currentActivity());

		assert currentZone != null;
		Set<Mode> availableModes = new LinkedHashSet<Mode>(
																this.modeAvailabilityModel.availableModes(person, currentZone, previousActivity)
															);
		availableModes.remove(Mode.PASSENGER);

		Time startDate = previousActivity.calculatePlannedEndDate();

		Collection<Zone> filteredTargetZones = this.reachableZonesModel.filter(	possibleTargetZones,
																																	person,
																																	currentZone,
																																	nextFixedZone,
																																	availableModes,
																																	startDate, 
																																	MAX_TRAVELTIME
																																);
		assert !filteredTargetZones.isEmpty();

		Map<Zone, Float> zoneAttractivities 
			= this.attractivityCalculator.calculateAttractivities( 
																											person,
																											nextActivity,
																											currentZone,
																											filteredTargetZones,
																											activityType, 
																											availableModes
																										);

		assert !zoneAttractivities.isEmpty();


		DiscreteRandomVariable<Zone> randomVariable = new DiscreteRandomVariable<Zone>(zoneAttractivities);
		Zone destination = randomVariable.realization(randomNumber);

		return destination;
	}

}

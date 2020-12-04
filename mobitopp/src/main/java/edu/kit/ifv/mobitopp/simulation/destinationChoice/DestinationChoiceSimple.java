package edu.kit.ifv.mobitopp.simulation.destinationChoice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;
import edu.kit.ifv.mobitopp.util.logit.DefaultLogitModel;
import edu.kit.ifv.mobitopp.util.logit.LogitModel;


public class DestinationChoiceSimple 
	implements DestinationChoiceModelChoiceSet
{

	protected final ImpedanceIfc impedance;
	protected final	DestinationChoiceUtilityFunction utilityFunction;
	protected final ModeAvailabilityModel modeAvailabilityModel;
	
	protected final boolean tourBased;

	private final LogitModel<Zone> logitModel = new DefaultLogitModel<Zone>();

	public DestinationChoiceSimple(
		DestinationChoiceUtilityFunction utilityFunction,
		ModeAvailabilityModel modeAvailabilityModel,
		ImpedanceIfc impedance, 
		boolean tourBased
	) {
		this.utilityFunction = utilityFunction;
		this.impedance = impedance;
		this.modeAvailabilityModel = modeAvailabilityModel;
		this.tourBased = tourBased;
	}

	@Override
  public boolean isTourBased() {
  	return tourBased;
  }	


  public Zone selectDestination(
    Person person, 
    Optional<Mode> tourMode,
    ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		Set<Zone> choiceSet, double randomNumber
	) {

		ActivityType activityType = nextActivity.activityType();
		
		Zone currentZone = previousActivity.zone();
		
		Set<Mode> choiceSetForModes = this.modeAvailabilityModel.availableModes(
																		person,
																		currentZone,
																		previousActivity
																	);
		
		Map<Zone,Set<Mode>> availableZones = new LinkedHashMap<Zone,Set<Mode>>();
		
		for(Zone zone : choiceSet) {
			availableZones.put(zone, choiceSetForModes);
		}
		
		Map<Zone, Double> utilities = utilityFunction.calculateUtilities( 
																	person,
																	nextActivity,
																	currentZone,
																	availableZones,
																	activityType
																	);

		return logitModel.select(utilities, randomNumber);
	}

}

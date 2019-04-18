package edu.kit.ifv.mobitopp.simulation.destinationChoice;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;



public class DestinationChoiceWithFixedLocations
  implements DestinationChoiceModel
{
	
	protected final Set<Zone> zones;

  private DestinationChoiceModelChoiceSet destinationChoiceModel;
  
 
  public DestinationChoiceWithFixedLocations(
  	Map<ZoneId, Zone> zones,
		DestinationChoiceModelChoiceSet destinationChoiceModel
	)
  {
    this.destinationChoiceModel = destinationChoiceModel;
    this.zones = Collections.unmodifiableSet(new LinkedHashSet<Zone>(zones.values()));
  }

  @Override
  public boolean isTourBased() {
  	return destinationChoiceModel.isTourBased(); 
  }

  @Override
  public Zone selectDestination(
      Person person, 
      Optional<Mode> tourMode,
      ActivityIfc previousActivity,
      ActivityIfc nextActivity,
			double randomNumber
	) {

		assert person != null;
		assert previousActivity != null;
		assert nextActivity != null;
  
    ActivityType activityType = nextActivity.activityType();


    if (activityType.isHomeActivity() ) 
    {
			return person.household().homeZone();
    } 
		else if (activityType.isFixedActivity()) 
		{
			return person.fixedZoneFor(activityType);
		}
    else
    {
			Zone zone = this.destinationChoiceModel.selectDestination(
																										person, 
																										tourMode, 
																										previousActivity,
																										nextActivity,
																										this.zones, randomNumber
																									);

			return zone;
		}
	}


}

package edu.kit.ifv.mobitopp.simulation.destinationChoice;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.time.Time;

public interface ReachableZonesFilter {
	
Set<Zone> filter(
			Set<Zone> zones,
			Person person,
			Zone currentZone,
			Zone nextFixedZone,
			Set<Mode> availableModes,
			Time date, int maxTravelTimeInMinutes
		);


default Set<Zone> filterAvailableZones(
		Set<Zone> zones,
		Zone currentZone
	) {
			LinkedHashSet<Zone> available_zones = new LinkedHashSet<Zone>(zones);
			
			for (Zone zone : zones) {

				if (zone.hasDemandData()) {
						available_zones.add(zone);
				}
			}

		if (available_zones.isEmpty()) {
			available_zones.add(currentZone);
		}

		return available_zones;
	}



}

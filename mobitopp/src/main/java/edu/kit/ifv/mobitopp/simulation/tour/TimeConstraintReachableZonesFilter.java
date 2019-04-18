package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.time.Time;

public class TimeConstraintReachableZonesFilter 
	// implements ReachableZonesFilter
{
	protected final ImpedanceIfc impedance;
	
	protected final double timeScalingFactor;
	protected final int timeOffset;
	
	public TimeConstraintReachableZonesFilter(
		ImpedanceIfc impedance,
		double timeScalingFactor,
		int timeOffset
	) {
		assert impedance != null;
		
		this.impedance = impedance;
		
		this.timeScalingFactor = timeScalingFactor;
		this.timeOffset = timeOffset;
	}
	

	public Map<Zone,Set<Mode>> filter(
			Set<Zone> zones,
			Person person,
			Zone currentZone,
			Zone nextFixedZone,
			Set<Mode> modes,
			Time date, 
			int maxTravelTimeInMinutes
		) {
			assert !modes.isEmpty();
		
			Set<Mode> availableModes = new LinkedHashSet<Mode>(modes);
	//		availableModes.remove(Mode.PASSENGER);
		
			assert person != null;
			assert currentZone != null;
			assert nextFixedZone != null;
			assert !availableModes.isEmpty() : modes;
			
			if (maxTravelTimeInMinutes < 1) {
				Map<Zone,Set<Mode>> result = new LinkedHashMap<Zone,Set<Mode>>();
				
				result.put(currentZone, availableModes);
				result.put(nextFixedZone, availableModes);
				
				return result;
			}

			// TODO: prüfen, ob man das braucht
			// zones = filterAvailableZones(zones, currentZone);

			LinkedHashMap<Zone,Set<Mode>> reachableZones = new LinkedHashMap<Zone,Set<Mode>>();
			
			assert this.impedance != null;

			for (Zone destination : zones) {
				for (Mode mode : availableModes) {
					
					assert currentZone != null;
					assert destination != null;

					double time = this.impedance.getTravelTime(currentZone.getInternalId(), destination.getInternalId(), mode, date)
									+ this.impedance.getTravelTime(destination.getInternalId(), nextFixedZone.getInternalId(), mode, date) ;

					if (time < Math.max(maxTravelTimeInMinutes, 0)*timeScalingFactor + timeOffset) {
						
						if(!reachableZones.containsKey(destination)){
							reachableZones.put(destination, new LinkedHashSet<Mode>());
						}
						reachableZones.get(destination).add(mode);
					}
				}
			}
			if (reachableZones.isEmpty()) {
					reachableZones.put(nextFixedZone, new LinkedHashSet<Mode>(availableModes));
			}

			return reachableZones;
		}


}

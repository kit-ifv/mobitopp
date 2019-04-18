package edu.kit.ifv.mobitopp.simulation.destinationChoice;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.time.Time;

public class CarRangeReachableZonesFilter
	implements ReachableZonesFilter
{
	
	protected final ImpedanceIfc impedance;
	
	public CarRangeReachableZonesFilter(ImpedanceIfc impedance) {
		this.impedance = impedance;
	}



		public Set<Zone> filter(
			Set<Zone> zones,
			Person person,
			Zone currentZone,
			Zone nextFixedZone,
			Set<Mode> availableModes,
			Time date, 
			int maxTravelTimeInMinutes
		) {

			zones = filterAvailableZones(zones, currentZone);

			final float DIVERSION_FACTOR = 1.5f;
			final float RANGE_BUFFER_KM = 20.0f;


			if (person.isCarDriver()) {

				return filterByRangeOfCar(zones, person, currentZone, nextFixedZone, DIVERSION_FACTOR, RANGE_BUFFER_KM);
			}

			return filterByTravelTime(zones, currentZone, nextFixedZone, availableModes, date, maxTravelTimeInMinutes);
		}


		private Set<Zone> filterByTravelTime(
			Set<Zone> zones, 
			Zone currentZone, 
			Zone nextFixedZone,
			Set<Mode> availableModes, 
			Time date, 
			int maxTravelTimeInMinutes
		) {
			
			LinkedHashSet<Zone> reachableZones = new LinkedHashSet<Zone>();

			for (Zone destination : zones) {
				for (Mode mode : availableModes) {

					ZoneId originId = currentZone.getId();
          ZoneId destinationId = destination.getId();
          double time = this.impedance.getTravelTime(originId, destinationId, mode, date);

					if (time < maxTravelTimeInMinutes) {
						reachableZones.add(destination);
					}
				}
			}
			if (reachableZones.isEmpty()) {
					reachableZones.add(nextFixedZone);
			}

			return reachableZones;
		}



		private Set<Zone> filterByRangeOfCar(Set<Zone> zones, Person person, Zone currentZone, Zone nextFixedZone,
				final float DIVERSION_FACTOR, final float RANGE_BUFFER_KM) {
			Car car = person.whichCar();
			Integer range = car.effectiveRange();

			Zone homeZone = person.household().homeZone();

			if (range == Integer.MAX_VALUE) {
				return zones;
			}

			LinkedHashSet<Zone> restricted_zones = new LinkedHashSet<Zone>(zones);

			for (Zone zone : zones) {
				
				ZoneId originId = currentZone.getId();
        ZoneId destinationId = zone.getId();
        ZoneId nextFixedDestinationId = nextFixedZone.getId();
        ZoneId homeId = homeZone.getId();
        float distance = this.impedance.getDistance(originId, destinationId)
												+ this.impedance.getDistance(destinationId, nextFixedDestinationId)
												+ this.impedance.getDistance(nextFixedDestinationId, homeId);
				float distanceKm = distance/1000.0f;

				if (range < distanceKm*DIVERSION_FACTOR
						&& range < distanceKm + RANGE_BUFFER_KM
				) {
					restricted_zones.remove(zone);
				}
			}
			if (restricted_zones.isEmpty()) {
				restricted_zones.add(nextFixedZone);
			}

			return restricted_zones;
		}




}

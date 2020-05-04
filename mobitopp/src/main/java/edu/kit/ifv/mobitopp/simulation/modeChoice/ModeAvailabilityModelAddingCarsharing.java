package edu.kit.ifv.mobitopp.simulation.modeChoice;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;

public class ModeAvailabilityModelAddingCarsharing
	extends BasicModeAvailabilityModel
	implements ModeAvailabilityModel
{

	public ModeAvailabilityModelAddingCarsharing(
		ImpedanceIfc impedance
	) {
		super(impedance);
	}

	@Override
	public Set<Mode> availableModes(
		Person person,
		Zone currentZone,
		ActivityIfc previousActivity,
		Collection<Mode> allModes
	) {

		Set<Mode> choiceSet = new LinkedHashSet<Mode>(
				super.availableModes(person, currentZone, previousActivity, allModes));

		assert choiceSet != null;

		if (isAtHome(previousActivity)) {

			if ( person.hasDrivingLicense() && person.household().getNumberOfAvailableCars() == 0) {

				if( currentZone.carSharing().isStationBasedCarSharingCarAvailable(person)) {
					choiceSet.add(StandardMode.CARSHARING_STATION);
				}

				if( currentZone.carSharing().isFreeFloatingCarSharingCarAvailable(person)) {
					choiceSet.add(StandardMode.CARSHARING_FREE);
				}
			}

			if(previousActivity.isModeSet()
					&& previousActivity.mode()==StandardMode.CARSHARING_FREE && person.hasParkedCar()) {

				assert !currentZone.carSharing().isFreeFloatingZone((CarSharingCar)person.whichCar());

				choiceSet = Collections.singleton(StandardMode.CARSHARING_FREE);
			} 
		} else {

			Mode previousMode = previousActivity.mode();

			if(previousMode==StandardMode.CARSHARING_STATION) {

					choiceSet = Collections.singleton(StandardMode.CARSHARING_STATION);

			} else if(previousMode==StandardMode.CARSHARING_FREE) {

				if(person.hasParkedCar()) {
	
					assert !currentZone.carSharing().isFreeFloatingZone((CarSharingCar)person.whichCar());
	
					choiceSet = Collections.singleton(StandardMode.CARSHARING_FREE);

				} 
				else {
					choiceSet.remove(StandardMode.CAR);
					choiceSet.remove(StandardMode.BIKE);
	
					if(currentZone.carSharing().isFreeFloatingCarSharingCarAvailable(person)) {
						choiceSet.add(StandardMode.CARSHARING_FREE);
					}
				}
			} 
		}

		return choiceSet;
	}

}

package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Location;


public class EducationInHomeZone implements Consumer<PersonBuilder> {

	@Override
	public void accept(PersonBuilder person) {
		Zone zone = person.homeZone();
		Location location = zone.centroidLocation();
		person.addFixedDestination(new FixedDestination(ActivityType.EDUCATION, zone, location));
	}

}

package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Location;


public class EducationInHomeZone implements Consumer<PersonBuilder> {

	private final DoubleSupplier random;

	public EducationInHomeZone(DoubleSupplier random) {
		super();
		this.random = random;
	}

	@Override
	public void accept(PersonBuilder person) {
		Zone zone = person.homeZone();
		Location location = zone.opportunities().selectRandomLocation(ActivityType.EDUCATION, nextRandom());
		person.addFixedDestination(new FixedDestination(ActivityType.EDUCATION, zone, location));
	}

	private double nextRandom() {
		return random.getAsDouble();
	}

}

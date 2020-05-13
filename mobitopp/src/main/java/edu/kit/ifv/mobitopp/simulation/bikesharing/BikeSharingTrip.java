package edu.kit.ifv.mobitopp.simulation.bikesharing;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.BaseTrip;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class BikeSharingTrip extends BaseTrip implements Trip {

	private Zone origin;
	private Zone destination;

	public BikeSharingTrip(TripData data, SimulationPerson person) {
		super(data, person);
		this.origin = data.origin().zone();
		this.destination = data.destination().zone();
	}

	@Override
	public void prepareTrip(ImpedanceIfc impedance, Time currentTime) {
		if (person().hasParkedBike()) {
			useParkedBike();
			return;
		}
		bookBike(currentTime);
	}

	private void useParkedBike() {
		person().takeBikeFromParking();
	}

	private void bookBike(Time currentTime) {
		BikeSharingDataForZone bikeSharing = origin.bikeSharing();
		if (!bikeSharing.isBikeAvailableFor(person())) {
			throw new IllegalStateException("No bike sharing bike is available for: " + person());
		}
		Bike bike = bikeSharing.bookBike(person());
		person().useBike(bike, currentTime);
	}

	@Override
	public FinishedTrip finish(Time currentDate, PersonListener listener) {
		FinishedTrip finish = super.finish(currentDate, listener);
		String bikeId = returnBike(currentDate);
		return new FinishedBikeSharingTrip(finish, bikeId);
	}

	private String returnBike(Time currentDate) {
		Bike bike = person().whichBike();
		if (destination.bikeSharing().isBikeSharingAreaFor(bike)) {
			Bike releasedBike = person().releaseBike(currentDate);
			releasedBike.returnBike(destination.getId());
			return releasedBike.getId();
		}
		Bike parkedBike = person().parkBike(destination, nextActivity().location(), currentDate);
		return parkedBike.getId();
	}
}

package edu.kit.ifv.mobitopp.simulation.bikesharing;

import java.util.Optional;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTripDecorator;

public class FinishedBikeSharingTrip extends FinishedTripDecorator implements FinishedTrip {

	private final String bikeId;

	public FinishedBikeSharingTrip(FinishedTrip trip, String bikeId) {
		super(trip);
		this.bikeId = bikeId;
	}

	@Override
	public Optional<String> vehicleId() {
		return Optional.of(bikeId);
	}

	@Override
	public Stream<FinishedTrip> trips() {
		return Stream.of(this);
	}

}

package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Optional;
import java.util.stream.Stream;

public class FinishedCarTrip extends FinishedTripDecorator implements FinishedTrip {

  private final int carId;

  public FinishedCarTrip(FinishedTrip trip, int carId) {
    super(trip);
    this.carId = carId;
  }

  @Override
  public Optional<String> vehicleId() {
    return Optional.of(String.valueOf(carId));
  }
  
  @Override
  public Stream<FinishedTrip> trips() {
  	return Stream.of(this);
  }

}

package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Optional;

public class FinishedCarTrip extends FinishedTripDecorator implements FinishedTrip {

  private final Integer carId;

  public FinishedCarTrip(FinishedTrip trip, Integer carId) {
    super(trip);
    this.carId = carId;
  }

  @Override
  public Optional<String> vehicleId() {
    return Optional.ofNullable(String.valueOf(carId));
  }

}

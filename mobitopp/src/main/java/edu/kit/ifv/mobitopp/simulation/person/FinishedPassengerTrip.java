package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Optional;

public class FinishedPassengerTrip extends FinishedTripDecorator implements FinishedTrip {

  private final String carId;

  public FinishedPassengerTrip(FinishedTrip trip, String carId) {
    super(trip);
    this.carId = carId;
  }
  
  @Override
  public Optional<String> vehicleId() {
    return Optional.of(carId);
  }

}

package edu.kit.ifv.mobitopp.simulation.carsharing;

import edu.kit.ifv.mobitopp.simulation.Car;

public interface CarSharingCar extends Car {

  public CarSharingOrganization owner();

  public String forLogging();

}

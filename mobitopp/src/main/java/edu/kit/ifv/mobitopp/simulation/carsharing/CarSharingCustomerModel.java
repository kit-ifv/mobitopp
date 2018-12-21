package edu.kit.ifv.mobitopp.simulation.carsharing;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonForSetup;

public interface CarSharingCustomerModel {

  public boolean estimateCustomership(PersonForSetup person);

}

package edu.kit.ifv.mobitopp.data;

import edu.kit.ifv.mobitopp.simulation.Car;

public interface MaasDataForZone {
  
  static MaasDataForZone everywhereAvailable() {
    return car -> true;
  }

  boolean isInMaasZone(Car car);

}

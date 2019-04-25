package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.local.DynamicTypeMapping;
import edu.kit.ifv.mobitopp.data.local.TypeMapping;
import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixType;
import edu.kit.ifv.mobitopp.simulation.Mode;

public abstract class DefaultMappings {

  public static TypeMapping noAutonomousModes() {
    return createNormalModes();
  }

  private static DynamicTypeMapping createNormalModes() {
    DynamicTypeMapping types = new DynamicTypeMapping();
    types.add(Mode.BIKE, TravelTimeMatrixType.bike);
    types.add(Mode.CAR, TravelTimeMatrixType.car);
    types.add(Mode.CARSHARING_FREE, TravelTimeMatrixType.car);
    types.add(Mode.CARSHARING_STATION, TravelTimeMatrixType.car);
    types.add(Mode.PASSENGER, TravelTimeMatrixType.car);
    types.add(Mode.PEDELEC, TravelTimeMatrixType.bike);
    types.add(Mode.PEDESTRIAN, TravelTimeMatrixType.pedestrian);
    types.add(Mode.PUBLICTRANSPORT, TravelTimeMatrixType.publictransport);
    types.add(Mode.TRUCK, TravelTimeMatrixType.truck);
    types.add(Mode.TAXI, TravelTimeMatrixType.car);
    types.add(Mode.PARK_AND_RIDE, TravelTimeMatrixType.car);
    return types;
  }
  
  public static DynamicTypeMapping createTaxiAndParkAndRide() {
    DynamicTypeMapping types = createNormalModes();
    types.add(Mode.TAXI, TravelTimeMatrixType.car);
    types.add(Mode.PARK_AND_RIDE, TravelTimeMatrixType.car);
    return types;
  }
  
  public static DynamicTypeMapping autonomousModes() {
    DynamicTypeMapping types = createTaxiAndParkAndRide();
    types.add(Mode.PREMIUM_RIDE_HAILING, TravelTimeMatrixType.car);
    types.add(Mode.RIDE_HAILING, TravelTimeMatrixType.car);
    types.add(Mode.RIDE_POOLING, TravelTimeMatrixType.ride_pooling);
    return types;
  }

}

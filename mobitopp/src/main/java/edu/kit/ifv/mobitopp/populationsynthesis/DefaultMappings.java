package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.local.DynamicTypeMapping;
import edu.kit.ifv.mobitopp.data.local.TypeMapping;
import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixType;
import edu.kit.ifv.mobitopp.simulation.StandardMode;

public abstract class DefaultMappings {

  public static TypeMapping noAutonomousModes() {
    return createNormalModes();
  }

  private static DynamicTypeMapping createNormalModes() {
    DynamicTypeMapping types = new DynamicTypeMapping();
    types.add(StandardMode.BIKE, TravelTimeMatrixType.bike);
    types.add(StandardMode.BIKESHARING, TravelTimeMatrixType.bike);
    types.add(StandardMode.CAR, TravelTimeMatrixType.car);
    types.add(StandardMode.CARSHARING_FREE, TravelTimeMatrixType.car);
    types.add(StandardMode.CARSHARING_STATION, TravelTimeMatrixType.car);
    types.add(StandardMode.PASSENGER, TravelTimeMatrixType.car);
    types.add(StandardMode.PEDELEC, TravelTimeMatrixType.bike);
    types.add(StandardMode.PEDESTRIAN, TravelTimeMatrixType.pedestrian);
    types.add(StandardMode.PUBLICTRANSPORT, TravelTimeMatrixType.publictransport);
    types.add(StandardMode.TRUCK, TravelTimeMatrixType.truck);
    types.add(StandardMode.TAXI, TravelTimeMatrixType.taxi);
    types.add(StandardMode.PARK_AND_RIDE, TravelTimeMatrixType.park_and_ride);
    return types;
  }
  
  public static DynamicTypeMapping autonomousModes() {
    DynamicTypeMapping types = createNormalModes();
    types.add(StandardMode.PREMIUM_RIDE_HAILING, TravelTimeMatrixType.premium_ride_hailing);
    types.add(StandardMode.RIDE_HAILING, TravelTimeMatrixType.ride_hailing);
    types.add(StandardMode.RIDE_POOLING, TravelTimeMatrixType.ride_pooling);
    return types;
  }

}

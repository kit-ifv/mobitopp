package edu.kit.ifv.mobitopp.simulation;

import java.util.Optional;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public interface ImpedanceIfc {

  Optional<PublicTransportRoute> getPublicTransportRoute(
      Location origin, Location destination, Mode mode, Time date);

  float getTravelTime(ZoneId origin, ZoneId destination, Mode mode, Time date);

  float getTravelCost(ZoneId origin, ZoneId destination, Mode mode, Time date);

  float getDistance(ZoneId origin, ZoneId destination);

  float getParkingCost(ZoneId destination, Time date);

  float getParkingStress(ZoneId destination, Time date);

  float getConstant(ZoneId origin, ZoneId destination, Time date);

  float getOpportunities(ActivityType activityType, ZoneId zone);

  Optional<PublicTransportRoute> getPublicTransportRoute(
      Stop start, Stop end, Mode mode, Time date);

}

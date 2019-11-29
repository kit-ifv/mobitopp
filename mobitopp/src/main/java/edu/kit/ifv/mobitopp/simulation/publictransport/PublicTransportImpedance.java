package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Optional;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationFinder;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationPaths;
import edu.kit.ifv.mobitopp.time.Time;

public class PublicTransportImpedance implements ImpedanceIfc {

	private final RouteSearch viaRouteSearch;
	private final StationFinder stationResolver;
	private final ImpedanceIfc impedance;

	public PublicTransportImpedance(
			RouteSearch routeSearch, StationFinder stationResolver, ImpedanceIfc impedance) {
		super();
		viaRouteSearch = routeSearch;
		this.stationResolver = stationResolver;
		this.impedance = impedance;
	}

	@Override
	public Optional<PublicTransportRoute> getPublicTransportRoute(
			Location source, Location target, Mode mode, Time atTime) {
		if (source.equals(target)) {
			return Optional.empty();
		}
		if (StandardMode.PUBLICTRANSPORT.equals(mode)) {
			StationPaths fromStart = stationResolver.findReachableStations(source);
			StationPaths toEnd = stationResolver.findReachableStations(target);
			return fromStart.findRoute(toEnd, atTime, viaRouteSearch);
		}
		return impedance.getPublicTransportRoute(source, target, mode, atTime);
	}
	
	@Override
	public Optional<PublicTransportRoute> getPublicTransportRoute(
			Stop start, Stop end, Mode mode, Time atTime) {
		if (start.equals(end)) {
			return Optional.empty();
		}
		if (StandardMode.PUBLICTRANSPORT.equals(mode)) {
			return viaRouteSearch.findRoute(start, end, atTime);
		}
		return Optional.empty();
	}

  @Override
  public float getDistance(ZoneId origin, ZoneId destination) {
    return impedance.getDistance(origin, destination);
  }

  @Override
  public float getTravelTime(ZoneId origin, ZoneId destination, Mode mode, Time date) {
    return impedance.getTravelTime(origin, destination, mode, date);
  }

  @Override
  public float getTravelCost(ZoneId origin, ZoneId destination, Mode mode, Time date) {
    return impedance.getTravelCost(origin, destination, mode, date);
  }

  @Override
  public float getParkingCost(ZoneId destination, Time date) {
    return impedance.getParkingCost(destination, date);
  }

  @Override
  public float getParkingStress(ZoneId destination, Time date) {
    return impedance.getParkingStress(destination, date);
  }

  @Override
  public float getConstant(ZoneId origin, ZoneId destination, Time date) {
    return impedance.getConstant(origin, destination, date);
  }

  @Override
  public float getOpportunities(ActivityType activityType, ZoneId zone) {
    return impedance.getOpportunities(activityType, zone);
  }
}
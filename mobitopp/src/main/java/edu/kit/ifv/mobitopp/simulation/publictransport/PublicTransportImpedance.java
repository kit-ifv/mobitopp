package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
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
	public float getTravelTime(int source, int target, Mode mode, Time date) {
		return impedance.getTravelTime(source, target, mode, date);
	}

	@Override
	public Optional<PublicTransportRoute> getPublicTransportRoute(
			Location source, Location target, Mode mode, Time atTime) {
		if (source.equals(target)) {
			return Optional.empty();
		}
		if (Mode.PUBLICTRANSPORT.equals(mode)) {
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
		if (Mode.PUBLICTRANSPORT.equals(mode)) {
			return viaRouteSearch.findRoute(start, end, atTime);
		}
		return Optional.empty();
	}

	@Override
	public float getTravelCost(int source, int target, Mode mode, Time date) {
		return impedance.getTravelCost(source, target, mode, date);
	}

	@Override
	public float getParkingStress(int target, Time date) {
		return impedance.getParkingStress(target, date);
	}

	@Override
	public float getParkingCost(int target, Time date) {
		return impedance.getParkingCost(target, date);
	}

	@Override
	public float getOpportunities(ActivityType activityType, int zoneOid) {
		return impedance.getOpportunities(activityType, zoneOid);
	}

	@Override
	public float getDistance(int source, int target) {
		return impedance.getDistance(source, target);
	}

	@Override
	public float getConstant(int source, int target, Time date) {
		return impedance.getConstant(source, target, date);
	}
}
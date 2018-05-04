package edu.kit.ifv.mobitopp.data.local.configuration;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.Time;

public class ZoneProfileImpedance implements ImpedanceIfc {

	private final ImpedanceIfc impedance;
	private final ZoneProfiles zoneProfiles;

	public ZoneProfileImpedance(ImpedanceIfc impedance, ZoneProfiles zoneProfiles) {
		super();
		this.impedance = impedance;
		this.zoneProfiles = zoneProfiles;
	}

	@Override
	public float getTravelTime(int source, int target, Mode mode, Time date) {
		if (Mode.PUBLICTRANSPORT.equals(mode)) {
			return zoneProfiles.getTravelTime(source, target, date);
		}
		return impedance.getTravelTime(source, target, mode, date);
	}

	@Override
	public Optional<PublicTransportRoute> getPublicTransportRoute(
			Location source, Location target, Mode mode, Time date) {
		return impedance.getPublicTransportRoute(source, target, mode, date);
	}

	@Override
	public float getTravelCost(int source, int target, Mode mode, Time date) {
		return impedance.getTravelCost(source, target, mode, date);
	}

	@Override
	public float getDistance(int source, int target) {
		return impedance.getDistance(source, target);
	}

	@Override
	public float getParkingCost(int target, Time date) {
		return impedance.getParkingCost(target, date);
	}

	@Override
	public float getParkingStress(int target, Time date) {
		return impedance.getParkingStress(target, date);
	}

	@Override
	public float getConstant(int source, int target, Time date) {
		return impedance.getConstant(source, target, date);
	}

	@Override
	public float getOpportunities(ActivityType activityType, int zoneOid) {
		return impedance.getOpportunities(activityType, zoneOid);
	}

}

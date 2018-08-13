package edu.kit.ifv.mobitopp.simulation.impedance;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.Time;

public class ImpedanceDecorator 
	implements ImpedanceIfc 
{
	
	private final ImpedanceIfc impedance;
	
	public ImpedanceDecorator(ImpedanceIfc impedance) {
		this.impedance = impedance;
	}

	@Override
	public float getTravelTime(int source, int target, Mode mode, Time date) {
		return this.impedance.getTravelTime(source, target, mode, date);
	}

	@Override
	public float getTravelCost(int source, int target, Mode mode, Time date) {
		return this.impedance.getTravelCost(source, target, mode, date);
	}

	@Override
	public float getDistance(int source, int target) {
		return this.impedance.getDistance(source, target);
	}

	@Override
	public float getParkingCost(int target, Time date) {
		return this.impedance.getParkingCost(target, date);
	}

	@Override
	public float getParkingStress(int target, Time date) {
		return this.impedance.getParkingStress(target, date);
	}

	@Override
	public float getConstant(int source, int target, Time date) {
		return this.impedance.getConstant(source, target, date);
	}

	@Override
	public float getOpportunities(ActivityType activityType, int zoneOid) {
		return this.impedance.getOpportunities(activityType, zoneOid);
	}

	@Override
	public Optional<PublicTransportRoute> getPublicTransportRoute(Location origin, Location destination, Mode mode,
			Time date) {
		return this.impedance.getPublicTransportRoute(origin, destination, mode, date);
	}

	@Override
	public Optional<PublicTransportRoute> getPublicTransportRoute(
			Stop start, Stop end, Mode mode, Time date) {
		return impedance.getPublicTransportRoute(start, end, mode, date);
	}

}

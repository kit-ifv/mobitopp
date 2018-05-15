package edu.kit.ifv.mobitopp.data.local;

import java.util.Optional;

import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.Time;

public class ImpedanceLocalData implements ImpedanceIfc {

	private final Matrices matrices;
	private final Time start;
	private final ZoneRepository zoneRepository;

	public ImpedanceLocalData(
			Matrices matrices, ZoneRepository zoneRepository, Time startDate) {
		super();
		this.matrices = matrices;
		this.zoneRepository = zoneRepository;
		this.start = startDate;
	}

	@Override
	public float getTravelTime(int source, int target, Mode mode, Time date) {
		return matrices.travelTimeFor(mode, date).get(source, target);
	}

	@Override
	public Optional<PublicTransportRoute> getPublicTransportRoute(
			Location source, Location target, Mode mode, Time date) {
		return Optional.empty();
	}
	
	@Override
	public Optional<PublicTransportRoute> getPublicTransportRoute(Stop start, Stop end, Mode mode, Time date) {
		return Optional.empty();
	}

	@Override
	public float getTravelCost(int source, int target, Mode mode, Time date) {
		if(mode == Mode.BIKE || mode == Mode.PASSENGER || mode == Mode.PEDESTRIAN) {
			return 0.0f;
		}
		return matrices.travelCostFor(mode, date).get(source, target);
	}

	@Override
	public float getDistance(int source, int target) {
		return matrices.distanceMatrix(start).get(source, target);
	}

	@Override
	public float getParkingCost(int target, Time date) {
		return matrices.parkingCostMatrix(date).get(target, target);
	}

	@Override
	public float getParkingStress(int target, Time date) {
		return matrices.parkingStressMatrix(date).get(target, target);
	}

	@Override
	public float getConstant(int source, int target, Time date) {
		return matrices.constantMatrix(date).get(source, target);
	}

	@Override
	public float getOpportunities(ActivityType activityType, int zoneOid) {
		return zoneRepository.getZoneByOid(zoneOid).getAttractivity(activityType);
	}

}

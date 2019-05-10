package edu.kit.ifv.mobitopp.data.local;

import java.util.Optional;

import edu.kit.ifv.mobitopp.data.ZoneId;
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
	public Optional<PublicTransportRoute> getPublicTransportRoute(
			Location source, Location target, Mode mode, Time date) {
		return Optional.empty();
	}
	
	@Override
	public Optional<PublicTransportRoute> getPublicTransportRoute(Stop start, Stop end, Mode mode, Time date) {
		return Optional.empty();
	}

  @Override
  public float getTravelTime(ZoneId origin, ZoneId destination, Mode mode, Time date) {
    return matrices.travelTimeFor(mode, date).get(origin, destination);
  }

  @Override
  public float getTravelCost(ZoneId origin, ZoneId destination, Mode mode, Time date) {
    if(mode == Mode.BIKE || mode == Mode.PASSENGER || mode == Mode.PEDESTRIAN) {
    	return 0.0f;
    }
    return matrices.travelCostFor(mode, date).get(origin, destination);
  }

  @Override
  public float getDistance(ZoneId origin, ZoneId destination) {
    return matrices.distanceMatrix(start).get(origin, destination);
  }

  @Override
  public float getParkingCost(ZoneId destination, Time date) {
    int destinationId = destination.getMatrixColumn();
    return matrices.parkingCostMatrix(date).get(destinationId, destinationId);
  }

  @Override
  public float getParkingStress(ZoneId destination, Time date) {
    return matrices.parkingStressMatrix(date).get(destination, destination);
  }

  @Override
  public float getConstant(ZoneId origin, ZoneId destination, Time date) {
    return matrices.constantMatrix(date).get(origin, destination);
  }

  @Override
  public float getOpportunities(ActivityType activityType, ZoneId zone) {
    return zoneRepository.getZoneById(zone).getAttractivity(activityType);
  }

}

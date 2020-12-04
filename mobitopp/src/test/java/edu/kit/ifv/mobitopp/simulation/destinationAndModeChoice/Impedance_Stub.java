package edu.kit.ifv.mobitopp.simulation.destinationAndModeChoice;

import java.util.Optional;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.Time;

public class Impedance_Stub
	implements ImpedanceIfc 
{

	private final float time;
	private final float cost;
	private final float distance;
	private final float parkingcost;
	private final float parkingstress;
	private final float opportunities;
	private final float constant = 0.0f;

	public Impedance_Stub(
		float time,
		float cost,
		float distance,
		float parkingcost,
		float parkingstress,
		float opportunities
	) {
		this.time = time;
		this.cost = cost;
		this.distance = distance;
		this.parkingcost = parkingcost;
		this.parkingstress = parkingstress;
		this.opportunities = opportunities;
	}

	@Override
	public Optional<PublicTransportRoute> getPublicTransportRoute(Location source, Location target, Mode mode, Time date) {
		return Optional.empty();
	}
	
	@Override
	public Optional<PublicTransportRoute> getPublicTransportRoute(Stop start, Stop end, Mode mode, Time date) {
		return Optional.empty();
	}

  @Override
  public float getDistance(ZoneId origin, ZoneId destination) {
    return distance;
  }
  
  @Override
  public float getTravelTime(ZoneId origin, ZoneId destination, Mode mode, Time date) {
    return time;
  }
  
  @Override
  public float getTravelCost(ZoneId origin, ZoneId destination, Mode mode, Time date) {
    return cost;
  }

  @Override
  public float getParkingCost(ZoneId destination, Time date) {
    return parkingcost;
  }

  @Override
  public float getParkingStress(ZoneId destination, Time date) {
    return parkingstress;
  }

  @Override
  public float getConstant(ZoneId origin, ZoneId destination, Time date) {
    return constant;
  }

  @Override
  public float getOpportunities(ActivityType activityType, ZoneId zone) {
    return opportunities;
  }

}

package edu.kit.ifv.mobitopp.simulation.impedance;

import java.util.Optional;

import edu.kit.ifv.mobitopp.data.ZoneId;
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
	public Optional<PublicTransportRoute> getPublicTransportRoute(Location origin, Location destination, Mode mode,
			Time date) {
		return impedance.getPublicTransportRoute(origin, destination, mode, date);
	}

	@Override
	public Optional<PublicTransportRoute> getPublicTransportRoute(
			Stop start, Stop end, Mode mode, Time date) {
		return impedance.getPublicTransportRoute(start, end, mode, date);
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

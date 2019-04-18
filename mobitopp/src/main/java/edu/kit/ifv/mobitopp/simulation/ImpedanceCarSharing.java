package edu.kit.ifv.mobitopp.simulation;

import java.util.Optional;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class ImpedanceCarSharing
	implements ImpedanceIfc 
{

	protected final static float CARSHARING_COST_FREE_FLOATING_EUR_PER_MINUTE = 0.29f;
	protected final static float CARSHARING_COST_FREE_FLOATING_EUR_PER_HOUR = 14.9f;
	protected final static float CARSHARING_COST_FREE_FLOATING_EUR_PER_DAY = 59.0f;

	public final static float CARSHARING_COST_STATION_BASED_EUR_PER_KM = 0.23f;
	public final static float CARSHARING_COST_STATION_BASED_EUR_PER_HOUR = 2.80f;
	public final static float CARSHARING_COST_STATION_BASED_EUR_PER_BOOKING = 0.0f;

	protected final static float CARSHARING_BOOKING_TIME_STATION_BASED = 0.0f;

	private final ImpedanceIfc impedance;

  public ImpedanceCarSharing(ImpedanceIfc impedance) {
    this.impedance = impedance;
  }

	@Override
	public Optional<PublicTransportRoute> getPublicTransportRoute(Location source, Location target, Mode mode, Time date) {
		return Optional.empty();
	}

	@Override
	public Optional<PublicTransportRoute> getPublicTransportRoute(Stop start, Stop end, Mode mode, Time date) {
		return Optional.empty();
	}

	private float freeFloatingCost(float time) {

		float cost;

		if (time < 60) {
			cost = CARSHARING_COST_FREE_FLOATING_EUR_PER_MINUTE*time;
		} else if (time >= 60 && time < 1440) {
			cost = CARSHARING_COST_FREE_FLOATING_EUR_PER_HOUR*time/60.0f;
		} else if (time >= 1440) {
			cost = CARSHARING_COST_FREE_FLOATING_EUR_PER_DAY*time/1440.0f;
		} else {
			throw new AssertionError();
		}

		return cost;
	}
	
  @Override
  public float getTravelTime(ZoneId origin, ZoneId destination, Mode mode, Time date) {
    if (mode == Mode.CARSHARING_FREE) {
    	float time = this.impedance.getTravelTime(origin, destination, Mode.CAR, date);
    
    	float parkingStress = getParkingStress(destination, date);
    
    	float accessTime = 11.0f - 0.5f*parkingStress;
    
    	time += accessTime;
    
    	assert accessTime >= 2.0f;
    	assert accessTime <= 11.0f;
    
    	return time;
    
    } else if (mode == Mode.CARSHARING_STATION) {
    	float time = this.impedance.getTravelTime(origin, destination, Mode.CAR, date);
    
    	float parkingStress = getParkingStress(destination, date);
    
    	float accessTime = 14.0f - 0.5f*parkingStress;
    
    	time += accessTime + CARSHARING_BOOKING_TIME_STATION_BASED;
    
    	assert accessTime >= 5.0f;
    	assert accessTime <= 14.0f;
    
    	return time;
    
    } else {
    
    	return this.impedance.getTravelTime(origin, destination, mode, date);
    }
  }

  @Override
  public float getTravelCost(ZoneId origin, ZoneId destination, Mode mode, Time date) {
    if (mode == Mode.CARSHARING_STATION) {
    
    	float distanceKm = getDistance(origin, destination)/1000.0f;
    	float timeMinutes = getTravelTime(origin, destination, Mode.CAR, date);
    
    	float billableDistanceKm = (float) Math.ceil(distanceKm);
    	float billableTimeHours = (float) Math.ceil(timeMinutes/60.0);
    
    	return CARSHARING_COST_STATION_BASED_EUR_PER_BOOKING
    				+ CARSHARING_COST_STATION_BASED_EUR_PER_KM*billableDistanceKm
    				+ CARSHARING_COST_STATION_BASED_EUR_PER_HOUR*billableTimeHours;
    
    } else if (mode == Mode.CARSHARING_FREE) {
    
    	float time = getTravelTime(origin, destination, Mode.CAR, date);
    
    	float cost = freeFloatingCost(time);
    
    	return cost;
    
    } else {
    
    	return this.impedance.getTravelCost(origin, destination, mode, date);
    }
  }

  @Override
  public float getDistance(ZoneId origin, ZoneId destination) {
    return impedance.getDistance(origin, destination);
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

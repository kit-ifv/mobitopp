package edu.kit.ifv.mobitopp.simulation;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
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

	private ImpedanceIfc impedance;


	public ImpedanceCarSharing(
		ImpedanceIfc impedance
	) {
		this.impedance = impedance;
	}

	public float getDistance(int source, int target) {
		return this.impedance.getDistance(source, target);
	}

	public float getConstant(int source, int target, Time date) {
		return this.impedance.getConstant(source, target, date);
	}

	public float getParkingCost(int target, Time date) {
		return this.impedance.getParkingCost(target, date);
	}

	public float getParkingStress(int target, Time date) {
		return this.impedance.getParkingStress(target, date);
	}

	public float getOpportunities(ActivityType activityType, int zoneOid) {	
		return this.impedance.getOpportunities(activityType, zoneOid);
	}


	public float getTravelTime(int source, int target, Mode mode, Time date) {

		if (mode == Mode.CARSHARING_FREE) {
			float time = this.impedance.getTravelTime(source, target, Mode.CAR, date);

			float parkingStress = getParkingStress(target, date);

			float accessTime = 11.0f - 0.5f*parkingStress;

			time += accessTime;

			assert accessTime >= 2.0f;
			assert accessTime <= 11.0f;
	
			return time;

		} else if (mode == Mode.CARSHARING_STATION) {
			float time = this.impedance.getTravelTime(source, target, Mode.CAR, date);

			float parkingStress = getParkingStress(target, date);

			float accessTime = 14.0f - 0.5f*parkingStress;

			time += accessTime + CARSHARING_BOOKING_TIME_STATION_BASED;

			assert accessTime >= 5.0f;
			assert accessTime <= 14.0f;

			return time;

		} else {

			return this.impedance.getTravelTime(source, target, mode, date);
		}
	}
	
	@Override
	public Optional<PublicTransportRoute> getPublicTransportRoute(Location source, Location target, Mode mode, Time date) {
		return Optional.empty();
	}

	public float getTravelCost(int source, int target, Mode mode, Time date) { 

		if (mode == Mode.CARSHARING_STATION) {

			float distanceKm = getDistance(source, target)/1000.0f;
			float timeMinutes = getTravelTime(source, target, Mode.CAR, date);

			float billableDistanceKm = (float) Math.ceil(distanceKm);
			float billableTimeHours = (float) Math.ceil(timeMinutes/60.0);

			return CARSHARING_COST_STATION_BASED_EUR_PER_BOOKING
						+ CARSHARING_COST_STATION_BASED_EUR_PER_KM*billableDistanceKm
						+ CARSHARING_COST_STATION_BASED_EUR_PER_HOUR*billableTimeHours;

		} else if (mode == Mode.CARSHARING_FREE) {

			float time = getTravelTime(source, target, Mode.CAR, date);

			float cost = freeFloatingCost(time);

			return cost;

		} else {

			return this.impedance.getTravelCost(source, target, mode, date);
		}
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

}

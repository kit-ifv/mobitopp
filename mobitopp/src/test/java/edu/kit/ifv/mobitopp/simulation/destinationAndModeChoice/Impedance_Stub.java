package edu.kit.ifv.mobitopp.simulation.destinationAndModeChoice;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.Time;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;

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
	public float getTravelTime(int source, int target, Mode mode, Time date) {
		return this.time;
	}
	
	@Override
	public Optional<PublicTransportRoute> getPublicTransportRoute(Location source, Location target, Mode mode, Time date) {
		return Optional.empty();
	}

	@Override
	public float getTravelCost(int source, int target, Mode mode, Time date) {
		return this.cost;
	}

	@Override
	public float getConstant(int source, int target, Time date) {
		return this.constant;
	}

	@Override
	public float getDistance(int source, int target){
		return this.distance;
	}

	@Override
	public float getParkingCost(int target, Time date){
		return this.parkingcost;
	}

	@Override
	public float getParkingStress(int target, Time date){
		return this.parkingstress;
	}

	@Override
	public float getOpportunities(ActivityType activityType, int zoneOid) {	
		return this.opportunities;
	}


}

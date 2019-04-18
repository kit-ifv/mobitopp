package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

import java.util.TreeSet;
import java.util.Set;
import java.util.Map;

public class AttractivityCalculatorCostNextPole
	extends AttractivityCalculatorCost
	implements AttractivityCalculatorIfc
{

	float poleSensitivity;


	public AttractivityCalculatorCostNextPole(
		Map<ZoneId, Zone> zones,
		ImpedanceIfc impedance,
		String filename, 
		float poleSensitivity
	) {
  	super(zones, impedance, filename);

		this.poleSensitivity = poleSensitivity;
	}

	protected float calculateImpedance(
		Person person,
		ActivityIfc nextActivity,
		ZoneId origin, 
		ZoneId destination,
		Set<Mode> choiceSetForModes
	) {
		ActivityIfc previousActivity  = person.activitySchedule().prevActivity(nextActivity);

		ActivityType activityType = nextActivity.activityType();
		DayOfWeek weekday = nextActivity.startDate().weekDay();
		ZoneId nextFixedDestination = person.nextFixedActivityZone(nextActivity).getInternalId();

		Time startDate = previousActivity.calculatePlannedEndDate();

		boolean commutationTicket = person.hasCommuterTicket();

		TreeSet<Float> impedances = new TreeSet<>();

		for (Mode mode : choiceSetForModes) {

			float time_coeff = getParameterTime(activityType, weekday);
			float cost_coeff = getParameterCost(activityType, weekday);

			float time_next = getTravelTime(mode,origin, destination, startDate);
			float cost_next = getTravelCost(mode,origin, destination, startDate, commutationTicket)
												+ getParkingCost(mode, destination, startDate, nextActivity.duration());

			float time_pole = getTravelTime(mode, destination, nextFixedDestination, startDate);
			float cost_pole = getTravelCost(mode, destination, nextFixedDestination, startDate, commutationTicket);

			float income = person.getIncome();

			assert income > 0.0;

			float cost = 2.0f*((1.0f-this.poleSensitivity)*cost_next + this.poleSensitivity*cost_pole);
			float time = 2.0f*((1.0f-this.poleSensitivity)*time_next + this.poleSensitivity*time_pole);

		
			double sum = 
										+ time_coeff * time
										+ cost_coeff * 1000/income*cost;
	
	
			float impedance = (float) Math.exp(sum);

			impedances.add(impedance);
		}

		return impedances.first();
	}


}

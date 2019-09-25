package edu.kit.ifv.mobitopp.simulation.destinationChoice;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.TargetChoiceParameterCost;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;


public class DestinationChoiceModelStuttgart
	implements DestinationChoiceUtilityFunction
{

	private final TargetChoiceParameterCost targetParameter;
	protected final ImpedanceIfc impedance;

	protected float poleSensitivity = 0.5f;

	public DestinationChoiceModelStuttgart(
		ImpedanceIfc impedance,
		String filename
	) {

		this.targetParameter = new TargetChoiceParameterCost(filename);
		this.impedance = impedance;
	}


	public double calculateUtility(
		Person person,
		ActivityIfc nextActivity,
		Zone source,
		Zone target,
		ActivityType activityType,
		Set<Mode> choiceSetForModes 
	) {

		ZoneId origin = source.getId();
		ZoneId destination = target.getId();

		Time date = person.activitySchedule().prevActivity(nextActivity).calculatePlannedEndDate();

		if (!isReachable(origin,destination)) {  return Double.NEGATIVE_INFINITY; }

		DayOfWeek weekday = nextActivity.startDate().weekDay();

		double	opportunity = getOpportunity(activityType, destination);

		if (opportunity == Double.NEGATIVE_INFINITY) { return Double.NEGATIVE_INFINITY; }

		double	opportunityAdjustment = getOpportunityAdjustment(activityType, target);

		ZoneId nextPoleOid = person.nextFixedActivityZone(nextActivity).getId();

		Mode mode = fastestMode(person, nextActivity, date, origin, destination, nextPoleOid, choiceSetForModes);
		boolean commuterTicket = person.hasCommuterTicket();


		float time_next = this.impedance.getTravelTime(origin, destination, mode, date);
		float cost_next = (mode == Mode.PUBLICTRANSPORT && commuterTicket ? 0.0f 
											: this.impedance.getTravelCost(origin, destination, mode, date)
											)
											+ calculateParkingCost(mode, destination, date, nextActivity.duration());

		float time_pole = this.impedance.getTravelTime(destination, nextPoleOid, mode, date);
		float cost_pole = mode == Mode.PUBLICTRANSPORT && commuterTicket ? 0.0f 
											: this.impedance.getTravelCost(destination, nextPoleOid, mode, date);

		float constant = this.impedance.getConstant(origin, destination, date);

		float income = person.getIncome();

		float time_coeff = this.getParameterTime(activityType, weekday);
		float cost_coeff = this.getParameterCost(activityType, weekday);

		float opportunity_coeff = this.getParameterOpportunity(activityType);


		double utility = 	opportunity_coeff * opportunity 
										+ opportunity_coeff * opportunityAdjustment 
										- time_coeff * ( time_next + time_pole)
										- cost_coeff * 1000/income * (cost_next + cost_pole)
										- constant;
		return utility;
	}

	protected Mode fastestMode(
		Person person,
		ActivityIfc nextActivity,
		Time date,
		ZoneId origin, 
		ZoneId destination,
		ZoneId nextPoleOid,
		Set<Mode> choiceSetForModes
	) {

		ActivityType activityType = nextActivity.activityType();
		DayOfWeek weekday = nextActivity.startDate().weekDay();

		boolean commuterTicket = person.hasCommuterTicket();

		TreeMap<Double,Mode> impedances = new TreeMap<Double,Mode>();

		for (Mode mode : getModes(choiceSetForModes)) {

			float time_coeff = this.getParameterTime(activityType, weekday);
			float cost_coeff = this.getParameterCost(activityType, weekday);

			float time_next = this.impedance.getTravelTime(origin, destination, mode, date);
			float cost_next = (mode == Mode.PUBLICTRANSPORT && commuterTicket ? 0.0f
												: this.impedance.getTravelCost(origin, destination, mode, date)
												)
											+ calculateParkingCost(mode, destination, date, nextActivity.duration());

			float time_pole = this.impedance.getTravelTime(destination, nextPoleOid, mode, date);
			float cost_pole = mode == Mode.PUBLICTRANSPORT && commuterTicket ? 0.0f
											 : this.impedance.getTravelCost(destination, nextPoleOid, mode, date);

			float constant = this.impedance.getConstant(origin, destination, date);

			float income = person.getIncome();

			float cost = 2.0f*((1.0f-this.poleSensitivity)*cost_next + this.poleSensitivity*cost_pole);
			float time = 2.0f*((1.0f-this.poleSensitivity)*time_next + this.poleSensitivity*time_pole);

		
			double sum = 
										+ time_coeff * time
										+ cost_coeff * 1000/income*cost
										+ constant;
	
			impedances.put(sum,mode);
		}

		Double minimum = impedances.firstKey();

		return impedances.get(minimum);
	}


	float calculateParkingCost(
		Mode mode, 
		ZoneId destination, 
		Time date,
		int durationInMinutes
	) {

		if ( mode == Mode.PEDESTRIAN 
				|| mode == Mode.BIKE 
				|| mode == Mode.PUBLICTRANSPORT
		) {
			return 0.0f;
		}

		if (mode == Mode.CAR) {

			float costPerHour = this.impedance.getParkingCost(destination, date);

			return durationInMinutes/60 * costPerHour;
		}

		throw new IllegalArgumentException();
	}

	protected double getOpportunity(
		ActivityType activityType,
		ZoneId destination
	) {

		float opportunity = this.impedance.getOpportunities(activityType, destination);

		return Math.log(opportunity);
	}

	protected double getOpportunityAdjustment(
		ActivityType activityType,
		Zone zone
	) {

		if(isInternal(zone)) {
			return 0.0f;
		}

		return externalAdjustmentFor(activityType);
	}

	private boolean isInternal(Zone zone) {
		String zoneId = zone.getId().getExternalId();
		
		boolean isOutlying = ZoneClassificationType.outlyingArea.equals(zone.getClassification())
				|| ZoneClassificationType.extendedStudyArea.equals(zone.getClassification());
		boolean isExternal = isOutlying && (zoneId.startsWith("7")
																				|| zoneId.startsWith("8")
																				|| zoneId.startsWith("9"));
		return !isExternal;
	}

	private double externalAdjustmentFor(ActivityType activityType) {
		switch(activityType.getTypeAsInt()) {
		 case 2:
				return Math.log(6e10f);
		 case 6:
				return Math.log(8e8f);
		 case 11:
				return Math.log(7e5f);
		 case 12:
				return Math.log(5e7f);
		 case 41:
				return Math.log(1e3f);
		 case 42:
				return Math.log(3e6f);
		 case 51:
				return Math.log(1e6f);
		 case 52:
				return Math.log(1e10f);
		 case 53:
				return Math.log(7e6f);
		 case 9:
				return Math.log(2e0f);
		 case 77:
				return Math.log(1e-1f);
		}

		return 0.0f;
	}


	protected boolean isReachable(
		ZoneId origin, 
		ZoneId destination
	) {

		float distance = this.impedance.getDistance(origin,destination);

		return distance < 999999.0f;
	}

	protected Collection<Mode> getModes(Set<Mode> choiceSetForModes) {
		Collection<Mode> c = new LinkedHashSet<Mode>();
		c.add(Mode.PEDESTRIAN);
		c.add(Mode.PUBLICTRANSPORT);
		c.addAll(choiceSetForModes);
		return c;
	}

	protected float getScalingParameterImpedance(ActivityType activityType, DayOfWeek weekday) {
		float scaling = 1.0f;

		if (weekday == DayOfWeek.FRIDAY) 	{ 
			
			switch(activityType.getTypeAsInt()) {
				case 2:  scaling = 1.0f; break;
				case 6:  scaling = 0.95f; break;
				case 11: scaling = 0.95f; break;
				case 12: scaling = 0.85f; break;
				case 51: scaling = 0.9f; break;
				case 52: scaling = 0.8f; break;
				case 53: scaling = 0.9f; break;
				default:
					scaling = 1.0f;
			}
		}

		if (weekday == DayOfWeek.SATURDAY) 	{ 
			
			switch(activityType.getTypeAsInt()) {
				case 2:  scaling = 1.1f; break;
				case 6:  scaling = 0.6f; break;
				case 11: scaling = 0.9f; break;
				case 12: scaling = 0.8f; break;
				case 41: scaling = 0.8f; break; 
				case 42: scaling = 0.8f; break; 
				case 51: scaling = 0.8f; break;
				case 52: scaling = 0.7f; break;
				case 53: scaling = 0.8f; break;
				default:
					scaling = 1.0f;
			}
		}

		if (weekday == DayOfWeek.SUNDAY) 	{ 
			
			switch(activityType.getTypeAsInt()) {
				case 2:  scaling = 1.05f; break;
				case 6:  scaling = 0.6f; break;
				case 11: scaling = 1.0f; break;
				case 12: scaling = 0.85f; break;
				case 41: scaling = 4.0f; break;
				case 42: scaling = 1.5f; break;
				case 51: scaling = 0.8f; break;
				case 52: scaling = 0.7f; break;
				case 53: scaling = 0.9f; break;
				default:
					scaling = 1.0f;
			}
		}

		return scaling;
	}

	public float getParameterCost(ActivityType activityType, DayOfWeek weekday) {
		return this.targetParameter.getParameterCost(activityType)
						* getScalingParameterImpedance(activityType,weekday);
	}

	public float getParameterTime(ActivityType activityType, DayOfWeek weekday) {
		return this.targetParameter.getParameterTime(activityType)
						* getScalingParameterImpedance(activityType,weekday);
	}

	public float getParameterOpportunity(ActivityType activityType) {
		return this.targetParameter.getParameterOpportunity(activityType);
	}



}

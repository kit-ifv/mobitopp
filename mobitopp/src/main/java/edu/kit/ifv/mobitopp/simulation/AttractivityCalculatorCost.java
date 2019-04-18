package edu.kit.ifv.mobitopp.simulation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

public class AttractivityCalculatorCost
	implements AttractivityCalculatorIfc {

	private TargetChoiceParameterCost targetParameter;

	private Map<Integer,Float> scalingFactor = new LinkedHashMap<Integer,Float>(); 

  private final Map<ZoneId, Zone> zones;

	private final ImpedanceIfc impedance;


	public AttractivityCalculatorCost(
		Map<ZoneId,Zone> zones,
		ImpedanceIfc impedance,
		String filename
	) {

		this.zones = Collections.unmodifiableMap(zones);

		this.impedance = impedance;
		this.targetParameter = new TargetChoiceParameterCost(filename);



		this.scalingFactor.put( 2, 6e10f);
		this.scalingFactor.put( 6, 8e8f);
		this.scalingFactor.put(11, 7e5f);
		this.scalingFactor.put(12, 5e7f);
		this.scalingFactor.put(41, 1e3f);
		this.scalingFactor.put(42, 3e6f);
		this.scalingFactor.put(51, 1e6f);
		this.scalingFactor.put(52, 1e10f);
		this.scalingFactor.put(53, 7e6f);
		this.scalingFactor.put( 9, 2e0f);
		this.scalingFactor.put(77, 1e-1f);

		this.scalingFactor.put(4, 1e3f);
		this.scalingFactor.put(5, 1e6f);

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

	protected float getParameterCost(ActivityType activityType, DayOfWeek weekday) {
		return this.targetParameter.getParameterCost(activityType)
						* getScalingParameterImpedance(activityType,weekday);
	}

	protected float getParameterTime(ActivityType activityType, DayOfWeek weekday) {
		return this.targetParameter.getParameterTime(activityType)
						* getScalingParameterImpedance(activityType,weekday);
	}

	protected float getParameterOpportunity(ActivityType activityType) {
		return this.targetParameter.getParameterOpportunity(activityType);
	}

	public Map<Zone, Float> calculateAttractivities(
		Person person,
		ActivityIfc nextActivity,
		Zone currentZone,
		Collection<Zone> possibleTargetZones,
		ActivityType activityType,
		Set<Mode> choiceSetForModes
	) {

		Map<Zone, Float> result = new LinkedHashMap<Zone, Float>();

		for (Zone possibleDestination : possibleTargetZones) {

			Float attractivity = calculateAttractivity( 
																						person,
																						nextActivity,
																						currentZone.getId(), 
																						possibleDestination.getId(), 
																						activityType, 
																						choiceSetForModes
																					);

			result.put(possibleDestination, attractivity);
		}

		return result;
	}

	protected float calculateAttractivity(
		Person person,
		ActivityIfc nextActivity,
		ZoneId origin, 
		ZoneId destination,
		ActivityType activityType,
		Set<Mode> choiceSetForModes 
	) {
		float opportunity = 0.0f;

		if (isReachable(origin,destination)) {
			opportunity = getOpportunity(activityType, destination);
		} 

		float impedance = calculateImpedance(	person, 
																					nextActivity, 
																					origin, 
																					destination, 
																					choiceSetForModes
																				);

		return opportunity / impedance;
	}

	protected float getOpportunity(
		ActivityType activityType,
		ZoneId destination
	) {

		Zone zone = this.zones.get(destination);

		Attractivities attractivity = zone.attractivities();

		float opportunity = 1.0f;

		if (attractivity.getItems().containsKey(activityType)) {
			opportunity = attractivity.getItems().get(activityType);
		}

		float scaling_factor = 1.0f;

		if(IsZoneExternal(destination)) {

			scaling_factor = this.scalingFactor.get(activityType.getTypeAsInt());
		}


		opportunity *= scaling_factor;

		float opportunity_coeff = getParameterOpportunity(activityType);

		return (float) Math.pow(opportunity,opportunity_coeff);
	}

	protected boolean IsVRS(ZoneId id) {
		String zoneId = id.getExternalId();

		boolean isStuttgart = zoneId.length() == 6 && !zoneId.substring(0,2).equals("Z2");

		return isStuttgart;
	}

	protected boolean IsZoneOutlying(ZoneId zoneId) {
		Zone zone = this.zones.get(zoneId);

		return ZoneClassificationType.outlyingArea.equals(zone.getClassification());
	}

	protected boolean IsZoneExternal(ZoneId destination) {

		Zone zone = this.zones.get(destination);
		String zoneId = zone.getId().getExternalId();

		boolean isOutlying = ZoneClassificationType.outlyingArea.equals(zone.getClassification());
		boolean isExternal = isOutlying && (zoneId.substring(0,2).equals("Z7")
																				|| zoneId.substring(0,2).equals("Z8")
																				|| zoneId.substring(0,2).equals("Z9"));


		return isExternal;
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

		Time startDate = previousActivity.calculatePlannedEndDate();


		TreeSet<Float> impedances = new TreeSet<>();

		for (Mode mode : choiceSetForModes) {

			float time_coeff = getParameterTime(activityType, weekday);
			float cost_coeff = getParameterCost(activityType, weekday);

			float time = getTravelTime(mode,origin,destination, startDate);
			float cost = getTravelCost(mode,origin,destination, startDate,
											person.hasCommuterTicket()
									)
									+ getParkingCost(mode, destination, startDate, nextActivity.duration()
									);
			
			float income = person.getIncome();

			double sum = 
										+ time_coeff * time
										+ cost_coeff * 1000*cost/income;

			float impedance = (float) Math.exp(sum);

			impedances.add(impedance);
		}

		return impedances.first();
	}

	protected float getTravelTime(Mode mode, ZoneId origin, ZoneId destination, Time date) {
		float travelTime = this.impedance.getTravelTime(origin, destination, mode, date);
		return mode == Mode.PUBLICTRANSPORT ?  Math.min(1440.0f,travelTime) : travelTime;
	}

	protected float getTravelCost(
		Mode mode,
		ZoneId origin, 
		ZoneId destination,
		Time date,
		boolean commmuterTicket
	) {

		if (mode == Mode.PEDESTRIAN || mode == Mode.BIKE) {
			return 0.0f;
		}

		if (mode == Mode.PUBLICTRANSPORT && commmuterTicket) {
			return 0.0f;
		}

		if (mode == Mode.CAR) {

			return this.impedance.getTravelCost(origin, destination, mode, date);
		}

		if (mode == Mode.PUBLICTRANSPORT) {

			return this.impedance.getTravelCost(origin, destination, mode, date);
		}

		return this.impedance.getTravelCost(origin, destination, mode, date);
	}

	protected float getParkingCost(
		Mode mode,
		ZoneId destination,
		Time date,
		int duration
	) {

		if ( mode == Mode.PEDESTRIAN 
				|| mode == Mode.BIKE 
				|| mode == Mode.PUBLICTRANSPORT
				|| mode == Mode.PASSENGER
		) {
			return 0.0f;
		}


		if (mode == Mode.CAR
				|| mode == Mode.CARSHARING_STATION
				|| mode == Mode.CARSHARING_FREE
		) {

			float parkingCostPerMinute = this.impedance.getParkingCost(destination, date);

			return duration/60 * parkingCostPerMinute;
		}

		throw new IllegalArgumentException();
	}

  protected float getDistance(ZoneId origin, ZoneId destination) {
		return this.impedance.getDistance(origin, destination);
	}

  boolean isReachable(ZoneId origin, ZoneId destination) {
		float distance = getDistance(origin,destination);
		return distance < 999999.0f;
	}

}

package edu.kit.ifv.mobitopp.simulation.destinationChoice;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Employment;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.HashMap;

public class DestinationChoiceModelEmployment
	implements DestinationChoiceUtilityFunction
{

	protected final ImpedanceIfc impedance;


	protected Map<ActivityType,Double> coeff_time_by_purpose = new HashMap<ActivityType,Double>();
	protected Map<ActivityType,Double> coeff_cost_by_purpose = new HashMap<ActivityType,Double>();
	protected Map<ActivityType,Double> coeff_opportunity_by_purpose = new HashMap<ActivityType,Double>();

	protected Map<Employment,Double> coeff_time_by_employment = new HashMap<Employment,Double>();

	protected Map<ActivityType,Double> scaling_by_purpose = new HashMap<ActivityType,Double>();
	protected Map<Employment,Double> scaling_by_employment = new HashMap<Employment,Double>();

	{
		coeff_time_by_purpose.put(ActivityType.BUSINESS,-0.01054540);
		coeff_time_by_purpose.put(ActivityType.SERVICE,-0.09110973);
		coeff_time_by_purpose.put(ActivityType.PRIVATE_BUSINESS,-0.07776060);
		coeff_time_by_purpose.put(ActivityType.PRIVATE_VISIT,-0.05457433);
		coeff_time_by_purpose.put(ActivityType.SHOPPING_DAILY,-0.11127299);
		coeff_time_by_purpose.put(ActivityType.SHOPPING_OTHER,-0.05947906);
		coeff_time_by_purpose.put(ActivityType.LEISURE_INDOOR,-0.03639682);
		coeff_time_by_purpose.put(ActivityType.LEISURE_OUTDOOR,-0.06822290);
		coeff_time_by_purpose.put(ActivityType.LEISURE_OTHER,-0.05259948);
		coeff_time_by_purpose.put(ActivityType.LEISURE_WALK,-0.17635836);
		coeff_time_by_purpose.put(ActivityType.UNDEFINED,-0.05);
		coeff_time_by_purpose.put(ActivityType.OTHERHOME,-0.05);

		coeff_time_by_employment.put(Employment.EDUCATION,0.00010795);
		coeff_time_by_employment.put(Employment.FULLTIME,0.0); // reference
		coeff_time_by_employment.put(Employment.UNEMPLOYED,-0.00224253);			// unemployed
		coeff_time_by_employment.put(Employment.NONE,-0.00699810);				// other
		coeff_time_by_employment.put(Employment.HOMEKEEPER,-0.01375566);	// not active in labour market
		coeff_time_by_employment.put(Employment.PARTTIME,-0.01155729);
		coeff_time_by_employment.put(Employment.MARGINAL,-0.01155729);
		coeff_time_by_employment.put(Employment.RETIRED,-0.00343436);
		coeff_time_by_employment.put(Employment.STUDENT,-0.01159740);
		// coeff_time_by_employment.put(Employment.STUDENT_PRIMARY,0.0);
		// coeff_time_by_employment.put(Employment.STUDENT_SECONDARY,0.0);
		// coeff_time_by_employment.put(Employment.STUDENT_TERTIARY,0.0);
		coeff_time_by_employment.put(Employment.STUDENT_PRIMARY,-0.01159740);
		coeff_time_by_employment.put(Employment.STUDENT_SECONDARY,-0.01159740);
		coeff_time_by_employment.put(Employment.STUDENT_TERTIARY,-0.01159740);
		coeff_time_by_employment.put(Employment.INFANT,-0.01159740);

		coeff_cost_by_purpose.put(ActivityType.BUSINESS,-0.41934536);
		coeff_cost_by_purpose.put(ActivityType.SERVICE,-0.33389025);
		coeff_cost_by_purpose.put(ActivityType.PRIVATE_BUSINESS,-0.26954419);
		coeff_cost_by_purpose.put(ActivityType.PRIVATE_VISIT,-0.12415494);
		coeff_cost_by_purpose.put(ActivityType.SHOPPING_DAILY,-0.47753511);
		coeff_cost_by_purpose.put(ActivityType.SHOPPING_OTHER,-0.33848300);
		coeff_cost_by_purpose.put(ActivityType.LEISURE_INDOOR,-0.49940005);
		coeff_cost_by_purpose.put(ActivityType.LEISURE_OUTDOOR,-0.22842223);
		coeff_cost_by_purpose.put(ActivityType.LEISURE_OTHER,-0.31403821);
		coeff_cost_by_purpose.put(ActivityType.LEISURE_WALK,-0.70369097);
		coeff_cost_by_purpose.put(ActivityType.OTHERHOME,-0.5);
		coeff_cost_by_purpose.put(ActivityType.UNDEFINED,-0.5);

		coeff_opportunity_by_purpose.put(ActivityType.BUSINESS,0.26737545);
		coeff_opportunity_by_purpose.put(ActivityType.SERVICE,0.33691243);
		coeff_opportunity_by_purpose.put(ActivityType.PRIVATE_BUSINESS,0.46352602);
		coeff_opportunity_by_purpose.put(ActivityType.PRIVATE_VISIT,0.37621891);
		coeff_opportunity_by_purpose.put(ActivityType.SHOPPING_DAILY,0.27836507);
		coeff_opportunity_by_purpose.put(ActivityType.SHOPPING_OTHER,0.35537183);
		coeff_opportunity_by_purpose.put(ActivityType.LEISURE_INDOOR,0.38143512);
		coeff_opportunity_by_purpose.put(ActivityType.LEISURE_OUTDOOR,0.29088396);
		coeff_opportunity_by_purpose.put(ActivityType.LEISURE_OTHER,0.47347275);
		coeff_opportunity_by_purpose.put(ActivityType.LEISURE_WALK,0.09158896);
		coeff_opportunity_by_purpose.put(ActivityType.OTHERHOME,0.0);
		coeff_opportunity_by_purpose.put(ActivityType.UNDEFINED,0.0);
		
		// last: v12, next: v13

		scaling_by_purpose.put(ActivityType.BUSINESS, 15.0);
		scaling_by_purpose.put(ActivityType.SERVICE, 1.3);
		scaling_by_purpose.put(ActivityType.PRIVATE_BUSINESS, 1.4); 
		scaling_by_purpose.put(ActivityType.PRIVATE_VISIT, 1.75); 
		scaling_by_purpose.put(ActivityType.SHOPPING_DAILY, 1.2);
		scaling_by_purpose.put(ActivityType.SHOPPING_OTHER, 1.45);
		scaling_by_purpose.put(ActivityType.LEISURE_INDOOR, 2.2);
		scaling_by_purpose.put(ActivityType.LEISURE_OUTDOOR, 1.4);
		scaling_by_purpose.put(ActivityType.LEISURE_OTHER, 2.0);
		scaling_by_purpose.put(ActivityType.LEISURE_WALK, 1.4);
		scaling_by_purpose.put(ActivityType.OTHERHOME, 2.1);
		scaling_by_purpose.put(ActivityType.UNDEFINED, 2.1);

		scaling_by_employment.put(Employment.FULLTIME,1.05); // reference
		scaling_by_employment.put(Employment.UNEMPLOYED,0.95);	
		scaling_by_employment.put(Employment.NONE,0.8);				
		scaling_by_employment.put(Employment.HOMEKEEPER,0.87);	
		scaling_by_employment.put(Employment.PARTTIME,0.98);
		scaling_by_employment.put(Employment.MARGINAL,1.05);
		scaling_by_employment.put(Employment.RETIRED,0.9);
		scaling_by_employment.put(Employment.STUDENT,0.8);
		scaling_by_employment.put(Employment.STUDENT_PRIMARY,0.92);
		scaling_by_employment.put(Employment.STUDENT_SECONDARY,0.92);
		scaling_by_employment.put(Employment.STUDENT_TERTIARY,0.8);
		scaling_by_employment.put(Employment.EDUCATION,1.15);
		scaling_by_employment.put(Employment.INFANT,0.95);
	}






	public DestinationChoiceModelEmployment(
		ImpedanceIfc impedance
	) {
		this.impedance = impedance;
	}


	// interface DestinationChoiceModel
	public double calculateUtility(
		Person person,
		ActivityIfc nextActivity,
		Zone origin, 
		Zone destination,
		ActivityType activityType,
		Set<Mode> availableModes 
	) {
		
		assert person != null;

		ZoneId originId = origin.getId();
		ZoneId destinationId = destination.getId();

		Time date = person.activitySchedule().prevActivity(nextActivity).calculatePlannedEndDate();

		boolean commuterTicket = person.hasCommuterTicket();
		ZoneId nextFixedDestinationId = person.nextFixedActivityZone(nextActivity).getId();
		
		Mode mode = fastestMode(person, nextActivity, date, originId, destinationId, nextFixedDestinationId, availableModes);


		float opportunity = this.impedance.getOpportunities(activityType, destinationId);

		float time_next = this.impedance.getTravelTime(originId, destinationId, mode, date);
		float cost_next = mode == StandardMode.PUBLICTRANSPORT && commuterTicket ? 0.0f 
													: this.impedance.getTravelCost(originId, destinationId, mode, date);

		float time_pole = this.impedance.getTravelTime(destinationId, nextFixedDestinationId, mode, date);
		float cost_pole = mode == StandardMode.PUBLICTRANSPORT && commuterTicket ? 0.0f 
													: this.impedance.getTravelCost(destinationId, nextFixedDestinationId, mode, date);

		double OPPORTUNITY_OFFSET = 1.0;

		double log_opportunity = Math.log(OPPORTUNITY_OFFSET + opportunity);

		// int activity = nextActivity.getActivityType().getTypeAsInt();
		// int activity = activityType.getTypeAsInt();
		
		assert person != null;
		assert person.employment() != null;
		assert coeff_time_by_employment != null;


		double time_coeff 						= coeff_time_by_purpose.get(activityType);
		double cost_coeff 						= coeff_cost_by_purpose.get(activityType);
		double opportunity_coeff 			= coeff_opportunity_by_purpose.get(activityType);
		double time_employment_coeff 	= coeff_time_by_employment.get(person.employment());
		

		double utility 	= opportunity_coeff * log_opportunity	
										+ cost_coeff * (cost_next + cost_pole)
										+ time_coeff * (time_next + time_pole)
										+ time_employment_coeff * (time_next + time_pole);

		double scaling = scaling_by_purpose.get(activityType);
		double scaling_emp = scaling_by_employment.get(person.employment());

		return scaling * scaling_emp * utility;
	}

	protected Mode fastestMode(
		Person person,
		ActivityIfc nextActivity,
		Time date,
		ZoneId origin, 
		ZoneId destination,
		ZoneId nextFixedDestination,
		Set<Mode> availableModes
	) {

		TreeMap<Float,Mode> travelTime = new TreeMap<Float,Mode>();

		for (Mode mode : availableModes) {

			float time_next = this.impedance.getTravelTime(origin, destination, mode, date);
			float time_pole = this.impedance.getTravelTime(destination, nextFixedDestination, mode, date);

			travelTime.put(time_next + time_pole,mode);
		}

		Float minimum = travelTime.firstKey();

		return travelTime.get(minimum);
	}


}

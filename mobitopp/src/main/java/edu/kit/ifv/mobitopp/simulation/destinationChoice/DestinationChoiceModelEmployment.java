package edu.kit.ifv.mobitopp.simulation.destinationChoice;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;
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


	protected Map<Integer,Double> coeff_time_by_purpose = new HashMap<Integer,Double>();
	protected Map<Integer,Double> coeff_cost_by_purpose = new HashMap<Integer,Double>();
	protected Map<Integer,Double> coeff_opportunity_by_purpose = new HashMap<Integer,Double>();

	protected Map<Employment,Double> coeff_time_by_employment = new HashMap<Employment,Double>();

	protected Map<Integer,Double> scaling_by_purpose = new HashMap<Integer,Double>();
	protected Map<Employment,Double> scaling_by_employment = new HashMap<Employment,Double>();

	{
		coeff_time_by_purpose.put( 2,-0.01054540);
		coeff_time_by_purpose.put( 6,-0.09110973);
		coeff_time_by_purpose.put(11,-0.07776060);
		coeff_time_by_purpose.put(12,-0.05457433);
		coeff_time_by_purpose.put(41,-0.11127299);
		coeff_time_by_purpose.put(42,-0.05947906);
		coeff_time_by_purpose.put(51,-0.03639682);
		coeff_time_by_purpose.put(52,-0.06822290);
		coeff_time_by_purpose.put(53,-0.05259948);
		coeff_time_by_purpose.put(77,-0.17635836);
		coeff_time_by_purpose.put( 9,0.0);

		coeff_time_by_employment.put(Employment.EDUCATION,0.00010795);
		coeff_time_by_employment.put(Employment.FULLTIME,0.0); // reference
		coeff_time_by_employment.put(Employment.UNEMPLOYED,-0.00224253);			// unemployed
		coeff_time_by_employment.put(Employment.NONE,-0.00699810);				// other
		coeff_time_by_employment.put(Employment.HOMEKEEPER,-0.01375566);	// not active in labour market
		coeff_time_by_employment.put(Employment.PARTTIME,-0.01155729);
		coeff_time_by_employment.put(Employment.RETIRED,-0.00343436);
		coeff_time_by_employment.put(Employment.STUDENT,-0.01159740);
		coeff_time_by_employment.put(Employment.STUDENT_PRIMARY,-0.01159740);
		coeff_time_by_employment.put(Employment.STUDENT_SECONDARY,-0.01159740);
		coeff_time_by_employment.put(Employment.STUDENT_TERTIARY,-0.01159740);
		coeff_time_by_employment.put(Employment.INFANT,-0.01159740);

		coeff_cost_by_purpose.put( 2,-0.41934536);
		coeff_cost_by_purpose.put( 6,-0.33389025);
		coeff_cost_by_purpose.put(11,-0.26954419);
		coeff_cost_by_purpose.put(12,-0.12415494);
		coeff_cost_by_purpose.put(41,-0.47753511);
		coeff_cost_by_purpose.put(42,-0.33848300);
		coeff_cost_by_purpose.put(51,-0.49940005);
		coeff_cost_by_purpose.put(52,-0.22842223);
		coeff_cost_by_purpose.put(53,-0.31403821);
		coeff_cost_by_purpose.put(77,-0.70369097);
		coeff_cost_by_purpose.put(9,0.0);

		coeff_opportunity_by_purpose.put( 2,0.26737545);
		coeff_opportunity_by_purpose.put( 6,0.33691243);
		coeff_opportunity_by_purpose.put(11,0.46352602);
		coeff_opportunity_by_purpose.put(12,0.37621891);
		coeff_opportunity_by_purpose.put(41,0.27836507);
		coeff_opportunity_by_purpose.put(42,0.35537183);
		coeff_opportunity_by_purpose.put(51,0.38143512);
		coeff_opportunity_by_purpose.put(52,0.29088396);
		coeff_opportunity_by_purpose.put(53,0.47347275);
		coeff_opportunity_by_purpose.put(77,0.09158896);
		coeff_opportunity_by_purpose.put( 9,0.0);

		scaling_by_purpose.put( 2, 1.6);
		scaling_by_purpose.put( 6, 0.95);
		scaling_by_purpose.put(11, 1.00); // private business
		scaling_by_purpose.put(12, 1.1); // visit
		scaling_by_purpose.put(41, 0.9);
		scaling_by_purpose.put(42, 0.95);
		scaling_by_purpose.put(51, 1.15);
		scaling_by_purpose.put(52, 1.05);
		scaling_by_purpose.put(53, 1.15);
		scaling_by_purpose.put(77, 3.0);
		scaling_by_purpose.put( 9, 1.0);

		scaling_by_employment.put(Employment.EDUCATION,1.0);
		scaling_by_employment.put(Employment.FULLTIME,1.0); // reference
		scaling_by_employment.put(Employment.UNEMPLOYED,0.9);			// unemployed
		scaling_by_employment.put(Employment.NONE,0.9);				// other
		scaling_by_employment.put(Employment.HOMEKEEPER,0.95);	// not active in labour market
		scaling_by_employment.put(Employment.PARTTIME,1.15);
		scaling_by_employment.put(Employment.RETIRED,1.0);
		scaling_by_employment.put(Employment.STUDENT,1.0);
		scaling_by_employment.put(Employment.STUDENT_PRIMARY,0.9);
		scaling_by_employment.put(Employment.STUDENT_SECONDARY,0.9);
		scaling_by_employment.put(Employment.STUDENT_TERTIARY,0.8);
		scaling_by_employment.put(Employment.INFANT,1.0);
	}

	public DestinationChoiceModelEmployment(ImpedanceIfc impedance) {
		this.impedance = impedance;
	}

	public double calculateUtility(
		Person person,
		ActivityIfc nextActivity,
		Zone sourceZone, 
		Zone destinationZone,
		ActivityType activityType,
		Set<Mode> availableModes 
	) {
		
		assert person != null;

		int source = sourceZone.getOid();
		int destination = destinationZone.getOid();

		Time date = person.activitySchedule().prevActivity(nextActivity).calculatePlannedEndDate();

		boolean commuterTicket = person.hasCommuterTicket();
		int nextPole = person.nextFixedActivityZone(nextActivity).getOid();

		Mode mode = fastestMode(date, source, destination, nextPole, availableModes);


		float opportunity = this.impedance.getOpportunities(activityType, destination);

		float time_next = this.impedance.getTravelTime(source, destination, mode, date);
		float cost_next = mode == Mode.PUBLICTRANSPORT && commuterTicket ? 0.0f 
													: this.impedance.getTravelCost(source, destination, mode, date);

		float time_pole = this.impedance.getTravelTime(destination, nextPole, mode, date);
		float cost_pole = mode == Mode.PUBLICTRANSPORT && commuterTicket ? 0.0f 
													: this.impedance.getTravelCost(destination, nextPole, mode, date);

		double OPPORTUNITY_OFFSET = 1.0;

		double log_opportunity = Math.log(OPPORTUNITY_OFFSET + opportunity);

		int activity = activityType.getTypeAsInt();
		
		assert person != null;
		assert person.employment() != null;
		assert coeff_time_by_employment != null;


		double time_coeff 						= coeff_time_by_purpose.get(activity);
		double cost_coeff 						= coeff_cost_by_purpose.get(activity);
		double opportunity_coeff 			= coeff_opportunity_by_purpose.get(activity);
		double time_employment_coeff 	= coeff_time_by_employment.get(person.employment());
		

		double utility 	= opportunity_coeff * log_opportunity	
										+ cost_coeff * (cost_next + cost_pole)
										+ time_coeff * (time_next + time_pole)
										+ time_employment_coeff * (time_next + time_pole);

		double scaling = scaling_by_purpose.get(activity);
		double scaling_emp = scaling_by_employment.get(person.employment());

		return scaling * scaling_emp * utility;
	}

	protected Mode fastestMode(
		Time date,
		int sourceZoneOid, 
		int targetZoneOid,
		int nextPoleOid,
		Set<Mode> availableModes
	) {

		TreeMap<Float,Mode> travelTime = new TreeMap<Float,Mode>();

		for (Mode mode : availableModes) {

			float time_next = this.impedance.getTravelTime(sourceZoneOid, targetZoneOid, mode, date);
			float time_pole = this.impedance.getTravelTime(targetZoneOid, nextPoleOid, mode, date);

			travelTime.put(time_next + time_pole,mode);
		}

		Float minimum = travelTime.firstKey();

		return travelTime.get(minimum);
	}


}

package edu.kit.ifv.mobitopp.simulation.destinationChoice;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.HashMap;

public class DestinationChoiceModelTime
	implements DestinationChoiceUtilityFunction
{

	static enum AgeGroup {AGE_0_9, AGE_10_17, AGE_18_25, AGE_26_35, AGE_36_50, AGE_51_60, AGE_61_70, AGE_70_ABOVE};

	protected final ImpedanceIfc impedance;

	protected Double coeff_time							= -0.02655185;
	protected Double coeff_time_nolicence		=  0.00113082;
	protected Double coeff_time_ticket			=  0.00338988;
	protected Double coeff_time_female			=  0.00148197;


	protected Map<Integer,Double> coeff_time_by_purpose 						= new HashMap<Integer,Double>();
	protected Map<Employment,Double> coeff_time_by_employment 	= new HashMap<Employment,Double>();
	protected Map<AgeGroup,Double> coeff_time_by_age 								= new HashMap<AgeGroup,Double>();

	protected Map<Integer,Double> coeff_opportunity_by_purpose = new HashMap<Integer,Double>();

	{
		coeff_time_by_purpose.put( 2,  0.0);					// reference level
		coeff_time_by_purpose.put( 6, -0.07186643);
		coeff_time_by_purpose.put(11, -0.05783302);
		coeff_time_by_purpose.put(12, -0.03177737);
		coeff_time_by_purpose.put(41, -0.08458656);
		coeff_time_by_purpose.put(42, -0.04015907);
		coeff_time_by_purpose.put(51, -0.03141726);
		coeff_time_by_purpose.put(52, -0.05361472);
		coeff_time_by_purpose.put(53, -0.04225520);
		coeff_time_by_purpose.put(77, -0.08543875);
		coeff_time_by_purpose.put( 9,  0.01067785);

		coeff_time_by_employment.put(Employment.FULLTIME,		0.0); // reference
		coeff_time_by_employment.put(Employment.EDUCATION,	-0.00420771);
		coeff_time_by_employment.put(Employment.UNEMPLOYED,		-0.00393118);			// unemployed
		coeff_time_by_employment.put(Employment.NONE,				-0.00793268);				// other
		coeff_time_by_employment.put(Employment.HOMEKEEPER,	-0.00776387);	// not active in labour market
		coeff_time_by_employment.put(Employment.PARTTIME,		-0.00514438);
		coeff_time_by_employment.put(Employment.RETIRED,		-0.00295935);
		coeff_time_by_employment.put(Employment.STUDENT,		-0.00605731);
		coeff_time_by_employment.put(Employment.STUDENT_PRIMARY,	-0.00605731);
		coeff_time_by_employment.put(Employment.STUDENT_SECONDARY,-0.00605731);
		coeff_time_by_employment.put(Employment.STUDENT_TERTIARY,	-0.00605731);

		coeff_time_by_age.put(AgeGroup.AGE_36_50,  	0.0);	// reference level
		coeff_time_by_age.put(AgeGroup.AGE_0_9, 		 	0.00156834);
		coeff_time_by_age.put(AgeGroup.AGE_10_17,	  0.00385940);	
		coeff_time_by_age.put(AgeGroup.AGE_18_25,	  0.00700259);	
		coeff_time_by_age.put(AgeGroup.AGE_26_35,	  0.00119587);	
		coeff_time_by_age.put(AgeGroup.AGE_51_60,	  0.00249476);	
		coeff_time_by_age.put(AgeGroup.AGE_61_70,  	0.00108137);	
		coeff_time_by_age.put(AgeGroup.AGE_70_ABOVE,-0.00214532);	

		coeff_opportunity_by_purpose.put( 2,0.12944530);
		coeff_opportunity_by_purpose.put( 6,0.42707997);
		coeff_opportunity_by_purpose.put(11,0.62059552);
		coeff_opportunity_by_purpose.put(12,0.41935184);
		coeff_opportunity_by_purpose.put(41,0.33015609);
		coeff_opportunity_by_purpose.put(42,0.40122776);
		coeff_opportunity_by_purpose.put(51,0.43941747);
		coeff_opportunity_by_purpose.put(52,0.34935856);
		coeff_opportunity_by_purpose.put(53,0.55577165);
		coeff_opportunity_by_purpose.put(77,0.26714031);
		coeff_opportunity_by_purpose.put( 9,0.67557780);
	}





	public DestinationChoiceModelTime(
		ImpedanceIfc impedance
	) {

		this.impedance = impedance;
	}


	public double calculateUtility(
		Person person,
		ActivityIfc nextActivity,
		Zone origin,
		Zone destination,
		ActivityType activityType,
		Set<Mode> availableModes 
	) {

		ZoneId originId = origin.getId();
		ZoneId destinationId = destination.getId();

		double OPPORTUNITY_OFFSET = 1.0;

		Time date = person.activitySchedule().prevActivity(nextActivity).calculatePlannedEndDate();

		boolean commuterTicket = person.hasCommuterTicket();
		boolean licence = person.hasDrivingLicense();
		boolean female = person.gender() == Gender.FEMALE;

		int activity = activityType.getTypeAsInt();
		AgeGroup age = age(person);
		Employment employment = person.employment();


		ZoneId nextFixedDestinationId = person.nextFixedActivityZone(nextActivity).getId();

		Mode mode = fastestMode(date, originId, destinationId, nextFixedDestinationId, availableModes);


		float opportunity = this.impedance.getOpportunities(activityType, destinationId);
		double log_opportunity = Math.log(OPPORTUNITY_OFFSET + opportunity);

		float time_next = this.impedance.getTravelTime(originId, destinationId, mode, date);
		float time_pole = this.impedance.getTravelTime(destinationId, nextFixedDestinationId, mode, date);





		double opportunity_coeff 			= this.coeff_opportunity_by_purpose.get(activity);
		double time_purpose_coeff 		= this.coeff_time_by_purpose.get(activity);
		double time_employment_coeff 	= this.coeff_time_by_employment.get(employment);
		double time_age_coeff 				= this.coeff_time_by_age.get(age);

		double time_coeff 					= this.coeff_time;
		double time_nolicence_coeff = licence ? 0.0 : this.coeff_time_nolicence;
		double time_ticket_coeff 		= commuterTicket ? this.coeff_time_ticket : 0.0;
		double time_female_coeff 		= female ? this.coeff_time_female : 0.0;



		double utility 	= opportunity_coeff * log_opportunity	
										+ (		time_coeff 
												+ time_nolicence_coeff 
												+ time_ticket_coeff 
												+ time_female_coeff
												+ time_purpose_coeff 
												+ time_employment_coeff 
												+ time_age_coeff
											)
			 								* (time_next + time_pole)
										;

		return utility;
	}

	protected Mode fastestMode(
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

	public AgeGroup age(Person person) {
		int age = person.age();
		if (age < 10) { return AgeGroup.AGE_0_9; }
		else if (age < 18) { return AgeGroup.AGE_10_17; }
		else if (age < 26) { return AgeGroup.AGE_18_25; }
		else if (age < 36) { return AgeGroup.AGE_26_35; }
		else if (age < 51) { return AgeGroup.AGE_36_50; }
		else if (age < 61) { return AgeGroup.AGE_51_60; }
		else if (age < 70) { return AgeGroup.AGE_61_70; }
		else { return AgeGroup.AGE_70_ABOVE; }
		
	}


}

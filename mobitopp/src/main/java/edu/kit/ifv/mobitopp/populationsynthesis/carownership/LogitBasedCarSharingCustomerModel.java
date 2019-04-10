package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import java.util.Random;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonForSetup;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.util.ParameterFileParser;

public class LogitBasedCarSharingCustomerModel 
	implements CarSharingCustomerModel {

	private final Random random;

	private final String company;


	protected final Double CONSTANT  										= null;
	protected final Double SEX_FEMALE 									= null;
	protected final Double TICKET  											= null;
	protected final Double EMPLOYMENT_FULLTIME  				= null;
	protected final Double EMPLOYMENT_PARTTIME  				= null;
	protected final Double EMPLOYMENT_JOBLESS  					= null;
	protected final Double EMPLOYMENT_STUDENT_SECONDARY = null;
	protected final Double EMPLOYMENT_STUDENT_TERTIARY  = null;
	protected final Double EMPLOYMENT_EDUCATION  				= null;
	protected final Double EMPLOYMENT_NOT_WORKING  			= null;
	protected final Double EMPLOYMENT_RETIRED		  			= null;
	protected final Double CARS_PER_HH_0					 			= null;
	protected final Double CARS_PER_HH_1								= null;
	protected final Double CARS_PER_HH_2					 			= null;
	protected final Double CARS_PER_HH_3					 			= null;
	protected final Double CS_CARS_PER_KM2				 			= null;
	protected final Double HH_SIZE_1							  		= null;
	protected final Double HH_SIZE_2							  		= null;
	protected final Double HH_SIZE_3							  		= null;
	protected final Double HH_SIZE_4							  		= null;
	protected final Double AGE_18_24						  			= null;
	protected final Double AGE_25_34						  			= null;
	protected final Double AGE_35_49						  			= null;
	protected final Double AGE_50_64						  			= null;
	protected final Double AGE_65_PLUS					  			= null;





	public LogitBasedCarSharingCustomerModel(
		Random random,
		String company,
		String configFile
	) {
		this.random = random;

		this.company = company;

		new ParameterFileParser().parseConfig(configFile, this);
	}


	public boolean estimateCustomership(
  	PersonForSetup person
  ) {

		if (person.age() < 18 || !person.hasDrivingLicense()) {
			return false;
		}

		HouseholdForSetup household = person.household();
		Zone zone = household.homeZone();

		double utility = calculateUtility(person, household, zone);

		double probability = Math.exp(utility) / (1.0 + Math.exp(utility));

		double rand = this.random.nextDouble();

		return rand < probability;
	}

	public Double calculateUtility(
  	PersonForSetup person,
    HouseholdForSetup household,
		Zone zone
	) {

		int number_of_cars 	= household.getTotalNumberOfCars();
		int hh_size 				= household.nominalSize();

		Employment employment = person.employment();

		int age = person.age();


		int sex_female 			=  person.isFemale() ? 1 : 0;
		int ticket 			=  person.hasCommuterTicket() ? 1 : 0;

		int employment_fulltime 						= employment == Employment.FULLTIME ? 1 : 0;
		int employment_parttime 						= employment == Employment.PARTTIME ? 1 : 0;
		int employment_jobless 							= employment == Employment.UNEMPLOYED ? 1 : 0;
		int employment_student_secondary 		= employment == Employment.STUDENT_SECONDARY ? 1 : 0;
		int employment_student_tertiary 		= employment == Employment.STUDENT_TERTIARY ? 1 : 0;
		int employment_education 						= employment == Employment.EDUCATION ? 1 : 0;
		int employment_not_working 					= employment == Employment.HOMEKEEPER ? 1 : 0;
		int employment_retired 							= employment == Employment.RETIRED ? 1 : 0;

		int cars_per_hh_0 = number_of_cars == 0 ? 1 : 0;
		int cars_per_hh_1 = number_of_cars == 1 ? 1 : 0;
		int cars_per_hh_2 = number_of_cars == 2 ? 1 : 0;
		int cars_per_hh_3 = number_of_cars == 3 ? 1 : 0;

		double cs_cars_per_km2 = zone.carSharing().carsharingcarDensity(this.company);

		int hh_size_1 = hh_size == 1 ? 1 : 0;
		int hh_size_2 = hh_size == 2 ? 1 : 0;
		int hh_size_3 = hh_size == 3 ? 1 : 0;
		int hh_size_4 = hh_size == 4 ? 1 : 0;

		int age_18_24		= (age >= 18 && age <= 24) ? 1 : 0;
		int age_25_34		= (age >= 25 && age <= 34) ? 1 : 0;
		int age_35_49		= (age >= 35 && age <= 49) ? 1 : 0;
		int age_50_64		= (age >= 50 && age <= 64) ? 1 : 0;
		int age_65_plus	= (age >= 65) 						 ? 1 : 0;


		double result = CONSTANT  										
									 + sex_female * SEX_FEMALE 									
									 + ticket * TICKET  											
									 + employment_fulltime * EMPLOYMENT_FULLTIME  				
									 + employment_parttime * EMPLOYMENT_PARTTIME  				
									 + employment_jobless * EMPLOYMENT_JOBLESS  					
									 + employment_student_secondary * EMPLOYMENT_STUDENT_SECONDARY 
									 + employment_student_tertiary * EMPLOYMENT_STUDENT_TERTIARY  
									 + employment_education * EMPLOYMENT_EDUCATION  				
									 + employment_not_working * EMPLOYMENT_NOT_WORKING  			
									 + employment_retired * EMPLOYMENT_RETIRED		  			
									 + cars_per_hh_0 * CARS_PER_HH_0					 			
									 + cars_per_hh_1 * CARS_PER_HH_1								
									 + cars_per_hh_2 * CARS_PER_HH_2					 			
									 + cars_per_hh_3 * CARS_PER_HH_3					 			
									 + cs_cars_per_km2 * CS_CARS_PER_KM2				 			
									 + hh_size_1 * HH_SIZE_1							  		
									 + hh_size_2 * HH_SIZE_2							  		
									 + hh_size_3 * HH_SIZE_3							  		
									 + hh_size_4 * HH_SIZE_4							  		
									 + age_18_24 * AGE_18_24						  			
									 + age_25_34 * AGE_25_34						  			
									 + age_35_49 * AGE_35_49						  			
									 + age_50_64 * AGE_50_64						  			
									 + age_65_plus * AGE_65_PLUS
									;

		return result;
	}

}

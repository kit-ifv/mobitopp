package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Random;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.util.ParameterFileParser;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

class CommutationTicketModelStuttgart
	implements CommutationTicketModelIfc {

	private static final Landkreis fallback = Landkreis.S;
	
	private Random randomizer;

	public final Double coeff_const = 0.0;

	public final Double coeff_lkS = 0.0;
	public final Double coeff_lkBB = 0.0;
	public final Double coeff_lkES = 0.0;
	public final Double coeff_lkGP = 0.0;
	public final Double coeff_lkLB = 0.0;
	public final Double coeff_lkWN = 0.0;

	public final Double coeff_emp_full = 0.0; // 1
	public final Double coeff_emp_part = 0.0; // 2
	public final Double coeff_emp_unemployed = 0.0; // 3
	public final Double coeff_emp_education = 0.0; // 5
	public final Double coeff_emp_jobless = 0.0; // 6
	public final Double coeff_emp_retired = 0.0; // 7
	public final Double coeff_emp_other = 0.0; // 9
	public final Double coeff_emp_student_pri_sec = 0.0; // 41
	public final Double coeff_emp_student_ter = 0.0; // 42

	public final Double coeff_apkw_not_ppkw = 0.0;
	public final Double coeff_ppkw          = 0.0;
	public final Double coeff_sex_female = 0.0;
	public final Double coeff_pkwhh_per_person = 0.0;

	enum Landkreis { S, BB, ES, GP, LB, WN }

	public CommutationTicketModelStuttgart(String configFile, long seed) {

		new ParameterFileParser().parseConfig(configFile, this);

		this.randomizer = new Random(seed);	
	}

	public boolean estimateCommutationTicket(
		PersonOfPanelData person,
		HouseholdOfPanelData household,
		Zone zone
	) {
		double utility = calculateUtility(person,household,zone);
		double random = this.randomizer.nextDouble();
		return utility > random;
	}

	protected double calculateUtility(
		PersonOfPanelData person,
		HouseholdOfPanelData household,
		Zone zone
	) {

		Landkreis lk = getLandkreis(zone);

		Employment employment = person.employment();
		Gender gender = person.gender();


		int caravailable_personally = person.hasPersonalCar() ? 1 : 0;
		int caravailable_generally = person.hasAccessToCar() ? 1 : 0;


		int pkwhh = household.numberOfCars();
		int hhgro = household.numberOfReportingPersons();

		assert hhgro > 0 : household;

		int lkBB = lk == Landkreis.BB ? 1 : 0;
		int lkES = lk == Landkreis.ES ? 1 : 0;
		int lkGP = lk == Landkreis.GP ? 1 : 0;
		int lkLB = lk == Landkreis.LB ? 1 : 0;
		int lkWN = lk == Landkreis.WN ? 1 : 0;

		int emp_full 					= employment == Employment.FULLTIME ? 1 : 0;
		int emp_part 					= employment == Employment.PARTTIME ? 1 : 0;
		int emp_unemployed 		= employment == Employment.HOMEKEEPER ? 1 : 0;
		int emp_education 		= employment == Employment.EDUCATION ? 1 : 0;
		int emp_jobless 			= employment == Employment.UNEMPLOYED ? 1 : 0;
		int emp_retired 			= employment == Employment.RETIRED ? 1 : 0;
		int emp_other 				= employment == Employment.NONE ? 1 : 0;
		int emp_student_pri 	= employment == Employment.STUDENT_PRIMARY ? 1 : 0;
		int emp_student_sec 	= employment == Employment.STUDENT_SECONDARY ? 1 : 0;
		int emp_student_ter 	= employment == Employment.STUDENT_TERTIARY ? 1 : 0;


		int ppkw = caravailable_personally;
		int apkw_not_ppkw = caravailable_generally==1 && caravailable_personally==0 ? 1 : 0;

		double pkwhh_per_person = (double) pkwhh / (double) hhgro;

		assert !Double.isNaN(pkwhh_per_person) : (pkwhh + ":" + hhgro);
		assert !Double.isInfinite(pkwhh_per_person) : (pkwhh + ":" + hhgro);


		int female = gender == Gender.FEMALE ? 1 : 0;

		double u = 0.0;

		u =	coeff_const 
				+ coeff_lkBB * lkBB
				+ coeff_lkES * lkES
				+ coeff_lkGP * lkGP
				+ coeff_lkLB * lkLB
				+ coeff_lkWN * lkWN

				+ coeff_emp_full * emp_full
				+ coeff_emp_part * emp_part
				+ coeff_emp_unemployed * emp_unemployed
				+ coeff_emp_education * emp_education
				+ coeff_emp_jobless * emp_jobless
				+ coeff_emp_retired * emp_retired
				+ coeff_emp_other * emp_other
				+ coeff_emp_student_pri_sec * emp_student_pri
				+ coeff_emp_student_pri_sec * emp_student_sec
				+ coeff_emp_student_ter * emp_student_ter

				+ coeff_sex_female * female
				+ coeff_ppkw * ppkw
				+ coeff_apkw_not_ppkw * apkw_not_ppkw
				+ coeff_pkwhh_per_person *  pkwhh_per_person
			;


		double logit_u = Math.exp(u) / (1.0+Math.exp(u));

		assert !Double.isNaN(logit_u) : ("\nu: " + u);

		return logit_u;
	}

	static Landkreis getLandkreis(Zone zone) {
		String id = zone.getId().getExternalId();

		verifyZoneId(id);

		if (id.substring(0,1).equals("1")) { return Landkreis.LB; }
		if (id.substring(0,1).equals("2")) { return Landkreis.S; }
		if (id.substring(0,1).equals("3")) { return Landkreis.WN; }
		if (id.substring(0,1).equals("4")) { return Landkreis.BB; }
		if (id.substring(0,1).equals("5")) { return Landkreis.ES; }
		if (id.substring(0,1).equals("6")) { return Landkreis.GP; }

		return fallback;
	}

	private static void verifyZoneId(String id) {
		if (id.length() > 5) {
			throw new IllegalArgumentException("zoneID out of range in 'CommutationTicketModel:getLandkreis'");
		}
	}

}

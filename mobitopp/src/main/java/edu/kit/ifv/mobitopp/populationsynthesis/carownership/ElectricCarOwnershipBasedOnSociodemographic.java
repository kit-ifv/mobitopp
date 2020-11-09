package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import static edu.kit.ifv.mobitopp.simulation.Employment.EDUCATION;
import static edu.kit.ifv.mobitopp.simulation.Employment.FULLTIME;
import static edu.kit.ifv.mobitopp.simulation.Employment.HOMEKEEPER;
import static edu.kit.ifv.mobitopp.simulation.Employment.PARTTIME;
import static edu.kit.ifv.mobitopp.simulation.Employment.RETIRED;
import static edu.kit.ifv.mobitopp.simulation.Employment.STUDENT_SECONDARY;
import static edu.kit.ifv.mobitopp.simulation.Employment.STUDENT_TERTIARY;
import static edu.kit.ifv.mobitopp.simulation.Employment.UNEMPLOYED;
import static edu.kit.ifv.mobitopp.simulation.Gender.MALE;

import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.util.logit.DefaultLogitModel;
import edu.kit.ifv.mobitopp.util.parameter.ParameterFormularParser;

public class ElectricCarOwnershipBasedOnSociodemographic
		implements ProbabilityForElectricCarOwnershipModel {

	final protected ImpedanceIfc impedance;

	final Double CONST_BEV = null;
	final Double WORKDIS_BEV = null;
	final Double AUSBDIS_BEV = null;
	final Double SEX_MALE_BEV = null;
	final Double JOB_FULLTIME_BEV = null;
	final Double JOB_PARTTIME_BEV = null;
	final Double JOBLESS_BEV = null;
	final Double STUDENT_TERTIARY_BEV = null;
	final Double STUDENT_SECONDARY_BEV = null;
	final Double EDUCATION_BEV = null;
	final Double UNEMPLOYED_BEV = null;
	final Double RETIRED_BEV = null;
	final Double AGE_18_TO_25_BEV = null;
	final Double AGE_25_TO_35_BEV = null;
	final Double AGE_35_TO_45_BEV = null;
	final Double AGE_45_TO_55_BEV = null;
	final Double AGE_55_TO_65_BEV = null;
	final Double AGE_65_TO_75_BEV = null;
	final Double AGE_75_TO_85_BEV = null;
	final Double ANZ_PKW_1_BEV = null;
	final Double ANZ_PKW_2_BEV = null;
	final Double ANZ_PKW_3_BEV = null;
	final Double ANZ_PKW_4_BEV = null;
	final Double HHGRO_1_BEV = null;
	final Double HHGRO_2_BEV = null;
	final Double HHGRO_3_BEV = null;
	final Double HHGRO_4_BEV = null;

	final Double CONST_EREV = null;
	final Double WORKDIS_EREV = null;
	final Double AUSBDIS_EREV = null;
	final Double SEX_MALE_EREV = null;
	final Double JOB_FULLTIME_EREV = null;
	final Double JOB_PARTTIME_EREV = null;
	final Double JOBLESS_EREV = null;
	final Double STUDENT_TERTIARY_EREV = null;
	final Double STUDENT_SECONDARY_EREV = null;
	final Double EDUCATION_EREV = null;
	final Double UNEMPLOYED_EREV = null;
	final Double RETIRED_EREV = null;
	final Double AGE_18_TO_25_EREV = null;
	final Double AGE_25_TO_35_EREV = null;
	final Double AGE_35_TO_45_EREV = null;
	final Double AGE_45_TO_55_EREV = null;
	final Double AGE_55_TO_65_EREV = null;
	final Double AGE_65_TO_75_EREV = null;
	final Double AGE_75_TO_85_EREV = null;
	final Double ANZ_PKW_1_EREV = null;
	final Double ANZ_PKW_2_EREV = null;
	final Double ANZ_PKW_3_EREV = null;
	final Double ANZ_PKW_4_EREV = null;
	final Double HHGRO_1_EREV = null;
	final Double HHGRO_2_EREV = null;
	final Double HHGRO_3_EREV = null;
	final Double HHGRO_4_EREV = null;

	public ElectricCarOwnershipBasedOnSociodemographic(ImpedanceIfc impedance, String configFile) {
		this.impedance = impedance;
		new ParameterFormularParser().parseConfig(Convert.asFile(configFile), this);
	}

	@Override
	public double calculateProbabilityForElectricCar(final PersonBuilder person, Car.Segment segment) {
		return probabilityFrom(bevUtility(person) + erevUtility(person));
	}

	private double probabilityFrom(double utility) {
		return Math.exp(utility) / (1.0f + Math.exp(utility));
	}

	@Override
	public TypeProbabilities calculateProbabilities(
			PersonBuilder person, Car.Segment segment) {
		Map<CarType, Double> utilities = new HashMap<>();
		utilities.put(CarType.conventional, 0.0d);
		utilities.put(CarType.bev, bevUtility(person));
		utilities.put(CarType.erev, erevUtility(person));
		DefaultLogitModel<CarType> defaultLogitModel = new DefaultLogitModel<>();
		Map<CarType, Double> logitProbabilities = defaultLogitModel.calculateProbabilities(utilities);
		return new TypeProbabilities(logitProbabilities);
	}

	private double distance(PersonBuilder person) {
		HouseholdForSetup household = person.household();
		ZoneId homeZone = household.homeZone().getId();
		ZoneId poleZone = person.fixedActivityZone().getId();
		return impedance.getDistance(homeZone, poleZone) / 1000.0d;
	}

	private double bevUtility(PersonBuilder person) {
		double distance = distance(person);
		return CONST_BEV
				+ WORKDIS_BEV * distance
				+ AUSBDIS_BEV * distance
				+ SEX_MALE_BEV * sexMale(person)
				+ JOB_FULLTIME_BEV * fulltime(person)
				+ JOB_PARTTIME_BEV * parttime(person)
				+ JOBLESS_BEV * jobless(person)
				+ STUDENT_TERTIARY_BEV * studentTertiary(person)
				+ STUDENT_SECONDARY_BEV * studentSecondary(person)
				+ EDUCATION_BEV * education(person)
				+ UNEMPLOYED_BEV * unemployed(person)
				+ RETIRED_BEV * retired(person)
				+ AGE_18_TO_25_BEV * age18To25(person)
				+ AGE_25_TO_35_BEV * age25To35(person)
				+ AGE_35_TO_45_BEV * age35To45(person)
				+ AGE_45_TO_55_BEV * age45To55(person)
				+ AGE_55_TO_65_BEV * age55To65(person)
				+ AGE_65_TO_75_BEV * age65To75(person)
				+ AGE_75_TO_85_BEV * age75To85(person)
				+ ANZ_PKW_1_BEV * oneCar(person)
				+ ANZ_PKW_2_BEV * twoCars(person)
				+ ANZ_PKW_3_BEV * threeCars(person)
				+ ANZ_PKW_4_BEV * moreThan4Cars(person)
				+ HHGRO_1_BEV * hhgro1(person)
				+ HHGRO_2_BEV * hhgro2(person)
				+ HHGRO_3_BEV * hhgro3(person)
				+ HHGRO_4_BEV * hhgro4(person);
	}

	private double erevUtility(PersonBuilder person) {
		double distance = distance(person);
		return CONST_EREV
				+ WORKDIS_EREV * distance
				+ AUSBDIS_EREV * distance
				+ SEX_MALE_EREV * sexMale(person)
				+ JOB_FULLTIME_EREV * fulltime(person)
				+ JOB_PARTTIME_EREV * parttime(person)
				+ JOBLESS_EREV * jobless(person)
				+ STUDENT_TERTIARY_EREV * studentTertiary(person)
				+ STUDENT_SECONDARY_EREV * studentSecondary(person)
				+ EDUCATION_EREV * education(person)
				+ UNEMPLOYED_EREV * unemployed(person)
				+ RETIRED_EREV * retired(person)
				+ AGE_18_TO_25_EREV * age18To25(person)
				+ AGE_25_TO_35_EREV * age25To35(person)
				+ AGE_35_TO_45_EREV * age35To45(person)
				+ AGE_45_TO_55_EREV * age45To55(person)
				+ AGE_55_TO_65_EREV * age55To65(person)
				+ AGE_65_TO_75_EREV * age65To75(person)
				+ AGE_75_TO_85_EREV * age75To85(person)
				+ ANZ_PKW_1_EREV * oneCar(person)
				+ ANZ_PKW_2_EREV * twoCars(person)
				+ ANZ_PKW_3_EREV * threeCars(person)
				+ ANZ_PKW_4_EREV * moreThan4Cars(person)
				+ HHGRO_1_EREV * hhgro1(person)
				+ HHGRO_2_EREV * hhgro2(person)
				+ HHGRO_3_EREV * hhgro3(person)
				+ HHGRO_4_EREV * hhgro4(person);
	}

	private static int sexMale(PersonBuilder person) {
		return MALE == person.gender() ? 1 : 0;
	}

	private static int fulltime(PersonBuilder person) {
		return FULLTIME == person.employment() ? 1 : 0;
	}

	private static int parttime(PersonBuilder person) {
		return PARTTIME == person.employment() ? 1 : 0;
	}

	private static int jobless(PersonBuilder person) {
		return UNEMPLOYED == person.employment() ? 1 : 0;
	}

	private static int studentTertiary(PersonBuilder person) {
		return STUDENT_TERTIARY == person.employment() ? 1 : 0;
	}

	private static int studentSecondary(PersonBuilder person) {
		return STUDENT_SECONDARY == person.employment() ? 1 : 0;
	}

	private static int education(PersonBuilder person) {
		return EDUCATION == person.employment() ? 1 : 0;
	}

	private static int unemployed(PersonBuilder person) {
		return HOMEKEEPER == person.employment() ? 1 : 0;
	}

	private static int retired(PersonBuilder person) {
		return RETIRED == person.employment() ? 1 : 0;
	}

	private static int age18To25(PersonBuilder person) {
		return 18 <= person.age() && 25 > person.age() ? 1 : 0;
	}

	private static int age25To35(PersonBuilder person) {
		return 25 <= person.age() && 35 > person.age() ? 1 : 0;
	}

	private static int age35To45(PersonBuilder person) {
		return 35 <= person.age() && 45 > person.age() ? 1 : 0;
	}

	private static int age45To55(PersonBuilder person) {
		return 45 <= person.age() && 55 > person.age() ? 1 : 0;
	}

	private static int age55To65(PersonBuilder person) {
		return 55 <= person.age() && 65 > person.age() ? 1 : 0;
	}

	private static int age65To75(PersonBuilder person) {
		return 65 <= person.age() && 75 > person.age() ? 1 : 0;
	}

	private static int age75To85(PersonBuilder person) {
		return 75 <= person.age() && 85 > person.age() ? 1 : 0;
	}

	private static int oneCar(PersonBuilder person) {
		return 1 == person.household().getTotalNumberOfCars() ? 1 : 0;
	}

	private static int twoCars(PersonBuilder person) {
		return 2 == person.household().getTotalNumberOfCars() ? 1 : 0;
	}

	private static int threeCars(PersonBuilder person) {
		return 3 == person.household().getTotalNumberOfCars() ? 1 : 0;
	}

	private static int moreThan4Cars(PersonBuilder person) {
		return 4 <= person.household().getTotalNumberOfCars() ? 1 : 0;
	}

	private static int hhgro1(PersonBuilder person) {
		return person.household().getSize() == 1 ? 1 : 0;
	}

	private static int hhgro2(PersonBuilder person) {
		return person.household().getSize() == 2 ? 1 : 0;
	}

	private static int hhgro3(PersonBuilder person) {
		return person.household().getSize() == 3 ? 1 : 0;
	}

	private static int hhgro4(PersonBuilder person) {
		return person.household().getSize() == 4 ? 1 : 0;
	}

}

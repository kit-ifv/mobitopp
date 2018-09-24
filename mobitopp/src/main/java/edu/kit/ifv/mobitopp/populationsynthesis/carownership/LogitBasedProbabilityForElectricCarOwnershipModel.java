package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Car.Segment;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.ZoneAreaType;
import edu.kit.ifv.mobitopp.util.ParameterFileParser;

public class LogitBasedProbabilityForElectricCarOwnershipModel
	implements ProbabilityForElectricCarOwnershipModel
{

	final protected ImpedanceIfc impedance;


	final Double COEFF_INTERCEPT          =        null;
	/** Reference is COEFF_SEX_FEMALE = 0.0 **/
	final Double COEFF_SEX_MALE           =        null;
	final Double COEFF_DIST_COMM_NOCOMMUTE=        null;
	final Double COEFF_DIST_COMM_0_10     =        null;
	final Double COEFF_DIST_COMM_10_20    =        null;
	final Double COEFF_DIST_COMM_20_30    =        null;
	final Double COEFF_DIST_COMM_30_40    =        null;
	final Double COEFF_DIST_COMM_40_50    =        null;
	/** Reference is dist_comm_50plus = 0.0 **/
	final Double COEFF_REG_RURAL          =       null;
	final Double COEFF_REG_SUBURBAN       =       null;
	/** Reference is COEFF_REG_URBAN = 0.0 **/
	final Double COEFF_PKWHH_1   			   	=       null;
	/** Reference is COEFF_PKWHH_2PLUS = 0.0 **/
	final Double COEFF_BERUF_FULLTIME     =       null;
	final Double COEFF_BERUF_PARTTIME     =       null;
	final Double COEFF_BERUF_JOBLESS      =       null;
	final Double COEFF_BERUF_STUDENT      =       null;
	final Double COEFF_BERUF_EDUCATION    =       null;
	final Double COEFF_BERUF_NOT_WORKING  =       null;
	final Double COEFF_BERUF_RETIRED      =       null;
	/** Reference is COEFF_BERUF_OTHER = 0.0 **/
	final Double COEFF_HHGRO_1            =       null;
	final Double COEFF_HHGRO_2            =       null;
	/** Reference is COEFF_HHGRO_3PLUS = 0.0 **/
	final Double PROBABILITY_BEV          =       null;
	/** Reference is COEFF_SEGMENT_SMALL **/
	final Double COEFF_SEGMENT_MIDSIZE          =       null;
	final Double COEFF_SEGMENT_LARGE          =       null;


	private final BevProbabilities bevProbabilites;



	public LogitBasedProbabilityForElectricCarOwnershipModel(
		ImpedanceIfc impedance,
		BevProbabilities bevProbabilites,
		String configFile
	) {

		this.impedance = impedance;
		this.bevProbabilites = bevProbabilites;

		new ParameterFileParser().parseConfig(configFile, this);
	}


	@Override
	public double calculateProbabilityForElectricCar(final Person person, Car.Segment segment) {

		double utility = calculateUtilityForElectricCar(person, segment);

		double p = (Math.exp(utility) / (1.0f + Math.exp(utility)));

		return p;
	}

	@Override
	public CarTypeSelector calculateProbabilities(Person person, Segment segment) {
		double probabilityElectricCar = calculateProbabilityForElectricCar(person, segment);
		return bevProbabilites.createFor(segment, probabilityElectricCar);
	}

	private double calculateUtilityForElectricCar(final Person person, Car.Segment segment) {
		final Household household = person.household();

		final int homeZone = household .homeZone().getOid();
		final int poleZone = person.fixedActivityZone().getOid();

		float commutingdistance_km = impedance.getDistance(homeZone, poleZone)/1000.0f;

		final Zone zone = household.homeZone();
		final AreaType areaType = zone.getAreaType();

		int sex_male = person.gender() == Gender.MALE ? 1 : 0;

		int dist_comm_NOCOMMUTE = homeZone == poleZone ? 1 : 0;
		int dist_comm_0_10 		= commutingdistance_km < 10 ? 1 : 0;
		int dist_comm_10_20 	= commutingdistance_km > 10 && commutingdistance_km < 20 ? 1 : 0;
		int dist_comm_20_30 	= commutingdistance_km > 20 && commutingdistance_km < 30 ? 1 : 0;
		int dist_comm_30_40 	= commutingdistance_km > 30 && commutingdistance_km < 40 ? 1 : 0;
		int dist_comm_40_50 	= commutingdistance_km > 40 && commutingdistance_km < 50 ? 1 : 0;


		int reg_rural 		= areaType == ZoneAreaType.RURAL || areaType == ZoneAreaType.PROVINCIAL ? 1 : 0;
		int reg_suburban 	= areaType == ZoneAreaType.CITYOUTSKIRT ? 1 : 0;


		int pkwhh_1   			  = household.getTotalNumberOfCars() == 1 ? 1 : 0;

		int employment_EDUCATION    = person.employment() == Employment.EDUCATION ? 1 : 0;
		int employment_JOBLESS      = person.employment() == Employment.UNEMPLOYED ? 1 : 0;
		int employment_NOT_WORKING  = person.employment() == Employment.HOMEKEEPER ? 1 : 0;
		int employment_PARTTIME     = person.employment() == Employment.PARTTIME ? 1 : 0;
		int employment_FULLTIME     = person.employment() == Employment.FULLTIME ? 1 : 0;
		int employment_RETIRED      = person.employment() == Employment.RETIRED ? 1 : 0;
		int employment_STUDENT      = person.employment() == Employment.STUDENT_PRIMARY
                           || person.employment() == Employment.STUDENT_SECONDARY
                           || person.employment() == Employment.STUDENT_TERTIARY
                           || person.employment() == Employment.STUDENT ? 1 : 0;

		int hhgro_1 		= household.getSize() == 1 ? 1 : 0;
		int hhgro_2 		= household.getSize() == 2 ? 1 : 0;

		int segment_midsize = segment == Car.Segment.MIDSIZE ? 1 : 0;
		int segment_large = segment == Car.Segment.LARGE ? 1 : 0;



		double utility =	COEFF_INTERCEPT
										+ COEFF_SEX_MALE * sex_male
										+ COEFF_DIST_COMM_NOCOMMUTE * dist_comm_NOCOMMUTE
										+ COEFF_DIST_COMM_0_10 * dist_comm_0_10
										+ COEFF_DIST_COMM_10_20 * dist_comm_10_20
										+ COEFF_DIST_COMM_20_30 * dist_comm_20_30
										+ COEFF_DIST_COMM_30_40 * dist_comm_30_40
										+ COEFF_DIST_COMM_40_50 * dist_comm_40_50
										+ COEFF_REG_RURAL * reg_rural
										+ COEFF_REG_SUBURBAN * reg_suburban
										+ COEFF_PKWHH_1 * pkwhh_1
										+ COEFF_BERUF_FULLTIME * employment_FULLTIME
										+ COEFF_BERUF_PARTTIME * employment_PARTTIME
										+ COEFF_BERUF_JOBLESS * employment_JOBLESS
										+ COEFF_BERUF_STUDENT * employment_STUDENT
										+ COEFF_BERUF_EDUCATION * employment_EDUCATION
										+ COEFF_BERUF_NOT_WORKING * employment_NOT_WORKING
										+ COEFF_BERUF_RETIRED * employment_RETIRED
										+ COEFF_HHGRO_1 * hhgro_1
										+ COEFF_HHGRO_2 * hhgro_2
										+ COEFF_SEGMENT_MIDSIZE * segment_midsize
										+ COEFF_SEGMENT_LARGE * segment_large
										;


		return utility;
	}

}

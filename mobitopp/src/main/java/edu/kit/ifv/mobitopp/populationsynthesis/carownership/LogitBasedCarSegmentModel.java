package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.util.ParameterFileParser;


public class LogitBasedCarSegmentModel 
	implements CarSegmentModel 
{

	// coefficients are set using reflection
	protected final Double MIDSIZE_CONSTANT     				 = null;
	protected final Double MIDSIZE_DIST_COMM_0_TO_9   	 = null;
	protected final Double MIDSIZE_DIST_COMM_10_TO_19    = null;
	protected final Double MIDSIZE_DIST_COMM_20_TO_29    = null;
	protected final Double MIDSIZE_DIST_COMM_30_TO_39    = null;
	protected final Double MIDSIZE_DIST_COMM_40_TO_49    = null;
	protected final Double MIDSIZE_DIST_COMM_50_PLUS   	 = null;
	protected final Double MIDSIZE_HHGRO_1 							 = null;
	protected final Double MIDSIZE_HHGRO_2 							 = null;
	protected final Double MIDSIZE_HHGRO_3_PLUS 				 = null;
	protected final Double MIDSIZE_HHINCOME_0_TO_500     = null;
	protected final Double MIDSIZE_HHINCOME_500_TO_1000  = null;
	protected final Double MIDSIZE_HHINCOME_1000_TO_1500 = null;
	protected final Double MIDSIZE_HHINCOME_1500_TO_2000 = null;
	protected final Double MIDSIZE_HHINCOME_2000_TO_2500 = null;
	protected final Double MIDSIZE_HHINCOME_2500_TO_3000 = null;
	protected final Double MIDSIZE_HHINCOME_3000_TO_3500 = null;
	protected final Double MIDSIZE_HHINCOME_3500_PLUS    = null;
	protected final Double MIDSIZE_HHINCOME_NA      		 = null;
	protected final Double MIDSIZE_PKWHH_2_PLUS 				 = null;
	protected final Double MIDSIZE_FEMALE         			 = null;

	protected final Double LARGE_CONSTANT       				= null;
	protected final Double LARGE_DIST_COMM_0_TO_9    		= null;
	protected final Double LARGE_DIST_COMM_10_TO_19  		= null;
	protected final Double LARGE_DIST_COMM_20_TO_29  		= null;
	protected final Double LARGE_DIST_COMM_30_TO_39  		= null;
	protected final Double LARGE_DIST_COMM_40_TO_49  		= null;
	protected final Double LARGE_DIST_COMM_50_PLUS   		= null;
	protected final Double LARGE_HHGRO_1             		= null;
	protected final Double LARGE_HHGRO_2         				= null;
	protected final Double LARGE_HHGRO_3_PLUS    				= null;
	protected final Double LARGE_HHINCOME_0_TO_500      = null;
	protected final Double LARGE_HHINCOME_500_TO_1000   = null;
	protected final Double LARGE_HHINCOME_1000_TO_1500  = null;
	protected final Double LARGE_HHINCOME_1500_TO_2000  = null;
	protected final Double LARGE_HHINCOME_2000_TO_2500  = null;
	protected final Double LARGE_HHINCOME_2500_TO_3000  = null;
	protected final Double LARGE_HHINCOME_3000_TO_3500  = null;
	protected final Double LARGE_HHINCOME_3500_PLUS     = null;
	protected final Double LARGE_HHINCOME_NA        		= null;
	protected final Double LARGE_PKWHH_2_PLUS   				= null;
	protected final Double LARGE_FEMALE           			= null;


	protected final ImpedanceIfc impedance;
	protected final Random randomizer;

	public LogitBasedCarSegmentModel(
		ImpedanceIfc impedance,
		long seed,
		String configFile
	) {

		this.impedance 	= impedance;
		this.randomizer = new Random(seed);	

		new ParameterFileParser().parseConfig(configFile, this);
	}

	private Map<Car.Segment, Double> calculateUtilities(Person person) {

		Household hh = person.household();


		int hhincome = person.getIncome() * hh.getSize();

		boolean is_commuting = person.hasFixedActivityZone();

		double commuting_dist = this.impedance.getDistance(	person.homeZone().getOid(), 
																														person.fixedActivityZone().getOid()) / 1000.0;

		boolean isFemale = person.isFemale();
		int number_of_cars =  hh.getTotalNumberOfCars();
		int hh_size =  hh.getSize();

		return calculateUtilities(
							hh_size,
							hhincome,
							number_of_cars,
							is_commuting,
							commuting_dist,
							isFemale
					);
	}

	private Map<Car.Segment, Double> calculateUtilities(
		int hh_size,
		int hhincome,
		int number_of_cars,
		boolean is_commuting,
		double commuting_dist,
		boolean isFemale
	) {


		double dist_comm_0_to_9 	= (is_commuting && commuting_dist < 10.0) ? 1.0 : 0.0;
		double dist_comm_10_to_19 = (is_commuting && commuting_dist >= 10.0 && commuting_dist < 20.0) ? 1.0 : 0.0;
		double dist_comm_20_to_29 = (is_commuting && commuting_dist >= 20.0 && commuting_dist < 30.0) ? 1.0 : 0.0;
		double dist_comm_30_to_39 = (is_commuting && commuting_dist >= 30.0 && commuting_dist < 40.0) ? 1.0 : 0.0;
		double dist_comm_40_to_49 = (is_commuting && commuting_dist >= 40.0 && commuting_dist < 50.0) ? 1.0 : 0.0;
		double dist_comm_50_plus 	= (is_commuting && commuting_dist >= 50.0) ? 1.0 : 0.0;

		double hhgro_1 = hh_size 			== 1 ? 1.0 : 0.0;
		double hhgro_2 = hh_size 			== 2 ? 1.0 : 0.0;
		double hhgro_3_plus = hh_size 	>= 3 ? 1.0 : 0.0;

		double hhincome_0_to_500 		 = (hhincome >     0 && hhincome <  500) ? 1.0 : 0.0;
		double hhincome_500_to_1000  = (hhincome >=  500 && hhincome < 1000) ? 1.0 : 0.0;
		double hhincome_1000_to_1500 = (hhincome >= 1000 && hhincome < 1500) ? 1.0 : 0.0;
		double hhincome_1500_to_2000 = (hhincome >= 1500 && hhincome < 2000) ? 1.0 : 0.0;
		double hhincome_2000_to_2500 = (hhincome >= 2000 && hhincome < 2500) ? 1.0 : 0.0;
		double hhincome_2500_to_3000 = (hhincome >= 2500 && hhincome < 3000) ? 1.0 : 0.0;
		double hhincome_3000_to_3500 = (hhincome >= 3000 && hhincome < 3500) ? 1.0 : 0.0;
		double hhincome_3500_plus 	 =  hhincome >= 3500 ? 1.0 : 0.0;
		double hhincome_na 					 =  hhincome <= 0 ? 1.0 : 0.0;
		double pkwhh_2_plus 	 =  number_of_cars >= 2 ? 1.0 : 0.0;
		double female 				 =  isFemale ? 1.0 : 0.0;



		double U_small = 0.0;

		double U_midsize = 		MIDSIZE_CONSTANT
												+ MIDSIZE_DIST_COMM_0_TO_9 	 *  dist_comm_0_to_9
												+ MIDSIZE_DIST_COMM_10_TO_19 *  dist_comm_10_to_19
												+ MIDSIZE_DIST_COMM_20_TO_29 *  dist_comm_20_to_29
												+ MIDSIZE_DIST_COMM_30_TO_39 *  dist_comm_30_to_39
												+ MIDSIZE_DIST_COMM_40_TO_49 *  dist_comm_40_to_49
												+ MIDSIZE_DIST_COMM_50_PLUS  *  dist_comm_50_plus
												+ MIDSIZE_HHGRO_1 * hhgro_1
												+ MIDSIZE_HHGRO_2 * hhgro_2
												+ MIDSIZE_HHGRO_3_PLUS * hhgro_3_plus
												+ MIDSIZE_HHINCOME_0_TO_500 * hhincome_0_to_500
												+ MIDSIZE_HHINCOME_500_TO_1000 	* hhincome_500_to_1000
												+ MIDSIZE_HHINCOME_1000_TO_1500 * hhincome_1000_to_1500
												+ MIDSIZE_HHINCOME_1500_TO_2000 * hhincome_1500_to_2000
												+ MIDSIZE_HHINCOME_2000_TO_2500 * hhincome_2000_to_2500
												+ MIDSIZE_HHINCOME_2500_TO_3000 * hhincome_2500_to_3000
												+ MIDSIZE_HHINCOME_3000_TO_3500 * hhincome_3000_to_3500
												+ MIDSIZE_HHINCOME_3500_PLUS 		* hhincome_3500_plus
												+ MIDSIZE_HHINCOME_NA 					* hhincome_na
												+ MIDSIZE_PKWHH_2_PLUS * pkwhh_2_plus
												+ MIDSIZE_FEMALE * female
											;

		double U_large = 			LARGE_CONSTANT
												+ LARGE_DIST_COMM_0_TO_9 	 *  dist_comm_0_to_9
												+ LARGE_DIST_COMM_10_TO_19 *  dist_comm_10_to_19
												+ LARGE_DIST_COMM_20_TO_29 *  dist_comm_20_to_29
												+ LARGE_DIST_COMM_30_TO_39 *  dist_comm_30_to_39
												+ LARGE_DIST_COMM_40_TO_49 *  dist_comm_40_to_49
												+ LARGE_DIST_COMM_50_PLUS  *  dist_comm_50_plus
												+ LARGE_HHGRO_1 * hhgro_1
												+ LARGE_HHGRO_2 * hhgro_2
												+ LARGE_HHGRO_3_PLUS * hhgro_3_plus
												+ LARGE_HHINCOME_0_TO_500 * hhincome_0_to_500
												+ LARGE_HHINCOME_500_TO_1000 	* hhincome_500_to_1000
												+ LARGE_HHINCOME_1000_TO_1500 * hhincome_1000_to_1500
												+ LARGE_HHINCOME_1500_TO_2000 * hhincome_1500_to_2000
												+ LARGE_HHINCOME_2000_TO_2500 * hhincome_2000_to_2500
												+ LARGE_HHINCOME_2500_TO_3000 * hhincome_2500_to_3000
												+ LARGE_HHINCOME_3000_TO_3500 * hhincome_3000_to_3500
												+ LARGE_HHINCOME_3500_PLUS 		* hhincome_3500_plus
												+ LARGE_HHINCOME_NA 					* hhincome_na
												+ LARGE_PKWHH_2_PLUS * pkwhh_2_plus
												+ LARGE_FEMALE * female
											;


		Map<Car.Segment, Double> map = new TreeMap<Car.Segment, Double>();

		map.put(Car.Segment.SMALL, U_small);
		map.put(Car.Segment.MIDSIZE, U_midsize);
		map.put(Car.Segment.LARGE, U_large);

		return map;
	}

	private Map<Car.Segment, Double> calculateProbabilities(Person person) {

		Map<Car.Segment, Double> U = calculateUtilities(person);
		Map<Car.Segment, Double> p = new TreeMap<Car.Segment, Double>();

		double expU_small 		= Math.exp(U.get(Car.Segment.SMALL));
		double expU_midsize 	= Math.exp(U.get(Car.Segment.MIDSIZE));
		double expU_large		= Math.exp(U.get(Car.Segment.LARGE));

		double total		= expU_small + expU_midsize + expU_large;

		p.put(Car.Segment.SMALL,   expU_small/total);
		p.put(Car.Segment.MIDSIZE, expU_midsize/total);
		p.put(Car.Segment.LARGE,   expU_large/total);

		return p;
	}

	public Car.Segment determineCarSegment(Person person) {

		Map<Car.Segment, Double> p = calculateProbabilities(person);

		double random = this.randomizer.nextDouble();

		assert random >= 0.0;
		assert random <= 1.0;

		double p_small   = p.get(Car.Segment.SMALL);
		double p_midsize = p.get(Car.Segment.MIDSIZE);
		double p_large   = p.get(Car.Segment.LARGE);

		assert Math.abs(p_small+p_midsize+p_large-1.0) < 0.01;

		assert p_small <= 1.0;
		assert p_midsize <= 1.0;
		assert p_large <= 1.0;

		if (random < p_small) {
			return Car.Segment.SMALL;
		} else if(random < p_small + p_midsize) {
			return Car.Segment.MIDSIZE;
		} else {
			return Car.Segment.LARGE;
		}
	}

}

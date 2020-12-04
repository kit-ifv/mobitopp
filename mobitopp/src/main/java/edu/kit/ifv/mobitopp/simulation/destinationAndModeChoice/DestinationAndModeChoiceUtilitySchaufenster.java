package edu.kit.ifv.mobitopp.simulation.destinationAndModeChoice;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.ImpedanceCarSharing;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.ParameterFileParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DestinationAndModeChoiceUtilitySchaufenster
		implements DestinationAndModeChoiceUtility {

	public final Double C_COST		 	= null; // Cost(to destination + to pole)
	public final Double C_TIME			= null; // Traveltime (to destination + to pole)

	public final Double ELECTRIC_CAR_COST_FACTOR	= null;
	public final Double USE_CAR_COST_AS_PASSENGER_COST_SCALING_FACTOR	= null; 
	public final Double IGNORE_VM3	= null; 

	// Pedestrian
	public final Double VM1				 	= null; // Constant
	public final Double C_ALT1_1		= null; // Age < 18
	public final Double C_ALT2_1		= null; // Age 18-64
	public final Double C_ALT3_1 		= null; // Age 65+ 
	public final Double C_PHHM1			= null; // Number of cars / household size
	public final Double C_PD1			 	= null; // Parking pressure
	public final Double C_ZK1				= null;	// Commutation ticket
	public final Double C_SEX1		 	= null; // weiblich
	public final Double C_KW1		 		= null; // kurzer Weg ( <= 1km)
	public final Double C_IZ1		 		= null; // intrazonaler Weg
	public final Double C_PVM_11	 	= null; // vorheriges Verkehrsmittel: zu Fuß
	public final Double C_PVM_21	 	= null; // vorheriges Verkehrsmittel: Fahrrad
	public final Double C_PVM_31	 	= null; // vorheriges Verkehrsmittel: MIV - Fahrer
	public final Double C_PVM_41	 	= null; // vorheriges Verkehrsmittel: MIV - Mitfahrer
	public final Double C_PVM_51	 	= null; // vorheriges Verkehrsmittel: ÖV

	// Bike
	public final Double VM2					= null;
	public final Double C_ALT1_2 	 	= null;
	public final Double C_ALT2_2 	 	= null;
	public final Double C_ALT3_2 		= null; 
	public final Double C_PHHM2			= null;
	public final Double C_PD2			 	= null;
	public final Double C_ZK2				= null;
	public final Double C_SEX2		 	= null;
	public final Double C_KW2			 	= null;
	public final Double C_IZ2		 		= null;
	public final Double C_PVM_12	 	= null;
	public final Double C_PVM_22	 	= null;
	public final Double C_PVM_32	 	= null;
	public final Double C_PVM_42	 	= null;
	public final Double C_PVM_52	 	= null;

	// Car as driver
	public final Double VM3					= null;

	// Car as passenger
	public final Double VM4					= null;
	public final Double C_ALT1_4	 	= null;
	public final Double C_ALT2_4	 	= null;
	public final Double C_ALT3_4 		= null; 
	public final Double C_PHHM4			= null;
	public final Double C_PD4				= null;
	public final Double C_ZK4				= null;
	public final Double C_SEX4		 	= null;
	public final Double C_KW4			 	= null;
	public final Double C_IZ4		 		= null;
	public final Double C_PVM_14	 	= null; 
	public final Double C_PVM_24	 	= null; 
	public final Double C_PVM_34	 	= null; 
	public final Double C_PVM_44	 	= null; 
	public final Double C_PVM_54	 	= null; 

	// Public transport
	public final Double VM5					= null;
	public final Double C_ALT1_5 	 	= null;
	public final Double C_ALT2_5 	 	= null;
	public final Double C_ALT3_5 		= null; 
	public final Double C_PHHM5			= null;
	public final Double C_PD5				= null;
	public final Double C_ZK5				= null;
	public final Double C_SEX5		 	= null;
	public final Double C_KW5			 	= null;
	public final Double C_IZ5		 		= null;
	public final Double C_PVM_15	 	= null; 
	public final Double C_PVM_25	 	= null; 
	public final Double C_PVM_35	 	= null; 
	public final Double C_PVM_45	 	= null; 
	public final Double C_PVM_55	 	= null; 

	// New modes
	public final Double VM16					= null;
	public final Double C_AL2_16		= null;
	public final Double C_PHHM16	 	= null;
	public final Double C_PD16		 	= null;
	public final Double C_ZK16			= null;
	public final Double C_FEM16		 	= null;
	public final Double C_KW16			 	= null;
	public final Double C_IZ16		 		= null;

	public final Double VM17				= null;
	public final Double C_AL2_17		= null;
	public final Double C_PHHM17	 	= null;
	public final Double C_PD17		 	= null;
	public final Double C_ZK17			= null;
	public final Double C_FEM17			= null;
	public final Double C_KW17			 	= null;
	public final Double C_IZ17		 		= null;

	public final Double VM81				= null;
	public final Double C_AL2_81		= null;
	public final Double C_PHHM81	 	= null;
	public final Double C_PD81		 	= null;
	public final Double C_ZK81			= null;
	public final Double C_FEM81			= null;
	public final Double C_KW81			 	= null;
	public final Double C_IZ81		 		= null;

	public final Double VM82			 	= null;
	public final Double C_AL2_82	 	= null;
	public final Double C_PHHM82		= null;
	public final Double C_PD82			= null;
	public final Double C_ZK82			= null;
	public final Double C_FEM82			= null;
	public final Double C_KW82			 	= null;
	public final Double C_IZ82		 		= null;

	public final Double RP		 	= null;
	public final Double SP116	 	= null;
	public final Double SP117	 	= null;
	public final Double SP181	 	= null;
	public final Double SP182	 	= null;

	public final Double GEL			 	= null;
	public final Double TIME_GES 	= null;
	public final Double IV_VALUE 	= null;
	public final Double DIS_GES	 	= null; // Distance (to destination + to pole)

	public final Double NUM_CHARGING		 	= null;
	public final Double MU		 	= null;

	private boolean use_car_cost_as_passenger_cost = false;
	private boolean ignore_miv_constant = false;

	protected final Set<Mode> modes = new LinkedHashSet<>(Arrays.asList(
																			new Mode[] {
																				StandardMode.PEDESTRIAN, StandardMode.BIKE, StandardMode.CAR, StandardMode.PASSENGER, StandardMode.PUBLICTRANSPORT,
																				StandardMode.PEDELEC, StandardMode.BIKESHARING,
																				StandardMode.CARSHARING_STATION, StandardMode.CARSHARING_FREE
																			}
																		));

	protected final ImpedanceIfc impedance;

	public DestinationAndModeChoiceUtilitySchaufenster(
		ImpedanceIfc impedance,
		String configFile
	) {

		this.impedance 	= impedance instanceof ImpedanceCarSharing ? 
												(ImpedanceCarSharing) impedance : new ImpedanceCarSharing(impedance);

		new ParameterFileParser().parseConfig(configFile, this);

		assert C_COST		 	!= null;
		assert C_TIME			!= null;
		assert ELECTRIC_CAR_COST_FACTOR	!= null : ("ELECTRIC_CAR_COST_FACTOR not set");
		assert USE_CAR_COST_AS_PASSENGER_COST_SCALING_FACTOR	!= null : 
							("USE_CAR_COST_AS_PASSENGER_COST_SCALING_FACTOR not set in file <" + configFile + ">" );

		assert IGNORE_VM3	!= null : 
							("IGNORE_VM3 not set in file <" + configFile + ">" );

		use_car_cost_as_passenger_cost = USE_CAR_COST_AS_PASSENGER_COST_SCALING_FACTOR > 0.0;
		ignore_miv_constant = IGNORE_VM3 > 0.0;

		if (USE_CAR_COST_AS_PASSENGER_COST_SCALING_FACTOR > 0.0) {
			log.info("car costs used as passenger costs (file: <" + configFile + ">");
			log.info("passenger costs scaling factor: " + USE_CAR_COST_AS_PASSENGER_COST_SCALING_FACTOR);
		} else {
			log.info("no passenger costs (file: <" + configFile + ">");
		}
		


		assert VM1				 	!= null;
		assert C_ALT1_1		!= null;
		assert C_ALT2_1		!= null;
		assert C_ALT3_1 		!= null;
		assert C_PHHM1			!= null;
		assert C_PD1			 	!= null;
		assert C_ZK1				!= null;
		assert C_SEX1		 	!= null;
		assert C_KW1		 		!= null;
		assert C_IZ1		 		!= null;
		assert C_PVM_11	 	!= null;
		assert C_PVM_21	 	!= null;
		assert C_PVM_31	 	!= null;
		assert C_PVM_41	 	!= null;
		assert C_PVM_51	 	!= null;

		assert VM2					!= null;
		assert C_ALT1_2 	 	!= null;
		assert C_ALT2_2 	 	!= null;
		assert C_ALT3_2 		!= null;
		assert C_PHHM2			!= null;
		assert C_PD2			 	!= null;
		assert C_ZK2				!= null;
		assert C_SEX2		 	!= null;
		assert C_KW2			 	!= null;
		assert C_IZ2			 	!= null;
		assert C_PVM_12	 	!= null;
		assert C_PVM_22	 	!= null;
		assert C_PVM_32	 	!= null;
		assert C_PVM_42	 	!= null;
		assert C_PVM_52	 	!= null;

		assert VM3				 	!= null;

		assert VM4					!= null;
		assert C_ALT1_4	 	!= null;
		assert C_ALT2_4	 	!= null;
		assert C_ALT3_4 		!= null;
		assert C_PHHM4			!= null;
		assert C_PD4				!= null;
		assert C_ZK4				!= null;
		assert C_SEX4		 	!= null;
		assert C_KW4			 	!= null;
		assert C_IZ4			 	!= null;
		assert C_PVM_14	 	!= null;
		assert C_PVM_24	 	!= null;
		assert C_PVM_34	 	!= null;
		assert C_PVM_44	 	!= null;
		assert C_PVM_54	 	!= null;

		assert VM5					!= null;
		assert C_ALT1_5 	 	!= null;
		assert C_ALT2_5 	 	!= null;
		assert C_ALT3_5 		!= null;
		assert C_PHHM5			!= null;
		assert C_PD5				!= null;
		assert C_ZK5				!= null;
		assert C_SEX5		 	!= null;
		assert C_KW5			 	!= null;
		assert C_IZ5				!= null;
		assert C_PVM_15	 	!= null;
		assert C_PVM_25	 	!= null;
		assert C_PVM_35	 	!= null;
		assert C_PVM_45	 	!= null;
		assert C_PVM_55	 	!= null;


		assert VM16					!= null;
		assert C_AL2_16		!= null;
		assert C_PHHM16	 	!= null;
		assert C_PD16		 	!= null;
		assert C_ZK16			!= null;
		assert C_FEM16		 	!= null;
		assert C_KW16			 	!= null;
		assert C_IZ16				!= null;

		assert VM17				!= null;
		assert C_AL2_17		!= null;
		assert C_PHHM17	 	!= null;
		assert C_PD17		 	!= null;
		assert C_ZK17			!= null;
		assert C_FEM17			!= null;
		assert C_KW17			 	!= null;
		assert C_IZ17				!= null;

		assert VM81				!= null;
		assert C_AL2_81		!= null;
		assert C_PHHM81	 	!= null;
		assert C_PD81		 	!= null;
		assert C_ZK81			!= null;
		assert C_FEM81			!= null;
		assert C_KW81			 	!= null;
		assert C_IZ81				!= null;

		assert VM82			 	!= null;
		assert C_AL2_82	 	!= null;
		assert C_PHHM82		!= null;
		assert C_PD82			!= null;
		assert C_ZK82			!= null;
		assert C_FEM82			!= null;
		assert C_KW82			 	!= null;
		assert C_IZ82				!= null;

		assert RP		 	!= null;
		assert SP116	 	!= null;
		assert SP117	 	!= null;
		assert SP181	 	!= null;
		assert SP182	 	!= null;

		assert GEL			 	!= null;
		assert TIME_GES 	!= null;
		assert IV_VALUE 	!= null;
		assert DIS_GES	 	!= null;

		assert NUM_CHARGING		 	!= null;
		assert MU		 	!= null;

	}

	public Map<Zone,Double> calculateUtilitiesForUpperModel(
		Set<Zone> choiceSet,
		Person person,
		ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		Zone origin,
		Map<Zone,Set<Mode>> availableModes
	) {
	 	Map<Zone,Double> utilities = new LinkedHashMap<Zone,Double>();

		assert person instanceof EmobilityPerson;

		for (Zone possibleDestination : choiceSet) {

			Set<Mode> modes = availableModes.get(possibleDestination);


			if (!modes.isEmpty()) {

				Map<Mode,Double> nestUtilities = calculateUtilitiesForLowerModel(
																											modes,
																											person,
																											previousActivity,
																											nextActivity,
																											origin,
																											possibleDestination,
																											ignore_miv_constant
																										);

				double logsum = calculateLogSum(nestUtilities);

				ZoneId originId = origin.getId();
				ZoneId destinationId = possibleDestination.getId();

				ActivityType activityType = nextActivity.activityType();

				ZoneId nextFixedDestinationId = person.nextFixedActivityZone(nextActivity).getId();

				Time date = nextActivity.startDate();
				Time nextDate = nextActivity.calculatePlannedEndDate();

				float opportunities = this.impedance.getOpportunities(activityType, destinationId);
				float log_opportunities = (float) Math.log(this.impedance.getOpportunities(activityType, destinationId));

				assert !Float.isNaN(log_opportunities);

				float time_h_car 	= this.impedance.getTravelTime(originId, destinationId, StandardMode.CAR, date)/60.0f;
				float time_h_car_pole	= this.impedance.getTravelTime(destinationId, nextFixedDestinationId, StandardMode.CAR, nextDate)/60.0f;

				float distance 				= (float) Math.max(0.0, this.impedance.getDistance(originId, destinationId)/1000.0);
				float distance_pole 	= (float) Math.max(0.0, this.impedance.getDistance(destinationId, nextFixedDestinationId)/1000.0);



				Double utility = 0.0 
												+ this.GEL * log_opportunities 
												+ this.TIME_GES * (time_h_car + time_h_car_pole)
												+ this.DIS_GES * (distance + distance_pole)
												+ this.IV_VALUE * logsum;

				assert !(utility.isInfinite() && utility > 0.0) : (opportunities + " " + log_opportunities);

				utilities.put(possibleDestination, utility);
			} 
		}

		return utilities;
	}

	private double calculateLogSum(Map<Mode,Double> nestUtilities) {

		Double total = 0.0;
		Double max = Double.NEGATIVE_INFINITY;

		for(Double v: nestUtilities.values()) {

			assert !v.isInfinite();

			max = v > max ? v : max;

			total += Math.exp(v);
		}

		assert !max.isInfinite();

		return !total.isInfinite() ? Math.log(total) : max;
	}


	private Map<Mode,Double> calculateUtilitiesForLowerModel(
		Set<Mode> choiceSet,
		Person person,
		ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		Zone source, 
		Zone destination,
		boolean ignoreMIVconstant
	) {

		Set<Mode> modes = new LinkedHashSet<>(choiceSet);
		modes.retainAll(this.modes);


	 	Map<Mode,Double> utilities = calculateUtilities(
																		person,
																		previousActivity,
																		nextActivity,
																		source, 
																		destination,
																		modes,	
																		ignoreMIVconstant
																	); 


		return utilities;
	}

	public Map<Mode,Double> calculateUtilitiesForLowerModel(
		Set<Mode> choiceSet,
		Person person,
		ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		Zone source, 
		Zone destination
	) {
		return calculateUtilitiesForLowerModel(
							choiceSet, person, previousActivity, nextActivity, source, destination, false
					);
	}



	private Map<Mode,Double> calculateUtilities(
		Person person,
		ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		Zone origin, 
		Zone destination,
		Set<Mode> choiceSetForModes, 
		boolean ignoreMIVconstant
	) {

		ZoneId originId = origin.getId();
		ZoneId destinationId = destination.getId();

		Time currentTime = previousActivity.startDate();
		Time date = nextActivity.startDate();
		Time nextDate = nextActivity.calculatePlannedEndDate();
		Household household = person.household();

		ZoneId nextFixedDestinationId = person.nextFixedActivityZone(nextActivity).getId();

		ZoneId home = person.household().homeZone().getId();

		ActivityIfc nextHomeActivity = person.nextHomeActivity();
		Time startOfNextHomeActivity = nextHomeActivity.startDate() ;




		Map<Mode,Double> utilities = new TreeMap<Mode,Double>();

		int from0to18  = person.age() < 18 ? 1 : 0; // c_alt1
		int from18to64 = (person.age() < 65 && person.age() >= 18) ? 1 : 0; // c_alt2
		int above65 = person.age() > 65 ? 1 : 0; // c_alt3

		int female = person.isFemale() ? 1 : 0; // c_sex

		float cars_per_person = household.nominalNumberOfCars() / household.nominalSize();

		int commticket_true			= person.hasCommuterTicket() ? 1 : 0; // c_zk

		float parkingstress_source 	= this.impedance.getParkingStress(originId, date);
		float parkingstress 				= this.impedance.getParkingStress(destinationId, date);

		float distance 						= (float) Math.max(0.0, this.impedance.getDistance(originId, destinationId)/1000.0);
		float distance_pole 			= (float) Math.max(0.0, this.impedance.getDistance(destinationId, nextFixedDestinationId)/1000.0);
		float distance_pole_home	= (float) Math.max(0.0, this.impedance.getDistance(nextFixedDestinationId, home)/1000.0);
		// Distance related parameters in km

		float tourDistanceKm = distance + distance_pole + distance_pole_home;

		boolean isCarElectric = false;

		if (choiceSetForModes.contains(StandardMode.CAR)) {
			assert person.isCarDriver() || person.hasParkedCar() || person.household().getNumberOfAvailableCars() > 0;
			Car availableCar = person.isCarDriver() || person.hasParkedCar() ?  person.whichCar() 
																							: person.household().nextAvailableCar(person, tourDistanceKm);

			isCarElectric = availableCar.isElectric();
		}


		float time_h_till_home = startOfNextHomeActivity.differenceTo(currentTime).toHours();


		float time_h_car 	= this.impedance.getTravelTime(originId, destinationId, StandardMode.CAR, date)/60.0f;

		float time_h_walk = this.impedance.getTravelTime(originId, destinationId, StandardMode.PEDESTRIAN, date)/60.0f;

		float time_h_pt 	= this.impedance.getTravelTime(originId, destinationId, StandardMode.PUBLICTRANSPORT, date)/60.0f;

		float time_h_pass = this.impedance.getTravelTime(originId, destinationId, StandardMode.PASSENGER, date)/60.0f;

		float time_h_bike = this.impedance.getTravelTime(originId, destinationId, StandardMode.BIKE, date)/60.0f;
		float time_h_bike_pole = this.impedance.getTravelTime(destinationId, nextFixedDestinationId, StandardMode.BIKE, nextDate)/60.0f;

		float time_h_pedelec = time_h_bike / 1.2f;

		float time_h_bikesharing = time_h_bike +(5.0f-parkingstress_source/5.0f) + (5.0f-parkingstress/5.0f);

		float time_h_cs_station = this.impedance.getTravelTime(originId, destinationId, StandardMode.CARSHARING_STATION, date)/60.0f;
		float time_h_cs_station_pole = this.impedance.getTravelTime(destinationId, nextFixedDestinationId, StandardMode.CARSHARING_STATION, nextDate)/60.0f;

		float time_h_cs_free = this.impedance.getTravelTime(originId, destinationId, StandardMode.CARSHARING_FREE, date)/60.0f;

		// Time related parameters in h

		float cost_car 	= this.impedance.getTravelCost(originId, destinationId, StandardMode.CAR, date);
		float cost_car_pole  = this.impedance.getTravelCost(destinationId, nextFixedDestinationId, StandardMode.CAR, nextDate);

		float cost_walk = 0.0f;
		float cost_walk_pole = 0.0f;

		float cost_pt 	= commticket_true==1 ? 0.0f
																: this.impedance.getTravelCost(originId, destinationId, StandardMode.PUBLICTRANSPORT, date);
		float cost_pt_pole   = commticket_true==1 ? 0.0f
																: this.impedance.getTravelCost(destinationId, nextFixedDestinationId, StandardMode.PUBLICTRANSPORT, nextDate);

		float cost_pass = use_car_cost_as_passenger_cost ? 
												USE_CAR_COST_AS_PASSENGER_COST_SCALING_FACTOR.floatValue()*cost_car : 0.0f;
		float cost_pass_pole = use_car_cost_as_passenger_cost ?
												USE_CAR_COST_AS_PASSENGER_COST_SCALING_FACTOR.floatValue()*cost_car_pole : 0.0f;

		float cost_bike = 0.0f;
		float cost_bike_pole = 0.0f;

		float cost_pedelec = 0.25f*distance;
		float cost_pedelec_pole = 0.25f*distance_pole;

		float cost_bikesharing = 0.08f*60f*time_h_bike;
		float cost_bikesharing_pole = 0.08f*60f*time_h_bike_pole;


		float cost_cs_station 	= this.impedance.getTravelCost(originId, destinationId, StandardMode.CARSHARING_STATION, date);
		float cost_cs_station_pole  = this.impedance.getTravelCost(destinationId, nextFixedDestinationId, StandardMode.CARSHARING_STATION, nextDate);
		float cost_cs_station_standing = Math.max(0.0f, time_h_till_home - time_h_cs_station - time_h_cs_station_pole)
																			* ImpedanceCarSharing.CARSHARING_COST_STATION_BASED_EUR_PER_HOUR;

		float cost_cs_free 	= this.impedance.getTravelCost(originId, destinationId, StandardMode.CARSHARING_FREE, date);
		float cost_cs_free_pole  = this.impedance.getTravelCost(destinationId, nextFixedDestinationId, StandardMode.CARSHARING_FREE, nextDate);



		int last_mode_car 	= previousActivity.mode() == StandardMode.CAR 
														|| previousActivity.mode() == StandardMode.CARSHARING_STATION 
														|| previousActivity.mode() == StandardMode.CARSHARING_FREE 
																																					? 1 : 0;
		int last_mode_walk 	= previousActivity.mode() == StandardMode.PEDESTRIAN ? 1 : 0;
		int last_mode_pt 		= previousActivity.mode() == StandardMode.PUBLICTRANSPORT ? 1 : 0;
		int last_mode_pass 	= previousActivity.mode() == StandardMode.PASSENGER ? 1 : 0;
		int last_mode_bike 	= previousActivity.mode() == StandardMode.BIKE ? 1 : 0;

		assert person instanceof EmobilityPerson;

		EmobilityPerson ePerson = (EmobilityPerson) person;

		int chargingInfluencesDestinantionChoice = 
			(ePerson.chargingInfluencesDestinantionChoice() == EmobilityPerson.PublicChargingInfluencesDestinationChoice.ALWAYS
			|| ePerson.chargingInfluencesDestinantionChoice() == EmobilityPerson.PublicChargingInfluencesDestinationChoice.ONLY_WHEN_BATTERY_LOW)
				? 1 : 0;

		int freeChargingPoints = destination.charging().numberOfAvailableChargingPoints();

		int kurzweg = distance <= 1.0f ? 1 : 0;
		int intrazonal = originId==destinationId ? 1 : 0;

		float income1000Eur = household.monthlyIncomeEur() / 1000.0f;

		assert income1000Eur > 0.0f;


		double u_walk = 			( VM1 
													+ C_COST * (cost_walk + cost_walk_pole) / income1000Eur 
													+ C_TIME * (time_h_walk + 0.0f) 
													+ C_ALT1_1 * from0to18
													+ C_ALT2_1 * from18to64
													+ C_ALT3_1 * above65
													+ C_PHHM1 * cars_per_person
													+ C_KW1 * kurzweg		
													+ C_IZ1 * intrazonal
													+ C_PD1 * parkingstress
													+ C_ZK1  * commticket_true
													+ C_SEX1 * female
													+ C_PVM_11 * last_mode_walk
													+ C_PVM_21 * last_mode_bike
													+ C_PVM_31 * last_mode_car
													+ C_PVM_41 * last_mode_pass
													+ C_PVM_51 * last_mode_pt
													) * RP
													; 

		double u_bike = 			( VM2 
													+ C_COST * (cost_bike + cost_bike_pole) / income1000Eur
													+ C_TIME * (time_h_bike + 0.0f)
													+ C_ALT1_2 * from0to18
													+ C_ALT2_2 * from18to64
													+ C_ALT3_2 * above65
													+ C_PHHM2 * cars_per_person
													+ C_KW2 * kurzweg
													+ C_IZ2 * intrazonal
													+ C_PD2 * parkingstress
													+ C_ZK2  * commticket_true
													+ C_SEX2 * female
													+ C_PVM_12 * last_mode_walk
													+ C_PVM_22 * last_mode_bike
													+ C_PVM_32 * last_mode_car
													+ C_PVM_42 * last_mode_pass
													+ C_PVM_52 * last_mode_pt
													) * RP
													; 

		double u_car_driver = ( (ignoreMIVconstant ? 0.0 : VM3)
													+ C_COST * (cost_car + cost_car_pole) 
																* (isCarElectric ? ELECTRIC_CAR_COST_FACTOR : 1.0)
																/ income1000Eur
													+ C_TIME * (time_h_car + 0.0f)
													+ NUM_CHARGING*freeChargingPoints*chargingInfluencesDestinantionChoice
													) * RP
													;

		double u_pass = 		( VM4 
													+ C_COST * (cost_pass + cost_pass_pole) / income1000Eur
													+ C_TIME * (time_h_pass + 0.0f)
													+ C_ALT1_4 * from0to18
													+ C_ALT2_4 * from18to64
													+ C_ALT3_4 * above65
													+ C_PHHM4 * cars_per_person
													+ C_KW4 * kurzweg	
													+ C_IZ4 * intrazonal
													+ C_PD4 * parkingstress
													+ C_ZK4  * commticket_true
													+ C_SEX4 * female
													+ C_PVM_14 * last_mode_walk
													+ C_PVM_24 * last_mode_bike
													+ C_PVM_34 * last_mode_car
													+ C_PVM_44 * last_mode_pass
													+ C_PVM_54 * last_mode_pt
												) * RP
													; 

		double u_pt = 			( VM5 
													+ C_COST * (cost_pt + cost_pt_pole) / income1000Eur
													+ C_TIME * (time_h_pt + 0.0f)
													+ C_ALT1_5 * from0to18
													+ C_ALT2_5 * from18to64
													+ C_ALT3_5 * above65
													+ C_PHHM5 * cars_per_person
													+ C_KW5 * kurzweg		
													+ C_IZ5 * intrazonal
													+ C_PD5 * parkingstress
													+ C_ZK5  * commticket_true
													+ C_SEX5 * female
													+ C_PVM_15 * last_mode_walk
													+ C_PVM_25 * last_mode_bike
													+ C_PVM_35 * last_mode_car
													+ C_PVM_45 * last_mode_pass
													+ C_PVM_55 * last_mode_pt
												) * RP
													; 

		double u_pedelec = 		  C_COST * (cost_pedelec + cost_pedelec_pole) / income1000Eur
													+ C_TIME * (time_h_pedelec + 0.0f)
													+ ( VM16
													+ C_AL2_16 * from18to64
													+ C_PHHM16 * cars_per_person
													+ C_PD16 * parkingstress
													+ C_KW16 * kurzweg		
													+ C_IZ16 * intrazonal
													+ C_ZK16  * commticket_true
													+ C_FEM16 * female
												) * SP116
													; 

		double u_bikesharing = 	C_COST * (cost_bikesharing + cost_bikesharing_pole) / income1000Eur
													+ C_TIME * (time_h_bikesharing + 0.0f)
													+ ( VM17
													+ C_AL2_17 * from18to64
													+ C_PHHM17 * cars_per_person
													+ C_PD17 * parkingstress
													+ C_KW17 * kurzweg		
													+ C_IZ17 * intrazonal
													+ C_ZK17  * commticket_true
													+ C_FEM17 * female
												) * SP117
													; 

		double u_cs_station =  	C_COST * (cost_cs_station + cost_cs_station_pole + cost_cs_station_standing) / income1000Eur
													+ C_TIME * (time_h_cs_station + 0.0f)
													+ ( VM81
													+ C_AL2_81 * from18to64
													+ C_PHHM81 * cars_per_person
													+ C_PD81 * parkingstress
													+ C_KW81 * kurzweg			
													+ C_IZ81 * intrazonal
													+ C_ZK81  * commticket_true
													+ C_FEM81 * female
												) * SP181
													; 

		double u_cs_free = 			C_COST * (cost_cs_free + cost_cs_free_pole) / income1000Eur
													+ C_TIME * (time_h_cs_free + 0.0f)
												+ (
														VM82
													+ C_AL2_82 * from18to64
													+ C_PHHM82 * cars_per_person
													+ C_PD82 * parkingstress
													+ C_KW82 * kurzweg			
													+ C_IZ82 * intrazonal
													+ C_ZK82  * commticket_true
													+ C_FEM82 * female
												) * SP182
													; 


		utilities.put(StandardMode.PEDESTRIAN, u_walk * MU );
		utilities.put(StandardMode.BIKE, u_bike * MU ); 
		utilities.put(StandardMode.CAR, u_car_driver * MU ); 
		utilities.put(StandardMode.PASSENGER, u_pass * MU ); 
		utilities.put(StandardMode.PUBLICTRANSPORT, u_pt * MU ); 

		utilities.put(StandardMode.PEDELEC, u_pedelec * MU ); 
		utilities.put(StandardMode.BIKESHARING, u_bikesharing * MU ); 
		utilities.put(StandardMode.CARSHARING_STATION, u_cs_station * MU ); 
		utilities.put(StandardMode.CARSHARING_FREE, u_cs_free * MU ); 

		utilities.keySet().retainAll(choiceSetForModes);


		return utilities;
	}



}

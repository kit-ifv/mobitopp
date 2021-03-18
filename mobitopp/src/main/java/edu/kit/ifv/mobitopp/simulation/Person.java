package edu.kit.ifv.mobitopp.simulation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityPeriodFixer;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityScheduleWithState;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.bikesharing.Bike;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.time.Time;

public interface Person extends BasePerson {

	boolean isCarDriver();
	boolean isCarPassenger();

	void useCar(Car car, Time time);
	Car whichCar();

	Car releaseCar(Time time);
	Car parkCar(Zone zone, Location location, Time time);
	boolean hasParkedCar();
	void takeCarFromParking();
	
	boolean isCycling();
	
	/**
	 * Uses the given {@link Bike} until it is released.
	 * @param bike to use
	 * @param time at which {@link Bike} usage starts
	 * @see Person#parkBike(Zone, Location, Time)
	 * @see Person#releaseBike(Time)
	 */
	void useBike(Bike bike, Time time);
	Bike whichBike();
	
	/**
	 * Releases the {@link Bike} from usage. The {@link Person} must use a {@link Bike} before
	 * releasing.
	 * 
	 * @param time
	 *          at which the {@link Bike} is released
	 * @return the released {@link Bike}
	 * @see Person#useBike(Bike, Time)
	 */
	Bike releaseBike(Time time);

	/**
	 * Parkes a bike in a parking lot of the given {@link Zone} and {@link Location} at the given
	 * {@link Time}. The {@link Person} must use a {@link Bike} before parking.
	 * 
	 * @param zone
	 *          to park the {@link Bike}
	 * @param location
	 *          to park the {@link Bike}
	 * @param time
	 *          at which the {@link Bike} is parked
	 * @return the parked {@link Bike}
	 * @see Person#useBike(Bike, Time)
	 */
	Bike parkBike(Zone zone, Location location, Time time);
	boolean hasParkedBike();

	/**
	 * The {@link Person} uses a parked bike on the next trip. The {@link Person} must have a parked
	 * bike.
	 * 
	 * @see Person#parkBike(Zone, Location, Time)
	 */
	void takeBikeFromParking();
	
	boolean isMobilityProviderCustomer(String company);
	Map<String, Boolean> mobilityProviderCustomership();

	Household household();

	boolean hasPersonalCar();

	int getOid();
	
	ActivityScheduleWithState activitySchedule();

	Zone nextFixedActivityZone(ActivityIfc activity); 

 	void useCarAsPassenger(Car car);
	void leaveCar();

	void assignPersonalCar(PrivateCar personalCar);
	boolean hasPersonalCarAssigned();
	PrivateCar personalCar();

	Optional<TourBasedActivityPattern> tourBasedActivityPattern();

	ActivityIfc currentActivity();
	ActivityIfc nextActivity();
	ActivityIfc nextHomeActivity();
	Trip currentTrip();
	void currentTrip(Trip trip);

	void initSchedule(TourFactory tourFactory, ActivityPeriodFixer fixer, ActivityStartAndDurationRandomizer activityDurationRandomizer, List<Time> days);

	String forLogging(ImpedanceIfc impedance);
	void startActivity(Time currentDate, ActivityIfc activity, Trip precedingTrip,
			ReschedulingStrategy rescheduling);

	PersonAttributes attributes();
}

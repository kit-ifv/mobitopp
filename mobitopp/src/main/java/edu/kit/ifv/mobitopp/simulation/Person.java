package edu.kit.ifv.mobitopp.simulation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityScheduleWithState;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.time.Time;

public interface Person extends BasePerson {

	boolean isCarDriver();
	boolean isCarPassenger();

	void useCar(Car car, Time time);
	Car whichCar();

	Car releaseCar(Time time);
	Car parkCar(Zone zone,Location location,Time time);
	boolean hasParkedCar();
	void takeCarFromParking();
	
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

	void initSchedule(TourFactory tourFactory, ActivityStartAndDurationRandomizer activityDurationRandomizer, List<Time> days);

	String forLogging(ImpedanceIfc impedance);
	void startActivity(Time currentDate, ActivityIfc activity, Trip precedingTrip,
			ReschedulingStrategy rescheduling);

	PersonAttributes attributes();
}

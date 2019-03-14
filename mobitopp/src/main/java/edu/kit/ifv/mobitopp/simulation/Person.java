package edu.kit.ifv.mobitopp.simulation;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityScheduleWithState;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.time.Time;

public interface Person {

	boolean isCarDriver();
	boolean isCarPassenger();

	void useCar(Car car, Time time);
	Car whichCar();

	Car releaseCar(Time time);
	Car parkCar(Zone zone,Location location,Time time);
	boolean hasParkedCar();
	void takeCarFromParking();

	Household household();

	boolean hasPersonalCar();
	boolean hasAccessToCar();
	boolean hasBike();

	boolean hasCommuterTicket();
	boolean hasDrivingLicense();

	int getOid();
	PersonId getId();

	ActivityScheduleWithState activitySchedule();

	Gender gender();
	Employment employment();
	int age();
	Graduation graduation();

	int getIncome();

	Zone homeZone();
	boolean hasFixedZoneFor(ActivityType activityType);
 	Zone fixedZoneFor(ActivityType activityType);
	boolean hasFixedActivityZone();
	Zone fixedActivityZone();
	Zone nextFixedActivityZone(ActivityIfc activity); 

 	Location fixedDestinationFor(ActivityType activityType);

	void useCarAsPassenger(Car car);
	void leaveCar();

	void assignPersonalCar(PrivateCar personalCar);
	boolean hasPersonalCarAssigned();
	PrivateCar personalCar();

	PatternActivityWeek getPatternActivityWeek();
	TourBasedActivityPattern tourBasedActivityPattern();

	ActivityIfc currentActivity();
	ActivityIfc nextActivity();
	ActivityIfc nextHomeActivity();
	Trip currentTrip();
	void currentTrip(Trip trip);

	void initSchedule(TourFactory tourFactory, ActivityStartAndDurationRandomizer activityDurationRandomizer, List<Time> days);

	boolean isFemale();
	boolean isMale();

	String forLogging(ImpedanceIfc impedance);
	Stream<FixedDestination> getFixedDestinations();
	void startActivity(Time currentDate, ActivityIfc activity, Trip precedingTrip,
			ReschedulingStrategy rescheduling);
	
	PersonAttributes attributes();
	
	ModeChoicePreferences modeChoicePrefsSurvey();
	ModeChoicePreferences modeChoicePreferences();

}

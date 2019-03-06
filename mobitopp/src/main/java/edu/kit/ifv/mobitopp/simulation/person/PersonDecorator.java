package edu.kit.ifv.mobitopp.simulation.person;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.PersonAttributes;
import edu.kit.ifv.mobitopp.simulation.ReschedulingStrategy;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityScheduleWithState;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.time.Time;

public class PersonDecorator
	implements Person
	, Serializable
{

	private static final long serialVersionUID = 1L;

	private final Person person;

	public PersonDecorator(Person person) {
		this.person=person;
	}

	protected Person person() {
		return this.person;
	}

	public boolean isCarDriver() {
		return person().isCarDriver();
	}

	public boolean isCarPassenger() {
		return person().isCarPassenger();
	}

	public void useCar(Car car, Time time) {
		person().useCar(car, time);
	}

	public Car whichCar() {
		return person().whichCar();
	}

	public Car releaseCar(Time time) {
		return person().releaseCar(time);
	}

	public Household household() {
		return person().household();
	}

	public boolean hasPersonalCar() {
		return person().hasPersonalCar();
	}

	public boolean hasAccessToCar() {
		return person().hasAccessToCar();
	}

	public boolean hasBike() {
		return person().hasBike();
	}

	public boolean hasCommuterTicket() {
		return person().hasCommuterTicket();
	}

	public boolean hasDrivingLicense() {
		return person().hasDrivingLicense();
	}

	public int getIncome() {
		return person().getIncome();
	}

	public int getOid() {
		return person().getOid();
	}

	public PersonId getId() {
		return person().getId();
	}

	public ActivityScheduleWithState activitySchedule() {
		return person().activitySchedule();
	}

	public Gender gender() {
		return person().gender();
	}

	public Employment employment() {
		return person().employment();
	}

	public int age() {
		return person().age();
	}

	public Zone homeZone() {
		return person().homeZone();
	}

 	public boolean hasFixedZoneFor(ActivityType activityType) {
		return person().hasFixedZoneFor(activityType);
	}

 	public Zone fixedZoneFor(ActivityType activityType) {
		return person().fixedZoneFor(activityType);
	}

	public Zone fixedActivityZone() {
		return person().fixedActivityZone();
	}

	public boolean hasFixedActivityZone() {
		return person().hasFixedActivityZone();
	}

	public Zone nextFixedActivityZone(ActivityIfc activity) {
		return person().nextFixedActivityZone(activity);
	}

	public Location fixedDestinationFor(ActivityType activityType) {
		return person().fixedDestinationFor(activityType);
	}
	
	@Override
	public Stream<FixedDestination> getFixedDestinations() {
		return person().getFixedDestinations();
	}

	public void useCarAsPassenger(Car car) {
		person().useCarAsPassenger(car);
	}

	public void leaveCar() {
		person().leaveCar();
	}

	public PrivateCar personalCar() {
		return person().personalCar();
	}

	public void assignPersonalCar(PrivateCar personalCar) {
		person().assignPersonalCar(personalCar);
	}

	public boolean hasPersonalCarAssigned() {
		return person().hasPersonalCarAssigned();
	}

	public PatternActivityWeek getPatternActivityWeek() {
		return person().getPatternActivityWeek();
	}
	
	public ActivityIfc currentActivity() {
		return person().currentActivity();
	}

	public ActivityIfc nextActivity() {
		return person().nextActivity();
	}

	public ActivityIfc nextHomeActivity() {
		return person().nextHomeActivity();
	}

	public Trip currentTrip() {
		return person().currentTrip();
	}

	public void currentTrip(Trip trip) {
  	person().currentTrip(trip);
	}

	@Override
	public void initSchedule(
			TourFactory tourFactory, 
			ActivityStartAndDurationRandomizer activityDurationRandomizer,
			List<Time> days) {
		person().initSchedule(tourFactory, activityDurationRandomizer, days);
		
	}

	public boolean isFemale() {
		return person().isFemale();
	}

	public boolean isMale() {
		return person().isMale();
	}

	public String forLogging(ImpedanceIfc impedance) {
		return person().forLogging(impedance);
	}
	
	@Override
	public PersonAttributes attributes() {
		return person().attributes();
	}

	public String toString() {

		return "Person: oid=" + getOid() + " " + isCarDriver() + " " + currentTrip()
						+ "\n" + activitySchedule();
	}

	@Override
	public void startActivity(Time currentDate, ActivityIfc activity, Trip precedingTrip,
			ReschedulingStrategy rescheduling) {
		person().startActivity(currentDate, activity, precedingTrip, rescheduling);
	}

	@Override
	public Car parkCar(Zone zone, Location location, Time time) {
		return person().parkCar(zone, location, time);
	}

	@Override
	public boolean hasParkedCar() {
		return person().hasParkedCar();
	}

	@Override
	public void takeCarFromParking() {
		person().takeCarFromParking();
	}

	@Override
	public ModeChoicePreferences modeChoicePrefsSurvey() {
		return person.modeChoicePrefsSurvey();
	}

	@Override
	public ModeChoicePreferences modeChoicePreferences() {
		return person.modeChoicePreferences();
	}

	@Override
	public TourBasedActivityPattern tourBasedActivityPattern() {
		return person.tourBasedActivityPattern();
	}


}

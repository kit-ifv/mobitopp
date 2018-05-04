package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Map;
import java.util.HashMap;
import java.util.List;


import java.text.DecimalFormat;

import edu.kit.ifv.mobitopp.data.person.PersonId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonForSetup;

import edu.kit.ifv.mobitopp.simulation.*;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivitySchedule;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityScheduleWithState;
import edu.kit.ifv.mobitopp.simulation.activityschedule.DefaultActivitySchedule;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;



public class PersonForDemand
  implements Person, PersonForSetup, Serializable
{

	private static final long serialVersionUID = 2936969065679820132L;
	
	protected int oid;
	protected Household household;

  private PersonId id = null;

  private final short age;


	private final Employment employment;
	private final Gender gender;

	private final int income;

	private final boolean hasBike;
	private final boolean hasAccessToCar;
	private final boolean hasPersonalCar;
	private final boolean hasCommuterTicket;
  
	private Map<ActivityType,Zone> fixedDestinationZones = new HashMap<ActivityType,Zone>();
	private Map<ActivityType,Location> fixedDestinations = new HashMap<ActivityType,Location>();
	private final List<FixedDestination> fixedDestinationElements;

	/** Planned activity program **/
  private PatternActivityWeek activityPattern;

	/** Realised activity program **/
  private transient ModifiableActivityScheduleWithState activitySchedule; 

	private transient TripIfc currentTrip; 

	private PrivateCar personalCar = null;

	private enum CarUsage { NONE, DRIVER, PASSENGER, PARKED }

	private Car car;
	private CarUsage currentCarUsage = CarUsage.NONE;

  public PersonForDemand(
		int oid,
		PersonId id,
		Household household,
		int age,
		Employment employment,
		Gender gender,
		int income,
		boolean hasBike,
		boolean hasAccessToCar,
		boolean hasPersonalCar,
		boolean hasCommuterTicket,
		PatternActivityWeek activitySchedule
	)
  {
		this.oid = oid;
		this.id = id;

		this.household = household;

		this.age = (short) age;
    this.activityPattern = activitySchedule;

		this.employment=employment;
		this.gender=gender;

		this.income=income;

		this.hasBike = hasBike;
		this.hasAccessToCar = hasAccessToCar;
		this.hasPersonalCar = hasPersonalCar;
		this.hasCommuterTicket = hasCommuterTicket;
		
		fixedDestinationElements = new ArrayList<>();
  }

	public int getOid() {

		return this.oid;
	}

	@Override
	public boolean isCarDriver() {

		return this.currentCarUsage == CarUsage.DRIVER;
	}

	@Override
	public boolean isCarPassenger() {

		return this.currentCarUsage == CarUsage.PASSENGER;
	}

	private boolean isUsingCar() {
		return isCarDriver() || isCarPassenger() || hasParkedCar();
	}

	@Override
	public Car whichCar() {
		assert isUsingCar();

		return this.car;
	}

	@Override
	public void useCar(Car car, Time time) {

		assert car != null;
		assert !car.isUsed();
		assert !isUsingCar() : forDebug();

		this.car = car;
		this.currentCarUsage = CarUsage.DRIVER;
		car.use(this, time);
	}

	@Override
	public void useCarAsPassenger(Car car) {

		assert car != null;
		assert car.isUsed(); // car needs a driver
		assert !isUsingCar() : ("car = " + this.car);

		this.car = car;
		this.currentCarUsage = CarUsage.PASSENGER;
		car.useAsPassenger(this);
	}

	@Override
	public void leaveCar() {

		Car car = this.car;

		this.car = null;
		this.currentCarUsage = CarUsage.NONE;
		car.leave(this);
	}

	@Override
	public Car releaseCar(Time time) {

		assert this.car != null : (nextActivity().mode() + ": " + getOid());

		Car car = this.car;

		this.car = null;
		this.currentCarUsage = CarUsage.NONE;
		car.release(this, time);

		return car;
	}

	@Override
	public Car parkCar(Zone zone, Location location, Time time) {
		
		assert this.car != null;
		
		this.currentCarUsage = CarUsage.PARKED;
		
		return this.car;
	}

	@Override
	public boolean hasParkedCar() {

		return this.currentCarUsage == CarUsage.PARKED;
	}

	@Override
	public void takeCarFromParking() {

		assert this.car != null;
		
		this.currentCarUsage = CarUsage.DRIVER;
	}

	public Household household() {

		return this.household;
	}

	public boolean hasBike() {
		return this.hasBike;
	}

	public boolean hasAccessToCar() {
		return this.hasAccessToCar;
	}

	public boolean hasPersonalCar() {
		return this.hasPersonalCar;
	}

	public boolean hasCommuterTicket() {
		return this.hasCommuterTicket;
	}

	public boolean hasDrivingLicense() {
		return hasAccessToCar() || hasPersonalCar();
	}



  public int age()
  {
    return this.age;
  }

  public PersonId getId()
  {
		assert this.id != null;

    return this.id;
  }


  public PatternActivityWeek getPatternActivityWeek()
  {
		assert activityPattern != null;

    return this.activityPattern;
  }


  public Employment employment()
  {
		return this.employment;
  }

  public Gender gender()
  {
		return this.gender;
  }

  public ActivityScheduleWithState activitySchedule()
  {
    return this.activitySchedule;
  }


	public int getIncome() 
	{
		return this.income;
	}

	public Zone homeZone() {

		return this.household.homeZone();
	}

	public Zone fixedDestinationZoneFor(ActivityType activityType) {

		return this.fixedDestinationZones.get(activityType);
	}

	public Location fixedDestinationFor(ActivityType activityType) {

		return this.fixedDestinations.get(activityType);
	}

	public int getHomeZoneOid() {

		return this.household.homeZone().getOid();
	}


	public Zone fixedZoneFor(ActivityType activityType) {

		return this.fixedDestinationZones.get(activityType);
	}

	public boolean hasFixedZoneFor(ActivityType activityType) {

		return this.fixedDestinationZones.containsKey(activityType);
	}

	public Zone fixedActivityZone() {

		Zone zone = household().homeZone();

		if ( this.fixedDestinationZones.containsKey(ActivityType.WORK)) {
			zone = this.fixedDestinationZones.get(ActivityType.WORK);
		} else if ( this.fixedDestinationZones.containsKey(ActivityType.EDUCATION)) {
			zone = this.fixedDestinationZones.get(ActivityType.EDUCATION);
		}

		return zone;
	}

	public boolean hasFixedActivityZone() {

		return this.fixedDestinationZones.containsKey(ActivityType.WORK)
					|| this.fixedDestinationZones.containsKey(ActivityType.EDUCATION);
	}


	public void setFixedDestination(ActivityType activityType, Zone zone, Location location) {
		this.fixedDestinationZones.put(activityType, zone);
		this.fixedDestinations.put(activityType, location);
		fixedDestinationElements.add(new FixedDestination(activityType, zone, location));
	}


	public Zone nextFixedActivityZone(
		ActivityIfc activity
	) {

		ActivityIfc act = activitySchedule().nextActivity(activity);

		while (act != null && !act.activityType().isFixedActivity()) {

			act = activitySchedule().nextActivity(act);
		}

		if (act == null || act.activityType().isHomeActivity()) {

			return household().homeZone();

		} else {
			assert this.fixedDestinationZones.containsKey(act.activityType());
			return this.fixedDestinationZones.get(act.activityType());

		}
	}

	public ActivityIfc currentActivity() {
		return this.activitySchedule.currentActivity();
	}
	
	
	public void startActivity(
			Time currentDate, 
			ActivityIfc activity, 
			TripIfc precedingTrip,
			ReschedulingStrategy rescheduling
		) {
			
			Time plannedStartDate = activity.startDate();
			activity.setStartDate(currentDate);

			rescheduling.adjustSchedule(this.activitySchedule, activity, plannedStartDate, currentDate);

			assert activity != null;

			activity.setRunning(true);
			activity.setMode(precedingTrip.mode());
			this.activitySchedule.startActivity(activity);
		}

	public ActivityIfc nextActivity() {
		assert currentTrip() != null;
		
		return currentTrip().nextActivity();
	}

	public ActivityIfc nextHomeActivity() {
		assert currentActivity() != null;
		ActivityIfc homeActivity = this.activitySchedule.nextHomeActivity(currentActivity());
		
		assert homeActivity.activityType().isHomeActivity();
		
		return homeActivity;
	}

	public TripIfc currentTrip() {
		return this.currentTrip;
	}

	public void currentTrip(TripIfc trip) {
		this.currentTrip=trip;
	}

	public void initSchedule(
		ActivityStartAndDurationRandomizer activityDurationRandomizer, 
		List<Time> days
	) {
		this.activitySchedule = new DefaultActivitySchedule(this.activityPattern, activityDurationRandomizer, days);
	}

	public void assignPersonalCar(PrivateCar personalCar) {
		assert this.personalCar == null;

		this.personalCar = personalCar;
	}

	public PrivateCar personalCar() {
		assert this.personalCar != null;

		return this.personalCar;
	}

	public boolean hasPersonalCarAssigned() {

		return personalCar != null;
	}

	public boolean isFemale() {
		return this.gender == Gender.FEMALE;
	}

	public boolean isMale() {
		return this.gender == Gender.MALE;
	}

	public boolean isCarSharingMember() {
		return false;
	}


	public String forLogging(ImpedanceIfc impedance) {

		Household household = household();
		Zone homeZone = homeZone();

		DecimalFormat outFormat = new DecimalFormat("#####0");

		int targetZoneOidForWork = -1;
		int targetZoneOidForEducation = -1;

		if (hasFixedZoneFor(ActivityType.WORK)) {
			targetZoneOidForWork = fixedZoneFor(ActivityType.WORK).getOid();
		}
		if (hasFixedZoneFor(ActivityType.EDUCATION)) {
			targetZoneOidForEducation = fixedZoneFor(ActivityType.EDUCATION).getOid();
		}

		int commutationTicket = hasCommuterTicket() ? 1 : 0;
		int drivingLicense = age() >= 17 && (hasAccessToCar() || hasPersonalCar()) ? 1 : 0;

		int employmentType = employment().getTypeAsInt();

		StringBuffer buffer = new StringBuffer();

			buffer.append("P; ");
			buffer.append(household.getOid()).append("; ");
			buffer.append(household.getId().getHouseholdNumber()).append("; ");
			buffer.append(household.getId().getYear()).append("; ");
			buffer.append(getOid()).append("; ");
			buffer.append(getId().getPersonNumber()).append("; ");
			buffer.append(gender().getTypeAsInt()).append("; ");
			buffer.append(age()).append("; ");
			buffer.append(employmentType + "; ");
			buffer.append(homeZone.getId() + "; ");

			if (targetZoneOidForWork > -1) {

				Zone workZone = fixedDestinationZoneFor(ActivityType.WORK);
				Location workLocation = fixedDestinationFor(ActivityType.WORK);

				int distance = (int) impedance.getDistance(homeZone.getOid(), workZone.getOid());

				buffer.append(outFormat.format(distance) + "; ");
				buffer.append(workZone.getId() + "; ");
				buffer.append(workLocation + "; ");
			} else {
				buffer.append("-1; ");
				buffer.append("-1; ");
				buffer.append("-1; ");
			}


			if (targetZoneOidForEducation > -1) {

				Zone eduZone = fixedDestinationZoneFor(ActivityType.EDUCATION);
				Location eduLocation = fixedDestinationFor(ActivityType.EDUCATION);

				int distance = (int) impedance.getDistance(homeZone.getOid(), eduZone.getOid());

				buffer.append(outFormat.format(distance) + "; ");

				buffer.append(eduZone.getId() + "; ");
				buffer.append(eduLocation + "; ");
			} else {
				buffer.append("-1; ");
				buffer.append("-1; ");
				buffer.append("-1; ");
			}
			buffer.append(commutationTicket + "; ");
			buffer.append(drivingLicense + "; ");
		
		return buffer.toString();
	}
	
	@Override
	public PersonAttributes attributes() {
		return new PersonAttributes(oid, id, household, age, employment, gender, income, hasBike,
				hasAccessToCar, hasPersonalCar, hasCommuterTicket);
	}

	public String forDebug() {

		String debug = "\nDEBUG PersonForDemand:"
										+ "\noid=" + oid 
										+ "\ncurrentCarUsage=" + currentCarUsage
										+ "\ncurrentTrip=" + currentTrip
										+ "\ncar=" + car
								;

  	ActivitySchedule schedule = activitySchedule();

		for (ActivityIfc act = schedule.firstActivity(); act != nextActivity(); act=schedule.nextActivity(act)) {

			debug += "\n" + act;
		}

		return debug;
	}

	@Override
	public void setFixedDestination(FixedDestination destination) {
		fixedDestinationElements.add(destination);
		ActivityType activityType = destination.activityType();
		fixedDestinationZones.put(activityType, destination.zone());
		fixedDestinations.put(activityType, destination.location());
	}


	@Override
	public Collection<FixedDestination> getFixedDestinations() {
		return Collections.unmodifiableCollection(fixedDestinationElements);
	}


}

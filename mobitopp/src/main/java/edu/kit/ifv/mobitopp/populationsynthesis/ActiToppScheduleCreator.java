package edu.kit.ifv.mobitopp.populationsynthesis;


import edu.kit.ifv.mobitopp.actitopp.*;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPatternCreator;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


/**
 * Klasse zur Erzeugung der Aktivitätenpläne
 * Implementiert das Interface
 *
 * @author Tim Hilgert
 */
@Slf4j
public class ActiToppScheduleCreator implements ActivityScheduleAssigner {

	// Debug-Counters for persons and households
	private int modeling_counter_pers=0;
	private int modeling_counter_hh=0;

	// Debug and Log Limit (every x persons)
	private int exportandloglimit=50000;

	// filebase to read relevant parameters
	private ModelFileBase fileBase;
	// filebase for detailed purpose modeling
	private ModelFileBase fileBase_detailedPurposes;

	// random number generator
	private RNGHelper randomgenerator;

	private PanelDataRepository repository;

	// HashMap for Output Handling
	private HashMap<Integer, ActitoppPerson> personmap = new HashMap<Integer, ActitoppPerson>();
	// Output Logger
	private CSVExportLogger logger;

	// only for debug reasons
	private int globalretries = 0;
	private long timeforcounting;

	public ActiToppScheduleCreator(long seed, PanelDataRepository repository)
	{
		Configuration.model_joint_actions = false;
		this.randomgenerator = new RNGHelper(seed);
		this.repository = repository;
		try {
			this.logger = new CSVExportLogger(new File("output/actitopp"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * create standard file base with custom parameterset and standard step information
		 */
		fileBase = new ModelFileBase("mopv14_nopkwhh");

		/*
		 * create custom model file base for detailed purposes
		 */
		HashSet<String> dcsteps_purposes = new HashSet<String>();
		dcsteps_purposes.add("98A");
		dcsteps_purposes.add("98B");
		dcsteps_purposes.add("98C");
		fileBase_detailedPurposes = new ModelFileBase(new File("config/parameters/actitopp_purposes"), dcsteps_purposes, null, null);

		System.out.println("actitopp - coordinated modeling: " + Configuration.coordinated_modelling);
		System.out.println("actitopp - modeling of joint actions: " + Configuration.model_joint_actions);

		timeforcounting = System.currentTimeMillis();
	}


	/**
	 *
	 * Create activity schedule of a complete household and returns
	 * a hash map with persons containing activity schedules
	 *
	 */
	@Override
	public void assignActivitySchedule(HouseholdForSetup household) {

		// initialization

		// Create household
		modeling_counter_hh++;
		ActiToppHousehold tmpactitopphousehold = new ActiToppHousehold(
				modeling_counter_hh, 										// HouseholdIndex
				household.getNumberOfPersonsInAgeRange(0,10),				// number of children aged 0-10
				household.getNumberOfPersonsInAgeRange(0,18),				// number of children aged under 18
				household.homeZone().getAreaType().getTypeAsInt(),			// AreaType
				household.getTotalNumberOfCars()  							// number of cars
		);

		List<PersonBuilder> persons = household.getPersons();

		// Create persons in household
		for (int i=0 ; i< persons.size(); i++) {
			PersonBuilder aPerson = persons.get(i);

			PersonOfPanelData panelperson = repository.getPerson(PersonOfPanelDataId.fromPersonId(aPerson.getId()));

			double commuting_distance_work = panelperson.getDistanceWork();
			double commuting_distance_education = panelperson.getDistanceEducation();

			/*
			 * model person between 6 and 9 years in actitopp as if they are 12 years old.
			 * this is a workaround since actitopp can model only persons 10 years and older
			 */
			int ageinyearsforactitopp = (aPerson.age() >=6 && aPerson.age() <=9 ? 12 : aPerson.age());

			ActitoppPerson tmpperson =
					new ActitoppPerson(
							tmpactitopphousehold,
							i,														// index in household
							aPerson.getId().getPersonNumber(), 						// person index
							ageinyearsforactitopp,									// modified age
							aPerson.employment().getTypeAsInt(),					// employment
							aPerson.gender().getTypeAsInt(),						// gender
							commuting_distance_work,								// commuting distance to work (0 if no commuting)
							commuting_distance_education							// commuting distance to education (0 if no commuting)
					);

			//disable modeling of working activities for persons between 6 and 9 years old
			if (aPerson.age() >=6 && aPerson.age() <=9) tmpperson.setAllowedToWork(false);

		}

		/*
		 * Generate schedules for the household until there is no longer a wrong schedule for a person
		 * modeling order of household members is given by the probability of joint actions for each person
		 *
		 * Sometimes, the model creates wrong schedules due to time conflicts between activities.
		 * In these cases, the modeling is repeated until the schedule for each person is free of conflicts.
		 */
		boolean householdscheduleOK = false;
		while (!householdscheduleOK) {
			try {
				//create schedules for all household members
				tmpactitopphousehold.generateSchedules(fileBase, randomgenerator);
				householdscheduleOK = true;
			} catch (InvalidPatternException e) {
				globalretries++;
				// reset household modeling results when error
				tmpactitopphousehold.resetHouseholdModelingResults();
			}
		}


		/*
		 * Switch actiTopp age back to original value for person between 6 and 9
		 */
		for (int i=0 ; i< persons.size(); i++) {
			if (persons.get(i).age() >=6 && persons.get(i).age() <=9) {
				tmpactitopphousehold.getHouseholdMember(i).setAge(persons.get(i).age());
			}
		}


		/*
		 *  Create activitySchedule Format for mobiTopp
		 */
		for (int i=0 ; i< persons.size(); i++) {
			PersonBuilder aPerson = persons.get(i);
			ActitoppPerson personOfActiTopp = tmpactitopphousehold.getHouseholdMember(i);

			PatternActivityWeek patternActivityWeek= generatepatternActivityWeek(personOfActiTopp);
			aPerson.setPatternActivityWeek(TourBasedActivityPatternCreator.fromPatternActivityWeek(patternActivityWeek));

			// only for debug and output
			personmap.put(modeling_counter_pers, personOfActiTopp);
			modeling_counter_pers++;
			if (modeling_counter_pers % exportandloglimit == 0) writeExportAndLogData();
		}

	}

	/**
	 *
	 * generate ActivityPattern in correct mobiTopp format
	 *
	 * @param personOfActiTopp person to create activity pattern for
	 * @return activity pattern
	 */
	private PatternActivityWeek generatepatternActivityWeek(ActitoppPerson personOfActiTopp)
	{
		PatternActivityWeek patternActivityWeek = new PatternActivityWeek();

		// Convert activities to mobiTopp activity data format
		for (HActivity act : personOfActiTopp.getWeekPattern().getAllActivities()) {
			patternActivityWeek.addPatternActivity(convertActiToppTomobiToppActivityType(act));
		}
		// Check correct format of mobitopp patternActivityWeek
		patternActivityWeek = new TourPatternFixer().ensureIsTour(patternActivityWeek);

		return patternActivityWeek;
	}


	/**
	 *
	 * method to convert actitopp activities into mobitopp format
	 *
	 */
	private PatternActivity convertActiToppTomobiToppActivityType(HActivity act) {
		ActivityType mobitoppActivityType = null;
		// Convert actiToppPurposes To mobiToppPurpose Types
		switch (act.getActivityType()) {
			case WORK:
				mobitoppActivityType = ActivityType.getTypeFromInt(detailedpurposemodeling(act, "98C"));
				break;
			case EDUCATION:
				if (act.getPerson().getEmployment()==40) {
					mobitoppActivityType = ActivityType.EDUCATION_PRIMARY;
				}
				else if (act.getPerson().getEmployment()==41) {
					mobitoppActivityType = ActivityType.EDUCATION_SECONDARY;
				}
				else if (act.getPerson().getEmployment()==5) {
					mobitoppActivityType = ActivityType.EDUCATION_SECONDARY;
				}
				else {
					mobitoppActivityType = ActivityType.EDUCATION_TERTIARY;
				}
				break;
			case SHOPPING:
				mobitoppActivityType = ActivityType.getTypeFromInt(detailedpurposemodeling(act, "98A"));
				break;
			case LEISURE:
				mobitoppActivityType = ActivityType.getTypeFromInt(detailedpurposemodeling(act, "98B"));
				break;
			case TRANSPORT:
				mobitoppActivityType = ActivityType.SERVICE;
				break;
			case HOME:
				mobitoppActivityType = ActivityType.HOME;
				break;
			default:
				System.err.println("unknown activity type");
		}

		DayOfWeek wd = DayOfWeek.fromDay(act.getDayIndex());
		PatternActivity activity = new PatternActivity(mobitoppActivityType, wd, act.isHomeActivity() ? 0 : act.getEstimatedTripTimeBeforeActivity(), act.getStartTimeWeekContext(), act.getDuration());
		return activity;
	}

	/**
	 *
	 * detailed modeling of activity purposes
	 *
	 * @param activity
	 * @param id
	 */
	private int detailedpurposemodeling(HActivity activity, String id) {
		ActitoppPerson person = activity.getPerson();
		HDay currentDay = activity.getDay();
		HTour currentTour = activity.getTour();

		// create attribute lookup
		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, activity);

		// create step object
		DCDefaultModelStep step = new DCDefaultModelStep(id, fileBase_detailedPurposes, lookup, randomgenerator);
		step.doStep();

		int chosenActivityType = Integer.parseInt(step.getAlternativeChosen());
		return chosenActivityType;
	}


	/**
	 *
	 * write actitopp result into log files
	 *
	 */
	private void writeExportAndLogData() {
		System.out.println("Doing person Nr... " + modeling_counter_pers);
		int percent = (int) Math.round(100*((double) globalretries/(double) personmap.size()));
		System.err.println("number of retries: " + globalretries + " (~ " + percent + "% of all persons)");
		globalretries=0;
		long duration_msec = System.currentTimeMillis() - timeforcounting;
		System.out.println("Duration total modeling: " + duration_msec + " milli sec");
		System.out.println("Duration per Pers modeling: " + (duration_msec/personmap.size()) + " milli sec");

		try {
			logger.writeLogging(personmap);
			System.out.println("wrote Log Data " + new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new java.util.Date()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		personmap.clear();
		timeforcounting = System.currentTimeMillis();
	}

}

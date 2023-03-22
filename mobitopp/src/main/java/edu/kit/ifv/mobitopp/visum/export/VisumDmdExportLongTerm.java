package edu.kit.ifv.mobitopp.visum.export;

import static edu.kit.ifv.mobitopp.simulation.ActivityType.HOME;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.PRIVATE_VISIT;
import static edu.kit.ifv.mobitopp.visum.export.VisumDmdIdProvider.householdId;
import static edu.kit.ifv.mobitopp.visum.export.VisumDmdIdProvider.locationIdOf;
import static edu.kit.ifv.mobitopp.visum.export.VisumDmdIdProvider.personId;
import static edu.kit.ifv.mobitopp.visum.export.WriterFactory.finishWriter;
import static edu.kit.ifv.mobitopp.visum.export.WriterFactory.getWriter;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandRegionDemandCalculator;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemandZoneBasedStep;
import edu.kit.ifv.mobitopp.populationsynthesis.region.PopulationSynthesisStep;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.BaseHousehold;
import edu.kit.ifv.mobitopp.simulation.BasePerson;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

/**
 * VisumDmdExportLongTerm exports the long-term results in visum dmd format.<br>
 * It can be applied as {@link PopulationSynthesisStep} given to a {@link DemandRegionDemandCalculator}.<br>
 * It writes activity codes, locations, activity-locations, households, persons, long-term choices and daily schedules. <br>
 * 
 * <br>Notes on how to apply:<br>
 * 1. Initialize export in LongTermModule constructor:
 * <pre>
 * public LongTermModule(SynthesisContext context) {
 * 	...
 * 	export = new VisumDmdExportLongTerm(context);
 * 	export.init(context);
 * 	...
 * }
 * </pre>
 * 2. Add export as synthesis step:
 * <pre>
 * {@literal @}Override
 * protected DemandDataCalculator createCalculator(...) {
 * 	...
 * 	LinkedList{@literal <PopulationSynthesisStep>} steps = new LinkedList{@literal <>}();
 * 	...
 * 	steps.add(export.asSynthesisStep());
 *  steps.add(storeData());
 *  steps.add(cleanData());
 * 	...
 * }
 * </pre>
 * 3. Finish export after synthesis:
 * <pre>
 * {@literal @}Override
 * protected void executeAfterCreation() {
 * 		export.finish();
 * }
 * </pre>
 */
public class VisumDmdExportLongTerm {

	private static final String SEP = ";";
	private static final String NEW_LINE = "\r\n";

	private final Writer locationWriter;
	private final Writer activityLocationWriter;
	private final Writer householdWriter;
	private final Writer personWriter;
	private final Writer longTermChoiceWriter;
	private final Writer dailyScheduleWriter;
	private final Map<Integer, List<Integer>> locationIds;
	
	
	/**
	 * Instantiates a new visum dmd long-term export with the given context.
	 *
	 * @param context the synthesis context
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public VisumDmdExportLongTerm(SynthesisContext context) throws IOException {
		
		File folder = new File(context.configuration().getResultFolder());
		
		this.locationWriter = getWriter(new File(folder, "locations.dmd"));
		this.activityLocationWriter = getWriter(new File(folder, "activityLocations.dmd"));
		this.householdWriter = getWriter(new File(folder, "households.dmd"));
		this.personWriter = getWriter(new File(folder, "persons.dmd"));
		this.longTermChoiceWriter = getWriter(new File(folder, "longTermChoices.dmd"));
		this.dailyScheduleWriter = getWriter(new File(folder, "dailySchedule.dmd"));
		
		this.locationIds = new HashMap<>();
	}
	
	/**
	 * Initializes the writers and logs the zones in the given context.
	 *
	 * @param context the synthesis context
	 */
	public void init(SynthesisContext context) {
		try {
			locationWriter.write(generateVersionHeader());
			locationWriter.write(NEW_LINE);
			locationWriter.write(generateDemandmodelTable());
			locationWriter.write(NEW_LINE);
			locationWriter.write(generateActivityTable());
			locationWriter.write(NEW_LINE);
			locationWriter.write(generateLoactionHeader());

			activityLocationWriter.write(generateActivityLoactionHeader());
			householdWriter.write(generateHouseholdHeader());
			personWriter.write(generatePersonHeader());
			longTermChoiceWriter.write(generateLongTermChoiceHeader());
			dailyScheduleWriter.write(generateDailyScheduleHeader());

		} catch (IOException e) {
			System.err.println("Could not init .dmd files");
		}
		
		context.zoneRepository().getZones().forEach(dz -> logZone(dz.zone()));
	}
	
	/**
	 * Creates a {@link DemandZoneBasedStep} using {@link VisumDmdExportLongTerm#logDemandZone(DemandZone)}.
	 *
	 * @return a population synthesis step for exporting to visum dmd format
	 */
	public PopulationSynthesisStep asSynthesisStep() {
		return new DemandZoneBasedStep(this::logDemandZone);
	}
	
	/**
	 * Finish export by closing all file writers.
	 */
	public void finish() {
		try {
			finishWriter(locationWriter);
			finishWriter(activityLocationWriter);
			finishWriter(householdWriter);
			finishWriter(personWriter);
			finishWriter(longTermChoiceWriter);
			finishWriter(dailyScheduleWriter);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}
	
	
	
	
	
	/**
	 * Log the given zone.
	 * 
	 * Log the zone's centroid location, and a activity location of for transfer activities.
	 * Log opportunities of the given zone.
	 *
	 * @param zone the zone to be logged
	 */
	private void logZone(Zone zone) {
		try {
			int id = locationIdOf(zone);
			
			if (!locationIds.containsKey(id)) {
				locationWriter.write(generateLocationRow(zone));
				activityLocationWriter.write(generateActivityLocationRow(zone));
				
				locationIds.put(id, new ArrayList<>(List.of(-1)));
			}
			
			zone.opportunities().forEach(this::logOpportunity);

		} catch (IOException e) {
			System.err.println("Could not log location of zone " + String.valueOf(zone));
			e.printStackTrace();
		}

	}

	/**
	 * Log the given demand zone.
	 * 
	 * Log the zones's opportunities and households.
	 *
	 * @param zone the zone to be logged
	 */
	private void logDemandZone(DemandZone zone) {
		logZone(zone.zone());
		
		zone.opportunities().forEach(this::logOpportunity);
		zone.getPopulation().households().forEach(this::logHousehold);	
	}
	
	/**
	 * Log the given opportunity.
	 * 
	 * Logs the opportunities location as well as activity-location.
	 * Only log (activity)locations that have not yet been logged.
	 *
	 * @param opportunity the opportunity
	 */
	private void logOpportunity(Opportunity opportunity) {
		try {
			int id = locationIdOf(opportunity);
			
			if (!locationIds.containsKey(id)) {
				locationWriter.write(generateLocationRow(opportunity));
				locationIds.put(id, new ArrayList<>());
			}
			
			List<Integer> locationActivities = locationIds.get(id);
			if (!locationActivities.contains(opportunity.activityType().getTypeAsInt())) {
				activityLocationWriter.write(generateActivityLocationRow(opportunity));
				locationActivities.add(opportunity.activityType().getTypeAsInt());
			}

		} catch (IOException e) {
			System.err.println("Could not log location of opportunity " + String.valueOf(opportunity));
			e.printStackTrace();
		}
	}

	/**
	 * Log the given household.
	 * 
	 * Log the household's location.
	 * Log the household as activity-location for HOME and PRIVATE_VISIT activities.
	 * Only log (activity)locations that have not yet been logged.
	 * Log the household properties.
	 * Log the household's persons.
	 *
	 * @param household the household to be logged
	 */
	private void logHousehold(HouseholdForSetup household) {
		try {
			int id = locationIdOf(household);
			
			if (!locationIds.containsKey(id)) {
				locationWriter.write(generateLocationRow(household));
				locationIds.put(id, new ArrayList<>());
			}
			
			
			List<Integer> locationActivities = locationIds.get(id);
			
			if (!locationActivities.contains(HOME.getTypeAsInt())) {
				activityLocationWriter.write(generateActivityLocationRow(household));
				locationActivities.add(HOME.getTypeAsInt());
			}
			
			if (!locationActivities.contains(PRIVATE_VISIT.getTypeAsInt())) {
				activityLocationWriter.write(generateActivityLocationRowVisit(household));
				locationActivities.add(PRIVATE_VISIT.getTypeAsInt());
			}
			
			
			householdWriter.write(generateHouseholdRow(household));
			household.persons().forEach(this::logPerson);

		} catch (IOException e) {
			System.err.println("Could not log household " + String.valueOf(household));
			e.printStackTrace();
		}
	}
	
	/**
	 * Log the given person.
	 * 
	 * Logs the person properties.
	 * Logs the person's daily schedule.
	 * Logs the person's fixed destinations.
	 *
	 * @param person the person to be logged
	 */
	private void logPerson(BasePerson person) {
		try {
			personWriter.write(generatePersonRow(person));
			dailyScheduleWriter.write(generateDailyScheduleRow(person));
				
			person.getFixedDestinations().forEach(f -> logLongTermChoice(person, f));

		} catch (IOException e) {
			System.err.println("Could not log person " + String.valueOf(person));
			e.printStackTrace();
		}
	}
	
	/**
	 * Log the given long term choice (fixed destination).
	 * 
	 * Logs the location and activity-location of the given fixed destination.
	 * Logs the long term destination choice for the given person.
	 * Only log (activity)locations that have not yet been logged.
	 *
	 * @param person the person to be logged
	 * @param fixed the fixed destination to be logged
	 */
	private void logLongTermChoice(BasePerson person, FixedDestination fixed) {
		try {
			int id = locationIdOf(fixed.location());
			
			if (!locationIds.containsKey(id)) {
				locationWriter.write(generateLocationRow(fixed));
				locationIds.put(id, new ArrayList<>());
			}
			
			List<Integer> locationActivities = locationIds.get(id);
			if (!locationActivities.contains(fixed.activityType().getTypeAsInt())) {
				activityLocationWriter.write(generateActivityLocationRow(fixed));
				locationActivities.add(fixed.activityType().getTypeAsInt());
			}
			
			longTermChoiceWriter.write(generateLongTermChoiceRow(person, fixed));

		} catch (IOException e) {
			System.err.println("Could not log fixed destination " + String.valueOf(fixed) + "of person " + String.valueOf(person));
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Generate visum version header.
	 *
	 * @return the visum version header
	 */
	public static String generateVersionHeader() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();
		String date = dtf.format(now);
		
		return "$VISION" + NEW_LINE
			 + "* KIT Karlsruher Institut f�r Technologie Fakult�t Bau, Geo + Umwelt Karlsruhe" + NEW_LINE
			 + "* " + date + NEW_LINE
			 + "*" + NEW_LINE
			 + "* Tabelle: Versionsblock" + NEW_LINE
			 + "*" + NEW_LINE
			 + "$VERSION:VERSNR;FILETYPE;LANGUAGE;UNIT" + NEW_LINE
			 + "13.000;Demand;ENG;KM"  + NEW_LINE;
	}
	
	/**
	 * Generate demand model table.
	 *
	 * @return the demand model tables
	 */
	private String generateDemandmodelTable() {
		return "*" + NEW_LINE
			 + "* Tabelle: Nachfragemodelle" + NEW_LINE
			 + "*" + NEW_LINE
			 + "$DEMANDMODEL:CODE;NAME;TYPE;MODESET" + NEW_LINE
			 + "ABM;ABM;ABM;"  + NEW_LINE;
	}
	
	/**
	 * Generate activity code table containing all {@link ActivityType}s 
	 * and the additional activity code transfer (-1).
	 *
	 * @return the activity code table
	 */
	private String generateActivityTable() {
		StringBuilder table = new StringBuilder();
		table.append("*" + NEW_LINE);
		table.append("* Tabelle: Aktivit�ten" + NEW_LINE);
		table.append("*" + NEW_LINE);
		table.append("$ACTIVITY:CODE;NAME;DEMANDMODELCODE;RANK;ISHOMEACTIVITY;STRUCTURALPROPCODES;CONSTRAINTDEST;CONSTRAINTDESTTYPE;CONSTRAINTMINFACTORDEST;CONSTRAINTMAXFACTORDEST" + NEW_LINE);

		table.append("-1;TRANSIT;ABM;1;0;;0;SEPARATELYPERDEMANDSTRATUM;1;1" + NEW_LINE);
		
		for (ActivityType activityType : ActivityType.values()) {
			table.append(activityType.getTypeAsInt() + SEP);
			table.append(activityType.name().toUpperCase() + SEP);
			table.append("ABM;1;");
			table.append((activityType.equals(HOME)) ? 1 : 0);
			table.append(";;0;SEPARATELYPERDEMANDSTRATUM;1;1");
			table.append(NEW_LINE);
		}
		
		return table.toString();
	}
	
	
	
	/**
	 * Generate visum loaction table header.
	 *
	 * @return the location table header for logging
	 */
	private String generateLoactionHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Standorte" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$LOCATION:NO;XCOORD;YCOORD;ZONENO" + NEW_LINE;
	}
	
	/**
	 * Generate household location row.
	 *
	 * @param household the household with a location to be logged
	 * @return the household's location row for logging
	 */
	private String generateLocationRow(BaseHousehold household) {
		return generateLocationRow(household.homeLocation(), household.homeZone());
	}
	
	/**
	 * Generate opportunity location row.
	 *
	 * @param opportunity the opportunity with a location to be logged
	 * @return the opportunity's location row for logging
	 */
	private String generateLocationRow(Opportunity opportunity) {
		return generateLocationRow(opportunity.location(), opportunity.zone());
	}
	
	/**
	 * Generate zone centroid location row.
	 *
	 * @param zone the zone with a centroid location to be logged
	 * @return the centroid location row for logging
	 */
	private String generateLocationRow(Zone zone) {
		return generateLocationRow(zone.centroidLocation(), zone);
	}

	/**
	 * Generate fixed destionation location row.
	 *
	 * @param fixed the fixed destination with a location to be logged
	 * @return the fixed destination location row for logging
	 */
	private String generateLocationRow(FixedDestination fixed) {
		return generateLocationRow(fixed.location(), fixed.zone());
	}
	
	/**
	 * Generate location row.
	 *
	 * @param location the location to be logged
	 * @param zone the zone containing the location
	 * @return the location row for logging
	 */
	private String generateLocationRow(Location location, Zone zone) {
		return generateLocationRow(location, zone.getId());
	}
		
	/**
	 * Generate location row.
	 *
	 * @param location the location
	 * @param zone the zone
	 * @return the string
	 */
	private String generateLocationRow(Location location, ZoneId zone) {
		StringBuilder row = new StringBuilder();
		
		row.append(locationIdOf(location)); //NO
		row.append(SEP);
		row.append(location.coordinatesP().getX()); //XCOORD
		row.append(SEP);
		row.append(location.coordinatesP().getY()); //YCOORD
		row.append(SEP);
		row.append(zone.getExternalId()); //ZONENO
		row.append(NEW_LINE);
		
		return row.toString();
	}
	
	
	
	/**
	 * Generate activity loaction table header.
	 *
	 * @return the activity location table header
	 */
	private String generateActivityLoactionHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Aktivit�tenstandorte" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$ACTIVITYLOCATION:ACTIVITYCODE;LOCATIONNO;ATTRACTIONPOTENTIAL" + NEW_LINE;
	}
	
	/**
	 * Generate household activity location row.
	 *
	 * @param household the household with a location to be logged
	 * @return the household activity location row for logging
	 */
	private String generateActivityLocationRow(BaseHousehold household) {
		return generateActivityLocationRow(household.homeLocation(), HOME, 0);
	}
	
	/**
	 * Generate activity location row for private visit.
	 *
	 * @param household the household with a location to be logged
	 * @return the household activity location private visit row for logging
	 */
	private String generateActivityLocationRowVisit(BaseHousehold household) {
		return generateActivityLocationRow(household.homeLocation(), PRIVATE_VISIT, 0);
	}
	
	/**
	 * Generate opportunity activity location row.
	 *
	 * @param opportunity the opportunity with a location to be logged
	 * @return the opportunity activity location row for logging
	 */
	private String generateActivityLocationRow(Opportunity opportunity) {
		return generateActivityLocationRow(opportunity.location(), opportunity.activityType(), opportunity.attractivity());
	}
	
	/**
	 * Generate zone centroid activity location row.
	 * The logged activity is 'transfer'.
	 *
	 * @param zone the zone with a centroid location for logging
	 * @return the centroid activity location row for logging
	 */
	private String generateActivityLocationRow(Zone zone) {
		return generateActivityLocationRow(zone.centroidLocation(), -1, 0);
	}
	
	/**
	 * Generate fixed destination activity location row.
	 *
	 * @param fixed the fixed destination with a location to be logged
	 * @return the fixed destination activity location row for logging
	 */
	private String generateActivityLocationRow(FixedDestination fixed) {
		return generateActivityLocationRow(fixed.location(), fixed.activityType(), 0);
	}
		
	/**
	 * Generate activity location row.
	 *
	 * @param location the location to be logged
	 * @param activity the activity to be logged
	 * @param attractivity the attractivity to be logged
	 * @return the activity location row for logging
	 */
	private String generateActivityLocationRow(Location location, ActivityType activity, int attractivity) {
		return generateActivityLocationRow(location, activity.getTypeAsInt(), attractivity);
	}
	
	/**
	 * Generate activity location row.
	 *
	 * @param location the location to be logged
	 * @param activity the activity to be logged
	 * @param attractivity the attractivity to be logged
	 * @return the activity location row for logging
	 */
	private String generateActivityLocationRow(Location location, int activity, int attractivity) {
		return activity + ";" + locationIdOf(location) + ";" + attractivity + NEW_LINE;
	}
	
	

	

	/**
	 * Generate household table header.
	 *
	 * @return the household table header
	 */
	private String generateHouseholdHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Haushalte" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$HOUSEHOLD:NO;RESIDENCEKEY;HHSIZE;INCOME;NRCAR;NRCH" + NEW_LINE;
	}
	
	/**
	 * Generate household row.
	 *
	 * @param household the household to be logged
	 * @return the household row for logging
	 */
	private String generateHouseholdRow(HouseholdForSetup household) {
		return 	  householdId(household) + SEP //NO
				+ "(" + HOME.getTypeAsInt() + "," + locationIdOf(household) + ")" + SEP //RESIDENCEKEY
				+ household.getSize() + SEP //HHSIZE
				+ household.monthlyIncomeEur() + SEP //INCOME
				+ household.getNumberOfOwnedCars() + SEP //NRCAR
				+ household.numberOfMinors() + NEW_LINE; //NRCH
	}	
	
	/**
	 * Generate person table header.
	 *
	 * @return the person table header
	 */
	private String generatePersonHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Personen" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$PERSON:NO;HOUSEHOLDNO;AGE;EMPL;HASTICKET;SEX" + NEW_LINE;
	}
	
	/**
	 * Generate person row.
	 *
	 * @param person the person to be logged
	 * @return the person row for logging
	 */
	private String generatePersonRow(BasePerson person) {
		return 	 personId(person) + SEP //NO
				+ householdId(person.household()) + SEP //HOUSEHOLDNO
				+ person.age() + SEP //AGE
				+ person.employment().toString().toUpperCase() + SEP //EMPL
				+ (person.hasCommuterTicket() ? 1 : 0) + SEP //HASTICKET
				+ person.gender().toString().toUpperCase() //SEX
				+ NEW_LINE; 
	}
	
	
	
	/**
	 * Generate long term choice table header.
	 *
	 * @return the long term choice table header
	 */
	private String generateLongTermChoiceHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Langfristige Entscheidungen" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$LONGTERMCHOICE:PERSONNO;ACTIVITYLOCATIONKEY" + NEW_LINE;
	}
	
	/**
	 * Generate long term choice row.
	 *
	 * @param person the person to be logged
	 * @param fixed the fixed destination to be logged
	 * @return the long term choice row for logging
	 */
	private String generateLongTermChoiceRow(BasePerson person, FixedDestination fixed) {
		return personId(person) + SEP  //PERSONNO
			+ "(" + fixed.activityType().getTypeAsInt() + "," + locationIdOf(fixed.location()) + ")" //ACTIVITYLOCATIONKEY
			+ NEW_LINE;
	}


	
	
	
	/**
	 * Generate daily schedule table header.
	 *
	 * @return the daily schedule table header
	 */
	private String generateDailyScheduleHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Tagespl�ne" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$SCHEDULE:PERSONNO;NO;DATE" + NEW_LINE;
	}
	
	/**
	 * Generate daily schedule row.
	 *
	 * @param person the person to be logged
	 * @return the person's daily schedule row for logging
	 */
	private String generateDailyScheduleRow(BasePerson person) {
		return personId(person) + SEP  //PERSONNO
			+  personId(person) + SEP  //NO
			+  1					   //DATE (always 'monday' but here monday has 168 hours)
			+  NEW_LINE;
	}
	


}

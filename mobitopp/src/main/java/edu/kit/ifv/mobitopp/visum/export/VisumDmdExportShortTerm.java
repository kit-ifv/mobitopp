package edu.kit.ifv.mobitopp.visum.export;

import static edu.kit.ifv.mobitopp.visum.export.VisumDmdIdProvider.locationIdOf;
import static edu.kit.ifv.mobitopp.visum.export.VisumDmdIdProvider.personId;
import static edu.kit.ifv.mobitopp.visum.export.WriterFactory.finishWriter;
import static edu.kit.ifv.mobitopp.visum.export.WriterFactory.getWriter;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.util.Comparator.comparing;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.PersonResults;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;
import edu.kit.ifv.mobitopp.simulation.StateChange;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.StartedTrip;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;
import edu.kit.ifv.mobitopp.time.DateFormat;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * VisumDmdExportShortTerm exports the short-term results in visum dmd format.<br>
 * It can be applied as {@link PersonListener} added to {@link PersonResults}.<br>
 * It writes activities, tours and trips. <br>
 * 
 * <br>Notes on how to apply:<br>
 * 1. Initialize export in SimulatorBuilder constructor and register as {@link PersonListener}: 
 * <pre>
 * public SimulatorBuilder(SimulationContext context) {
 * 	...
 * 	export = new VisumDmdExportShortTerm(context);
	export.init();
	context.personResults().addListener(export);
 * 	...
 * }
 * </pre>
 * 2. Provide public getter:
 * <pre>
 * public VisumDmdExportShortTerm getExport(){...}
 * </pre>
 * 3. In ShortTermModule save VisumDmdExportShortTerm before building the simulator:
 * <pre>
 * {@literal @}Override
 * protected DemandSimulator simulator() {
 * 		SimulatorBuilder builder = new SimulatorBuilder(context());
 * 		this.export = builder.getExport();
 * 		return builder.build();
 * }
 * </pre>
 * 4. Finish export in ShortTermModule after the simulation finished:
 * <pre>
 * {@literal @}Override
 * public void simulate() {
 * 		super.simulate();
 * 		export.finish();
 * }
 * </pre>
 */
public class VisumDmdExportShortTerm implements PersonListener {

	private static final String SEP = ";";
	private static final String NEW_LINE = "\r\n";

	/**
	 * Log the given finished trip
	 * 
	 * Log start of new tour if previous activity was haome.
	 * Collect legs of finished trip: if intermodal log legs and intermediate transfer activities.
	 * If the next activity is after end of simulation: log next activity.
	 * 
	 * @param person the person to be logged
	 * @param trip the trip to be logged
	 */
	@Override
	public void notifyEndTrip(Person person, FinishedTrip trip) {
		
		try {
			
			if (isTripFromHome(trip) || isFirstTripOf(person)) {
				tourWriter.write(generateTourRow(person, trip));
			}
			
		} catch (IOException e) {
			System.err.println("Could not log tour starting with " + String.valueOf(trip) + " of person" + String.valueOf(person));
			e.printStackTrace();
		}
		
		List<FinishedTrip> legs = collectLegs(trip);
			
		int i = 1;
		for (FinishedTrip leg : legs) {
			logTripRow(person, generateTripRow(person, leg));
			
			if (i < legs.size()) {
				logActivityRow(person, generateActivityRow(person, -1, leg.endDate(), 0, leg.destination().location()));
			}
			
			i++;
		}
		
		if (trip.plannedEndDate().isAfterOrEqualTo(Time.start.plusWeeks(1))) {
			logActivityRow(person, generateActivityRow(person, trip.nextActivity()));
		}
	}
	
	private boolean isFirstTripOf(Person person) {
		return !personTourIndex.containsKey(person.getOid());
	}

	private boolean isTripFromHome(FinishedTrip trip) {
		return trip.previousActivity().activityType().isHomeActivity();
	}

	/**
	 * Notify start trip: do nothing
	 *
	 * @param person the person
	 * @param trip the trip
	 */
	@Override
	public void notifyStartTrip(Person person, StartedTrip trip) {}

	/**
	 * Notify finish car trip: do nothing
	 *
	 * @param person the person
	 * @param car the car
	 * @param trip the trip
	 * @param activity the activity
	 */
	@Override
	public void notifyFinishCarTrip(Person person, Car car, FinishedTrip trip, ActivityIfc activity) {}

	/**
	 * Log the given activity.
	 *
	 * @param person the person to be logged
	 * @param activity the activity to be logged
	 */
	@Override
	public void notifyStartActivity(Person person, ActivityIfc activity) {

		logActivityRow(person, generateActivityRow(person, activity));

	}

	/**
	 * Notify select car route: do nothing
	 *
	 * @param person the person
	 * @param car the car
	 * @param trip the trip
	 * @param route the route
	 */
	@Override
	public void notifySelectCarRoute(Person person, Car car, TripData trip, Path route) {}

	/**
	 * Write subtourinfo to file: do nothing
	 *
	 * @param person the person
	 * @param tour the tour
	 * @param subtour the subtour
	 * @param tourMode the tour mode
	 */
	@Override
	public void writeSubtourinfoToFile(Person person, Tour tour, Subtour subtour, Mode tourMode) {}

	/**
	 * Write tourinfo to file: do nothing
	 *
	 * @param person the person
	 * @param tour the tour
	 * @param tourDestination the tour destination
	 * @param tourMode the tour mode
	 */
	@Override
	public void writeTourinfoToFile(Person person, Tour tour, Zone tourDestination, Mode tourMode) {}

	/**
	 * Notify state changed: do nothing
	 *
	 * @param stateChange the state change
	 */
	@Override
	public void notifyStateChanged(StateChange stateChange) {}

	/**
	 * Notify finish simulation: do nothing
	 */
	@Override
	public void notifyFinishSimulation() {}
	
	
	private final Writer tourWriter;
	private final Writer activityWriter;
	private final Writer tripsWriter;
	private final Map<Integer, Integer> personTourNo;
	private final Map<Integer, Integer> personTourIndex;
	private final Map<Integer, Integer> personActivityIndex;
	private final Map<Integer, String>  personActivityLogs;
	private final Map<Integer, String>  personTripLogs;
	
	private int tourNoCnt = 1;
	
	/**
	 * Instantiates a new short term visum dmd export.
	 *
	 * @param context the simulation context
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public VisumDmdExportShortTerm(SimulationContext context) throws IOException {
		
		File folder = new File(context.configuration().getResultFolder());
		
		this.tourWriter = getWriter(new File(folder, "tours.dmd"));
		this.activityWriter = getWriter(new File(folder, "activities.dmd"));
		this.tripsWriter = getWriter(new File(folder, "trips.dmd"));
		this.personTourNo = new HashMap<>();
		this.personTourIndex = new HashMap<>();
		this.personActivityIndex = new HashMap<>();
		this.personActivityLogs = new HashMap<>();
		this.personTripLogs = new HashMap<>();
	}
	
	/**
	 * Initializes the writers and logs the zones in the given context.
	 */
	public void init() {
		try {
			tourWriter.write(VisumDmdExportLongTerm.generateVersionHeader());
			tourWriter.write(NEW_LINE);
			tourWriter.write(generateTourHeader());
			activityWriter.write(NEW_LINE);
			activityWriter.write(generateActivityHeader());
			tripsWriter.write(NEW_LINE);
			tripsWriter.write(generateTripHeader());

		} catch (IOException e) {
			System.err.println("Could not init .dmd files");
		}

	}
	
	/**
	 * Finish export by closing all file writers.
	 * Before closing, write all activities and trips in correct order.
	 */
	public void finish() {
		writeActivitiesSorted();
		writeTripsSorted();
		try {
			finishWriter(tourWriter);		
			finishWriter(activityWriter);
			finishWriter(tripsWriter);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}
	
	/**
	 * Write activities sorted by person id.
	 */
	private void writeActivitiesSorted() {
		personActivityLogs.entrySet()
		  .stream()
		  .sorted(comparing(e -> e.getKey()))
		  .forEach(e -> {
			  
			  try {
				activityWriter.write(e.getValue());
			} catch (IOException e1) {
				System.err.println("Could not log activites of person " + e.getKey());
				e1.printStackTrace();
			}
			  
		  });
	}
	
	/**
	 * Write trips sorted by person id.
	 */
	private void writeTripsSorted() {
		personTripLogs.entrySet()
		  .stream()
		  .sorted(comparing(e -> e.getKey()))
		  .forEach(e -> {
			  
			  try {
				tripsWriter.write(e.getValue());
			} catch (IOException e1) {
				System.err.println("Could not log trips of person " + e.getKey());
				e1.printStackTrace();
			}
			  
		  });
	}
	
	/**
	 * Generate tour table header.
	 *
	 * @return the tour table header
	 */
	private String generateTourHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Touren" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$TOUR:PERSONNO;SCHEDULENO;NO;MAINDSEGCODE" + NEW_LINE;
	}
	

	
	/**
	 * Generate new tour row for the given trip.
	 *
	 * @param person the person to be logged
	 * @param trip the trip to be logged
	 * @return the tour row for logging
	 */
	private String generateTourRow(Person person, FinishedTrip trip) {
		int tourNo = tourNoCnt++;
		personTourNo.put(person.getOid(), tourNo);
		personTourIndex.put(person.getOid(), 1);
				
		return personId(person) + SEP //PERSONNO
			 + personId(person) + SEP //SCHEDULENO
			 + tourNo + SEP //NO
			 + mapModeToCode(trip.mode().mainMode()) //MAINDSEGCODE
			 + NEW_LINE;
		
	}

	/**
	 * Generate activity table header.
	 *
	 * @return the activity table header
	 */
	private String generateActivityHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Aktivit�tsaus�bungen" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$ACTIVITYEXECUTION:PERSONNO;SCHEDULENO;INDEX;STARTTIME;DURATION;ACTIVITYCODE;LOCATIONNO;STARTDAY;STARTTIMEDAY" + NEW_LINE;
	}
	
	/**
	 * Add the given activity row to the persons activity log
	 *
	 * @param person the person
	 * @param row the row to be logged
	 */
	private void logActivityRow(Person person, String row) {
		personActivityLogs.merge(person.getOid(), row, (s, r) -> s + r);
	}
	
	/**
	 * Generate activity row.
	 *
	 * @param person the person to be logged
	 * @param activity the activity to be logged
	 * @return the activity row for logging
	 */
	private String generateActivityRow(Person person, ActivityIfc activity) {
		return generateActivityRow(person, activity.activityType().getTypeAsInt(), activity.startDate(), activity.duration(), activity.location());
	}
	
	/**
	 * Generate activity row.
	 *
	 * @param person the person to be logged
	 * @param activityCode the activity code to be logged
	 * @param startDate the start date to be logged
	 * @param durationMin the duration min to be logged
	 * @param location the location to be logged
	 * @return the activity row for logging
	 */
	private String generateActivityRow(Person person, int activityCode, Time startDate, int durationMin, Location location) {
		int index = personActivityIndex.getOrDefault(person.getOid(), 1);
		personActivityIndex.put(person.getOid(), index + 1);
		
		if (startDate.isBefore(Time.start)) {
			int cutOff = abs(Time.start.differenceTo(startDate).toMinutes());
			durationMin = Math.max(durationMin-cutOff, 0);
			startDate = Time.start;
		}
		
		return personId(person) + SEP 	//PERSONNO
			 + personId(person) + SEP 	//SCHEDULENO
			 + index + SEP		//INDEX
			 + sumHourFormat(startDate) + SEP //STARTTIME
			 + max(0, durationMin * 60) + "s" + SEP 		  //DURATION
			 + activityCode + SEP //ACTIVITYCODE
			 + locationIdOf(location) + SEP //LOCATIONNO
			 + dayCode(startDate) + SEP 	//STARTDAY
			 + new DateFormat().asTime(startDate) 	//STARTTIMEDAY
			 + NEW_LINE; 
	}

	
	/**
	 * Generate trip table header.
	 *
	 * @return the trip table header
	 */
	private String generateTripHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Trips" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$TRIP:PERSONNO;SCHEDULENO;TOURNO;INDEX;SCHEDDEPTIME;DURATION;DSEGCODE;FROMACTIVITYEXECUTIONINDEX;TOACTIVITYEXECUTIONINDEX;STARTDAY;STARTTIMEDAY" + NEW_LINE;
	}
	
	/**
	 * Add the given trip row to the person's trip log
	 *
	 * @param person the person
	 * @param row the row to be logged
	 */
	private void logTripRow(Person person, String row) {
		personTripLogs.merge(person.getOid(), row, (s, r) -> s + r);
	}
	
	/**
	 * Generate trip row.
	 *
	 * @param person the person to be logged
	 * @param trip the trip to be logged
	 * @return the trip row for logging
	 */
	private String generateTripRow(Person person, FinishedTrip trip) {
//		personTourNo.putIfAbsent(person.getOid(), 1);
//		personTourIndex.putIfAbsent(person.getOid(), 1);
		
		int index = personTourIndex.get(person.getOid());
		personTourIndex.put(person.getOid(), index+1);
		
		int activityIndex = personActivityIndex.get(person.getOid());
		int durationMin = trip.plannedDuration();
		Time startDate = trip.startDate();
		if (startDate.isBefore(Time.start)) {
			int cutOff = abs(Time.start.differenceTo(startDate).toMinutes());
			durationMin = Math.max(durationMin-cutOff, 0);
			startDate = Time.start;
		}
		
		return personId(person) + SEP 	//PERSONNO
			 + personId(person) + SEP	//SCHEDULENO
			 + personTourNo.get(person.getOid()) + SEP //TOURNO
			 + index + SEP //INDEX
			 + sumHourFormat(startDate) + SEP //SCHEDDEPTIME
			 + max(0, durationMin*60) + "s" + SEP //DURATION
			 + mapModeToCode(trip.mode().legMode()) + SEP //DSEGCODE
			 + (activityIndex - 1) + SEP //FROMACTIVITYEXECUTIONINDEX
			 + activityIndex + SEP //TOACTIVITYEXECUTIONINDEX
			 + dayCode(startDate) + SEP 			//STARTDAY
			 + new DateFormat().asTime(startDate) 		//STARTTIMEDAY
			 + NEW_LINE;
	}
	
	/**
	 * Map mode to visum code.
	 *
	 * @param mode the mode to be mapped
	 * @return the visum code for the viven mode
	 */
	private String mapModeToCode(Mode mode) {
		switch (mode.forLogging()) {
			case "0": return "B";
			case "1": return "C_Pkw";
			case "2": return "CP";
			case "3": return "F_Fuss";
			case "4": return "X";
			case "7": return "TX";
			case "11": return "CS_S";
			case "12": return "CS_F";
			case "15": return "E";
			case "17": return "BS";
			case "21": return "M";
			default: return mode.forLogging();
		}
	}
	
	/**
	 * Collect the legs of the given trip.
	 *
	 * @param trip the trip
	 * @return the list of legs or a list containing the given trip if no legs are found
	 */
	private List<FinishedTrip> collectLegs(FinishedTrip trip) {
		List<FinishedTrip> legs = new ArrayList<>();
		
		trip.forEachFinishedLeg(legs::add);
		
		if (legs.isEmpty()) {
			return List.of(trip);
			
		} else if (legs.size() == 1) {
			return legs;
			
		}

		return legs.stream().flatMap(l -> collectLegs(l).stream()).collect(Collectors.toList());
	}
	
	
	/**
	 * Return cumulative hour format of the given time.
	 *
	 * @param time the time
	 * @return the cumulative hour format of the given time
	 */
	private String sumHourFormat(Time time) {
		return String.format("%02d:%02d:%02d", Math.floorDiv(time.toSeconds(), 60*60), time.getMinute() , time.getSecond());
	}
	
	/**
	 * Map time to day code.
	 *
	 * @param time the time to be mapped
	 * @return the day code of the given time
	 */
	private String dayCode(Time time) {
		switch (time.weekDay()) {
			case MONDAY:
				return "Mo.";
			case TUESDAY:
				return "Di.";
			case WEDNESDAY:
				return "Mi.";
			case THURSDAY:
				return "Do.";
			case FRIDAY:
				return "Fr.";
			case SATURDAY:
				return "Sa.";
			case SUNDAY:
				return "So.";
			default:
				return "??";
		}
	}
	
}

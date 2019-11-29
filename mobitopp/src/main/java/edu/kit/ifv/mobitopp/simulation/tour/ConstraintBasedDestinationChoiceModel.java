package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModelChoiceSet;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceUtilityFunction;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.logit.DefaultLogitModel;
import edu.kit.ifv.mobitopp.util.logit.LogitModel;

public class ConstraintBasedDestinationChoiceModel 
	implements DestinationChoiceModelChoiceSet
{
	
	protected final DestinationChoiceUtilityFunction utilityFunction;
	protected final ModeAvailabilityModel modeAvailabilityModel;
	protected final TimeConstraintReachableZonesFilter zonesFilter;
	
	private final LogitModel<Zone> logitModel = new DefaultLogitModel<Zone>();
	
	public ConstraintBasedDestinationChoiceModel(
		DestinationChoiceUtilityFunction destinationChoiceUtility,
		ModeAvailabilityModel modeAvailabilityModel,
		ImpedanceIfc impedance, double timeScalingFactor, int timeOffset
	) {
		this.utilityFunction = destinationChoiceUtility;
		this.modeAvailabilityModel = modeAvailabilityModel;
		this.zonesFilter = new TimeConstraintReachableZonesFilter(impedance, timeScalingFactor, timeOffset);
	}
	
	@Override
	public boolean isTourBased() {
		return true;
	}

	@Override
	public Zone selectDestination(
		Person person, 
		Optional<Mode> tourMode, 
		ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		Set<Zone> choiceSet, double randomNumber
	) {
		
		ActivityIfc activityWithKnownLocation = nextActivityWithKnownLocation(person, previousActivity, nextActivity);
		
		assert activityWithKnownLocation.isLocationSet() 
		|| activityWithKnownLocation.activityType().isHomeActivity() : 
			("\n" + previousActivity + "\n" + activityWithKnownLocation + "\n" + nextActivity);
		assert nextActivity != activityWithKnownLocation;
		
		Zone currentZone = previousActivity.zone();
		ActivityType activityType = nextActivity.activityType();
		
		assert tourMode != null;
		
		Set<Mode> availableModes = tourMode.isPresent()
															? Set.of(tourMode.get())
															: modeAvailabilityModel.availableModes(person, currentZone, previousActivity);
		
		Map<Zone,Set<Mode>> reachableZones = calculateReachableZones(
																	person,
																	previousActivity,
																	nextActivity,
																	activityWithKnownLocation,
																	availableModes,
																	choiceSet
																);
		
		assert !reachableZones.isEmpty();
		
		
		Map<Zone, Double> utilities = utilityFunction.calculateUtilities( 
				person,
				nextActivity,
				currentZone,
				reachableZones,
				activityType
				);

			return logitModel.select(utilities, randomNumber);
	}

	private ActivityIfc nextActivityWithKnownLocation(
		Person person, 
		ActivityIfc previousActivity,
		ActivityIfc nextActivity
	) {
		assert person.activitySchedule() instanceof TourAwareActivitySchedule;
		
		TourAwareActivitySchedule schedule = (TourAwareActivitySchedule) person.activitySchedule();
		
		Tour tour = schedule.correspondingTour(nextActivity);
		
		assert tour != null;
		
		
		ActivityIfc mainActivity = tour.mainActivity();
		ActivityIfc homeActivity = schedule.nextHomeActivity(previousActivity);
		
		assert homeActivity != null : ("\nprev: " +  previousActivity 
																		+ "\nnext: " + nextActivity
																		+ "\nmain: " + mainActivity
																		+ "\n" + schedule
																		+ "\n" + person.getId().getPersonNumber()
																		+ "\n" + person.getId()
																	);
		
		assert homeActivity.activityType().isHomeActivity() : homeActivity.activityType();
		
		if (!mainActivity.isLocationSet()) {
			return homeActivity;
		}
		
		assert mainActivity.isLocationSet();
		
		
		ActivityIfc nextActivityWithKnownLocation = nextActivity.startDate().isBefore(mainActivity.startDate()) 
																				? mainActivity : homeActivity;
		
		assert nextActivityWithKnownLocation.activityType().isHomeActivity()
				|| nextActivityWithKnownLocation.isLocationSet();
		
		return nextActivityWithKnownLocation;
	}

	private Map<Zone,Set<Mode>> calculateReachableZones(
		Person person, 
		ActivityIfc previousActivity, 
		ActivityIfc nextActivity,
		ActivityIfc mainActivity,
		Set<Mode> availableModes, 
		Set<Zone> zones
	) {
		
		assert previousActivity.isLocationSet();
		assert nextActivity != mainActivity: (previousActivity + "\n " + nextActivity + "\n" + mainActivity + "\n\n"
				+ person.activitySchedule()
				+ "\n" + ((PersonBuilder)person).getPatternActivityWeek());
		assert mainActivity.isLocationSet() || mainActivity.activityType().isHomeActivity() : 
			(previousActivity + "\n " + nextActivity + "\n" + mainActivity + "\n\n"
					+ person.activitySchedule()
					+ "\n" + ((PersonBuilder)person).getPatternActivityWeek());
		assert !nextActivity.isLocationSet();
		
		Zone currentZone = previousActivity.zone();
		Zone nextKnownZone = mainActivity.isLocationSet() ? mainActivity.zone() : person.household().homeZone();
		
		Time endOfCurrentActivity = previousActivity.calculatePlannedEndDate();
		
		int availableMinutes = Math.toIntExact(mainActivity.startDate().differenceTo(endOfCurrentActivity).toMinutes());
		
		if (availableMinutes <= 0) {
			Map<Zone,Set<Mode>> minimalZoneSet = new LinkedHashMap<Zone,Set<Mode>>();
			minimalZoneSet.put(currentZone, availableModes);
			minimalZoneSet.put(nextKnownZone, availableModes);
			
			return minimalZoneSet;
		}
		
		assert availableMinutes  > 0 : (availableMinutes + "," 
		+ endOfCurrentActivity + "," + mainActivity.startDate() 
		+ "\n" + previousActivity
		+ "\n" + nextActivity
		+ "\n" + mainActivity
		);
		
		// previousActivity.calculatePlannedEnd();
		// mainActivity.Begin();
		// verfügbare Reisezeit bestimmen
		
		// Anzahl/Dauer Zwischenaktivitäten bestimmen
		
		
		
		// korrigierte verfügbare Reisezeit bestimmen -(dauer Zwischenaktivitäten + x*Anzahl)
		
		// Für alle Zonen:
			// prüfen ob Zone mit einem der verfügbaren VM erreichbar ist
		
		List<ActivityIfc> activitiesBetween = activitiesBetween(person, previousActivity, mainActivity);
		
		assert activitiesBetween.size() > 0;
		assert activitiesBetween.contains(nextActivity) : ( "\n" + previousActivity + "\n" + nextActivity + "\n" + mainActivity
								+ "\n\n" + person.activitySchedule());
		
		int totalActivityTime = calculateTotalActivityTime(activitiesBetween);
		
		int additionalActivities = activitiesBetween.size()-1;
		
		final int ADDITIONAL_TRIP_TIME = 10;
		
		int minutesForTravel = Math.max(0, availableMinutes 
														- totalActivityTime 
														- additionalActivities * ADDITIONAL_TRIP_TIME);
		
		assert minutesForTravel >= 0 : (minutesForTravel + ", " + availableMinutes + ", " + totalActivityTime);

		
		return zonesFilter.filter(zones, person, currentZone, nextKnownZone, availableModes, 
																endOfCurrentActivity, minutesForTravel);
	}
	
	private List<ActivityIfc> activitiesBetween(
		Person person,
		ActivityIfc previousActivity, 
		ActivityIfc mainActivity
	) {
		return person.activitySchedule().activitiesBetween(previousActivity, mainActivity);
	}

	private int calculateTotalActivityTime(List<ActivityIfc> activitiesBetween) {
	
		int total = 0;
		
		for(ActivityIfc act : activitiesBetween){
			total += act.duration();
		}
		
		int total_x = activitiesBetween.stream().mapToInt(ActivityIfc::duration).sum();
		
		assert total == total_x;
		
		return total;
	}
}

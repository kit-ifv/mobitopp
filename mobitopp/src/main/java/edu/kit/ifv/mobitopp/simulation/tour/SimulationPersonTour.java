package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportBehaviour;
import edu.kit.ifv.mobitopp.simulation.person.SimulationOptions;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPersonPassenger;
import edu.kit.ifv.mobitopp.simulation.person.TripFactory;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;
import edu.kit.ifv.mobitopp.simulation.tour.TourAwareActivitySchedule;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;



public class SimulationPersonTour
	extends SimulationPersonPassenger
	implements SimulationPerson
{


	private static final long serialVersionUID = 1L;



	public SimulationPersonTour(
		Person person,
		ZoneRepository zoneRepository,
		EventQueue queue,
		SimulationOptions options,
		List<Time> simulationDays,
		Set<Mode> modesInSimulation,
		TourFactory tourFactory,
		TripFactory tripFactory,
		PersonState initialState,
		PublicTransportBehaviour publicTransportBehaviour,
		long seed, 
		PersonListener listener
	) {
		super(person, queue, options, simulationDays, modesInSimulation, tourFactory, tripFactory, initialState, publicTransportBehaviour, seed, listener);
	}





	public void selectDestinationAndMode(
		DestinationChoiceModel destinationChoiceModel,
		TourBasedModeChoiceModel modeChoiceModel,
		ImpedanceIfc impedance,
		boolean passengerAsOption
	) {
		
		assert isTourBasedSimulation(destinationChoiceModel, modeChoiceModel);
		
		ActivityIfc previousActivity = currentActivity();
		ActivityIfc nextActivity = nextActivity();
		
		if (isStartOfTour(previousActivity, nextActivity)) {
			
			selectTourDestinationAndTourMode(destinationChoiceModel, modeChoiceModel, impedance, passengerAsOption,
					previousActivity, nextActivity);
		}
		
		Tour tour = correspondingTour(nextActivity);
		
		assert tour != null;
		assert tour.mode() != null : (tour + "\n" + previousActivity + "\n" + nextActivity
																	+ "\n" + isStartOfTour(previousActivity, nextActivity));
	
		Optional<Mode> tourMode =  Optional.of(correspondingTour(nextActivity).mode());
		
		ZoneAndLocation destination = selectAndSetDestinationOfActivity(destinationChoiceModel, previousActivity, nextActivity, tourMode);
		
		assert nextActivity.isLocationSet();
		assert ((TourAwareActivitySchedule)activitySchedule()).correspondingTour(nextActivity).mainActivity().isLocationSet();
		
		selectModeAndCreateTrip(modeChoiceModel, impedance, passengerAsOption, previousActivity, nextActivity, destination.zone);
	}


	private void selectTourDestinationAndTourMode(
		DestinationChoiceModel destinationChoiceModel,
		TourBasedModeChoiceModel modeChoiceModel, 
		ImpedanceIfc impedance, 
		boolean passengerAsOption, 
		ActivityIfc previousActivity,
		ActivityIfc nextActivity
	) {
		Tour tour = correspondingTour(nextActivity);
		
		assert previousActivity.activityType().isHomeActivity(); // Methode sollte nur zu Beginn der Tour aufgerufen werden
		assert nextActivity != null;
		
		assert destinationChoiceModel.isTourBased();
		
		ActivityIfc mainActivity = tour.mainActivity(); 
		
		assert mainActivity != null;
		
		ZoneAndLocation tourDestination = selectAndSetDestinationForMainActivity(tour, previousActivity, mainActivity, destinationChoiceModel, impedance);
		Mode tourMode = selectAndSetTourMode(tour, previousActivity, mainActivity, tourDestination, modeChoiceModel, passengerAsOption, impedance);
		
		listener.writeTourinfoToFile(person(), tour, tourDestination.zone, tourMode);
	}
	
	private ZoneAndLocation selectAndSetDestinationForMainActivity(
		Tour tour, 
		ActivityIfc homeActivity,
		ActivityIfc mainActivity, 
		DestinationChoiceModel destinationChoiceModel, 
		ImpedanceIfc impedance
	) {
		
		assert mainActivity != null;
		
		ZoneAndLocation destination;
		
		if (mainActivity.isLocationSet()) {
			destination = mainActivity.zoneAndLocation();
		} else {
			destination = selectAndSetDestinationOfActivity(destinationChoiceModel, homeActivity, mainActivity, Optional.empty());

			setLocationForOtherPartsOfMainActivity(tour, mainActivity, destination);
		}
		
		assert mainActivity.isLocationSet();
		
		return destination;
	}


	private void setLocationForOtherPartsOfMainActivity(
		Tour tour, 
		ActivityIfc mainActivity,
		ZoneAndLocation destination
	) {

		if(tour.containsSubtour()) {
			for(int i=0; i < tour.numberOfSubtours(); i++) {
				Subtour subtour = tour.nthSubtour(i);
					
				ActivityIfc last = subtour.lastActivity();
				ActivityIfc before = person().activitySchedule().prevActivity(subtour.firstActivity());
				
				assert last.activityType() == mainActivity.activityType() || subtour.purpose()==ActivityType.LEISURE_WALK;
				assert before.activityType() == mainActivity.activityType() || subtour.purpose()==ActivityType.LEISURE_WALK;
				
				if ( last.activityType() == mainActivity.activityType()
							&& before.activityType() == mainActivity.activityType() ) { // echte Subtour
					
					if (mainActivity != last && !last.isLocationSet()) {
						last.setLocation(destination);
					}
					if (mainActivity != before && !before.isLocationSet()) {
						before.setLocation(destination);
					}
				} else { // Spazierweg als Subtour
					assert subtour.purpose()==ActivityType.LEISURE_WALK;
				}
			}
		}
	}


	private Tour correspondingTour(ActivityIfc nextActivity) {
		TourAwareActivitySchedule schedule = (TourAwareActivitySchedule) activitySchedule();
		Tour tour = schedule.correspondingTour(nextActivity);
		return tour;
	}
	
	
	private boolean isTourBasedSimulation(DestinationChoiceModel destinationChoiceModel, TourBasedModeChoiceModel modeChoiceModel) {
	
		return destinationChoiceModel.isTourBased() && modeChoiceModel.isTourBased();
	}


	private Mode selectAndSetTourMode(
		Tour tour, 
		ActivityIfc previousActivity, 
		ActivityIfc nextActivity,
		ZoneAndLocation tourDestination,
		TourBasedModeChoiceModel modeChoiceModel, 
		boolean passengerAsOption,
		ImpedanceIfc impedance
	) {
		Set<Mode> choiceSet = new LinkedHashSet<Mode>(this.modesInSimulation);
		if (!passengerAsOption) {
			choiceSet.remove(StandardMode.PASSENGER);
		}
		
		Mode mode = modeChoiceModel.selectMode( tour,
																							person(), 
																							choiceSet,
																							this.getNextRandom()
																					);
		
		assert mode != null;
		
		assert tour.mode() ==  null;
		
		tour.firstActivity().setMode(mode);
		
		assert tour.mode() !=  null;
		
		return mode;
	}


	private boolean isStartOfTour(
		ActivityIfc previousActivity,
		ActivityIfc nextActivity	
	) {
		return previousActivity.activityType().isHomeActivity();
	}
	
	
	private ZoneAndLocation selectAndSetDestinationOfActivity(
		DestinationChoiceModel destinationChoiceModel,
		ActivityIfc previousActivity, 
		ActivityIfc nextActivity, 
		Optional<Mode> tourMode
	) {
		
		if (nextActivity.isLocationSet()) {
			return nextActivity.zoneAndLocation();
		}
		
		assert !nextActivity.isLocationSet();
		
		// Wenn Spazierweg sich in Subtour der Länge=1 bewfindet,
		// DANN: Ziel des Spaziergwegs ist aktuelle Position
	
		if (nextActivity.activityType()==ActivityType.LEISURE_WALK 
				&& correspondingTour(nextActivity).isInSubtour(nextActivity)
				&& correspondingTour(nextActivity).correspondingSubtour(nextActivity).get().numberOfTrips()==1) {
			
			nextActivity.setLocation(previousActivity.zoneAndLocation());
			
			return previousActivity.zoneAndLocation();
		}
		
		Zone destination = destinationChoiceModel.selectDestination(
																																	person(), 
																																	tourMode,
																																	previousActivity, 
																																	nextActivity,
																																	this.getNextRandom()
																																);  

		Location location = selectLocation(person(), nextActivity, destination);

		nextActivity.setLocation(new ZoneAndLocation(destination, location));

		assert nextActivity.isLocationSet();
		return nextActivity.zoneAndLocation();
	}


	private void selectModeAndCreateTrip(
		TourBasedModeChoiceModel modeChoiceModel, 
		ImpedanceIfc impedance, 
		boolean passengerAsOption,
		ActivityIfc previousActivity, 
		ActivityIfc nextActivity, 
		Zone destination
	) {
		
		Set<Mode> choiceSet = new LinkedHashSet<Mode>(this.modesInSimulation);
		if (!passengerAsOption) {
			choiceSet.remove(StandardMode.PASSENGER);
		}
		
		Zone origin = previousActivity.zone();
		
		Tour tour = correspondingTour(nextActivity);
		Mode tourMode = tour.mode();
		
		assert tour.firstActivity().isModeSet();
		assert isFirstActivityOfTour(nextActivity, tour) || !nextActivity.isModeSet();
		assert nextActivity != tour.firstActivity() || nextActivity.isModeSet();
		
		//  Für erste Aktivität gilt mode == tourMode
		Mode mode =  isFirstActivityOfTour(nextActivity, tour) 
																				? tour.mode()
																				: modeChoiceModel.selectMode( 
																							tour,
																							tourMode,
																							person(), 
																							origin,
																							destination,
																							previousActivity, 
																							nextActivity,
																							choiceSet,
																							this.getNextRandom()
																					);
		
		assert mode != null;
		
		Trip trip = createTrip(
																							impedance,
																							mode, 
																							previousActivity, 
																							nextActivity
																						);

		currentTrip(trip);
	}


	protected boolean isFirstActivityOfTour(ActivityIfc nextActivity, Tour tour) {
		return nextActivity == tour.firstActivity();
	}


	protected boolean isTourbased() {
		return true;
	}

	

}

package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;
import edu.kit.ifv.mobitopp.simulation.StandardChoiceSet;
import edu.kit.ifv.mobitopp.simulation.TripfileWriter;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;


public class TourModeChoiceModelWithTimeRestrictions 
	implements TourBasedModeChoiceModel
{
	
	final ImpedanceIfc impedance;
	private final TourOnlyModeChoiceModel tourModeChoiceModel;
	private final WithinTourModeChoiceModel withinTourModeChoiceModel;
	private final SubtourModeChoiceModel subtourModeChoiceModel;
	
	private final FeasibleModesModel feasibleModesModel;
	
	private final SimulationContext context;

	public TourModeChoiceModelWithTimeRestrictions(
		TourOnlyModeChoiceModel tourModeChoiceModel, 
		WithinTourModeChoiceModel withinTourModeChoiceModel, 
		SubtourModeChoiceModel subtourModeChoiceModel,
		FeasibleModesModel feasibleModesModel,
		ImpedanceIfc impedance, 
		SimulationContext context
	) {
		this.tourModeChoiceModel = tourModeChoiceModel;
		
		this.impedance = impedance;
		this.feasibleModesModel = feasibleModesModel;
		
		this.withinTourModeChoiceModel = withinTourModeChoiceModel;
		this.subtourModeChoiceModel = subtourModeChoiceModel;
		
		this.context = context;
	}
	

	@Override
	public Mode selectMode(
		Tour tour, 
		Mode tourMode, 
		Person person, 
		Zone source, 
		Zone destination,
		ActivityIfc previousActivity, 
		ActivityIfc nextActivity, 
		Set<Mode> choiceSet, 
		double randomNumber
	) {
		assert !choiceSet.isEmpty();
		assert tour != null;
		
		if (!tour.containsSubtour() || !tour.isInSubtour(nextActivity)) {
			if (choiceSet.size() == 1) {
				return singleElement(choiceSet); 
			}
			
			Set<Mode> currentChoiceSet = new LinkedHashSet<Mode>(choiceSet);
			
			if (tour.containsSubtour() & tour.isEndOfSubtour(previousActivity)) {
				
				Subtour subtour = tour.correspondingSubtour(previousActivity).get();
				
				context.personResults().writeSubourinfoToFile(person, tour, subtour, tourMode);
			
				if (!tourMode.isFlexible()) {
					currentChoiceSet = Mode.exclusive(tourMode);
				}
			}

			Set<Mode> feasibleChoiceSet = feasibleModesModel.feasibleModes(previousActivity, nextActivity, 0, currentChoiceSet);
			
			return withinTourModeChoiceModel.selectMode(tour, tourMode, person, source, destination, previousActivity, nextActivity, feasibleChoiceSet, randomNumber);
		}
		
		
		assert tour.containsSubtour();
		assert tour.contains(nextActivity);
		assert tour.isInSubtour(nextActivity);
		
		Set<Mode> augmentedChoiceSet = new LinkedHashSet<Mode>(choiceSet);

		if (tour.isStartOfSubtour(nextActivity)) {
			
			augmentedChoiceSet.addAll(StandardChoiceSet.CHOICE_SET_FLEXIBLE);
		}
		
		Set<Mode> feasibleChoiceSet = feasibleModesModel.feasibleModes(previousActivity, nextActivity, 0, augmentedChoiceSet);

		return subtourModeChoiceModel.selectMode(tour, tourMode, person, source, destination, previousActivity, nextActivity, feasibleChoiceSet, randomNumber);
	}

	@Override
	public Mode selectMode(
		Tour tour, 
		Person person, 
		Map<Mode, Double> preferences, 
		Set<Mode> choiceSet,
		double randomNumber
	) {
		assert !choiceSet.isEmpty();
		
		if (choiceSet.size() == 1) {
			return singleElement(choiceSet);
		}
		
		TourAwareActivitySchedule activitySchedule = (TourAwareActivitySchedule) person.activitySchedule();
		ActivityIfc previousActivity = activitySchedule.prevActivity(tour.firstActivity());
	
		Set<Mode> feasibleChoiceSet = feasibleModesModel.feasibleModes(previousActivity, tour, choiceSet);
		
		return tourModeChoiceModel.selectMode(tour, person, preferences, feasibleChoiceSet, randomNumber);
	}
	
	private Mode singleElement(Set<Mode> modes) {
		assert modes.size() == 1;
		
		return modes.iterator().next();
	}


	@Override
	public boolean isTourBased() {
	
		return true;
	}
	
}

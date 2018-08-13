package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoiceModel;

public class TourBasedModeChoiceModelDummy implements TourBasedModeChoiceModel {
	
	ModeChoiceModel modeChoiceModel;
	
	public TourBasedModeChoiceModelDummy(ModeChoiceModel modeChoiceModel) {
		this.modeChoiceModel = modeChoiceModel;
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
		double randomNumber) {
		
		return modeChoiceModel.selectMode(person, source, destination, previousActivity, nextActivity, choiceSet, randomNumber);
	}

	@Override
	public Mode selectMode(Tour tour, Person person, Map<Mode, Double> preferences, Set<Mode> choiceSet,
			double randomNumber) {
		
		throw new AssertionError("Must not be called!");
	}

	@Override
	public boolean isTourBased() {
		
		return false;
	}

}

package edu.kit.ifv.mobitopp.simulation.tour;

public interface TourBasedModeChoiceModel 
	extends WithinTourModeChoiceModel, TourOnlyModeChoiceModel 
{

	boolean isTourBased();
}

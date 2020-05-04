package edu.kit.ifv.mobitopp.simulation.modeChoice;


import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.CarSharingCategories;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceModelWithTimeRestrictions;

public class ModeChoiceModelUsingAvailableModes
	implements TourBasedModeChoiceModel
{

	private	TourBasedModeChoiceModel modeChoiceModel;

	protected final ModeAvailabilityModel modeAvailabilityModel;
	private final Results results;
	private final CarSharingCategories categories;

	public ModeChoiceModelUsingAvailableModes(
		ModeAvailabilityModel modeAvailabilityModel,
		TourBasedModeChoiceModel modeChoiceModel, 
		Results results
	) {
		this.modeAvailabilityModel = modeAvailabilityModel;
		this.modeChoiceModel =  modeChoiceModel;
		this.results = results;
		categories = new CarSharingCategories();
	}

	public boolean isTourBased() {
		return this.modeChoiceModel instanceof TourModeChoiceModelWithTimeRestrictions;
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
		assert previousActivity.isLocationSet();
		assert nextActivity.isLocationSet();

		Set<Mode> availableModes = this.modeAvailabilityModel.filterAvailableModes(
																											person,
																											source,
																											destination,
																											previousActivity,
																											nextActivity,
																											choiceSet
																									);


		Mode selectedMode = selectMode(tour, tourMode, person, source, destination, previousActivity,
				nextActivity, randomNumber, availableModes);

		if (selectedMode == StandardMode.CARSHARING_FREE) {
			String message = "person: " + person.getOid();
			message += "\n Zone: " + source.getId().getExternalId();
			message += "\n selected mode: " + selectedMode;
			message += "\n available modes: " + availableModes;
			message += "\n car available: "
					+ source.carSharing().isFreeFloatingCarSharingCarAvailable(person);
			message += "\n is driver: " + person.isCarDriver();
			results().write(categories.carsharing, message);
		}

		return selectedMode;
	}

	private Mode selectMode(
			Tour tour, Mode tourMode, Person person, Zone source, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, double randomNumber,
			Set<Mode> availableModes) {
		if (1 == availableModes.size()) {
			return availableModes.iterator().next();
		}
		return this.modeChoiceModel.selectMode(
																									tour,
																									tourMode,
																									person, 
																									source,
																									destination,
																									previousActivity, 
																									nextActivity,
																									availableModes,
																									randomNumber
																								);
	}

	private Results results() {
		return results;
	}

	@Override
	public Mode selectMode(
		Tour tour, 
		Person person, 
		Map<Mode, Double> preferences, 
		Set<Mode> choiceSet,
		double randomNumber
		) {
		
		ActivityIfc previousActivity = person.activitySchedule().prevActivity(tour.firstActivity());
		
		ActivityIfc nextActivity = tour.mainActivity();
		
		assert previousActivity.isLocationSet();
		assert nextActivity.isLocationSet();
		
		Zone source = previousActivity.zone();
		Zone destination = nextActivity.zone();

		Set<Mode> availableModes = this.modeAvailabilityModel.filterAvailableModes(
																											person,
																											source,
																											destination,
																											previousActivity,
																											nextActivity,
																											choiceSet
																									);
		

		return modeChoiceModel.selectMode(tour, person, preferences, availableModes, randomNumber);
	}

}

package edu.kit.ifv.mobitopp.simulation.modeChoice;


import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.simulation.CarSharingCategories;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;
import edu.kit.ifv.mobitopp.simulation.tour.TourModeChoiceModelWithTimeRestrictions;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;

import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingPerson;

public class ModeChoiceModelUsingAvailableModes
	implements TourBasedModeChoiceModel
{

	private	TourBasedModeChoiceModel modeChoiceModel;

	protected final ModeAvailabilityModel modeAvailabilityModel;
	private final ResultWriter results;
	private final CarSharingCategories categories;

	public ModeChoiceModelUsingAvailableModes(
		ModeAvailabilityModel modeAvailabilityModel,
		TourBasedModeChoiceModel modeChoiceModel, 
		ResultWriter results
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


		Mode selectedMode = this.modeChoiceModel.selectMode(
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

		if (selectedMode == Mode.CARSHARING_FREE) {
			String message = "person: " + person.getOid();
			message += "\n Zone: " + source.getInternalId().getExternalId();
			message += "\n selected mode: " + selectedMode;
			message += "\n available modes: " + availableModes;
			message += "\n car available: " 
					+ source.carSharing().isFreeFloatingCarSharingCarAvailable((CarSharingPerson)person);
			message += "\n is driver: " + person.isCarDriver();
			results().write(categories.carsharing, message);
		}

		return selectedMode;
	}

	private ResultWriter results() {
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

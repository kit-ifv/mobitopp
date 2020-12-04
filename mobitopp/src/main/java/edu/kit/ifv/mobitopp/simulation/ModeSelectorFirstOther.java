package edu.kit.ifv.mobitopp.simulation;

import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoiceModel;
import edu.kit.ifv.mobitopp.time.DayOfWeek;


public class ModeSelectorFirstOther
	implements ModeChoiceModel
{

	private	ModeChoiceModel modeSelectorFirst;
	private ModeChoiceModel modeSelectorOther;

	protected final ModeAvailabilityModel modeAvailabilityModel;

	public ModeSelectorFirstOther(
		ModeAvailabilityModel modeAvailabilityModel,
		ModeChoiceModel modeSelectorFirst,
		ModeChoiceModel modeSelectorOther
	) {
		this.modeSelectorFirst =  modeSelectorFirst;
		this.modeSelectorOther = modeSelectorOther;

		this.modeAvailabilityModel = modeAvailabilityModel;
	}


	public Mode selectMode(
		Person person,
		Zone source,
		Zone target,
		ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		Set<Mode> choiceSet,
		double randomNumber
	) {

		Set<Mode> availableModes = this.modeAvailabilityModel.filterAvailableModes(
																											person,
																											source,
																											target,
																											previousActivity,
																											nextActivity,
																											choiceSet
																									);


		if (isFirstActivityOfWeek(previousActivity)) {

			return this.modeSelectorFirst.selectMode(
																									person, 
																									source,
																									target,
																									previousActivity, 
																									nextActivity,
																									availableModes,
																									randomNumber
																								);
		} else {

			return this.modeSelectorOther.selectMode(
																									person, 
																									source,
																									target,
																									previousActivity, 
																									nextActivity,
																									availableModes,
																									randomNumber
																								);
		}
	}


	private boolean isFirstActivityOfWeek(
		ActivityIfc activity
	) {

		return activity.startDate().weekDay() == DayOfWeek.MONDAY 
						&& activity.startDate().isMidnight();
	}

}

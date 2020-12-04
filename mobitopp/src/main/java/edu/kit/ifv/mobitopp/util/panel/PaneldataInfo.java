package edu.kit.ifv.mobitopp.util.panel;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class PaneldataInfo {

	public HouseholdInfo household	= new HouseholdInfo();
	public PersonInfo	person	= new PersonInfo();

	public List<Integer> sequence_of_day_types = new ArrayList<>();
	public List<ActivityOfPanelData> activity_pattern = new ArrayList<>();

	public String toString() {

		return getClass().getName()
					+ ": "
					+ household.toString()
					+ "; "
					+ person.toString() ;
	}


	public void printActivities() {

		String person_info = household.household_number 
												+ ", " +  household.year
												+ ", " +  person.person_number
												+ ", " +  household.area_type
												+ ", " +  household.household_size
												+ ", " +  household.domcode
												+ ", " +  household.additionalchildren
												+ ", " +  household.cars
												+ ", " +  person.sex
												+ ", " +  person.birthYear
												+ ", " +  person.employmentType
												+ ", " +  person.distanceWork
												+ ", " +  person.distanceEducation
												;
		String data;

		int cnt = 1;

		for (ActivityOfPanelData pattern: activity_pattern) {

			data = person_info 
						+ ", " + (cnt++)
						+ ", " + pattern.getObservedTripDuration()
						+ ", " + pattern.getActivityTypeAsInt()
						+ ", " + pattern.getStarttime()
						+ ", " + pattern.getDuration() ;

			log.info(data);
		}
	}

}

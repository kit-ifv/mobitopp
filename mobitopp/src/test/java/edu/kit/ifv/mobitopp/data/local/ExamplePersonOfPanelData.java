package edu.kit.ifv.mobitopp.data.local;

import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

public class ExamplePersonOfPanelData {

	public static final int aNumber = 1;
	public static final int otherNumber = 2;
	public static final PersonOfPanelDataId anId = new PersonOfPanelDataId(
			ExampleHouseholdOfPanelData.anId, aNumber);
	public static final PersonOfPanelDataId otherId = new PersonOfPanelDataId(
			ExampleHouseholdOfPanelData.otherId, otherNumber);
	public static final int genderType = 1;
	public static final int birthyear = 4;
	public static final int age = 5;
	public static final int employmentType = 6;
	public static final float poleDistance = 7;
	public static final boolean commuterTicket = false;
	public static final boolean bicycle = true;
	public static final boolean personalCar = false;
	public static final boolean carAvailable = true;
	public static final float weight = 8;
	public static final Integer income = 9;
	public static final String activityPattern = "pattern";
	public static final PersonOfPanelData aPerson = person(anId);
	public static final PersonOfPanelData otherPerson = person(otherId);

	public static PersonOfPanelData person(PersonOfPanelDataId id) {
		return new PersonOfPanelData(id, genderType, birthyear, age, employmentType, poleDistance,
				commuterTicket, bicycle, personalCar, carAvailable, weight, income, activityPattern);
	}
	
	public static PersonOfPanelData personWithGender(int gender) {
		return new PersonOfPanelData(anId, gender, birthyear, age, employmentType, poleDistance,
				commuterTicket, bicycle, personalCar, carAvailable, weight, income, activityPattern);
	}
}

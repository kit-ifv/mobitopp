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
	private static final int graduation = -1;
	public static final int age = 25;
	public static final int birthyear = ExampleHouseholdOfPanelData.year - age;
	public static final int employmentType = 6;
	public static final float poleDistance = 7;
	public static final boolean commuterTicket = false;
	public static final boolean bicycle = true;
	public static final boolean personalCar = false;
	public static final boolean carAvailable = true;
	public static final boolean hasLicense = true;
	public static final Integer income = ExampleHouseholdOfPanelData.income;
	public static final String activityPattern = "-1;7;460;-1;15;1;275;475;12;7;278;-1";
	public static final PersonOfPanelData aPerson = person(anId);
	public static final PersonOfPanelData otherPerson = person(otherId);
	public static float pref_cardriver = 0.5f;
	public static float pref_carpassenger = 0.1f;
	public static float pref_walking = 0.1f;
	public static float pref_cycling = 0.1f;
	public static float pref_publictransport = 0.1f;

	public static PersonOfPanelData person(PersonOfPanelDataId id) {
		return createPersonWith(id, activityPattern);
	}

	public static PersonOfPanelData createPersonWith(
			PersonOfPanelDataId id, String activityPattern) {
		return new PersonOfPanelData(id, genderType, graduation, birthyear, age, employmentType, poleDistance,
				commuterTicket, bicycle, personalCar, carAvailable, hasLicense, income,
				activityPattern, pref_cardriver, pref_carpassenger, pref_walking, pref_cycling,
				pref_publictransport);
	}

	public static PersonOfPanelData personWithGender(int gender) {
		return new PersonOfPanelData(anId, gender, graduation, birthyear, age, employmentType, poleDistance,
				commuterTicket, bicycle, personalCar, carAvailable, hasLicense, income,
				activityPattern, pref_cardriver, pref_carpassenger, pref_walking, pref_cycling,
				pref_publictransport);
	}
	
	public static PersonOfPanelData personWithPattern(String pattern) {
		return new PersonOfPanelData(anId, genderType, graduation, birthyear, age, employmentType, poleDistance,
				commuterTicket, bicycle, personalCar, carAvailable, hasLicense, income, pattern,
				pref_cardriver, pref_carpassenger, pref_walking, pref_cycling, pref_publictransport);
	}
}

package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

public class PersonOfPanelDataBuilder {

  public static final int aNumber = 1;
  public static final int otherNumber = 2;
  public static final PersonOfPanelDataId anId = new PersonOfPanelDataId(
      ExampleHouseholdOfPanelData.anId, aNumber);
  public static final PersonOfPanelDataId otherId = new PersonOfPanelDataId(
      ExampleHouseholdOfPanelData.otherId, otherNumber);
  public static final int genderMale = 1;
  public static final int genderFemale = 2;
  public static final int defaultGender = genderMale;
  public static final int defaultBirthyear = 4;
  public static final int defaultAge = 5;
  public static final int defaultEmployment = 6;
  public static final float defaultPoleDistance = 7;
  public static final boolean defaultCommuterTicket = false;
  public static final boolean defaultBicycle = true;
  public static final boolean defaultPersonalCar = false;
  public static final boolean defaultCarAvailable = true;
  public static final boolean defaultHasLicense = true;
  public static final float defaultWeight = 8;
  public static final Integer defaultIncome = 9;
  public static final String defaultActivityPattern = "pattern";
  public static final float defaultPreferenceCardriver = 0.5f;
  public static final float defaultPreferenceCarpassenger = 0.1f;
  public static final float defaultPreferenceWalking = 0.1f;
  public static final float defaultPrefCycling = 0.1f;
  public static final float defaultPrefPublictransport = 0.1f;

  private PersonOfPanelDataId id;
  private int gender;
  private int age;
  private int income;
  private float poleDistance;

  public PersonOfPanelDataBuilder() {
    id = anId;
    gender = defaultGender;
    age = defaultAge;
    income = defaultIncome;
    poleDistance = defaultPoleDistance;
  }

  public static PersonOfPanelDataBuilder personOfPanelData() {
    return new PersonOfPanelDataBuilder();
  }

  public PersonOfPanelDataBuilder withId(PersonOfPanelDataId id) {
    this.id = id;
    return this;
  }

  public PersonOfPanelDataBuilder withGender(int gender) {
    this.gender = gender;
    return this;
  }

  public PersonOfPanelDataBuilder withAge(int age) {
    this.age = age;
    return this;
  }

  public PersonOfPanelDataBuilder withIncome(int income) {
    this.income = income;
    return this;
  }
  
  public PersonOfPanelDataBuilder withPoleDistance(float poleDistance) {
    this.poleDistance = poleDistance;
    return this;
  }

  public PersonOfPanelData build() {
    return new PersonOfPanelData(id, gender, defaultBirthyear, age, defaultEmployment,
        poleDistance, defaultCommuterTicket, defaultBicycle, defaultPersonalCar,
        defaultCarAvailable, defaultHasLicense, defaultWeight, income,
        defaultActivityPattern, defaultPreferenceCardriver, defaultPreferenceCarpassenger,
        defaultPreferenceWalking, defaultPrefCycling, defaultPrefPublictransport);
  }


}

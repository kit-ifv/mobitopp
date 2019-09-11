package edu.kit.ifv.mobitopp.util.panel;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Graduation;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class PersonOfPanelData implements Comparable<PersonOfPanelData> {

	@NonNull
	private final PersonOfPanelDataId id;

	private final byte employmentType;
	private final byte genderType;
	private final byte graduation;

	private final short birthyear;
	private final short age;
	@NonNull
	private final String activityPattern;
	private final int income;
	private final float distanceWork;
	private final float distanceEducation;
	@Getter(AccessLevel.NONE)
	private final boolean commuterTicket;
	@Getter(AccessLevel.NONE)
	private final boolean licence;
	@Getter(AccessLevel.NONE)
	private final boolean bicycle;
	@Getter(AccessLevel.NONE)
	private final boolean personalCar;
	@Getter(AccessLevel.NONE)
	private final boolean carAvailable;

	public final float pref_cardriver;
	public final float pref_carpassenger;
	public final float pref_walking;
	public final float pref_cycling;
	public final float pref_publictransport;

	public PersonOfPanelData(
		PersonOfPanelDataId personId,
		int genderType,
		int graduation,
		int  birthyear,
		int  age,
		int employmentType,
		float distance_work,
		float distance_education,
		boolean commuterTicket,
		boolean licence,
		boolean bicycle,
		boolean personalCar,
		boolean carAvailable,
		int income,
		String activityPattern,
		float pref_cardriver,
		float pref_carpassenger,
		float pref_walking,
		float pref_cycling,
		float pref_publictransport
	) {
		verifyId(personId);
		verifyAge(age);
		verifyGender(genderType);

		this.id = personId;
		this.genderType = (byte) genderType;
		this.graduation = (byte) graduation;
		this.birthyear = (short) birthyear;
		this.age = (short) age;
		this.employmentType = (byte) employmentType;
		this.distanceWork = distance_work;
		this.distanceEducation = distance_education;

		this.commuterTicket = commuterTicket;
		this.licence = licence;
		this.bicycle = bicycle;
		this.personalCar = personalCar;
		this.carAvailable = carAvailable;

		this.income = income;
		this.activityPattern = activityPattern;

		this.pref_cardriver = pref_cardriver;
		this.pref_carpassenger = pref_carpassenger;
		this.pref_walking = pref_walking;
		this.pref_cycling = pref_cycling;
		this.pref_publictransport = pref_publictransport;
	}

	private void verifyGender(int genderType) {
		if (genderType != 1 && genderType != 2) {
			throw new IllegalArgumentException("Invalid value for gender: " + genderType);
		}
	}

	private void verifyId(PersonOfPanelDataId personId) {
		if (null == personId) {
			throw new NullPointerException("personId is not allowed to be null.");
		}
	}

	private void verifyAge(int age) {
		if (age < 0) {
			throw new IllegalArgumentException("Age is lower than 0: " + age);
		}
		if (age > 120) {
			throw new IllegalArgumentException("Age is higher than 120: " + age);
		}
	}

	public int getGenderTypeAsInt() {
		return getGenderType();
	}

	public int getBirthyear() {
		return this.birthyear;
	}

	public int getEmploymentTypeAsInt() {
		return this.employmentType;
	}

	public boolean hasCommuterTicket() {
		return this.commuterTicket;
	}

	public boolean hasBicycle() {
		return this.bicycle;
	}

	public boolean hasAccessToCar() {
		return this.carAvailable;
	}

	public boolean hasPersonalCar() {
		return this.personalCar;
	}

	public Gender gender() {
		return Gender.getTypeFromInt(getGenderType());
	}

	public Graduation graduation() {
		return Graduation.getTypeFromNumeric(getGraduation());
	}

	public Employment employment() {
		return Employment.getTypeFromInt(getEmploymentTypeAsInt());
	}

	public int age() {
		return getAge();
	}

	public boolean hasLicence() {
		return this.licence;
	}

	public int compareTo(PersonOfPanelData o) {
		PersonOfPanelDataId id = getId();
		return id.compareTo(o.getId());
	}
	
	public float getPoleDistance() {
		if (0.0f != distanceWork) {
			return distanceWork;
		}
		return distanceEducation;
	}

	public float getPoleDistance(ActivityType activityType) {
		if (ActivityType.WORK.equals(activityType)) {
			return distanceWork;
		}
		return distanceEducation;
	}

}

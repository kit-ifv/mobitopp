package edu.kit.ifv.mobitopp.data.demand;

import java.io.Serializable;

import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;

public class Demography implements Serializable {

	private static final long serialVersionUID = 1L;

	private final EmploymentDistribution employment;
	private final HouseholdDistribution household;
	private final FemaleAgeDistribution femaleAge;
	private final MaleAgeDistribution maleAge;

	public Demography(
			EmploymentDistribution employment, HouseholdDistribution household,
			FemaleAgeDistribution femaleAge, MaleAgeDistribution maleAge) {
		super();
		this.employment = employment;
		this.household = household;
		this.femaleAge = femaleAge;
		this.maleAge = maleAge;
	}

	public EmploymentDistribution employment() {
		return employment;
	}

	public HouseholdDistribution household() {
		return household;
	}

	public FemaleAgeDistribution femaleAge() {
		return femaleAge;
	}

	public MaleAgeDistribution maleAge() {
		return maleAge;
	}

	public void incrementHousehold(int type) {
		household.getItem(type).increment();
	}
	
	public void incrementEmployment(Employment employment) {
		this.employment.getItem(employment).increment();
	}
	
	public void incrementAge(Gender gender, int age) {
		if (Gender.MALE == gender) {
			maleAge.getItem(age).increment();
		} else {
			femaleAge.getItem(age).increment();
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((employment == null) ? 0 : employment.hashCode());
		result = prime * result + ((femaleAge == null) ? 0 : femaleAge.hashCode());
		result = prime * result + ((household == null) ? 0 : household.hashCode());
		result = prime * result + ((maleAge == null) ? 0 : maleAge.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Demography other = (Demography) obj;
		if (employment == null) {
			if (other.employment != null)
				return false;
		} else if (!employment.equals(other.employment))
			return false;
		if (femaleAge == null) {
			if (other.femaleAge != null)
				return false;
		} else if (!femaleAge.equals(other.femaleAge))
			return false;
		if (household == null) {
			if (other.household != null)
				return false;
		} else if (!household.equals(other.household))
			return false;
		if (maleAge == null) {
			if (other.maleAge != null)
				return false;
		} else if (!maleAge.equals(other.maleAge))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [employment=" + employment + ", household=" + household
				+ ", femaleAge=" + femaleAge + ", maleAge=" + maleAge + "]";
	}

}

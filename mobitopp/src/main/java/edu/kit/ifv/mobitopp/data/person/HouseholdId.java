package edu.kit.ifv.mobitopp.data.person;

import java.io.Serializable;

public class HouseholdId implements Comparable<HouseholdId>, Serializable {

	private static final long serialVersionUID = 8599877663307485839L;
	
	private final short year;
	private final long householdNumber;

	public HouseholdId(short year, long householdNumber) {
		super();

		verifyYear(year);
		verifyHouseholdNumber(householdNumber);
		this.year = year;
		this.householdNumber = householdNumber;
	}

  private void verifyHouseholdNumber(long householdNumber) {
  	if (0 > householdNumber) {
  		throw new IllegalArgumentException("Household number is less than 0!");
  	}
	}

	private void verifyYear(int year) {
  	if (0 > year) {
  		throw new IllegalArgumentException("Incorrect year: " + year);
  	}
	}

	public int getYear() {
		return this.year;
	}

	public long getHouseholdNumber() {
		return this.householdNumber;
	}

	public int compareTo(HouseholdId other) {
		if (equals(other)) {
			return 0;
		}
		if (this.year < other.year) {
			return -1;
		}
		if (this.year > other.year) {
			return 1;
		}
		if (this.householdNumber < other.householdNumber) {
			return -1;
		}
		if (this.householdNumber > other.householdNumber) {
			return 1;
		}
		return 0;
	}
	
	public String toString() {
		return "(" + householdNumber + "," + year + ")";
	}

  @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (householdNumber ^ (householdNumber >>> 32));
		result = prime * result + year;
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
		HouseholdId other = (HouseholdId) obj;
		if (householdNumber != other.householdNumber)
			return false;
		if (year != other.year)
			return false;
		return true;
	}
}

package edu.kit.ifv.mobitopp.data.person;

import java.io.Serializable;

public class HouseholdId implements Comparable<HouseholdId>, Serializable {

	
	private static final long serialVersionUID = 8599877663307485839L;
	
	private short _year = Short.MIN_VALUE;
	private long _householdNumber = Integer.MIN_VALUE;

	public HouseholdId(int year, long householdNumber) {
		super();

		verifyYear(year);
		verifyHouseholdNumber(householdNumber);
		this._year = (short) year;
		this._householdNumber = householdNumber;
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
		return this._year;
	}

	public long getHouseholdNumber() {
		return this._householdNumber;
	}

	public int compareTo(HouseholdId other) {
		if (equals(other)) {
			return 0;
		}
		if (this._year < other._year) {
			return -1;
		}
		if (this._year > other._year) {
			return 1;
		}
		if (this._householdNumber < other._householdNumber) {
			return -1;
		}
		if (this._householdNumber > other._householdNumber) {
			return 1;
		}
		return 0;
	}
	
	public String toString() {
		return "(" + _householdNumber + "," + _year + ")";
	}

  @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (_householdNumber ^ (_householdNumber >>> 32));
		result = prime * result + _year;
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
		if (_householdNumber != other._householdNumber)
			return false;
		if (_year != other._year)
			return false;
		return true;
	}
}

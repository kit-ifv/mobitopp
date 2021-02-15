package edu.kit.ifv.mobitopp.data.person;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.Serializable;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HouseholdId implements Comparable<HouseholdId>, Serializable {

	private static final long serialVersionUID = 8599877663307485839L;

	private final int oid;
	private final short year;
	private final long householdNumber;

	public HouseholdId(int oid, short year, long householdNumber) {
		super();
		verifyYear(year);
		verifyHouseholdNumber(householdNumber);
		this.oid = oid;
		this.year = year;
		this.householdNumber = householdNumber;
	}

	private void verifyHouseholdNumber(long householdNumber) {
		if (0 > householdNumber) {
			throw warn(new IllegalArgumentException("Household number is less than 0!"), log);
		}
	}

	private void verifyYear(int year) {
		if (0 > year) {
			throw warn(new IllegalArgumentException("Incorrect year: " + year), log);
		}
	}

	public int getOid() {
		return oid;
	}

	public short getYear() {
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
		return this.oid - other.oid;
	}

	public String toString() {
		return "(" + householdNumber + "," + year + ")";
	}

	@Override
	public int hashCode() {
		return Objects.hash(householdNumber, oid, year);
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
		return householdNumber == other.householdNumber && oid == other.oid && year == other.year;
	}

}

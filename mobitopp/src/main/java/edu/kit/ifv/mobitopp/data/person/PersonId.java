package edu.kit.ifv.mobitopp.data.person;

import java.io.Serializable;

public class PersonId implements Comparable<PersonId>, Serializable {

  
	private static final long serialVersionUID = 1155657645242284988L;
	
	private final int oid;
	private final HouseholdId householdId;
  private final byte personNumber;

	public PersonId(int oid, HouseholdId householdId, int personNumber) {
		super();

		verify(householdId);
		verify(personNumber);
		this.oid = oid;
		this.householdId = householdId;
		this.personNumber = (byte) personNumber;
  }

  private void verify(int personNumber) {
  	if(0 > personNumber) {
  		throw new IllegalArgumentException("Person number is less than 0!");
  	}
	}

	private void verify(HouseholdId householdId) {
  	if (null == householdId) {
  		throw new IllegalArgumentException("Household id is not allowed to be null!");
  	}
	}

  public int getOid() {
    return this.oid;
  }
  
  public HouseholdId getHouseholdId() {
    return this.householdId;
  }

	public int getPersonNumber() {
		return this.personNumber;
	}
	

	public String toString() {
		return "(" + householdId + "," + personNumber + ")";
	}


  public int compareTo(PersonId other) {
		if (equals(other)) {
			return 0;
		}

		if (this.householdId.compareTo(other.householdId) == 0) {
			if (this.personNumber < other.personNumber) {
				return -1;
			}
			if (this.personNumber > other.personNumber) {
				return 1;
			}
			return 0;
		}

		return this.householdId.compareTo(other.householdId);
  }
  
  @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((householdId == null) ? 0 : householdId.hashCode());
		result = prime * result + personNumber;
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
		PersonId other = (PersonId) obj;
		if (householdId == null) {
			if (other.householdId != null)
				return false;
		} else if (!householdId.equals(other.householdId))
			return false;
		if (personNumber != other.personNumber)
			return false;
		return true;
	}
}

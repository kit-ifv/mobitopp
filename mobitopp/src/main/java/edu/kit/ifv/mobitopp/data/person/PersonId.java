package edu.kit.ifv.mobitopp.data.person;

import java.io.Serializable;
import java.util.Objects;

public class PersonId implements Comparable<PersonId>, Serializable {

  
	private static final long serialVersionUID = 1155657645242284988L;
	
	private final int oid;
	private final HouseholdId householdId;
  private final int personNumber;

	public PersonId(int oid, HouseholdId householdId, int personNumber) {
		super();

		verify(householdId);
		verify(personNumber);
		this.oid = oid;
		this.householdId = householdId;
		this.personNumber = personNumber;
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
		return oid + "(" + householdId + "," + personNumber + ")";
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
			return this.oid - other.oid;
		}

		return this.householdId.compareTo(other.householdId);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(householdId, oid, personNumber);
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
    return Objects.equals(householdId, other.householdId) && oid == other.oid
        && personNumber == other.personNumber;
  }
}

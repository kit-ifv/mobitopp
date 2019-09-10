package edu.kit.ifv.mobitopp.util.panel;

import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class PersonOfPanelDataId implements Comparable<PersonOfPanelDataId> {

	private final HouseholdOfPanelDataId householdId;
  private final byte personNumber;

  public PersonOfPanelDataId(
		HouseholdOfPanelDataId householdId,
		int personNumber)
  {
		assert householdId != null;
		assert personNumber >= 0;


    this.householdId = householdId;
    this.personNumber = (byte) personNumber;
  }

  public HouseholdOfPanelDataId getHouseholdId()
  {
    return this.householdId;
  }

  public int getPersonNumber()
  {
    return this.personNumber;
  }

	public int compareTo(PersonOfPanelDataId o) {

		int hh_comparison = householdId.compareTo(o.householdId);

		if (hh_comparison != 0) {
			return hh_comparison;
		}

		return personNumber - o.personNumber;
	}

	public static PersonOfPanelDataId fromPersonId(PersonId id) {
		return new PersonOfPanelDataId(createPanelId(id.getHouseholdId()), id.getPersonNumber());
	}

	private static HouseholdOfPanelDataId createPanelId(HouseholdId id) {
		return new HouseholdOfPanelDataId(id.getYear(), id.getHouseholdNumber());
	}

}

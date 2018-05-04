package edu.kit.ifv.mobitopp.util.panel;

import java.util.Objects;

public class HouseholdOfPanelDataId 
  implements  Comparable<HouseholdOfPanelDataId>
		, PanelDataIdIfc 
{

  private final short year;
  private final int   householdNumber;

  private double weight	=	0.0;

	public HouseholdOfPanelDataId(int year, int householdNumber) {
		assert year > 1990 && year < 2100 : year;
		assert householdNumber >= 0;

		this.year = (short) year;
    this.householdNumber = householdNumber;
  }

  public short getYear()
  {
    return this.year;
  }

  public int getHouseholdNumber()
  {
    return this.householdNumber;
  }

  public int compareTo(
      HouseholdOfPanelDataId o)
  {

    if (equals(o)) { return 0; }
    
    if (this.year < o.year) { return -1; }
    if (this.year > o.year) { return 1; }

    if (this.householdNumber < o.householdNumber) { return -1; }
    if (this.householdNumber > o.householdNumber) { return 1; }

    return 0; 
  }


  public boolean equals(Object obj)
  {

    HouseholdOfPanelDataId otherObject = 
      (HouseholdOfPanelDataId) obj;

    if (this.householdNumber ==  otherObject.householdNumber &&
        this.year == otherObject.year)
    {
      return true;
    }

    return false;
  }

	public double get_weight() {
		return weight;
	}

	public void set_weight(double weight) {
		this.weight = weight;
	}

	public String toString() {
		return "[" + year + "," + householdNumber + "]";
	}

	public int hashCode() {
		return Objects.hash(year, householdNumber);
	}
}

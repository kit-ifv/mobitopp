package edu.kit.ifv.mobitopp.util.panel;

public class HouseholdOfPanelDataId implements Comparable<HouseholdOfPanelDataId> {

  private final short year;
	private final long householdNumber;

  private double weight	=	0.0;

	public HouseholdOfPanelDataId(short year, long household_number) {
		assert year > 1990 && year < 2100 : year;
		assert household_number >= 0;

		this.year = year;
    this.householdNumber = household_number;
  }

  public short getYear()
  {
    return this.year;
  }

  public long getHouseholdNumber()
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


  @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HouseholdOfPanelDataId other = (HouseholdOfPanelDataId) obj;
		if (householdNumber != other.householdNumber)
			return false;
		if (year != other.year)
			return false;
		return true;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (householdNumber ^ (householdNumber >>> 32));
		result = prime * result + year;
		return result;
	}
}

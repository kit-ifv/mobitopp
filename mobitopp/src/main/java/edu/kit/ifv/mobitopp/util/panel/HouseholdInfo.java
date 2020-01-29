package edu.kit.ifv.mobitopp.util.panel;

class HouseholdInfo {

	public long household_number;
	public short year;
	public int area_type;
	public int household_size;
	public int household_type;
	public int domcode;
	public int additionalchildren;
	public int additionalchildrenmaxage;
	public int cars;
	public int income = -1;
  public int income_class = -1;
	public float activity_radius_time;
	public int activity_radius_mode;

	public String id() {
		return household_number + "," + year;
	}


	public String toString() {

		return getClass().getName()
					+ ": "
					+ household_number + ", "
					+ year + ", "
					+ area_type + ", "
					+ household_size + ", "
					+ household_type + ", "
					+ domcode + ", "
					+ additionalchildren + ", "
					+ cars 
				;
	}
}

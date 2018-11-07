package edu.kit.ifv.mobitopp.util.panel;

class HouseholdInfo {

	public long household_number;
	public short year;
	public int area_type;
	public int household_size;
	public int domcode;
	public int additionalchildren;
	public int additionalchildrenmaxage;
	public int cars;
	public int income = -1;


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
					+ domcode + ", "
					+ additionalchildren + ", "
					+ cars 
				;
	}
}

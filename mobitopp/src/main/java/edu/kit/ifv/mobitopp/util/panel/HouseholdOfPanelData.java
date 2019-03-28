package edu.kit.ifv.mobitopp.util.panel;

import edu.kit.ifv.mobitopp.data.ProjectAreaType;

public class HouseholdOfPanelData
{
  private final static byte UNDEFINED_BYTE     = Byte.MIN_VALUE;
  private final static int UNDEFINED_INT       = Integer.MIN_VALUE;


  private final HouseholdOfPanelDataId id;
  private final int areaType;
  private final byte numberOfCars;
  private final byte numberOfReportingPersons;
  private final byte numberOfNotReportingChildren;
  private final byte householdType;
  private final int income;
  private final byte size;
  private final byte numberOfMinors;

  public HouseholdOfPanelData(
  	HouseholdOfPanelDataId id,
		int areaType,
		int size,
		int domCode,
		int numberOfReportingPersons,
		int numberOfMinors,
		int numberOfNotReportingChildren,
		int numberOfCars,
		int income
	)
  {
		assert id != null;

		this.id = id;

		this.areaType = areaType;
		this.size = (byte) size;
		this.householdType = (byte) domCode;
		this.numberOfReportingPersons = (byte) numberOfReportingPersons;
		this.numberOfMinors = (byte) numberOfMinors;
		this.numberOfNotReportingChildren = (byte) numberOfNotReportingChildren;
		this.numberOfCars = (byte) numberOfCars;
		this.income = income;
	}

  public HouseholdOfPanelData(HouseholdOfPanelData household) {
    this(household.id, household.areaType, household.size, household.householdType,
        household.numberOfReportingPersons, household.numberOfMinors,
        household.numberOfNotReportingChildren, household.numberOfCars, household.income);
  }

  public HouseholdOfPanelDataId id()
  {
		assert this.id != null;

    return this.id;
  }

  public int numberOfCars()
  {
		assert this.numberOfCars != UNDEFINED_BYTE;

    return this.numberOfCars;
  }


  public int numberOfReportingPersons()
  {
		assert this.numberOfReportingPersons != UNDEFINED_BYTE;

    return this.numberOfReportingPersons;
  }


  public int numberOfNotReportingChildren()
  {
		assert this.numberOfNotReportingChildren != UNDEFINED_BYTE;

    return this.numberOfNotReportingChildren;
  }

	public int numberOfMinors() {

		return this.numberOfMinors;
	}

  public int areaTypeAsInt()
  {
		assert this.areaType != UNDEFINED_BYTE;

    return this.areaType;
  }

  public ProjectAreaType areaType()
  {
		return ProjectAreaType.getTypeFromInt(areaTypeAsInt());
  }

  public int size()
  {
		assert this.size != UNDEFINED_BYTE;

    return this.size;
  }

  public int domCode()
  {
		assert this.householdType != UNDEFINED_BYTE;

    return this.householdType;
  }

  public int income()
  {
		assert this.income != UNDEFINED_INT;

    return this.income;
  }


  public boolean isCompletelySpecified()
  {
    return (size() == (numberOfReportingPersons() + numberOfNotReportingChildren()));
  }

	public String toString() {

		return "[" + id + "," +
  		+ areaType + "," +
  		+ numberOfCars + "," +
  		+ numberOfNotReportingChildren + "," +
  		+ householdType + "," +
  		+ income + "," +
  		+ size + "," +
  		+ numberOfMinors + "]";
	}

}

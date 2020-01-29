package edu.kit.ifv.mobitopp.util.panel;

import edu.kit.ifv.mobitopp.data.ProjectAreaType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class HouseholdOfPanelData
{
  private final static byte UNDEFINED_BYTE     = Byte.MIN_VALUE;
  private final static int UNDEFINED_INT       = Integer.MIN_VALUE;


  private final HouseholdOfPanelDataId id;
  private final int areaType;
  private final byte size;
  private final byte domCode;
  private final byte numberOfReportingPersons;
  private final byte numberOfMinors;
  private final byte numberOfNotReportingChildren;
  private final byte numberOfCars;
  private final int income;
  private final int incomeClass;
	private final float activityRadius;
	private final int activityRadiusMode;

  public HouseholdOfPanelData(
  	final HouseholdOfPanelDataId id,
		final int areaType,
		final int size,
		final int domCode,
		final int numberOfReportingPersons,
		final int numberOfMinors,
		final int numberOfNotReportingChildren,
		final int numberOfCars,
		final int income, 
		final int incomeClass, 
		final float activityRadius, 
		final int activityRadiusMode
	)
	{
		this(id, areaType, (byte) size, (byte) domCode, (byte) numberOfReportingPersons,
				(byte) numberOfMinors, (byte) numberOfNotReportingChildren, (byte) numberOfCars, income,
				incomeClass, activityRadius, activityRadiusMode);
	}

  public HouseholdOfPanelData(final HouseholdOfPanelData household) {
    this(household.id, household.areaType, household.size, household.domCode,
        household.numberOfReportingPersons, household.numberOfMinors,
        household.numberOfNotReportingChildren, household.numberOfCars, household.income,
        household.incomeClass, household.activityRadius, household.activityRadiusMode);
  }

  public HouseholdOfPanelDataId id()
  {
		assert this.id != null;

    return getId();
  }

  public int numberOfCars()
  {
		assert this.numberOfCars != UNDEFINED_BYTE;

    return getNumberOfCars();
  }


  public int numberOfReportingPersons()
  {
		assert this.numberOfReportingPersons != UNDEFINED_BYTE;

    return getNumberOfReportingPersons();
  }


  public int numberOfNotReportingChildren()
  {
		assert this.numberOfNotReportingChildren != UNDEFINED_BYTE;

    return getNumberOfNotReportingChildren();
  }

	public int numberOfMinors() {

		return getNumberOfMinors();
	}

  public int areaTypeAsInt()
  {
		assert this.areaType != UNDEFINED_BYTE;

    return getAreaType();
  }

  public ProjectAreaType areaType()
  {
		return ProjectAreaType.getTypeFromInt(areaTypeAsInt());
  }

  public int size()
  {
		assert this.size != UNDEFINED_BYTE;

    return getSize();
  }

  public int domCode()
  {
		assert this.domCode != UNDEFINED_BYTE;

    return getDomCode();
  }

  public int income()
  {
		assert this.income != UNDEFINED_INT;

    return getIncome();
  }

  public int incomeClass() {
    return getIncomeClass();
  }
  
  public boolean isCompletelySpecified()
  {
    return (size() == (numberOfReportingPersons() + numberOfNotReportingChildren()));
  }

}

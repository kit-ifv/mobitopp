package edu.kit.ifv.mobitopp.util.panel;

import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;

public class PersonOfPanelData
	implements Comparable<PersonOfPanelData>
{
  protected final static byte UNDEFINED_BYTE     = Byte.MIN_VALUE;
  protected final static short UNDEFINED_SHORT   = Short.MIN_VALUE;
  protected final static int UNDEFINED_INT       = Integer.MIN_VALUE;
  protected final static float UNDEFINED_FLOAT   = -Float.MAX_VALUE;

	private final PersonOfPanelDataId id;

  private final byte employmentType;
  private final byte genderType;

  private final short  birthyear;
  private final short  age;
  private final String activityPattern;
  private final float  weight;
  private final Integer income;
  private final float poleDistance;

	private final boolean commuterTicket;
	private final boolean licence;
	private final boolean bicycle;
	private final boolean personalCar;
	private final boolean carAvailable;
	
	public final float pref_cardriver;
	public final float pref_carpassenger;
	public final float pref_walking;
	public final float pref_cycling;
	public final float pref_publictransport;



	public PersonOfPanelData(
		PersonOfPanelDataId personId,
		int genderType,
		int  birthyear,
		int  age,
		int employmentType,
		float poleDistance,	
		// int commutationTicketType,
		boolean commuterTicket,
		boolean licence,
		boolean bicycle,
		boolean personalCar,
		boolean carAvailable,
		// String modeTypeWeights,
		float weight,
		Integer income,
		String activityPattern,
		float pref_cardriver,
		float pref_carpassenger,
		float pref_walking,
		float pref_cycling,
		float pref_publictransport
	) {
		assert personId != null;
		assert age >= 0 && age <= 120 : age;
		
		assert genderType==1 || genderType==2 : ("invalid value for sex: " + genderType);

		this.id = personId;
    this.genderType = (byte) genderType;
    this.birthyear = (short) birthyear;
    this.age = (short) age;
    this.employmentType = (byte) employmentType;
    this.poleDistance = poleDistance;

		this.commuterTicket = commuterTicket;
		this.licence = licence;
		this.bicycle = bicycle;
		this.personalCar = personalCar;
		this.carAvailable = carAvailable;

    this.weight = weight;
		this.income = income;
		this.activityPattern = activityPattern;
		
		this.pref_cardriver = pref_cardriver;
		this.pref_carpassenger = pref_carpassenger;
		this.pref_walking = pref_walking;
		this.pref_cycling = pref_cycling;
		this.pref_publictransport = pref_publictransport;
	}

  public PersonOfPanelDataId getId()
	{
		assert id != null;

    return this.id;
	}

  public int getGenderTypeAsInt()
  {
		assert this.genderType != UNDEFINED_BYTE;

    return this.genderType;
  }

  public int getBirthyear()
  {
		assert this.birthyear != UNDEFINED_SHORT;

    return this.birthyear;
  }


  public int getEmploymentTypeAsInt()
  {
		assert this.employmentType != UNDEFINED_BYTE;

    return this.employmentType;
  }

  public String getActivityPattern()
  {
		assert this.activityPattern != null;

    return this.activityPattern;
  }

  public float getPoleDistance()
  {
		assert this.poleDistance != UNDEFINED_FLOAT;

    return this.poleDistance;
  }

	public boolean hasCommuterTicket() {
		
		return this.commuterTicket;
	}

	public boolean hasBicycle() {

		return this.bicycle;
	}

	public boolean hasAccessToCar() {

		return this.carAvailable;
	}

	public boolean hasPersonalCar() {

		return this.personalCar;
	}


  public float getWeight()
  {
		assert this.weight != UNDEFINED_FLOAT;

    return this.weight;
  }

  public int getIncome()
  {
		assert this.income != UNDEFINED_INT;

    return this.income;
  }


  public Gender gender()
  {
    return Gender.getTypeFromInt(getGenderTypeAsInt());
  }


  public Employment employment()
  {
    return Employment.getTypeFromInt(getEmploymentTypeAsInt());
  }


  public int age()
  {
		return this.age;
  }
  
  public boolean hasLicence()
  {
		return this.licence;
  }



	public int compareTo(PersonOfPanelData o) {
		PersonOfPanelDataId id = getId();
		return id.compareTo(o.getId());
	}
}


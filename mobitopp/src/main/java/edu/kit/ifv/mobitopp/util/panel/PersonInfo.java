package edu.kit.ifv.mobitopp.util.panel;

class PersonInfo {

	public int person_number;
	public int sex;
	public int graduation;
	public int birthYear;
	public int employmentType;
	public int pole_distance;
	public float distanceWork;
	public float distanceEducation;
	public int income = -1;
	public boolean commutationTicket;
	public boolean licence;
	
	//modetypeweights
	public boolean fahrrad;
	public boolean apkwverf;
	public boolean ppkwverf;

	public float pref_cardriver;
	public float pref_carpassenger;
	public float pref_walking;
	public float pref_cycling;
	public float pref_publictransport;

	public String toString() {

		return getClass().getName()
					+ ": "
					+ person_number + ", "
					+ sex + ", "
					+ graduation + ", "
					+ birthYear + ", "
					+ employmentType + ", "
					+ pole_distance + ", "
					+ commutationTicket + ", "
					+ fahrrad + ", "
					+ apkwverf + ", "
					+ ppkwverf + ", "
				;
	}
}

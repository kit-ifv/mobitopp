package edu.kit.ifv.mobitopp.simulation;


import edu.kit.ifv.mobitopp.simulation.activityschedule.OccupationIfc;
import edu.kit.ifv.mobitopp.time.Time;


public class Occupation_Stub
	implements OccupationIfc
{ 

	private final int oid;
	private final Time start;
	private final int duration_in_min;

	public Occupation_Stub(
		int oid,
		Time start,
		int duration_in_min) 
	{
		this.oid=oid;
		this.start=start;
		this.duration_in_min=duration_in_min;
	}

	public int getOid() { return this.oid; }

  public Time startDate() { return this.start; }

  public Time calculatePlannedEndDate() { return this.start.plusMinutes(duration_in_min); }

}

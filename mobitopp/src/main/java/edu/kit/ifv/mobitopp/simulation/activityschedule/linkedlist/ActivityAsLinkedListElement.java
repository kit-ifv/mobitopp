package edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.Activity;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

public class ActivityAsLinkedListElement
	extends Activity
	implements ActivityIfc, LinkedListElement 
{

	private LinkedListElement next;
	private LinkedListElement prev;

	public LinkedListElement next() {	
		return next;
	}
	public LinkedListElement prev() {
		return prev;
	}

	public void setNext(LinkedListElement next) {
		assert next != this;
		this.next = next;
	}

	public void setPrev(LinkedListElement prev) {
		assert prev != this;
		this.prev = prev;
	}


  public ActivityAsLinkedListElement(
		int oid,
		int activityNrOfWeek,
		ActivityType activityType,
		Time startDate,
		int duration,
		int observedTripDuration
	)
  { 
		super(oid, (byte) activityNrOfWeek, activityType, startDate, duration, observedTripDuration);
  }
  
  public ActivityAsLinkedListElement(
		int oid,
		int activityNrOfWeek,
		ActivityType activityType,
		Time startDate,
		int duration,
		int observedTripDuration,
		float startFlexibility,
		float endFlexibility,
		float durationFlexibiliy
	)
  { 
		super(oid, (byte) activityNrOfWeek, activityType, startDate, duration, observedTripDuration,
				startFlexibility, endFlexibility, durationFlexibiliy);
  }
}

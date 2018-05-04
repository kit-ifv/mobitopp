package edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivitySequence;
import edu.kit.ifv.mobitopp.time.Time;

public class ActivitySequenceAsLinkedList 
	extends DefaultLinkedList
	implements Serializable
		, LinkedList
		, ActivitySequence
{

	private static final long serialVersionUID = 4675055857471430180L;
	
	public ActivitySequenceAsLinkedList() {
		super();
	}

	public ActivityIfc lastActivity() {
		return (ActivityIfc) last();
	}

	public ActivityIfc firstActivity() {
		return (ActivityIfc) first();
	}

	public ActivityIfc nextActivity(ActivityIfc activity) {
		return (ActivityIfc) next((LinkedListElement)activity);
	}

	public ActivityIfc prevActivity(ActivityIfc activity) {
		return (ActivityIfc) prev((LinkedListElement)activity);
	}

	public void addAsLastActivity(ActivityIfc activity) {
		addToBack((LinkedListElement) activity);
	}
	
	public void addAsFirstActivity(ActivityIfc activity) {
		addToFront((LinkedListElement) activity);
	}

	public void insertAfter(ActivityIfc previous, ActivityIfc toBeInserted) {
		insertAfter((LinkedListElement) previous, (LinkedListElement) toBeInserted);
	}

	public void removeActivity(ActivityIfc activity) {
		remove((LinkedListElement) activity);
	}
	
	public boolean contains(ActivityIfc activity) {
		
		if (isEmpty()) {
			return false;
		}
		
		ActivityIfc current = firstActivity();
		
		if (current == activity) {
			return true;
		}
		
		while(!isLastActivity(current)) {
			current = nextActivity(current);
			
			if (current == activity) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isLastActivity(ActivityIfc activity) {
		return nextActivity(activity) == null;
	}

	public static ActivityIfc newActivity(
			int id,
			int nrWithinDay, 
			Time startDate, 
			ActivityType activityType,
			int duration, 
			int tripDuration
	) {
			ActivityIfc activity = 	new ActivityAsLinkedListElement(
																		id,
																		(byte)nrWithinDay,
																		activityType,
																		startDate,
																		(short) duration,
																		(short) tripDuration
																);
			return activity;
		}

	@Override
	public List<ActivityIfc> activitiesBetween(ActivityIfc activityBefore, ActivityIfc activityAfter) {
		
		if (activityBefore == activityAfter) {
			return new ArrayList<ActivityIfc>();
		}
		
		List<ActivityIfc> result = new ArrayList<ActivityIfc>();
		
		ActivityIfc current = activityBefore;
		
		while (current != activityAfter) {
			current = nextActivity(current);
			if (current != activityAfter) {
				result.add(current);
			}
		}
		
		return result;
	}
}
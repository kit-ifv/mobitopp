package edu.kit.ifv.mobitopp.simulation;

import java.util.Set;

import edu.kit.ifv.mobitopp.data.OutputHandler;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.StartedTrip;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;

public class AggregateDemandPerMode implements PersonListener {

	private final Set<OutputHandler> outputHandlers;

  public AggregateDemandPerMode(Set<OutputHandler> outputHandlers) {
    super();
    this.outputHandlers = outputHandlers;
  } 

	@Override
	public void notifyEndTrip(Person person, FinishedTrip trip) {
		ZoneId origin = trip.origin().zone().getId();
		ZoneId destination = trip.destination().zone().getId();
		for (OutputHandler oneOutputHandler : outputHandlers) {
			if (oneOutputHandler.mode() == trip.mode()
					&& oneOutputHandler.from() <= trip.startDate().getHour()
					&& oneOutputHandler.to() >= trip.startDate().getHour()) {
				oneOutputHandler.matrix().add(origin, destination, oneOutputHandler.scalingfactor());
			}

		}
	}
	
	@Override
	public void notifyStartTrip(Person person, StartedTrip trip) {
		// TODO Move writing to matrix from notifyEndTrip to notifyStartTrip?
	}
  
  @Override
  public void notifyFinishSimulation() {   
  	for (OutputHandler outputHandler : outputHandlers) {
  		outputHandler.writer().accept(outputHandler.matrix());
  	}
  }

  @Override
  public void notifyFinishCarTrip(Person person, Car car, FinishedTrip trip, ActivityIfc activity) {
  }

  @Override
  public void notifyStartActivity(Person person, ActivityIfc activity) {
  }

  @Override
  public void notifySelectCarRoute(Person person, Car car, TripData trip, Path route) {
  }

  @Override
  public void writeSubourinfoToFile(Person person, Tour tour, Subtour subtour, Mode tourMode) {
  }

  @Override
  public void writeTourinfoToFile(Person person, Tour tour, Zone tourDestination, Mode tourMode) {
  }

  @Override
  public void notifyStateChanged(StateChange stateChange) {
  }

}

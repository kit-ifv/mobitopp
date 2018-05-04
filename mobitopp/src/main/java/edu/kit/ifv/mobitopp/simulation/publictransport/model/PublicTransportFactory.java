package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.RoutePoints;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.serializer.JourneyFactory;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumPtStopPoint;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourney;

public interface PublicTransportFactory extends JourneyFactory {

	Connection connectionFrom(
			Stop start, Stop end, Time departure, Time arrival, Journey journey,
			RoutePoints route);

	Stop stopFrom(VisumPtStopPoint visum);

	RoutePoints coordinates(VisumPtVehicleJourney visumJourney, int previous, int current, Stop start, Stop end);

}

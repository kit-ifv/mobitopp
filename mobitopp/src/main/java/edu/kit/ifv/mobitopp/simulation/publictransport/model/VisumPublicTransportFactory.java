package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.publictransport.model.DefaultModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.RoutePoints;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.TransportSystem;
import edu.kit.ifv.mobitopp.publictransport.serializer.StationResolver;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumPtLineRouteElement;
import edu.kit.ifv.mobitopp.visum.VisumPtStopPoint;
import edu.kit.ifv.mobitopp.visum.VisumPtTimeProfileElement;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourney;

public class VisumPublicTransportFactory implements PublicTransportFactory {

	private int connections = 0;
	private int stops = 0;
	private StationResolver stationResolver;

	public VisumPublicTransportFactory(StationResolver stationResolver) {
		super();
		this.stationResolver = stationResolver;
	}

	@Override
	public Connection connectionFrom(
			Stop start, Stop end, Time departure, Time arrival, Journey journey, RoutePoints route) {
		return Connection.from(nextConnection(), start, end, departure, arrival, journey, route);
	}

	private ConnectionId nextConnection() {
		return ConnectionId.of(connections++);
	}
	
	@Override
	public ModifiableJourney createJourney(int id, Time day, int capacity, TransportSystem system) {
		return new DefaultModifiableJourney(id, day, system, capacity);
	}

	@Override
	public Stop stopFrom(VisumPtStopPoint visum) {
		int stationId = visum.stopArea.stop.id;
		Station station = stationResolver.getStation(stationId);
		RelativeTime minimumChangeTime = station.minimumChangeTime(visum.id);
		Stop stop = new Stop(nextStop(), visum.name, visum.coordinate(), minimumChangeTime, station,
				visum.id);
		station.add(stop);
		return stop;
	}

	private int nextStop() {
		return stops++;
	}
	
	@Override
	public RoutePoints coordinates(VisumPtVehicleJourney visumJourney, int previous, int current, Stop start, Stop end) {
		VisumPtTimeProfileElement startElement = visumJourney.timeProfile.elements.get(previous);
		VisumPtTimeProfileElement endElement = visumJourney.timeProfile.elements.get(current);
		List<Point2D> points = new ArrayList<>();
		for (int index = startElement.lrElemIndex; index <= endElement.lrElemIndex; index++) {
			points.add(asPoints(visumJourney.route.elements.get(index)));
		}
		return RoutePoints.from(start, end, points);
	}

	private Point2D asPoints(VisumPtLineRouteElement lineRouteElement) {
		if (lineRouteElement.stopPoint != null) {
			return lineRouteElement.stopPoint.coordinate();
		}
		return lineRouteElement.node.coordinate();
	}

}

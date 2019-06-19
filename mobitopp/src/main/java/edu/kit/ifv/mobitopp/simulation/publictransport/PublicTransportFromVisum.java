package edu.kit.ifv.mobitopp.simulation.publictransport;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.serializer.NeighbourhoodCoupler;
import edu.kit.ifv.mobitopp.publictransport.serializer.NodeResolver;
import edu.kit.ifv.mobitopp.routing.VisumLinkFactory;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.JourneyTemplate;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.JourneyTemplates;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.ModifiableJourneys;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.PublicTransportFactory;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationFinder;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationFromVisum;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Stations;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StopPoints;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.TransferWalkTime;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumPublicTransportFactory;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumStopResolver;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.StopAreaPair;
import edu.kit.ifv.mobitopp.visum.VisumLinkType;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumPtStop;
import edu.kit.ifv.mobitopp.visum.VisumPtStopArea;
import edu.kit.ifv.mobitopp.visum.VisumPtStopPoint;
import edu.kit.ifv.mobitopp.visum.VisumPtTimeProfile;
import edu.kit.ifv.mobitopp.visum.VisumPtTransferWalkTimes;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourney;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;

public class PublicTransportFromVisum extends BasePublicTransportConverter implements SearchFootpath {

	private static final RelativeTime maximumDuration = RelativeTime.of(15, MINUTES);
	private static final int targetsToResolve = 2;

	private final List<VisumTransportSystem> walking;
	private final VisumNetwork visumNetwork;
	private final Map<JourneyKey, Journey> mapping;
	private VisumPublicTransportFactory factory;

	public PublicTransportFromVisum(Time date, VisumNetwork visumNetwork) {
		this(defaultWalkingSystems(visumNetwork), date, visumNetwork);
	}

	public PublicTransportFromVisum(
			List<VisumTransportSystem> walking, Time date, VisumNetwork visumNetwork) {
		this(walking, asList(date), visumNetwork);
	}

	public PublicTransportFromVisum(
			List<Time> simulationDates, VisumNetwork visumNetwork) {
		this(defaultWalkingSystems(visumNetwork), simulationDates, visumNetwork);
	}

	public PublicTransportFromVisum(
			List<VisumTransportSystem> walking, List<Time> simulationDates,
			VisumNetwork visumNetwork) {
		super(simulationDates);
		this.walking = walking;
		this.visumNetwork = visumNetwork;
		mapping = new HashMap<>();
	}

	private PublicTransportFactory factory() {
		return factory;
	}

	@Override
	protected void createFactory(Stations stations) {
		factory = new VisumPublicTransportFactory(stations);
	}

	@Override
	public StationFinder createStationFinder(Stations stations) {
		return stations.finder(this::search);
	}

	private ShortestPathSearch search(Map<Node, Station> targetNodes) {
		VisumLinkFactory walkLinkFactory = new VisumWalkLinkFactory();
		return DijkstraSearch.from(visumNetwork, walkLinkFactory, maximumDuration, walking,
				targetsToResolve, targetNodes.keySet());
	}

	@Override
	protected Stations convertStations() {
		TransferWalkTime walkTime = walkTimeResolver();
		return stations(walkTime);
	}

	private TransferWalkTime walkTimeResolver() {
		return VisumWalkTime.from(visumWalkTimes(), visumStopPoints().values());
	}

	Map<StopAreaPair, VisumPtTransferWalkTimes> visumWalkTimes() {
		return visumNetwork.walkTimes;
	}

	Map<Integer, VisumPtStopPoint> visumStopPoints() {
		return visumNetwork.ptStopPoints;
	}

	private Stations stations(TransferWalkTime walkTime) {
		Map<Integer, List<Node>> nodesForStations = nodesForStations();
		List<Station> stations = new LinkedList<>();
		for (VisumPtStop stop : visumStations().values()) {
			List<Node> nodes = nodesForStations.getOrDefault(stop.id, emptyList());
			StationFromVisum station = new StationFromVisum(stop.id, walkTime, nodes);
			stations.add(station);
		}
		return Stations.from(stations);
	}

	private Map<Integer, List<Node>> nodesForStations() {
		return visumStopAreas().values().stream().collect(
				groupingBy(area -> area.stop.id, mapping(area -> area.node, toList())));
	}

	Map<Integer, VisumPtStopArea> visumStopAreas() {
		return visumNetwork.ptStopAreas;
	}

	Map<Integer, VisumPtStop> visumStations() {
		return visumNetwork.ptStops;
	}

	@Override
	protected ModifiableJourneys convertJourneys(StopPoints stopPoints) {
		JourneyTemplates timeProfiles = journeyTemplates(stopPoints);
		ModifiableJourneys journeys = journeys();
		for (Time day : simulationDates()) {
			for (VisumPtVehicleJourney visum : visumNetwork.ptVehicleJourneys) {
				Journey journey = journeys.add(visum, timeProfiles, factory, day);
				addToMapping(visum, journey);
			}
		}
		return journeys;
	}

	private void addToMapping(VisumPtVehicleJourney visum, Journey journey) {
		Time simulationStart = simulationStart();
		JourneyKey key = JourneyKey.from(visum);
		for (Connection connection : journey.connections().asCollection()) {
			int departure = connection.departure().differenceTo(simulationStart).seconds();
			JourneyKey derived = key.derive(departure);
			mapping.put(derived, journey);
		}
		mapping.put(key, journey);
	}

	public Time simulationStart() {
		return simulationDates().get(0);
	}

	private JourneyTemplates journeyTemplates(VisumStopResolver stopPoints) {
		JourneyTemplates templates = new JourneyTemplates();
		for (VisumPtTimeProfile visum : visumNetwork.ptTimeProfiles.values()) {
			templates.add(visum.route.direction, visum.name, visum.route.name, visum.route.line.name,
					JourneyTemplate.from(visum, stopPoints));
		}
		return templates;
	}

	ModifiableJourneys journeys() {
		return new ModifiableJourneys();
	}

	@Override
	protected Connections convertConnections(StopPoints stopPoints, ModifiableJourneys journeys) {
		return journeys.connections();
	}

	Connections connections() {
		return new Connections();
	}

	@Override
	protected StopPoints convertStopPoints(Stations stations) {
		ShortestPathSearch search = shortestPathSearch();
		NeighbourhoodCoupler walkLinks = walkLinks(search);
		return stopPoints(walkLinks, factory());
	}

	private ShortestPathSearch shortestPathSearch() {
		VisumLinkFactory walkLinkFactory = new VisumWalkLinkFactory();
		return DijkstraSearch.from(visumNetwork, walkLinkFactory, maximumDuration, walking);
	}

	private NeighbourhoodCoupler walkLinks(ShortestPathSearch search) {
		int transferWalkTypeId = 7;
		VisumLinkType transferWalkType = visumNetwork.getLinkType(transferWalkTypeId);
		return VisumWalkLinks.from(visumNetwork.links, visumStopAreas(),
				visumStopPoints().values(), search, transferWalkType);
	}

	private StopPoints stopPoints(NeighbourhoodCoupler walkLinks, PublicTransportFactory factory) {
		StopPoints stopPoints = new StopPoints();
		for (VisumPtStopPoint point : visumStopPoints().values()) {
			Stop stop = factory.stopFrom(point);
			stopPoints.add(stop);
		}
		stopPoints.initialiseNeighbourhood(walkLinks);
		return stopPoints;
	}
	
	@Override
	public NodeResolver nodeResolver() {
		return visumNetwork.nodes::get;
	}

	public Map<JourneyKey, Journey> journeyMapping() {
		return mapping;
	}

}

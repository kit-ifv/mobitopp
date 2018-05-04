package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import static java.util.Collections.emptyList;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.local.ChargingType;
import edu.kit.ifv.mobitopp.dataimport.DefaultPower;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.populationsynthesis.LocalZoneRepository;
import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.PublicTransportConverter;
import edu.kit.ifv.mobitopp.simulation.publictransport.PublicTransportFromVisum;
import edu.kit.ifv.mobitopp.simulation.publictransport.PublicTransportTimetable;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.ProfileBuilder;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.StopWatch;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumNetworkReader;
import edu.kit.ifv.mobitopp.visum.VisumReader;

public class MatrixGenerator {

	private static final Time day = new SimpleTime();
	private final File visumFile;
	private final File attractivityDataFile;

	private PublicTransportTimetable publicTransport;
	private VisumNetwork network;
	private ProfileBuilder builder;
	private ZoneRepository zoneRepository;
	private SimpleRoadNetwork roadNetwork;

	public MatrixGenerator(File visumFile, File attractivityDataFile) {
		super();
		this.visumFile = visumFile;
		this.attractivityDataFile = attractivityDataFile;
	}

	private void generateTo(File outputFolder) throws IOException {
		StopWatch stopWatch = new StopWatch(LocalDateTime::now);
		stopWatch.start();
		loadPublicTransport();
		stopWatch.measurePoint("Loading public transport");
		loadZones();
		stopWatch.measurePoint("Loading zones");
		ZonesToStops zonesToStops = assignStopsToZones();
		stopWatch.measurePoint("Assign stops to zones");
		Matrices matrices = zonesToStops.calculateMatricesAt(day, builder);
		matrices.fixInnerZoneTime(network.zones.values(), zoneRepository);
		stopWatch.measurePoint("Calculate Matrices");
		matrices.saveTo(outputFolder);
		stopWatch.measurePoint("Serialise matrices");
		stopWatch.forEach(this::log);
	}

	private void loadZones() {
		zoneRepository = LocalZoneRepository.from(network, roadNetwork, Integer.MAX_VALUE,
				ChargingType.limited, DefaultPower.zero, attractivityDataFile);
	}

	ZonesToStops assignStopsToZones() {
		ZonesToStops zonesToStops = zonesToStops();
		Collection<Stop> stops = stops();
		for (Stop stop : stops) {
			zonesToStops.assign(stop);
		}
		return zonesToStops;
	}

	List<Zone> zones() {
		return zoneRepository.getZones();
	}

	Collection<Stop> stops() {
		return publicTransport.stops();
	}

	ZonesToStops zonesToStops() {
		return new ZonesToStops(zones());
	}

	private void loadPublicTransport() {
		System.out.println("visum network file: " + visumFile.getAbsolutePath());
		loadNetwork();
		PublicTransportConverter converter = new PublicTransportFromVisum(emptyList(), day, network);
		publicTransport = converter.convert();
		Collection<Connection> connections = publicTransport.createConnections();
		builder = ProfileBuilder.from(connections);
	}

	private void loadNetwork() {
		VisumReader reader = new VisumReader();
		VisumNetworkReader networkReader = new VisumNetworkReader(reader);
		network = networkReader.readNetwork(visumFile);
		roadNetwork = new SimpleRoadNetwork(network);
	}

	private void log(String label, Duration runtime) {
		System.out.println(label + ": " + runtime);
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 3) {
			System.out.println(
					"Usage: ... <visum file> <attractivity data file> <output folder>");
			System.exit(-1);
		}

		long start = System.currentTimeMillis();
		File visumFile = new File(args[0]);
		File attractivityDataFile = new File(args[1]);
		File outputFolder = new File(args[2]);

		MatrixGenerator generator = new MatrixGenerator(visumFile, attractivityDataFile);
		generator.generateTo(outputFolder);
		long end = System.currentTimeMillis();
		Duration runtime = Duration.ofMillis(end - start);
		System.out.println("Runtime: " + runtime);
	}
}

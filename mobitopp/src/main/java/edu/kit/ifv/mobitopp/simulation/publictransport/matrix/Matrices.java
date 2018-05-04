package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.FloatMatrix;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Profile;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.ProfileBuilder;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumMatrixParser;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class Matrices {

	private static final long hours = Duration.ofDays(1).toHours();
	private static final String fileExtension = ".mtx";
	private final TravelTimes travelTimes;

	public Matrices(TravelTimes travelTimes) {
		super();
		this.travelTimes = travelTimes;
	}

	public void fixInnerZoneTime(Collection<VisumZone> visumZones, ZoneRepository zoneRepository) {
		Map<String, VisumZone> zoneMapping = visumZones
				.stream()
				.collect(toMap(zone -> "Z" + zone.id, Function.identity()));
		for (Zone zone : zoneRepository.getZones()) {
			String id = zone.getId();
			VisumZone visumZone = zoneMapping.get(id);
			float seconds = visumZone.innerZonePublicTransport;
			travelTimes.fixInnerZoneTime(zone, seconds);
		}
	}

	public void saveTo(File outputFolder) throws IOException {
		for (Integer hour : travelTimes.hours()) {
			save(outputFolder, hour);
		}
	}

	private void save(File toOutputFolder, int hour) throws IOException {
		toOutputFolder.mkdirs();
		try (BufferedWriter toOutput = write(valueOf(hour), toOutputFolder)) {
			writeMatrixOf(hour, toOutput);
		}
	}

	private void writeMatrixOf(int hour, BufferedWriter writer) throws IOException {
		String serialised = serialize(matrix(hour));
		write(writer, serialised);
	}

	protected String serialize(FloatMatrix matrix) {
		return VisumMatrixParser.serialize(matrix);
	}

	protected FloatMatrix matrix(int hour) {
		return travelTimes.matrixIn(hour).toFloatMatrix();
	}

	private void write(BufferedWriter writer, String line) throws IOException {
		writer.write(line);
		writer.newLine();
	}

	BufferedWriter write(String slice, File outputFolder) throws IOException {
		outputFolder.mkdirs();
		return new BufferedWriter(new FileWriter(new File(outputFolder, slice + fileExtension)));
	}

	public void logProfiles() {
		for (Profile profile : travelTimes.profiles()) {
			System.out.println(profile);
		}
	}

	public static Matrices from(
			Time day, ProfileBuilder builder, Collection<Zone> zones, Function<Zone, Stop> stop) {
		MatrixCalculator matrixCalculator = new MatrixCalculator(zones, builder, stop, day);
		return new Matrices(matrixCalculator.createMatrices(hours));
	}

}
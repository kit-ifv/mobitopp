package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.Timetable;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class Read implements ProfileReader {

	static final String entrySeparator = "|";
	static final String endOfEntry = ";";
	private static final int separatorWidth = 1;
	private static final int startOfId = 0;
	private final Timetable timetable;
	private final BufferedReader reader;
	private String currentEntry;

	public Read(BufferedReader reader, Timetable timetable) {
		super();
		this.reader = reader;
		this.timetable = timetable;
	}

	public static ProfileReader from(File file, Timetable timetable) throws FileNotFoundException {
		return new Read(new BufferedReader(new FileReader(file)), timetable);
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	@Override
	public Stop readStop() throws IOException {
		return timetable.stopFor(id());
	}

	private int id() {
		String id = currentEntry.substring(startOfId, endOfId());
		return parseInt(id);
	}

	private int endOfId() {
		return currentEntry.indexOf(endOfEntry);
	}

	@Override
	public ArrivalTimeFunction readFunction() throws IOException {
		String function = currentEntry.substring(functionStart());
		ArrivalTimeFunction arrival = newFunction();
		deserialize(function, arrival, timetable);
		return arrival;
	}
	
	private void deserialize(String function, ArrivalTimeFunction arrival, Timetable timetable) {
		StringTokenizer serialization = new StringTokenizer(function, endOfEntry);
		while (serialization.hasMoreTokens()) {
			FunctionEntry entry = newEntry(serialization.nextToken(), timetable);
			arrival.addDeserialized(entry);
		}
	}

	FunctionEntry newEntry(String serializedEntry, Timetable timetable) {
		StringTokenizer parts = new StringTokenizer(serializedEntry, entrySeparator);
		Time departure = SimpleTime.ofSeconds(parseLong(parts.nextToken()));
		Time arrivalAtTarget = SimpleTime.ofSeconds(parseLong(parts.nextToken()));
		Connection connection = timetable.connectionFor(connectionIdOf(parts.nextToken()));
		return newEntry(departure, arrivalAtTarget, connection);
	}

	private ConnectionId connectionIdOf(String nextToken) {
		return ConnectionId.of(parseInt(nextToken));
	}

	private FunctionEntry newEntry(
			Time departure, Time arrivalAtTarget, Connection connection) {
		return new FunctionEntry(departure, arrivalAtTarget, connection);
	}

	private int functionStart() {
		return endOfId() + separatorWidth;
	}

	ArrivalTimeFunction newFunction() {
		return new ArrivalTimeFunction();
	}

	@Override
	public boolean next() throws IOException {
		currentEntry = reader.readLine();
		return null != currentEntry;
	}

}

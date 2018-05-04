package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.Timetable;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ReadBinary implements ProfileReader {

	private final Timetable timetable;
	private final DataInputStream input;

	public ReadBinary(DataInputStream input, Timetable timetable) {
		super();
		this.input = input;
		this.timetable = timetable;
	}

	public static ProfileReader from(File file, Timetable timetable) throws FileNotFoundException {
		return new ReadBinary(new DataInputStream(new FileInputStream(file)), timetable);
	}

	@Override
	public void close() throws IOException {
		input.close();
	}

	@Override
	public Stop readStop() throws IOException {
		return timetable.stopFor(id());
	}

	private int id() throws IOException {
		return input.readInt();
	}

	@Override
	public ArrivalTimeFunction readFunction() throws IOException {
		ArrivalTimeFunction arrivalFunction = newFunction();
		int numberOfEntries = input.readInt();
		for (int current = 0; current < numberOfEntries; current++) {
			Time departure = nextTime();
			Time arrival = nextTime();
			Connection connection = nextConnection();
			FunctionEntry entry = new FunctionEntry(departure, arrival, connection);
			arrivalFunction.addDeserialized(entry);
		}
		return arrivalFunction;
	}

	private Connection nextConnection() throws IOException {
		return timetable.connectionFor(input.readInt());
	}

	private Time nextTime() throws IOException {
		return SimpleTime.ofSeconds(input.readInt());
	}

	ArrivalTimeFunction newFunction() {
		return new ArrivalTimeFunction();
	}

	@Override
	public boolean next() throws IOException {
		return 0 < input.available();
	}

}

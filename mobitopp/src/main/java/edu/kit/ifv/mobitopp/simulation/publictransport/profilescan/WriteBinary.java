package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WriteBinary implements ProfileWriter {

	private final DataOutputStream output;

	public WriteBinary(DataOutputStream stream) {
		super();
		this.output = stream;
	}

	public static ProfileWriter to(File file) throws IOException {
		return new WriteBinary(new DataOutputStream(new FileOutputStream(file)));
	}

	@Override
	public void close() throws IOException {
		output.close();
	}

	@Override
	public void write(Stop stop, ArrivalTimeFunction function) {
		try {
			output.writeInt(stop.id());
			output.writeInt(function.size());
			function.forEach(this);
		} catch (IOException e) {
			throw warn(new RuntimeException(e), log);
		}
	}

	@Override
	public void write(FunctionEntry entry) {
		try {
			writeEntry(entry);
		} catch (IOException e) {
			throw warn(new RuntimeException(e), log);
		}
	}

	private void writeEntry(FunctionEntry entry) throws IOException {
		output.writeInt(asSeconds(entry.departure()));
		output.writeInt(asSeconds(entry.arrivalAtTarget()));
		output.writeInt(entry.connection().id().asInteger());
	}

	private int asSeconds(Time departure) {
		return Math.toIntExact(departure.toSeconds());
	}

}

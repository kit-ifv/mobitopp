package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;

import edu.kit.ifv.mobitopp.publictransport.model.Neighbourhood;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;

class DefaultStopSerializer extends BaseSerializer implements StopSerializer {

	private final BufferedWriter stopOutput;
	private final BufferedWriter footpathOutput;
	private final HashMap<Stop, Integer> numberOfNeighbours;
	private final StopFormat stopFormat;
	private final TransferFormat transferFormat;

	DefaultStopSerializer(BufferedWriter stopOutput, BufferedWriter footpathOutput) {
		super();
		this.stopOutput = stopOutput;
		this.footpathOutput = footpathOutput;
		numberOfNeighbours = new HashMap<>();
		stopFormat = new CsvStopFormat();
		transferFormat = new CsvTransferFormat();
	}

	static DefaultStopSerializer at(TimetableFiles timetableFiles) throws IOException {
		return new DefaultStopSerializer(timetableFiles.stopWriter(), timetableFiles.footpathWriter());
	}

	@Override
	public void serialize(Stop stop) {
		try {
			write(stop, stopOutput);
			writeFootpath(stop, footpathOutput);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	private void write(Stop stop, BufferedWriter toOutput) throws IOException {
		String serialized = stopFormat().serialize(stop);
		toOutput.write(serialized);
		toOutput.newLine();
	}

	StopFormat stopFormat() {
		return stopFormat;
	}

	private void writeFootpath(Stop stop, BufferedWriter toOutput) throws IOException {
		Neighbourhood neighbours = stop.neighbours();
		for (Stop neighbour : neighbours) {
			RelativeTime walkTime = neighbours.walkTimeTo(neighbour).get();
			writeTransfer(toOutput, stop, neighbour, walkTime);
		}
		numberOfNeighbours.put(stop, neighbours.size());
	}

	private void writeTransfer(
			BufferedWriter toOutput, Stop stop, Stop neighbour, RelativeTime walkTime)
			throws IOException {
		String serialized = transferFormat().serialize(stop, neighbour, walkTime);
		toOutput.write(serialized);
		toOutput.newLine();
	}

	TransferFormat transferFormat() {
		return transferFormat;
	}

	public void close() throws IOException {
		stopOutput.close();
		footpathOutput.close();
	}

	@Override
	public void writeHeader() throws IOException {
		writeStopHeader();
		writeFootpathHeader();
	}

	private void writeStopHeader() throws IOException {
		stopOutput.write(stopFormat().header());
		stopOutput.newLine();
	}

	private void writeFootpathHeader() throws IOException {
		footpathOutput.write(transferFormat().header());
		footpathOutput.newLine();
	}
}

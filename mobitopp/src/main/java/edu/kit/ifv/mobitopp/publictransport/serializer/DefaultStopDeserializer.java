package edu.kit.ifv.mobitopp.publictransport.serializer;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;

class DefaultStopDeserializer extends BaseDeserializer implements StopDeserializer {

	private final File stopInput;
	private final File transferInput;
	private final StopFormat stopFormat;
	private final TransferFormat transferFormat;

	DefaultStopDeserializer(File stopInput, File transferInput) {
		super();
		this.stopInput = stopInput;
		this.transferInput = transferInput;
		stopFormat = new CsvStopFormat();
		transferFormat = new CsvTransferFormat();
	}

	static DefaultStopDeserializer at(TimetableFiles timetableFiles) {
		return new DefaultStopDeserializer(timetableFiles.stopFile(), timetableFiles.transferFile());
	}

	StopFormat stopFormat() {
		return stopFormat;
	}

	@Override
	public Stop deserializeStop(String serializedStop, StationResolver stationResolver) {
		return stopFormat().deserialize(serializedStop, stationResolver);
	}

	@Override
	public List<String> stops() {
		try {
			return stopLines();
		} catch (IOException e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	private List<String> stopLines() throws IOException {
		return removeHeaderFrom(stopInput).collect(toList());
	}

	TransferFormat transferFormat() {
		return transferFormat;
	}

	StopTransfer deserializeTransfer(String serialized, StopResolver stopResolver) {
		return transferFormat().deserialize(serialized, stopResolver);
	}

	@Override
	public NeighbourhoodCoupler neighbourhoodCoupler(StopResolver stopResolver) {
		List<StopTransfer> transfers = readTransfers(stopResolver);
		return TransferCoupler.from(transfers);
	}

	private List<StopTransfer> readTransfers(StopResolver stopResolver) {
		try {
			return transferLines(stopResolver);
		} catch (IOException e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	private List<StopTransfer> transferLines(StopResolver stopResolver) throws IOException {
		return removeHeaderFrom(transferInput)
				.map(line -> deserializeTransfer(line, stopResolver))
				.collect(toList());
	}

}

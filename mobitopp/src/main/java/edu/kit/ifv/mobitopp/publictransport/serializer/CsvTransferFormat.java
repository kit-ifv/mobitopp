package edu.kit.ifv.mobitopp.publictransport.serializer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;

class CsvTransferFormat extends CsvFormat implements TransferFormat {

	private static final int stopIndex = 0;
	private static final int neighbourIndex = 1;
	private static final int walkTimeIndex = 2;

	@Override
	public String header() {
		return "departure_stop;arrival_stop;duration";
	}

	@Override
	public String serialize(Stop stop, Stop neighbour, RelativeTime walkTime) {
		StringBuilder transfer = new StringBuilder();
		transfer.append(stop.id());
		transfer.append(separator);
		transfer.append(neighbour.id());
		transfer.append(separator);
		transfer.append(asSeconds(walkTime));
		return transfer.toString();
	}

	@Override
	public StopTransfer deserialize(String serialized, StopResolver stopResolver) {
		String[] fields = serialized.split(separator);
		Stop stop = stopOf(stopResolver, fields);
		Stop neighbour = neighbourOf(stopResolver, fields);
		RelativeTime walkTime = walkTimeOf(fields);
		return new StopTransfer(stop, neighbour, walkTime);
	}

	private Stop neighbourOf(StopResolver stopResolver, String[] fields) {
		int internalId = Integer.parseInt(fields[neighbourIndex]);
		return stopResolver.resolve(internalId);
	}

	private Stop stopOf(StopResolver stopResolver, String[] fields) {
		int internalId = Integer.parseInt(fields[stopIndex]);
		return stopResolver.resolve(internalId);
	}

	private RelativeTime walkTimeOf(String[] fields) {
		int walkTime = Integer.parseInt(fields[walkTimeIndex]);
		return RelativeTime.ofSeconds(walkTime);
	}

}

package edu.kit.ifv.mobitopp.publictransport.serializer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;

interface TransferFormat {

	String serialize(Stop stop, Stop neighbour, RelativeTime walkTime);

	StopTransfer deserialize(String serialized, StopResolver stopResolver);

	String header();

}

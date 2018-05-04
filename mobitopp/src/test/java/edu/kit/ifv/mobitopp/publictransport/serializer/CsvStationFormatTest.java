package edu.kit.ifv.mobitopp.publictransport.serializer;

import static edu.kit.ifv.mobitopp.network.NodeBuilder.node;
import static edu.kit.ifv.mobitopp.publictransport.serializer.CsvFormat.separator;
import static edu.kit.ifv.mobitopp.publictransport.serializer.CsvStationFormat.nodeSeparator;
import static edu.kit.ifv.mobitopp.simulation.publictransport.StationBuilder.station;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.publictransport.model.Station;

public class CsvStationFormatTest {

	private Station station;
	private int stationId;
	private int node1Id;
	private int node2Id;
	private NodeResolver nodeResolver;
	private Node node1;
	private Node node2;
	private List<Node> nodes;

	@Before
	public void initialise() throws Exception {
		stationId = 0;
		node1Id = 1;
		node2Id = 2;
		node1 = node().withId(node1Id).build();
		node2 = node().withId(node2Id).build();
		nodes = asList(node1, node2);
		station = station().with(stationId).at(nodes).build();
		nodeResolver = mock(NodeResolver.class);
		when(nodeResolver.resolve(node1Id)).thenReturn(node1);
		when(nodeResolver.resolve(node2Id)).thenReturn(node2);
	}

	@Test
	public void serializeStation() throws Exception {
		String serialized = serialize(station);

		assertThat(serialized, is(equalTo(stationId + separator + node1Id + nodeSeparator + node2Id)));
	}

	@Test
	public void deserialize() throws Exception {
		String serialized = serialize(station);

		Station deserialized = deserialize(serialized);

		assertIdOf(deserialized);
		assertNodesOf(deserialized);
	}

	private Station deserialize(String serialized) {
		Station deserialized = format().deserialize(serialized, nodeResolver);
		return deserialized;
	}

	private void assertIdOf(Station deserialized) {
		assertThat(deserialized.id(), is(equalTo(stationId)));
	}

	private void assertNodesOf(Station deserialized) {
		List<Node> deserializedNodes = new ArrayList<>();
		Consumer<Node> consumer = deserializedNodes::add;
		deserialized.forEachNode(consumer);
		assertThat(deserializedNodes , contains(node1, node2));
	}

	private String serialize(Station station) {
		return format().serialize(station);
	}

	private CsvStationFormat format() {
		return new CsvStationFormat();
	}
}

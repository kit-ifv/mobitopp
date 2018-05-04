package edu.kit.ifv.mobitopp.simulation.publictransport;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;

public class RouteTest {

	@Test
	public void buildsUp() {
		Collection<Connection> connections = new ArrayList<>();
		connections.add(Data.fromSomeToAnother());
		connections.add(Data.fromAnotherToOther());

		Route route = Route.from(connections);

		assertThat(route, contains(Data.someStop(), Data.anotherStop(), Data.otherStop()));
	}
	
	@Test
	public void buildsUpEmptyRoute() {
		List<Connection> connections = Collections.emptyList();
		
		Route route = Route.from(connections);
		
		assertThat(route, is(emptyIterable()));
	}
}

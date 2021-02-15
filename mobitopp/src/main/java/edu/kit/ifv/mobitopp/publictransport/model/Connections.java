package edu.kit.ifv.mobitopp.publictransport.model;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;
import static java.util.Collections.unmodifiableCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Connections {

	private final Map<ConnectionId, Connection> idToConnection;
	private final List<Connection> connections;

	public Connections() {
		super();
		connections = new ArrayList<>();
		idToConnection = new HashMap<>();
	}

	public void add(Connection connection) {
		if (connection.isValid()) {
			connections.add(connection);
			idToConnection.put(connection.id(), connection);
		}
	}

	public void addAll(Connections newConnections) {
		connections.addAll(newConnections.connections);
		idToConnection.putAll(newConnections.idToConnection);
	}

	public Connection get(ConnectionId id) {
		return idToConnection.get(id);
	}

	public Collection<Connection> asCollection() {
		return unmodifiableCollection(connections);
	}

	public void apply(ConnectionConsumer consumer) {
		connections.stream().forEach(consumer::accept);
	}

	int size() {
		return connections.size();
	}
	
	public boolean hasNextAfter(Connection connection) {
		Iterator<Connection> iterator = connections.iterator();
		while (iterator.hasNext()) {
			Connection current = iterator.next();
			if (current.equals(connection)) {
				return iterator.hasNext();
			}
		}
		return false;
	}

	public Connection nextAfter(Connection connection) {
		Iterator<Connection> iterator = connections.iterator();
		while (iterator.hasNext()) {
			Connection current = iterator.next();
			if (current.equals(connection)) {
				if (iterator.hasNext()) {
					return iterator.next();
				}
				throw warn(new IllegalArgumentException("There is no connection after: " + connection), log);
			}
		}
		throw warn(new IllegalArgumentException("There is no matching connection to: " + connection), log);
	}

	public int positionOf(Connection connection) {
		for (int index = 0; index < connections.size(); index++) {
			if (connections.get(index).equals(connection)) {
				return index;
			}
		}
		throw warn(new IllegalArgumentException("Connection is not included: " + connection), log);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connections == null) ? 0 : connections.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Connections other = (Connections) obj;
		if (connections == null) {
			if (other.connections != null) {
				return false;
			}
		} else if (!connections.equals(other.connections)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Connections [connections=" + connections + "]";
	}

}

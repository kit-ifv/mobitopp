package edu.kit.ifv.mobitopp.publictransport.model;

public class ConnectionId implements Comparable<ConnectionId> {

	private final int id;

	public ConnectionId(int id) {
		super();
		this.id = id;
	}

	public static ConnectionId of(int id) {
		return new ConnectionId(id);
	}

	@Override
	public int compareTo(ConnectionId other) {
		return Integer.compare(id, other.id);
	}

	public int asInteger() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectionId other = (ConnectionId) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.valueOf(id);
	}

}

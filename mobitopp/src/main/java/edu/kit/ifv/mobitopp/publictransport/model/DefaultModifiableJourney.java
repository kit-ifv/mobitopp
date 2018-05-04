package edu.kit.ifv.mobitopp.publictransport.model;

import edu.kit.ifv.mobitopp.time.Time;

public class DefaultModifiableJourney implements ModifiableJourney {

	private final int id;
	private final int capacity;
	private final Connections connections;
	private final Time day;
	private final TransportSystem system;

	public DefaultModifiableJourney(int id, Time day, TransportSystem system, int capacity) {
		super();
		this.id = id;
		this.day = day;
		this.system = system;
		this.capacity = capacity;
		connections = new Connections();
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public Time day() {
		return day;
	}

	@Override
	public Connections connections() {
		return connections;
	}

	@Override
	public void add(Connection connection) {
		connections.add(connection);
	}

	@Override
	public int capacity() {
		return capacity;
	}

	@Override
	public TransportSystem transportSystem() {
		return system;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + id;
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
		DefaultModifiableJourney other = (DefaultModifiableJourney) obj;
		if (day == null) {
			if (other.day != null) {
				return false;
			}
		} else if (!day.equals(other.day)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "DefaultModifiableJourney [id=" + id + ", day=" + day + "]";
	}

}

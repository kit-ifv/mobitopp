package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;

class StopJourney {

	private final Stop stop;
	private final Journey journey;

	StopJourney(Stop stop, Journey journey) {
		super();
		this.stop = stop;
		this.journey = journey;
	}

	void apply(BiConsumer<Stop, Vehicle> consumer, Vehicles vehicles) {
		Vehicle vehicle = vehicles.vehicleServing(journey);
		consumer.accept(stop, vehicle);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((journey == null) ? 0 : journey.hashCode());
		result = prime * result + ((stop == null) ? 0 : stop.hashCode());
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
		StopJourney other = (StopJourney) obj;
		if (journey == null) {
			if (other.journey != null) {
				return false;
			}
		} else if (!journey.equals(other.journey)) {
			return false;
		}
		if (stop == null) {
			if (other.stop != null) {
				return false;
			}
		} else if (!stop.equals(other.stop)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "StopJourney [stop=" + stop + ", journey=" + journey + "]";
	}

}

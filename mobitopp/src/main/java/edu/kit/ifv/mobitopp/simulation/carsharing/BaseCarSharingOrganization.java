package edu.kit.ifv.mobitopp.simulation.carsharing;

import java.io.Serializable;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.CarSharingListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseCarSharingOrganization implements CarSharingOrganization, Serializable {

	private static final long serialVersionUID = 1L;

	private transient CarSharingListener listener;
	private final String name;

	public BaseCarSharingOrganization(String name) {
		super();
		this.name = name;
	}

	@Override
	public void register(CarSharingListener listener) {
		this.listener = listener;
	}

	protected void bookCar(
			Zone zone, int availableBefore, CarSharingCar car, int availableCars, String nextAvailableCar,
			String availableCarsAsString) {
		if (null == listener) {
			log.warn("No car sharing listener/writer available.");
			return;
		}
		listener.bookCar(zone, availableBefore, car, availableCars, nextAvailableCar,
				availableCarsAsString);
	}

	protected void returnCar(
			CarSharingCar car, Zone zone, int availableBefore, int availableCars,
			CarSharingCar nextAvailableCar, String availableCarsAsString) {
		if (null == listener) {
			log.warn("No car sharing listener/writer available.");
			return;
		}
		listener.returnCar(car, zone, availableBefore, availableCars, nextAvailableCar,
				availableCarsAsString);
	}

	public String name() {
		return this.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		BaseCarSharingOrganization other = (BaseCarSharingOrganization) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BaseCarSharingOrganization [name=" + name + "]";
	}

}
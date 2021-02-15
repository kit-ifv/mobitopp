package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.car.BatteryElectricCar;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;
import edu.kit.ifv.mobitopp.simulation.car.ExtendedRangeElectricCar;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum CarType {

	conventional("conventional", ConventionalCar.class), 
	bev("battery electric", BatteryElectricCar.class), 
	erev("EREV", ExtendedRangeElectricCar.class);

	private final String name;
	private final Class<? extends Car> type;

	private CarType(String name, Class<? extends Car> type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public static CarType of(Car realCar) {
		for (CarType type : values()) {
			if (type.type.equals(realCar.getClass())) {
				return type;
			}
		}
		throw warn(new IllegalArgumentException("Unkown car type requested: " + realCar.getClass()), log);
	}
}

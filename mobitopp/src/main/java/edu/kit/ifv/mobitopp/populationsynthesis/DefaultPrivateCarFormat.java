package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.ForeignKeySerialiserFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PopulationContext;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.SerialiserFormat;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.car.DefaultPrivateCar;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public class DefaultPrivateCarFormat implements ForeignKeySerialiserFormat<PrivateCar> {

	private static final int householdIndex = 0;
	private static final int mainUserIndex = 1;
	private static final int personalUserIndex = 2;
	private static final int carTypeIndex = 3;
	private static final int carAttributeStart = 4;
	
	private final Map<CarType, SerialiserFormat<? extends Car>> formats;

	public DefaultPrivateCarFormat() {
		super();
		formats = new HashMap<>();
	}

	public <T extends Car> void register(CarType carType, SerialiserFormat<T> format) {
		formats.put(carType, format);
	}
	
	@Override
	public List<String> header() {
		return asList("ownerId", "mainUserId", "personalUserId", "carType", "car attributes");
	}

	@Override
	public List<String> prepare(PrivateCar car, PopulationContext context) {
		int personalUser = null != car.personalUser() ? car.personalUser().getOid() : -1;
		ArrayList<String> attributes = new ArrayList<>();
		attributes.add(valueOf(car.owner().getOid()));
		attributes.add(valueOf(car.mainUser().getOid()));
		attributes.add(valueOf(personalUser));
		attributes.add(valueOf(carTypeOf(car)));
		attributes.addAll(carAttributes(car));
		return attributes;
	}

	private List<String> carAttributes(PrivateCar car) {
		CarType carType = carTypeOf(car);
		if (formats.containsKey(carType)) {
			return formats.get(carType).prepare(cast(car.car()));
		}
		return emptyList();
	}

	private CarType carTypeOf(PrivateCar car) {
		CarType carType = CarType.of(car.car());
		return carType;
	}

	@SuppressWarnings("unchecked")
	private static <T extends Car> T cast(Car realCar) {
		return (T) realCar;
	}

	@Override
	public Optional<PrivateCar> parse(List<String> data, PopulationContext context) {
		Optional<Household> household = householdOf(data, context);
		Optional<Person> mainUser = mainUserOf(data, context);
		Optional<Person> personalUser = personalUserOf(data, context);
		Optional<? extends Car> car = parseCar(data);
		return household
				.flatMap(h -> createCar(mainUser, personalUser, car, h));
	}

	private Optional<PrivateCar> createCar(
			Optional<Person> mainUser, Optional<Person> personalUser, Optional<? extends Car> car,
			Household household) {
		return mainUser.flatMap(
				main -> personalUser.flatMap(
						personal -> car.map(
								c -> new DefaultPrivateCar(c, household, main, personal))));
	}

	private Optional<? extends Car> parseCar(List<String> data) {
		CarType carType = carTypeOf(data);
		return formats.get(carType).parse(data.subList(carAttributeStart, data.size()));
	}

	private CarType carTypeOf(List<String> data) {
		String carType = data.get(carTypeIndex);
		return CarType.valueOf(carType);
	}

	private Optional<Household> householdOf(List<String> data, PopulationContext context) {
		int oid = Integer.parseInt(data.get(householdIndex));
		return context.getHouseholdByOid(oid);
	}

	private Optional<Person> mainUserOf(List<String> data, PopulationContext context) {
		int oid = Integer.parseInt(data.get(mainUserIndex));
		return context.getPersonByOid(oid);
	}

	private Optional<Person> personalUserOf(List<String> data, PopulationContext context) {
		int oid = Integer.parseInt(data.get(personalUserIndex));
		return context.getPersonByOid(oid);
	}

}

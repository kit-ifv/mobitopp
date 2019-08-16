package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Collections.emptyList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.ForeignKeySerialiserFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PopulationContext;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.SerialiserFormat;
import edu.kit.ifv.mobitopp.simulation.Car;

public class DefaultPrivateCarFormat implements ForeignKeySerialiserFormat<PrivateCarForSetup> {

	private final Map<CarType, SerialiserFormat<? extends Car>> formats;
	private ColumnMapping<PrivateCarForSetup> columns;

	public DefaultPrivateCarFormat() {
		super();
		formats = new HashMap<>();
		columns = new ColumnMapping<>();
		columns.add("ownerId", e -> e.getOwner().getOid());
		columns.add("mainUserId", e -> e.getMainUser().getOid());
		columns.add("personalUserId", e -> personalUserOf(e));
		columns.add("carType", e -> carTypeOf(e));
		columns.add("car attributes", e -> carAttributes(e));
	}

	public <T extends Car> void register(CarType carType, SerialiserFormat<T> format) {
		formats.put(carType, format);
	}

	@Override
	public List<String> header() {
		return columns.header();
	}

	@Override
	public List<String> prepare(PrivateCarForSetup car) {
		return columns.prepare(car);
	}

	private int personalUserOf(PrivateCarForSetup car) {
		return null != car.getPersonalUser() ? car.getPersonalUser().getOid() : -1;
	}

	private List<String> carAttributes(PrivateCarForSetup car) {
		CarType carType = carTypeOf(car);
		if (formats.containsKey(carType)) {
			return formats.get(carType).prepare(cast(car.getCar()));
		}
		return emptyList();
	}

	private CarType carTypeOf(PrivateCarForSetup car) {
		CarType carType = CarType.of(car.getCar());
		return carType;
	}

	@SuppressWarnings("unchecked")
	private static <T extends Car> T cast(Car realCar) {
		return (T) realCar;
	}

	@Override
	public Optional<PrivateCarForSetup> parse(List<String> data, PopulationContext context) {
		Optional<HouseholdForSetup> household = householdOf(data, context);
		Supplier<PersonId> mainUser = () -> mainUserOf(data, context);
		Supplier<PersonId> personalUser = () -> personalUserOf(data, context);
		Optional<? extends Car> car = parseCar(data);
		return household.flatMap(h -> createCar(mainUser, personalUser, car, h));
	}

	private Optional<PrivateCarForSetup> createCar(
			Supplier<PersonId> mainUser, Supplier<PersonId> personalUser, Optional<? extends Car> car,
			HouseholdForSetup household) {
		return car
				.map(c -> new DefaultPrivateCarForSetup(c, household.getId(), mainUser.get(),
						personalUser.get()));
	}

	private Optional<? extends Car> parseCar(List<String> data) {
		CarType carType = carTypeOf(data);
		int attributesStart = columns.indexOf("car attributes");
		return formats.get(carType).parse(data.subList(attributesStart, data.size()));
	}

	private CarType carTypeOf(List<String> data) {
		String carType = columns.get("carType", data).asString();
		return CarType.valueOf(carType);
	}

	private Optional<HouseholdForSetup> householdOf(List<String> data, PopulationContext context) {
		int oid = columns.get("ownerId", data).asInt();
		return context.getHouseholdForSetupByOid(oid);
	}

	private PersonId mainUserOf(List<String> data, PopulationContext context) {
		int oid = columns.get("mainUserId", data).asInt();
		return context
				.getPersonBuilderByOid(oid)
				.map(PersonBuilder::getId)
				.orElseThrow(() -> new IllegalArgumentException("Main missing with id: " + oid));
	}

	private PersonId personalUserOf(List<String> data, PopulationContext context) {
		int oid = columns.get("personalUserId", data).asInt();
		return context.getPersonBuilderByOid(oid).map(PersonBuilder::getId).orElse(null);
	}

}

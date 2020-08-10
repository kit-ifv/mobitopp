package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Objects;

import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.HouseholdForDemand;
import edu.kit.ifv.mobitopp.simulation.car.DefaultPrivateCar;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public class DefaultPrivateCarForSetup implements PrivateCarForSetup {

	private final Car car;
	private final HouseholdId owner;
	private final PersonId mainUser;
	private final PersonId personalUser;

	public DefaultPrivateCarForSetup(
			Car car, HouseholdId owner, PersonId mainUser, PersonId personalUser) {
		Objects.requireNonNull(owner);
		Objects.requireNonNull(mainUser);
		this.car = car;
		this.owner = owner;
		this.mainUser = mainUser;
		this.personalUser = personalUser;
	}

	@Override
	public Car getCar() {
		return car;
	}

	@Override
	public HouseholdId getOwner() {
		return owner;
	}

	@Override
	public PersonId getMainUser() {
		return mainUser;
	}

	@Override
	public PersonId getPersonalUser() {
		return personalUser;
	}
	
	@Override
	public boolean isPersonal() {
		return null != personalUser;
	}
	
	@Override
	public PrivateCarForSetup copy(
			int id, HouseholdId owner, PersonId mainUser, PersonId personalUser) {
		Car copyCar = car.copy(id);
		return new DefaultPrivateCarForSetup(copyCar, owner, mainUser, personalUser);
	}

	@Override
	public PrivateCar toCar(HouseholdForDemand household) {
		if (owner.equals(household.getId())) {
			return new DefaultPrivateCar(car, household, mainUser, personalUser);
		}
		throw new IllegalArgumentException(String
				.format("Owner of car (%s) and given household (%s) must be equal", owner,
						household.getId()));
	}

}

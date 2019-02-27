package edu.kit.ifv.mobitopp.simulation.car;

import java.io.Serializable;

import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Household;

public class DefaultPrivateCar extends CarDecorator implements PrivateCar, Car, Serializable {

	private static final long serialVersionUID = 1L;

	protected final Household owner;
	protected final PersonId mainUser;
	protected final PersonId personalUser;

	public DefaultPrivateCar(
		Car car,
		Household owner,
		PersonId mainUser,
		PersonId personalUser
	) {
		super(car);

		assert owner != null;
		assert mainUser != null;

		this.owner = owner;
		this.mainUser = mainUser;
		this.personalUser = personalUser;
	}
	
	public Car car() {
		return car;
	}

	public Household owner() {
		return this.owner;
	}

	public boolean isPersonal() {
		return this.personalUser != null;
	}

	public PersonId personalUser() {
		return this.personalUser;
	}

	public PersonId mainUser() {
		return this.mainUser;
	}

	@Override
	public String forLogging() {
		StringBuffer buffer = new StringBuffer();

		String personal = isPersonal() ? "1" : "0";
	
		buffer.append(owner.getOid() + "; ");
		buffer.append(mainUser.getOid() + "; ");
		buffer.append(personal + "; ");
		buffer.append(car.forLogging());

		return buffer.toString();
	}

	public String statusForLogging() {

		return car.statusForLogging();
	}
}

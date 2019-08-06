package edu.kit.ifv.mobitopp.simulation.emobility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingPerson;
import edu.kit.ifv.mobitopp.simulation.person.PersonDecorator;
import edu.kit.ifv.mobitopp.simulation.person.PersonForDemand;

public class EmobilityPerson extends PersonDecorator
		implements Person, CarSharingPerson, Serializable {

	private static final long serialVersionUID = 1L;

	public enum PublicChargingInfluencesDestinationChoice {
		ALWAYS, ONLY_WHEN_BATTERY_LOW, NEVER
	}

	protected final float eMobilityAcceptance;

	protected	final Map<String, Boolean> carSharingCustomership;

	protected final PublicChargingInfluencesDestinationChoice chargingInfluencesDestinantionChoice;

  public EmobilityPerson(
		Person person,
		float eMobilityAcceptance,
		PublicChargingInfluencesDestinationChoice chargingInfluencesDestinantionChoice,
		Map<String, Boolean> carSharingCustomership
	) {
		super(person);

		this.eMobilityAcceptance = eMobilityAcceptance;
		this.chargingInfluencesDestinantionChoice = chargingInfluencesDestinantionChoice;
		this.carSharingCustomership = Collections.unmodifiableMap(carSharingCustomership);
	}


	public boolean isCarSharingCustomer(String company) {
		if (carSharingCustomership.containsKey(company)) {
			return carSharingCustomership.get(company);
		}
		return false;
	}
	
	public Map<String, Boolean> carSharingCustomership() {
		return carSharingCustomership;
	}

	public float eMobilityAcceptance() {
		return this.eMobilityAcceptance;
	}

	public PublicChargingInfluencesDestinationChoice chargingInfluencesDestinantionChoice() {
		return this.chargingInfluencesDestinantionChoice;
	}

	public String forLogging(ImpedanceIfc impedance) {
		assert person() instanceof PersonForDemand;

		String s = person().forLogging(impedance);

		s += eMobilityAcceptance + ";";
		s += chargingInfluencesDestinantionChoice + ";";

		List<String> customer = new ArrayList<String>();

		for (String company : this.carSharingCustomership.keySet()) {

			if (isCarSharingCustomer(company)) {
				customer.add(company);
			}
		}

		s += Arrays.toString(customer.toArray());

		s += ";";

		return s;
	}

	public PersonBuilder personForSetup() {
		assert person() instanceof PersonBuilder;

		return (PersonBuilder) person();
	}

}

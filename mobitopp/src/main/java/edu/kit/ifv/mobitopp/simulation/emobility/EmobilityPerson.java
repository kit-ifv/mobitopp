package edu.kit.ifv.mobitopp.simulation.emobility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.person.PersonDecorator;
import edu.kit.ifv.mobitopp.simulation.person.PersonForDemand;

public class EmobilityPerson extends PersonDecorator implements Person, Serializable {

	private static final long serialVersionUID = 1L;

	public enum PublicChargingInfluencesDestinationChoice {
		ALWAYS, ONLY_WHEN_BATTERY_LOW, NEVER
	}

	private final float eMobilityAcceptance;
	private final PublicChargingInfluencesDestinationChoice chargingInfluencesDestinantionChoice;

	public EmobilityPerson(
			final Person person, final float eMobilityAcceptance,
			final PublicChargingInfluencesDestinationChoice chargingInfluencesDestinantionChoice) {
		super(person);

		this.eMobilityAcceptance = eMobilityAcceptance;
		this.chargingInfluencesDestinantionChoice = chargingInfluencesDestinantionChoice;
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

		for (String company : person().mobilityProviderCustomership().keySet()) {

			if (isMobilityProviderCustomer(company)) {
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

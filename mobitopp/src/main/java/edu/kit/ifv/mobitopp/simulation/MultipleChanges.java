package edu.kit.ifv.mobitopp.simulation;

import java.util.List;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PersonChanger;


public class MultipleChanges implements PersonChanger {

	private final List<PersonChanger> changers;

	public MultipleChanges(List<PersonChanger> changers) {
		this.changers = changers;
	}

	@Override
	public PersonBuilder attributesOf(PersonBuilder person) {
		changers.forEach(changer -> changer.attributesOf(person));
		return person;
	}

}

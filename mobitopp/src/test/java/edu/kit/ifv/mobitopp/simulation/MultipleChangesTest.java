package edu.kit.ifv.mobitopp.simulation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PersonChanger;


public class MultipleChangesTest {

	@Test
	void usesAllChangers() throws Exception {
		PersonBuilder person = mock(PersonBuilder.class);
		PersonChanger someChanger = mock(PersonChanger.class);
		PersonChanger otherChanger = mock(PersonChanger.class);
		MultipleChanges changer = new MultipleChanges(List.of(someChanger, otherChanger));
		
		PersonBuilder changedPerson = changer.attributesOf(person);
		
		assertThat(changedPerson).isSameAs(person);
		
		verify(someChanger).attributesOf(person);
		verify(otherChanger).attributesOf(person);
	}
}

package edu.kit.ifv.mobitopp.simulation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.Population;

public class LocalPersonLoaderTest {

	private static final int id = 1;
	private Population population;

	@BeforeEach
	public void initialise() {
		population = mock(Population.class);
	}

	@Test
	public void getExistingHousehold() {
		Household existingHousehold = configureExistingHousehold();

		LocalPersonLoader loader = new LocalPersonLoader(population);

		Household household = loader.getHouseholdByOid(id);

		assertThat(household, is(equalTo(existingHousehold)));
	}

	private Household configureExistingHousehold() {
		Household existingHousehold = mock(Household.class);
		when(population.getHouseholdByOid(id)).thenReturn(Optional.of(existingHousehold));
		return existingHousehold;
	}

	@Test
	public void getMissingHousehold() {
		configureMissingHousehold();

		LocalPersonLoader loader = new LocalPersonLoader(population);

		assertThrows(NoSuchElementException.class, () -> loader.getHouseholdByOid(id));
	}

	private void configureMissingHousehold() {
		when(population.getHouseholdByOid(id)).thenReturn(Optional.empty());
	}

	@Test
	public void getExistingPerson() {
		Person existingPerson = configureExistingPerson();

		LocalPersonLoader loader = new LocalPersonLoader(population);

		Person household = loader.getPersonByOid(id);

		assertThat(household, is(equalTo(existingPerson)));
	}

	private Person configureExistingPerson() {
		Person existing = mock(Person.class);
		when(population.getPersonByOid(id)).thenReturn(Optional.of(existing));
		return existing;
	}

	@Test
	public void getMissingPerson() {
		configureMissingPerson();

		LocalPersonLoader loader = new LocalPersonLoader(population);

		assertThrows(NoSuchElementException.class, () -> loader.getPersonByOid(id));
	}
	
	private void configureMissingPerson() {
		when(population.getPersonByOid(id)).thenReturn(Optional.empty());
	}
	
	@Test
	void removePerson() throws Exception {
		Person existingPerson = configureExistingPerson();

		LocalPersonLoader loader = new LocalPersonLoader(population);

		loader.removePerson(existingPerson.getOid());

		verify(population).removePerson(existingPerson.getOid());
	}
	
	@Test
	void clearInput() throws Exception {
		LocalPersonLoader loader = new LocalPersonLoader(population);
		
		loader.clearInput();
		
		verify(population).clearLongTermData();
	}
}

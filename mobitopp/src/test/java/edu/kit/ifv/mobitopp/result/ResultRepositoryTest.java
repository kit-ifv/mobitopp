package edu.kit.ifv.mobitopp.result;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

public class ResultRepositoryTest {

	private static final Category category = new Category("category");
	private ResultRepository repository;
	private Function<Category, ResultOutput> factory;

	@SuppressWarnings("unchecked")
	@Before
	public void initialise() {
		factory = mock(Function.class);

		repository = new ResultRepository(factory);
	}

	@Test
	public void createsFileOnFirstAccess() {
		returnLogFor(category);

		ResultOutput file = repository.fileFor(category);

		assertThat(file, is(not(nullValue())));
		verify(factory).apply(category);
	}

	private void returnLogFor(Category category) {
		ResultOutput output = mock(ResultOutput.class);
		when(factory.apply(category)).thenReturn(output);
	}

	@Test
	public void cachesOutputInstances() {
		ResultOutput first = repository.fileFor(category);
		ResultOutput second = repository.fileFor(category);

		assertThat(first, is(sameInstance(second)));
		verify(factory).apply(category);
	}

	@Test
	public void closesOpenOutputs() throws IOException {
		ResultOutput notUsed = mock(ResultOutput.class);
		Category notUsedCategory = new Category("notUsed");
		when(factory.apply(notUsedCategory)).thenReturn(notUsed);
		ResultOutput used = mock(ResultOutput.class);
		when(factory.apply(category)).thenReturn(used);

		repository.fileFor(category);
		repository.close();

		verify(used).close();
		verifyZeroInteractions(notUsed);
	}

}

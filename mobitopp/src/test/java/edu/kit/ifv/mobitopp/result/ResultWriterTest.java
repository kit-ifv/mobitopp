package edu.kit.ifv.mobitopp.result;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

public class ResultWriterTest {

	private ResultOutput output;
	private ResultWriter results;
	private ResultRepository fileRepository;
	private Function<Category, ResultOutput> factory;

	@SuppressWarnings("unchecked")
	@Before
	public void initialise() {
		output = mock(ResultOutput.class);
		factory = mock(Function.class);
		when(factory.apply(any())).thenReturn(output);
		fileRepository = new ResultRepository(factory);

		results = new ResultWriter(fileRepository);
	}

	@Test
	public void writesToOutput() {
		Category category = new Category("category");
		String text = "text";

		results.write(category, text);

		verify(output).writeLine(text);
	}

	@Test
	public void close() throws IOException {
		ResultRepository repository = mock(ResultRepository.class);
		ResultWriter logManager = new ResultWriter(repository);

		logManager.close();

		verify(repository).close();
	}
}

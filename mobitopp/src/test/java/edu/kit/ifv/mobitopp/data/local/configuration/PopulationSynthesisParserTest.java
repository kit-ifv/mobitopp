package edu.kit.ifv.mobitopp.data.local.configuration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.nodes.Tag;

import edu.kit.ifv.mobitopp.data.DataSource;
import edu.kit.ifv.mobitopp.populationsynthesis.WrittenConfiguration;

public class PopulationSynthesisParserTest {

	private static final String customClass = "dataSource: !test";
	private static final File dummyFile = new File("dummy.dummy");

	private PopulationSynthesisParser parser;

	@Before
	public void initialise() {
		parser = newParser(customClass);

	}

	@Test
	public void loadCustomClass() throws IOException {
		WrittenConfiguration configuration = parse();

		DataSource creator = configuration.getDataSource();

		assertThat(creator, is(instanceOf(TestSource.class)));
	}

	private WrittenConfiguration parse() throws IOException {
		return parser.parse(dummyFile);
	}

	@Test
	public void serialiseCustomClass() throws IOException {
		WrittenConfiguration configuration = parse();

		String serialised = parser.serialise(configuration);

		assertThat(serialised, containsString(customClass));
	}

	private PopulationSynthesisParser newParser(String content) {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());
		InputStreamReader reader = new InputStreamReader(inputStream);
		return newParser(reader);
	}

	private PopulationSynthesisParser newParser(Reader reader) {
		Map<Class<?>, Tag> tags = new HashMap<>();
		tags.put(TestSource.class, new Tag("!test"));
		return new PopulationSynthesisParser(tags) {

			@Override
			protected Reader readerFor(File configurationFile) throws IOException {
				return reader;
			}
		};
	}
}

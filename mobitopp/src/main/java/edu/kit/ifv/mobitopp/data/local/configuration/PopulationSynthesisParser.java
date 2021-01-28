package edu.kit.ifv.mobitopp.data.local.configuration;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.yaml.snakeyaml.nodes.Tag;

import edu.kit.ifv.mobitopp.populationsynthesis.WrittenConfiguration;

public class PopulationSynthesisParser extends MergingParser {

	public PopulationSynthesisParser(Map<Class<?>, Tag> tags) {
		super(tags);
	}

	public WrittenConfiguration parse(File configurationFile) throws IOException {
		Reader reader = readerFor(configurationFile);
		return yaml().loadAs(reader, WrittenConfiguration.class);
	}

	public String serialise(WrittenConfiguration configuration) {
		return yaml().dump(configuration);
	}

}

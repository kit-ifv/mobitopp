package edu.kit.ifv.mobitopp.data.local.configuration;

import java.io.InputStream;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

class MatrixConfigurationSerialiser {

	private static final String timeSpanSeparator = "to";
	private static final Pattern timeSpanPatter = Pattern
			.compile("(\\d+)[ ]*" + timeSpanSeparator + "[ ]*(\\d+)");

	public StoredMatrices loadFrom(InputStream input) {
		return yaml().loadAs(input, StoredMatrices.class);
	}

	public String serialise(StoredMatrices matrices) {
		return yaml().dumpAsMap(matrices);
	}

	private static Yaml yaml() {
		ConfigurationConstructor constructor = new ConfigurationConstructor(timeSpanTag(), timeSpanPatter);
		Representer representer = new ConfigurationRepresenter(timeSpanTag(), timeSpanSeparator);
		Yaml yaml = new Yaml(constructor, representer, new DumperOptions());
		yaml.addImplicitResolver(timeSpanTag(), timeSpanPatter, "0123456789");
		return yaml;
	}

	private static Tag timeSpanTag() {
		return new Tag("!timeSpan");
	}

}

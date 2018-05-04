package edu.kit.ifv.mobitopp.data.local.configuration;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.Map;
import java.util.function.BiConsumer;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public abstract class Parser {

	private final Yaml yaml;

	public Parser(Map<Class<?>, Tag> tags) {
		super();
		yaml = new Yaml(constructor(tags), representer(tags));
	}

	private static Constructor constructor(Map<Class<?>, Tag> tags) {
		Constructor constructor = new Constructor();
		map(tags, (type, tag) -> constructor.addTypeDescription(new TypeDescription(type, tag)));
		return constructor;
	}

	private static Representer representer(Map<Class<?>, Tag> tags) {
		Representer representer = new Representer();
		map(tags, representer::addClassTag);
		return representer;
	}

	private static void map(Map<Class<?>, Tag> tags, BiConsumer<Class<?>, Tag> consumer) {
		tags.entrySet().stream().forEach(entry -> consumer.accept(entry.getKey(), entry.getValue()));
	}

	protected Reader readerFor(File configurationFile) throws IOException {
		return Files.newBufferedReader(configurationFile.toPath());
	}

	protected Yaml yaml() {
		return this.yaml;
	}

}

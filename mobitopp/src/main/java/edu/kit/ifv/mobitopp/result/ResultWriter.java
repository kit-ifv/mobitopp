package edu.kit.ifv.mobitopp.result;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.time.DateFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResultWriter implements Results, Closeable {

	private final ResultRepository files;

	ResultWriter(ResultRepository fileRepository) {
		super();
		files = fileRepository;
	}

	public static ResultWriter createDefaultWriter() {
		return create(defaultFolder());
	}

	public static File defaultFolder() {
		return new File("log");
	}

	public static ResultWriter create(File folder) {
		folder.mkdirs();
		return new ResultWriter(new ResultRepository(in(folder)));
	}

	private static Function<Category, ResultOutput> in(File folder) {
		return category -> ResultFile.create(folder, category);
	}

	public DateFormat dateFormat() {
		return new DateFormat();
	}

	@Override
	public void write(Category category, String message) {
		resultFile(category).writeLine(message);
	}

	ResultOutput resultFile(Category category) {
		return files.fileFor(category);
	}

	public void close() throws UncheckedIOException {
		try {
			files.close();
		} catch (IOException cause) {
			throw warn(new UncheckedIOException(cause), log);
		}
	}

}

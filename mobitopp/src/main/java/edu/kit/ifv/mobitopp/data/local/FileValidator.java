package edu.kit.ifv.mobitopp.data.local;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileValidator {

	private final List<File> files;

	public FileValidator(List<File> files) {
		super();
		this.files = files;
	}

	public void doExist() {
		List<File> missingFiles = files.stream().filter(missingFiles()).collect(toList());
		if (!missingFiles.isEmpty()) {
			throw warn(new IllegalArgumentException("Files are missing: " + missingFiles), log);
		}
	}

	private Predicate<File> missingFiles() {
		return file -> !file.exists();
	}

}

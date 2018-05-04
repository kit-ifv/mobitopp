package edu.kit.ifv.mobitopp.data.local;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

public class FileValidator {

	private final List<File> files;

	public FileValidator(List<File> files) {
		super();
		this.files = files;
	}

	public void doExist() {
		List<File> missingFiles = files.stream().filter(missingFiles()).collect(toList());
		if (!missingFiles.isEmpty()) {
			throw new IllegalArgumentException("Files are missing: " + missingFiles);
		}
	}

	private Predicate<File> missingFiles() {
		return file -> !file.exists();
	}

}

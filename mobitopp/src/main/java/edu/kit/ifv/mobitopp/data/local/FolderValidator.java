package edu.kit.ifv.mobitopp.data.local;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.File;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FolderValidator {

	private final File folder;

	public FolderValidator(File folder) {
		super();
		this.folder = folder;
	}

	public void isValid() {
		isNotNull();
		exists();
		isFolder();
	}


	private void isNotNull() {
		if (null == folder) {
			throw warn(new IllegalArgumentException("Folder is not specified in configuration!"), log);
		}
	}

	private void exists() {
		if (!folder.exists()) {
			throw warn(new IllegalArgumentException("Folder is missing: " + folder.getAbsolutePath()), log);
		}
	}
	
	private void isFolder() {
		if (folder.isFile()) {
			throw warn(new IllegalArgumentException("Path does not point to a folder: " + folder.getAbsolutePath()), log);
		}
	}

}

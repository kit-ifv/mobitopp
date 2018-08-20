package edu.kit.ifv.mobitopp.data.local;

import java.io.File;

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
			throw new IllegalArgumentException("Folder is not specified in configuration!");
		}
	}

	private void exists() {
		if (!folder.exists()) {
			throw new IllegalArgumentException("Folder is missing: " + folder.getAbsolutePath());
		}
	}
	
	private void isFolder() {
		if (folder.isFile()) {
			throw new IllegalArgumentException("Path does not point to a folder: " + folder.getAbsolutePath());
		}
	}

}

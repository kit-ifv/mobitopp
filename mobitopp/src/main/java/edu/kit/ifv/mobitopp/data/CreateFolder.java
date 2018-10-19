package edu.kit.ifv.mobitopp.data;

import java.io.File;
import java.io.IOException;

public class CreateFolder {

	private final File folder;

	public CreateFolder(File folder) {
		super();
		this.folder = folder;
	}

	public static CreateFolder at(File folder) {
		return new CreateFolder(folder);
	}

	public void ifMissing() throws IOException {
		if (exists()) {
			return;
		}
		boolean created = create();
		verify(created);
	}

	private boolean exists() {
		return folder.exists();
	}

	private boolean create() {
		return folder.mkdirs();
	}

	private void verify(boolean created) throws IOException {
		if (!created || !folder.exists()) {
			throw new IOException("Could not create folder at: " + folder.getAbsolutePath());
		}
	}

}

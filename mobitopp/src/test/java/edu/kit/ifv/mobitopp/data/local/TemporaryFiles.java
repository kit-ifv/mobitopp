package edu.kit.ifv.mobitopp.data.local;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.junit.rules.TemporaryFolder;

public class TemporaryFiles {

	public static File newMissingFile(TemporaryFolder baseFolder) throws IOException {
		File newFile = baseFolder.newFile();
		newFile.delete();
		assertFalse(newFile.exists());
		return newFile;
	}

}

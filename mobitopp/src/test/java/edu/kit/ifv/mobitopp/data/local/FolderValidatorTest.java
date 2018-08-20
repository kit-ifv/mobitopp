package edu.kit.ifv.mobitopp.data.local;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FolderValidatorTest {

	@Rule
	public TemporaryFolder baseFolder = new TemporaryFolder();
	private File existingFolder;
	private File missingFolder;
	private File existingFile;

	@Before
	public void initialise() throws IOException {
		existingFolder = baseFolder.newFolder();
		missingFolder = newMissingFolder();
		existingFile = baseFolder.newFile();
	}

	private File newMissingFolder() throws IOException {
		File newFile = baseFolder.newFolder();
		newFile.delete();
		assertFalse(newFile.exists());
		return newFile;
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void isNotNull() {
		new FolderValidator(null).isValid();
	}

	@Test
	public void existingFiles() {
		new FolderValidator(existingFolder).isValid();
	}

	@Test(expected = IllegalArgumentException.class)
	public void missingSomeFile() {
		new FolderValidator(missingFolder).isValid();
	}

	@Test(expected = IllegalArgumentException.class)
	public void fileIsNotAFolder() {
		new FolderValidator(existingFile).isValid();
	}

}

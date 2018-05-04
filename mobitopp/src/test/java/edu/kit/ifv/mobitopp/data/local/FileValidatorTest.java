package edu.kit.ifv.mobitopp.data.local;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileValidatorTest {

	@Rule
	public TemporaryFolder baseFolder = new TemporaryFolder();
	private List<File> existingFiles;
	private List<File> missingSomeFile;
	private List<File> missingAnotherFile;

	@Before
	public void initialise() throws IOException {
		File someFile = baseFolder.newFile();
		File anotherFile = baseFolder.newFile();
		File someMissing = TemporaryFiles.newMissingFile(baseFolder);
		File anotherMissing = TemporaryFiles.newMissingFile(baseFolder);
		existingFiles = asList(someFile, anotherFile);
		missingSomeFile = asList(someMissing, anotherFile);
		missingAnotherFile = asList(someFile, anotherMissing);
	}

	@Test
	public void existingFiles() {
		new FileValidator(existingFiles).doExist();
	}

	@Test(expected = IllegalArgumentException.class)
	public void missingSomeFile() {
		new FileValidator(missingSomeFile).doExist();
	}

	@Test(expected = IllegalArgumentException.class)
	public void missingAnotherFile() {
		new FileValidator(missingAnotherFile).doExist();
	}
}

package edu.kit.ifv.mobitopp.data;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CreateFolderTest {

	@Rule
	public final TemporaryFolder tempFolder = new TemporaryFolder();
	private File baseFolder;

	@Before
	public void initialise() throws IOException {
		baseFolder = tempFolder.newFolder();
	}

	@Test
	public void createsMissingFolder() throws IOException {
		File folder = new File(baseFolder, "sub-folder");

		CreateFolder.at(folder).ifMissing();

		assertTrue(folder.exists());
	}
	
	@Test
	public void cannotCreateExistingFolder() throws IOException {
		File existing = new File(baseFolder, "sub-folder");
		CreateFolder.at(existing).ifMissing();
		
		CreateFolder.at(existing).ifMissing();
		
		assertTrue(existing.exists());
	}

	@Test(expected=IOException.class)
	public void failsCreationOfMissingFolder() throws IOException {
		File cannotBeCreated = uncreatableFolder();
		
		CreateFolder.at(cannotBeCreated).ifMissing();
	}

	@SuppressWarnings("serial")
	private File uncreatableFolder() {
		return new File(baseFolder, "missing-folder") {

			public boolean mkdirs() {
				return false;
			};
		};
	}
}

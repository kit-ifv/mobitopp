package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.kit.ifv.mobitopp.populationsynthesis.carownership.MobilityProviderCustomerModel;

public class CarSharingParserTest {

	private static final long seed = 1;

	@Rule
	public TemporaryFolder baseFolder = new TemporaryFolder();

	private File existingFile;

	@Before
	public void initialise() throws IOException {
		existingFile = baseFolder.newFile();
	}

	@Test
	public void createOneModelPerInputFile() {
		Map<String, String> models = new LinkedHashMap<>();
		models.put("Stadtmobil", existingFile.getAbsolutePath());
		models.put("Flinkster", existingFile.getAbsolutePath());
		CarSharingParser carSharing = new CarSharingParser(models, seed);
		
		Map<String, MobilityProviderCustomerModel> customerModels = carSharing.parse();
		
		assertThat(customerModels.entrySet()).hasSize(models.size());
	}
}

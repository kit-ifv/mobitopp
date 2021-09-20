package edu.kit.ifv.mobitopp.visum.exportconfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.StandardNetfileLanguages;
import edu.kit.ifv.mobitopp.visum.Table;
import edu.kit.ifv.mobitopp.visum.reader.VisumNetworkReader;

public class NetExportConfiguration {

	private final String ptSystemCode;
	private final String carSystem;
	private final String individualWalkSystem;
	private final String publicTransportWalkSystem;

	public NetExportConfiguration(String ptSystemCode, String carSystem,
		String individualWalkSystem, String publicTransportWalkSystem) {
		super();
		this.ptSystemCode = ptSystemCode;
		this.carSystem = carSystem;
		this.individualWalkSystem = individualWalkSystem;
		this.publicTransportWalkSystem = publicTransportWalkSystem;
	}

	private void writeNetExportFileTo(Path outputPath) throws IOException {
		ReportingVisumReader reportingReader = new ReportingVisumReader(defaultValues());
		File dummyFile = new File("dummy.net");
		new VisumNetworkReader(language(), reportingReader).readNetwork(dummyFile, ptSystemCode);
		try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
			reportingReader.serialiseExportFile(line -> {
				try {
					writer.write(line);
					writer.newLine();
				} catch (IOException cause) {
					throw new UncheckedIOException(cause);
				}
			});
		}
	}

	private NetfileLanguage language() {
		return StandardNetfileLanguages
			.builder()
			.carSystem(carSystem)
			.individualWalkSystem(individualWalkSystem)
			.publicTransportWalkSystem(publicTransportWalkSystem)
			.build()
			.english();
	}

	private Map<String, Map<String, String>> defaultValues() {
		return Map
			.of(//
				language().resolve(Table.connectors),
				Map.of(language().resolve(StandardAttributes.direction), "O"),
				language().resolve(Table.timeProfileElement),
				Map
					.of(language().resolve(StandardAttributes.arrival), "00:00:00",
						language().resolve(StandardAttributes.departure), "00:00:00"),
				language().resolve(Table.vehicleJourney),
				Map.of(language().resolve(StandardAttributes.departure), "00:00:00"));
	}

	public static void main(String[] args) throws IOException {
		String carSystem = "CAR";
		String ptSystemCode = "PUTW";
		String individualWalkSystem = "FUSS";
		String publicTransportWalkSystem = "F";
		NetExportConfiguration exporter = new NetExportConfiguration(ptSystemCode, carSystem, individualWalkSystem, publicTransportWalkSystem);
		Path outputPath = Paths.get("netexport.net");
		exporter.writeNetExportFileTo(outputPath);
	}

}

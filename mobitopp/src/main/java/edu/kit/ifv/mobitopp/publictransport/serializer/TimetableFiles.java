package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import edu.kit.ifv.mobitopp.util.file.StreamContent;

public class TimetableFiles {

	public static final Charset encoding = StandardCharsets.UTF_8;
	private static final String stations = "stations.csv";
	private static final String stops = "stop.csv";
	private static final String transfers = "footpath.csv";
	private static final String journeys = "journey.csv";
	private static final String connections = "connection.csv";

	private final File baseFolder;

	private TimetableFiles(File baseFolder) {
		this.baseFolder = baseFolder;
	}

	BufferedWriter stationWriter() throws IOException {
		return outputTo(stationFile());
	}

	BufferedWriter stopWriter() throws IOException {
		return outputTo(stopFile());
	}

	BufferedWriter footpathWriter() throws IOException {
		return outputTo(transferFile());
	}

	BufferedWriter journeyWriter() throws IOException {
		return outputTo(journeyFile());
	}

	BufferedWriter connectionWriter() throws IOException {
		return outputTo(connectionFile());
	}

	private BufferedWriter outputTo(File file) throws IOException {
		if (file.exists()) {
			file.delete();
		}
		return Files.newBufferedWriter(file.toPath(), encoding);
	}

	private File fileFor(String fileName) {
		File uncompressedFile = new File(baseFolder, fileName);
		File compressedFile = new File(baseFolder, fileName + "." + StreamContent.bzipExtension);
		long lastUncompressedModification = uncompressedFile.lastModified();
		long lastCompressedModification = compressedFile.lastModified();
		if (compressedFile.exists()) {
			if (uncompressedFile.exists()) {
				return lastUncompressedModification < lastCompressedModification ? compressedFile
					: uncompressedFile;
			}
			return compressedFile;
		}
		return uncompressedFile;
	}

	public static TimetableFiles at(File timetable) {
		timetable.mkdirs();
		return new TimetableFiles(timetable);
	}

	File stationFile() {
		return fileFor(stations);
	}

	File stopFile() {
		return fileFor(stops);
	}

	File transferFile() {
		return fileFor(transfers);
	}

	File journeyFile() {
		return fileFor(journeys);
	}

	File connectionFile() {
		return fileFor(connections);
	}

	public boolean exist() {
		return stopFile().exists() && transferFile().exists() && journeyFile().exists()
			&& connectionFile().exists() && stationFile().exists();
	}

}

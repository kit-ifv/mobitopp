package edu.kit.ifv.mobitopp.data.local;

import static java.util.Arrays.asList;

import java.io.File;

import edu.kit.ifv.mobitopp.data.local.configuration.StoredMatrices;

public class Validate {

	public static FileValidator files(File... files) {
		return new FileValidator(asList(files));
	}

	public static FolderValidator folder(File folder) {
		return new FolderValidator(folder);
	}

	public static MatrixValidator matrices(StoredMatrices matrices, File baseFolder) {
		return new MatrixValidator(matrices, baseFolder);
	}

}

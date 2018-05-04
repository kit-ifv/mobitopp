package edu.kit.ifv.mobitopp.data.local;

import java.io.File;

public class Convert {

	public static String asString(File file) {
		return file.getPath();
	}

	public static File asFile(String path) {
		return new File(path);
	}
}

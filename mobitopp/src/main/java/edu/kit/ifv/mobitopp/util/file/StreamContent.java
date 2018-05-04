package edu.kit.ifv.mobitopp.util.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

public class StreamContent {

	private static final String bzipExtension = "bz2";

	public static InputStream of(File file) throws IOException {
		FileInputStream fileInput = new FileInputStream(file);
		if (file.getName().endsWith(bzipExtension)) {
			return uncompressBZip2From(fileInput);
		}
		return fileInput;
	}

	/**
	 * {@link BZip2CompressorInputStream} can not be directly put into {@link InputStreamReader}.
	 * Therefore the content is uncompressed into memory.
	 */
	private static InputStream uncompressBZip2From(FileInputStream fin) throws IOException {
		BufferedInputStream in = new BufferedInputStream(fin);
		BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte[] buffer = new byte[1024];
		int n = 0;
		while (-1 != (n = bzIn.read(buffer))) {
			out.write(buffer, 0, n);
		}
		out.close();
		bzIn.close();
		return new ByteArrayInputStream(out.toByteArray());
	}
}

package edu.kit.ifv.mobitopp.util.matcher.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

public class FileUtil {

	static List<String> contentOf(File item) {
		try {
			return Files.readAllLines(item.toPath());
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
	
	static List<String> contentOfCompressed(File item) {
		ArrayList<String> content = new ArrayList<>();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(uncompressBZip2From(new FileInputStream(item))))) {
			String line = null;
			while((line = reader.readLine()) != null) {
				content.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
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

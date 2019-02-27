package edu.kit.ifv.mobitopp.visum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.util.file.StreamContent;

public class VisumReader {

	private final String charSet;
  private final NetfileAttributes attributes;

	public VisumReader() {
		this("ISO-8859-1", StandardNetfileAttributes.german());
	}

	public VisumReader(String charSet, NetfileAttributes attributes) {
		this.charSet = charSet;
    this.attributes = attributes;
	}

	public Map<String,VisumTable> read(
		String filename,
		Collection<String> tablesToRead
	) {

		File file = new File(filename);

		return read(file, tablesToRead);
	}

	public Map<String,VisumTable> read(
		File file,
		Collection<String> tablesToRead
	) {

		Map<String,VisumTable> tables = new HashMap<String,VisumTable>();

		try {

			BufferedReader reader = openReader(file);

			System.out.println("file opened");
			tables = read(reader, tablesToRead);

		} catch(FileNotFoundException e) {
			System.out.println("File '" + file.getName() + "' not found!");
			e.printStackTrace();
		} catch(IOException e) {
			System.out.println("Error reading file '" + file.getName() + "'!");
			e.printStackTrace();
		}

		System.out.println("finished reading tables");

		return tables;
	}

	private BufferedReader openReader(File file) throws IOException {
		InputStream inputStream = openFileStream(file);
		Reader fileReader = new InputStreamReader(inputStream, Charset.forName(charSet));
		return new BufferedReader(fileReader);
	}

	private InputStream openFileStream(File file)
			throws IOException {
		return StreamContent.of(file);
	}

	protected Map<String,VisumTable> read(
		BufferedReader reader,
		Collection<String> tablesToRead
	)
		throws IOException {

		Map<String,VisumTable> tables = new HashMap<String,VisumTable>();


		while (reader.ready()) {

			String line = reader.readLine();

			if (	!line.isEmpty()
						&& line.charAt(0) == '$'
						&& line.contains(":"))
			{
				String name = tableName(line);

				System.out.println(name);


				if (tablesToRead.isEmpty() || tablesToRead.contains(name) || name.startsWith(poiCategoryPrefix())) {
					List<String> attributes = tableAttributes(line);


					VisumTable table = readTable(reader, name, attributes);

					tables.put(name,table);

					System.out.println(table.name + " " + table.numberOfRows());
				}

			}

		}

		return tables;

	}

  private String poiCategoryPrefix() {
    return attributes.resolve(Table.poiCategoryPrefix);
  }

	protected String tableName(String line) {

		String[] fields = line.split(":");

		return fields[0].substring(1);
	}

	protected List<String> tableAttributes(String line) {

		String[] fields = line.split(":");
		String[] attributes = fields[1].split(";");

		return Arrays.asList(attributes);
	}

	protected List<String> parseLine(String line, int numFields) {
		String[] fields = line.split(";");

		List<String> values = new ArrayList<String>(numFields);

		values.addAll(Arrays.asList(fields));

		for (int i=values.size(); i<numFields; i++) {
			values.add("");
		}

		return values;
	}

	protected VisumTable readTable(
		BufferedReader reader,
		String name,
		List<String> attributes
	) throws IOException {

		VisumTable table = new VisumTable(name, attributes);

		String line = reader.readLine();

		while (!line.isEmpty()) {

			List<String> values =	parseLine(line, attributes.size());

			table.addRow(values);

			line = reader.readLine();
		}

		return table;
	}

}

package edu.kit.ifv.mobitopp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ParameterFileParser {

	private class Coefficient {
		final public String name;
		final public Double value;

		public Coefficient(String name, Double value) {
			this.name=name;
			this.value=value;
		}

		public String toString() {
			return "'" + name + "': " + value;
		}
	}

	public void parseConfig(String filename, Object model) {
		log.info("parsing config");
		File f = new File(filename);
		parseConfig(f, model);
	}

  public void parseConfig(File file, Object model) {
    assert file.exists() : ("file '" + file.getName() + "' does not exist!");

		String line = null;

		Collection<Coefficient> coeffs = new ArrayList<Coefficient>();

		try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {

			while ((line = reader.readLine()) != null) {

				Coefficient coeff = parseLine(line);

				if(coeff != null) { 
					coeffs.add(coeff);
				}
			}
		} catch (FileNotFoundException e) {
			log.error(" Datei wurde nicht gefunden : " + file.getName());
		} catch (IOException e) {
			log.error(" Datei konnte nicht geöffnet werden :" + e);
		}

		findCoefficientsUsingReflection(coeffs, model);
  }

	private Coefficient parseLine(String line) {

		String fields[] = line.split("[ \t]+");

		if (fields.length == 2) {

			String name =  fields[0];	
			Double value = Double.valueOf(fields[1]);

			assert !name.isEmpty();
			assert !value.isNaN();
			assert !value.isInfinite();

			return new Coefficient(name, value);
		}

		return null;
	}

	private void findCoefficientsUsingReflection(
		Collection<Coefficient> coeffs,
		Object obj
	) {

			for(Coefficient coeff : coeffs) {

				writeField(coeff.name, coeff.value, obj);
			}

	}

	private void writeField(String name, Double value, Object obj) {

		assert name != null;
		assert !name.isEmpty();
		assert value != null;
		assert obj != null;

		Class<?> clazz = obj.getClass();

		try {

			Field field = clazz.getDeclaredField(name);

			assert field != null;
			assert field.getType() == Double.class || field.getType() == Integer.class
							: ("class=" + field.getType());
	
			field.setAccessible(true);

			if (field.getType() == Double.class) 	{
				field.set(obj, value);
			} else if (field.getType() == Integer.class) 	{
				field.set(obj, new Integer(value.intValue()));
			} else {
				throw new AssertionError("class=" + field.getType());
			}

		} catch(NoSuchFieldException e) {
			log.error("Field '" + name + "' not found!");
			e.printStackTrace();
			System.exit(-1);
		} catch(IllegalAccessException e) {
			log.error("Field '" + name + "' not accessible!");
			e.printStackTrace();
			System.exit(-1);
		}
	}
}

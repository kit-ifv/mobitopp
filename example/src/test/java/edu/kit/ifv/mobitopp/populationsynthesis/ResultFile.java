package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.util.matcher.FileMatchers.hasEqualContent;

import java.io.File;
import java.net.URISyntaxException;

import org.hamcrest.Matcher;

public class ResultFile {

  private final String fileName;
  private final File outputFolder;
	private final String subpackage;

  public ResultFile(File outputFolder, String fileName, String subpackage) {
    super();
    this.outputFolder = outputFolder;
    this.fileName = fileName;
		this.subpackage = subpackage;
  }

  public File actualFile() {
    return new File(outputFolder, fileName);
  }

  public Matcher<File> createMatcher(Class<?> clazz) throws URISyntaxException {
    return hasEqualContent(expectedFile(clazz));
  }

  private File expectedFile(Class<?> clazz) throws URISyntaxException {
    return new File(clazz.getResource(subpackage + "/" + fileName).toURI());
  }

  @Override
  public String toString() {
    return fileName;
  }

}

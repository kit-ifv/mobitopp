package edu.kit.ifv.mobitopp.util.parameter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParameterFormularParserTest {

  private static final double margin = 1e-6;
  private DummyModel model;
  private ParameterFormularParser parser;

  @BeforeEach
  public void initialise() {
    model = new DummyModel();
    parser = new ParameterFormularParser();
  }

  @Test
  public void readsOneParameterPerLine() {
    String content = "parameter1 = 0.5" + System.lineSeparator() + "parameter2 = 2";
    InputStream fileContent = readerWith(content);

    parser.parseConfig(fileContent, model);

    assertThat(model.parameter1, is(closeTo(0.5d, margin)));
    assertThat(model.parameter2, is(2));
  }

  @Test
  public void evaluatesFormularsAsParameter() {
    String content = "parameter1 = 0.5 - 2.5" + System.lineSeparator() + "parameter2 = 2 + 3";
    InputStream fileContent = readerWith(content);

    parser.parseConfig(fileContent, model);

    assertThat(model.parameter1, is(closeTo(-2.0d, margin)));
    assertThat(model.parameter2, is(5));
  }

  @Test
  public void evaluatesNonNumberParameter() {
    String content = "parameter3 = text";
    InputStream fileContent = readerWith(content);
    assertThrows(IllegalArgumentException.class, () -> parser.parseConfig(fileContent, model));
  }

  @Test
  public void failsToEvaluateMissingParameter() {
    String content = "parameter4 = 0.5";
    InputStream fileContent = readerWith(content);
    assertThrows(IllegalArgumentException.class, () -> parser.parseConfig(fileContent, model));
  }

  @Test
  public void filtersEmptyLines() {
    String emptyLine = "";
    String content = "parameter1 = 0.5 - 2.5" + System.lineSeparator() + emptyLine
        + System.lineSeparator();
    InputStream fileContent = readerWith(content);

    parser.parseConfig(fileContent, model);

    assertThat(model.parameter1, is(closeTo(-2.0d, margin)));
  }

  @Test
  public void filtersCommentedLines() {
    assertAll(ParameterFormularParser.lineCommentPrefixes
        .stream()
        .map(prefix -> () -> parseCommentedLineWith(prefix)));
  }

  private void parseCommentedLineWith(String prefix) {
    String emptyLine = prefix + " Test";
    String content = "parameter1 = 0.5 - 2.5" + System.lineSeparator() + emptyLine
        + System.lineSeparator();
    InputStream fileContent = readerWith(content);

    parser.parseConfig(fileContent, model);

    assertThat(model.parameter1, is(closeTo(-2.0d, margin)));
  }

  @Test
  void filtersCommentedCode() throws Exception {
    assertAll(ParameterFormularParser.inlineComment
        .stream()
        .map(prefix -> () -> parseCommentedCodeWith(prefix)));
  }

  private void parseCommentedCodeWith(String prefix) {
    String commentedCode = prefix + " Test";
    String content = "parameter1 = 0.5 - 2.5" + commentedCode + System.lineSeparator();
    InputStream fileContent = readerWith(content);

    parser.parseConfig(fileContent, model);

    assertThat(model.parameter1, is(closeTo(-2.0d, margin)));
  }

  private InputStream readerWith(String content) {
    return new ByteArrayInputStream(content.getBytes());
  }
}

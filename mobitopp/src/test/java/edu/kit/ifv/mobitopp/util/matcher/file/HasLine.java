package edu.kit.ifv.mobitopp.util.matcher.file;

import static edu.kit.ifv.mobitopp.util.matcher.file.FileUtil.contentOf;

import java.io.File;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class HasLine extends TypeSafeMatcher<File> {

	private final String line;

	public HasLine(String line) {
		super();
		this.line = line;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("has line: " + line);
	}

	@Override
	protected boolean matchesSafely(File item) {
		List<String> content = contentOf(item);
		return content.stream().anyMatch(line -> this.line.equals(line));
	}

	@Override
	protected void describeMismatchSafely(File item, Description mismatchDescription) {
		mismatchDescription.appendText("has content: " + contentOf(item));
	}
}
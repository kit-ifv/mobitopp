package edu.kit.ifv.mobitopp.util.matcher.file;

import static edu.kit.ifv.mobitopp.util.matcher.file.FileUtil.contentOf;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;


public class HasEqualSortedContent extends TypeSafeMatcher<File> {

	private final File expected;

	public HasEqualSortedContent(File expected) {
		super();
		this.expected = expected;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("is equal to ");
		description.appendText(expected.getAbsolutePath());
	}

	@Override
	protected boolean matchesSafely(File file) {
		List<String> expectedContent = contentOf(expected);
		List<String> fileContent = contentOf(file);
		Collections.sort(expectedContent);
		Collections.sort(fileContent);
		return expectedContent.equals(fileContent);
	}

	@Override
	protected void describeMismatchSafely(File item, Description mismatchDescription) {
		mismatchDescription.appendText("differs from ");
		mismatchDescription.appendText(expected.getAbsolutePath());
	}

}

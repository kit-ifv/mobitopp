package edu.kit.ifv.mobitopp.util.matcher.file;

import static edu.kit.ifv.mobitopp.util.matcher.file.FileUtil.contentOf;

import java.io.File;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class IsEmpty extends TypeSafeMatcher<File> {

	@Override
	public void describeTo(Description description) {
		description.appendText("file is empty");
	}

	@Override
	protected boolean matchesSafely(File item) {
		List<String> content = contentOf(item);
		return content.isEmpty();
	}

	@Override
	protected void describeMismatchSafely(File item, Description mismatchDescription) {
		mismatchDescription.appendText("file contains: " + contentOf(item));
	}
}
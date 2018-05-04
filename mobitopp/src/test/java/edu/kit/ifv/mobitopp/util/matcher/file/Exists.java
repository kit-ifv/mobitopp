package edu.kit.ifv.mobitopp.util.matcher.file;

import java.io.File;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class Exists extends TypeSafeMatcher<File> {

	@Override
	public void describeTo(Description description) {
		description.appendText("exists");
	}

	@Override
	protected boolean matchesSafely(File item) {
		return item.exists();
	}

	@Override
	protected void describeMismatchSafely(File item, Description mismatchDescription) {
		mismatchDescription.appendText("is missing: " + item);
	}

}
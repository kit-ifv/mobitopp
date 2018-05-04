package edu.kit.ifv.mobitopp.result;

public interface Results {

	static Results no() {
		return (category, message) -> {
		};
	}

	void write(Category category, String message);

}

package edu.kit.ifv.mobitopp.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class DateFormat {

	private static final LocalDateTime monday = LocalDateTime.of(2018, 1, 1, 0, 0);
	
	private static final String weekdayDateSeparator = ", ";
	private static final String dateTimeSeparator = " ";
	private static final int width = 2;
	private static final DateTimeFormatter dayFormat = weekdayFormat();
	private static final DateTimeFormatter timeFormat = timeFormat();
	private static final DateTimeFormatter fullFormat = fullFormat();
	private static final DateTimeFormatter weekdayTimeFormat = weekdayTimeFormat();
	private static final DateTimeFormatter dayTimeFormat = dayTimeFormat();

	public DateFormat() {
		super();
	}

	public String asDay(Time date) {
		return format(date, dayFormat);
	}

	public String asTime(Time date) {
		return format(date, timeFormat);
	}

	public String asFullDate(Time date) {
		return format(date, fullFormat);
	}

	public String asWeekdayTime(Time date) {
		return format(date, weekdayTimeFormat);
	}

	public String asDayTime(Time date) {
		return format(date, dayTimeFormat);
	}

	private String format(Time date, DateTimeFormatter format) {
		return monday.plus(date.fromStart().toDuration()).format(format);
	}

	private static DateTimeFormatter fullFormat() {
		return new DateTimeFormatterBuilder()
				.append(weekdayFormat())
				.appendLiteral(dateTimeSeparator)
				.append(timeFormat())
				.toFormatter();
	}

	private static DateTimeFormatter timeFormat() {
		return hourMinute()
				.appendLiteral(':')
				.appendValue(ChronoField.SECOND_OF_MINUTE, width)
				.toFormatter();
	}

	private static DateTimeFormatterBuilder hourMinute() {
		return new DateTimeFormatterBuilder()
				.appendValue(ChronoField.HOUR_OF_DAY, width)
				.appendLiteral(':')
				.appendValue(ChronoField.MINUTE_OF_HOUR, width);
	}

	private static DateTimeFormatter dateFormat() {
		return new DateTimeFormatterBuilder()
				.appendValue(ChronoField.DAY_OF_MONTH, width)
				.toFormatter();
	}

	private static DateTimeFormatter weekdayFormat() {
		return new DateTimeFormatterBuilder().appendPattern("EE").toFormatter();
	}

	private static DateTimeFormatter weekdayTimeFormat() {
		return new DateTimeFormatterBuilder()
				.append(weekdayFormat())
				.appendLiteral(weekdayDateSeparator)
				.append(dateFormat())
				.appendLiteral(dateTimeSeparator)
				.append(timeFormat())
				.toFormatter();
	}
	
	private static DateTimeFormatter dayTimeFormat() {
		return new DateTimeFormatterBuilder()
				.append(weekdayFormat())
				.appendLiteral(dateTimeSeparator)
				.append(hourMinute().toFormatter())
				.toFormatter();
	}

}

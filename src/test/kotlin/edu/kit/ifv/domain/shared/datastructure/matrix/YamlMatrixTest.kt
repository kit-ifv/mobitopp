package edu.kit.ifv.domain.shared.datastructure.matrix
import edu.kit.ifv.core.datastructure.calendarLookup.CalendarWeekLookup
import edu.kit.ifv.core.datastructure.calendarLookup.CalendarWeekLookupBuilder
import edu.kit.ifv.core.datastructure.calendarLookup.TimeLookupBuilder
import edu.kit.ifv.core.datastructure.calendarLookup.TimeLookupOperation
import edu.kit.ifv.core.datastructure.calendarLookup.WeekLookupOperation
import edu.kit.ifv.utils.WithExpiration
import edu.kit.ifv.utils.units.AbsoluteTime
import edu.kit.ifv.utils.units.sinceStart
import edu.kit.ifv.utils.units.weeks
import org.junit.jupiter.api.assertThrows
import java.time.DayOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class YamlMatrixTest {

    private val standardStructure: CalendarWeekLookup<Int>
        get() {
            val structure = createStructure {
                week(0) {
                    default {
                        setDefault(5)
                    }
                    workdays {
                        setDefault(4)
                    }
                    day(DayOfWeek.MONDAY) {
                        this[2, 3] = 6
                        this[4, 5] = 7
                    }

                    day(DayOfWeek.SUNDAY) {
                        this[3, 4] = 5
                        this[10, 11] = 8
                    }
                }
            }
            return structure
        }

    private val secondStructure: CalendarWeekLookup<Int>
        get() {
            val structure = createStructure {

                week(0) {
                    day(DayOfWeek.MONDAY) {
                        this[0, 2] = 1
                    }

                    day(DayOfWeek.TUESDAY) {
                        this[2, 3] = 2
                    }
                }
            }
            return structure
        }

    @Test
    fun defaultForAllEntries() {
        val structure = createStructure {
            allWeeks {
                default {
                    this[0, 24] = 3
                }
            }
        }
        for (i in listOf(-1, 0, 1, 2, 3, 999999, -9999999)) {
            val (output, expiration) = structure[0, 0, i]
            assertEquals(3, output)
            assertEquals(AbsoluteTime.INFINITY, expiration)
        }
    }

    @Test
    fun useLastEntryOfDayIfNothingElse() {
        val structure = secondStructure

        structure.test {
            expected = 1
            time {
                hours = 4
            }
            expiration {
                dayOfWeek = DayOfWeek.TUESDAY
                hours = 2
            }
        }
        structure.test {
            expected = 2
            time {
                dayOfWeek = DayOfWeek.TUESDAY
                hours = 2.5
            }
            expiration {
                hours = Double.POSITIVE_INFINITY
            }
        }
    }

    @Test
    fun missingEntryForDayThrows() {
        assertThrows<IllegalArgumentException> {
            secondStructure.test {
                expected = 2
                time {
                    dayOfWeek = DayOfWeek.WEDNESDAY
                    hours = 4
                }
                expiration {
                    hours = Double.POSITIVE_INFINITY
                }
            }
        }
    }

    @Test
    fun monday() {
        standardStructure.test {
            expected = 6
            time {
                hours = 2
            }
            expiration {
                hours = 3
            }
        }

        standardStructure.test {
            expected = 4
            time {
                hours = 3
            }
            expiration {
                hours = 4
            }
        }

        standardStructure.test {
            expected = 7
            time {
                hours = 4.5
            }
            expiration {
                hours = 5
            }
        }
    }

    /**
     * The default is 5 and the change during sunday 3, 4 = 5 does nothing. Thus the expiration should be
     * at the next change that actually matters, which is hour 10 of sunday.
     */
    @Test
    fun saturday() {
        standardStructure.test {
            expected = 5
            time {
                dayOfWeek = DayOfWeek.SATURDAY
                hours = 19
            }
            expiration {
                dayOfWeek = DayOfWeek.SUNDAY
                hours = 10
            }
        }
    }

    @Test
    fun sunday() {
        standardStructure.test {
            expected = 8
            time {
                dayOfWeek = DayOfWeek.SUNDAY
                hours = 10.5
            }
            expiration {
                dayOfWeek = DayOfWeek.SUNDAY
                hours = 11
            }
        }
    }

    private data class Timepoint(
        var week: Int = 0,
        var dayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
        var hours: Number = 0,
    ) {
        fun toAbsolute(): AbsoluteTime = (week.weeks + dayOfWeek.ordinal.days + hours.toDouble().hours).sinceStart
    }

    private class TestBuilder<T : Any> {
        lateinit var expected: T
        lateinit var targetTime: Timepoint
        lateinit var expectedExpiration: Timepoint

        fun expiration(lambda: Timepoint.() -> Unit) {
            expectedExpiration = Timepoint()
            expectedExpiration.apply(lambda)
        }

        fun time(lambda: Timepoint.() -> Unit) {
            targetTime = Timepoint()
            targetTime.apply(lambda)
        }
    }

    private fun <T : Any> CalendarWeekLookup<T>.test(lambda: TestBuilder<T>.() -> Unit) {
        val builder = TestBuilder<T>()
        builder.apply(lambda)
        val targetTime = builder.targetTime.toAbsolute()
        val targetExpiration = builder.expectedExpiration.toAbsolute()
        val expectedElement = builder.expected
        val (element, expiration) = this[targetTime]
        assertEquals(expectedElement, element)
        assertEquals(targetExpiration, expiration)
    }

    private operator fun <T> CalendarWeekLookup<T>.get(week: Int, day: Int, hour: Number): WithExpiration<T> = get(
        (week.weeks + day.days + hour.toDouble().hours).sinceStart,
    )

    private operator fun <T> TimeLookupBuilder<T>.set(a: Number, b: Number, element: T) = set(
        a.toDouble().hours,
        b.toDouble().hours,
        element,
    )
}

private fun <T> createStructure(lambda: MutableCalendarLookupBuilder<T>.() -> Unit): CalendarWeekLookup<T> {
    val builder = MutableCalendarLookupBuilder<T>()
    builder.apply(lambda)
    return builder.build()
}

private class MutableCalendarLookupBuilder<T> {
    private val allWeeks = CalendarWeekLookupBuilder<T>()
    fun week(number: Int, lambda: MutableWeekLookupBuilder<T>.() -> Unit) {
        val builder = MutableWeekLookupBuilder<T>()
        builder.apply(lambda)
        allWeeks[number] = builder.build()
    }

    fun allWeeks(lambda: MutableWeekLookupBuilder<T>.() -> Unit) {
        val builder = MutableWeekLookupBuilder<T>()
        builder.apply(lambda)
        val lookup = builder.build()
        allWeeks.applyDefaultInstructions(lookup)
    }

    fun build(): CalendarWeekLookup<T> = allWeeks.build()
}

private class MutableWeekLookupBuilder<T> {
    private val thisWeek: MutableList<WeekLookupOperation<T>> = mutableListOf()

    fun day(day: DayOfWeek, lambda: TimeLookupOperation<T>) {
        thisWeek.add {
            this[day] = lambda
        }
    }

    fun default(lambda: TimeLookupOperation<T>) {
        thisWeek.add {
            this.setDefault(lambda)
        }
    }

    fun workdays(lambda: TimeLookupOperation<T>) {
        thisWeek.add {
            this.setWorkdays(lambda)
        }
    }

    fun build(): Collection<WeekLookupOperation<T>> = thisWeek
}

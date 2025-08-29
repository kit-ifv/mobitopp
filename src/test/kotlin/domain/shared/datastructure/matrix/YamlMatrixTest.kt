package domain.shared.datastructure.matrix

import kotlinx.datetime.DayOfWeek
import org.junit.jupiter.api.assertThrows
import utils.WithExpiration
import utils.units.AbsoluteTime
import utils.units.sinceStart
import utils.units.weeks
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
        fun toAbsolute(): AbsoluteTime {
            return (week.weeks + dayOfWeek.ordinal.days + hours.toDouble().hours).sinceStart
        }
    }

    private class TestBuilder<T : Any>() {
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

    private operator fun <T> CalendarWeekLookup<T>.get(week: Int, day: Int, hour: Number): WithExpiration<T> {
        return get((week.weeks + day.days + hour.toDouble().hours).sinceStart)
    }

    private operator fun <T> DayLookupBuilder<T>.set(a: Number, b: Number, element: T) = set(a.toDouble().hours, b.toDouble().hours, element)
}


private fun <T> createStructure(lambda: MutableWeeksLookupBuilder<T>.() -> Unit): CalendarWeekLookup<T> {
    val builder = MutableWeeksLookupBuilder<T>()
    builder.apply(lambda)
    return builder.build()
}

private class MutableWeeksLookupBuilder<T> {
    private val allWeeks = CalendarWeekLookupBuilder<T>()
    fun week(number: Int, lambda: MutableWeekLookupBuilder<T>.() -> Unit) {
        val builder = MutableWeekLookupBuilder<T>()
        builder.apply(lambda)
        val lookup = builder.build()
        allWeeks[number] = lookup
    }

    fun allWeeks(lambda: MutableWeekLookupBuilder<T>.() -> Unit) {
        val builder = MutableWeekLookupBuilder<T>()
        builder.apply(lambda)
        val lookup = builder.build()
        allWeeks.setDefault(lookup)
    }

    fun build(): CalendarWeekLookup<T> {
        return allWeeks.build()
    }
}

private class MutableWeekLookupBuilder<T> {
    private val thisWeek: WeekLookupBuilder<T> = WeekLookupBuilder()

    fun day(day: DayOfWeek, lambda: DayLookupBuilder<T>.() -> Unit) {
        val timeLookup = buildLookup(lambda)
        thisWeek[day] = timeLookup

    }

    fun default(lambda: DayLookupBuilder<T>.() -> Unit) {
        val timeLookup = buildLookup(lambda)
        thisWeek.setDefault(timeLookup)
    }

    fun workdays(lambda: DayLookupBuilder<T>.() -> Unit) {
        val timeLookup = buildLookup(lambda)
        thisWeek.setWorkdays(timeLookup)
    }

    private fun buildLookup(lambda: DayLookupBuilder<T>.() -> Unit): DayLookupBuilder<T> {
        val timeLookup = DayLookupBuilder<T>(modulus = 1.days)
        timeLookup.apply(lambda)
        return timeLookup
    }

    fun build(): WeekLookup<T> {
        return thisWeek.build()
    }
}

package edu.kit.ifv.core.datastructure.calendarLookup
data class PrioritizedOperation<T>(val operation: TimeLookupOperation<T>, val priority: Int)

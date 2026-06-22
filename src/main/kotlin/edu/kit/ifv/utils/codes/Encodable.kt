package edu.kit.ifv.utils.codes
/**
 * An object is encodable if it can provide an integer based on the attributes present. In the future this could be
 * abstracted to a template type in case that the encoding might return a different type (such as Strings)
 */
interface Encodable {
    val code: Int
    val description: String
}

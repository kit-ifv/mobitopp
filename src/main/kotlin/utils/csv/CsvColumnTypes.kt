@file:Suppress("TooManyFunctions")
package utils.csv

import CodePlan
import Encodable
import ID
import utils.units.CurrencyUnits
import utils.units.DistanceUnit
import utils.units.euros
import utils.units.kilometers
import utils.units.meters
import utils.units.share
import utils.units.toCurrency
import utils.units.toDistance

fun Row.byte(column: String) = this.invoke(column, String::toByte)
fun Row.short(column: String) = this.invoke(column, String::toShort)
fun Row.int(column: String) = this.invoke(column, String::toInt)
fun Row.long(column: String) = this.invoke(column, String::toLong)
fun Row.float(column: String) = this.invoke(column, String::toFloat)
fun Row.double(column: String) = this.invoke(column, String::toDouble)
fun Row.boolean(column: String) = this.invoke(column, String::toBoolean)
fun <E> Row.id(column: String) = this.invoke(column) { s -> ID<E>(s.toLong()) }
fun <T: Encodable> Row.decode(column: String, codePlan: CodePlan<T>) =
    this.invoke(column) { s ->  codePlan.decode(s.toInt()) }

fun <T: Encodable> Row.decodeName(column: String, codePlan: CodePlan<T>) =
    this.invoke(column) { s ->  codePlan.decode(s) }

fun Row.unitShare(column: String) = this.invoke(column) { s ->  s.toDouble().share() }


fun Row.byte() = TypedRow(this, String::toByte)
fun Row.short() = TypedRow(this, String::toShort)
fun Row.int() = TypedRow(this, String::toInt)
fun Row.long() = TypedRow(this, String::toLong)
fun Row.float() = TypedRow(this, String::toFloat)
fun Row.double() = TypedRow(this, String::toDouble)
fun Row.boolean() = TypedRow(this, String::toBoolean)
fun <E> Row.id() = TypedRow(this) { s -> ID<E>(s.toLong()) }
fun <T: Encodable> Row.decode(codePlan: CodePlan<T>) =
    TypedRow(this) { s ->  codePlan.decode(s.toInt()) }

fun <T: Encodable> Row.decodeName(codePlan: CodePlan<T>) =
    TypedRow(this) { s ->  codePlan.decode(s) }


fun <T> T.distance(unit: DistanceUnit) where T: TypedRow<Int> = this.wrap { it.toDistance(unit) }
fun <T> T.distance(column: String, unit: DistanceUnit) where T: TypedRow<Double> =
    this.wrap { it.toDistance(unit) }.invoke(column)
//fun <L> L.distance(unit: DistanceUnit) where L: TypedRow<Long> = this.wrap { it.toDistance(unit) }
//fun <D> D.distance(unit: DistanceUnit) where D: TypedRow<Double> = this.wrap { it.toDistance(unit) }

fun <T> T.meters() where T: TypedRow<Int> = this.wrap { it.meters }
fun <T> T.meters(column: String) where T: TypedRow<Int> = this.wrap { it.meters }.invoke(column)
//fun <L> L.meters() where L: TypedRow<Long> = this.wrap { it.meters }
//fun <D> D.meters() where D: TypedRow<Double> = this.wrap { it.meters }

fun <T> T.kilometers() where T: TypedRow<Int> = this.wrap { it.kilometers }
fun <T> T.kilometers(column: String) where T: TypedRow<Int> = this.wrap { it.kilometers }.invoke(column)
//fun <L> L.kilometers() where L: TypedRow<Long> = this.wrap { it.kilometers }
//fun <D> D.kilometers() where D: TypedRow<Double> = this.wrap { it.kilometers }

fun <T> T.currency(unit: CurrencyUnits) where T: TypedRow<Int> = this.wrap { it.toCurrency(unit) }
fun <T> T.currency(column: String, unit: CurrencyUnits) where T: TypedRow<Int> =
    this.wrap { it.toCurrency(unit) }.invoke(column)
//fun <L> L.currency(unit: CurrencyUnits) where L: TypedRow<Long> = this.wrap { it.toCurrency(unit) }
//fun <D> D.currency(unit: CurrencyUnits) where D: TypedRow<Double> = this.wrap { it.toCurrency(unit) }

fun <T> T.euros() where T: TypedRow<Int> = this.wrap { it.euros }
fun <T> T.euros(column: String) where T: TypedRow<Int> = this.wrap { it.euros }.invoke(column)


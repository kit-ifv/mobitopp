package utils.collections

import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarBuilder
import me.tongfei.progressbar.ProgressBarStyle

private var MUTE_PROGRESSBAR = false
// TODO check if still needed

fun muteProgressBars() {
    MUTE_PROGRESSBAR = true
}

fun unmuteProgressBars() {
    MUTE_PROGRESSBAR = false
}

fun <S, T> S.addProgressBar(
    label: String,
    expectedCount: Long,
    visible: Boolean = true,
): Iterator<T> where S : Sequence<T> = if (!MUTE_PROGRESSBAR and visible) {
    val pbb = defaultProgressBarBuilder(label, expectedCount)
    ProgressBar.wrap(this.iterator(), pbb)
} else {
    this.iterator()
}

fun standardProgressBar(label: String, expectedCount: Number) = defaultProgressBarBuilder(label, expectedCount).build()
fun <T> Collection<T>.addProgressBar(label: String, visible: Boolean = true) = this.addProgressBar(label, size, visible)
fun <T> Iterable<T>.addProgressBar(label: String, expectedCount: Int, visible: Boolean = true) =
    this.addProgressBar(label, expectedCount.toLong(), visible)
fun <T> Iterable<T>.addProgressBar(label: String, expectedCount: Long, visible: Boolean = true): Iterable<T> =
    if (!MUTE_PROGRESSBAR && visible) {
        val pbb = defaultProgressBarBuilder(label, expectedCount)
        ProgressBar.wrap(this, pbb)
    } else {
        this
    }

@Suppress("MagicNumber")
fun defaultProgressBarBuilder(label: String, expectedCount: Number): ProgressBarBuilder = ProgressBarBuilder()
    .setUpdateIntervalMillis(250)
    .setMaxRenderedLength(120)
    .setStyle(ProgressBarStyle.COLORFUL_UNICODE_BAR)
    .setTaskName(label)
    .setInitialMax(expectedCount.toLong())

fun ProgressBar.stepBy(n: Int): ProgressBar = stepBy(n.toLong())

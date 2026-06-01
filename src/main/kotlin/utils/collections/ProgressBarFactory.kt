package utils.collections

import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarBuilder
import me.tongfei.progressbar.ProgressBarStyle

private var muteProgressbar = false
// TODO check if still needed

fun muteProgressBars() {
    muteProgressbar = true
}

fun unmuteProgressBars() {
    muteProgressbar = false
}

fun <S, T> S.addProgressbar(
    label: String,
    expectedCount: Long,
    visible: Boolean = true
): Iterator<T> where S : Sequence<T> {
    return if (!MUTE_PROGRESSBAR and visible) {
        val pbb = defaultProgressBarBuilder(label, expectedCount)
        ProgressBar.wrap(this.iterator(), pbb)
    } else {
        this.iterator()
    }
}

/** Add a progress bar logging the progress of the given iterator on the console.
 * @param label label of the progressbar
 * @param expectedCount expected number of elements in the iterator
 * @param visible whether the progressbar should be shown on the console
 */
fun <I, T> I.addProgressBar(
    label: String,
    expectedCount: Long,
    visible: Boolean = true,
): Iterator<T> where I : Iterator<T> = if (!muteProgressbar and visible) {
    val pbb = defaultProgressBarBuilder(label, expectedCount)
    ProgressBar.wrap(this, pbb)
} else {
    this
}
fun standardProgressBar(label: String, expectedCount: Number) = defaultProgressBarBuilder(label, expectedCount).build()
fun <T> Collection<T>.addProgressBar(label: String, visible: Boolean = true) = this.addProgressBar(label, size, visible)
fun <T> Iterable<T>.addProgressBar(label: String, expectedCount: Int, visible: Boolean = true) =
    this.addProgressBar(label, expectedCount.toLong(), visible)
fun <T> Iterable<T>.addProgressBar(label: String, expectedCount: Long, visible: Boolean = true): Iterable<T> =
    if (!muteProgressbar && visible) {
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

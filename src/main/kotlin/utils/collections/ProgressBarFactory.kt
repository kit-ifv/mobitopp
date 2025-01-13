package utils.collections

import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarBuilder
import me.tongfei.progressbar.ProgressBarStyle

private var MUTE_PROGRESSBAR = false

fun muteProgressBars() {
    MUTE_PROGRESSBAR = true
}

fun unmuteProgressBars() {
    MUTE_PROGRESSBAR = false
}

/** Add a progress bar logging the progress of the given iterator on the console.
 * @param label label of the progressbar
 * @param expectedCount expected number of elements in the iterator
 * @param visible whether the progressbar should be shown on the console
 */
fun <I, T> I.addProgressBar(
    label: String,
    expectedCount: Long,
    visible: Boolean = true
): Iterator<T> where I : Iterator<T> {
    return if (!MUTE_PROGRESSBAR and visible) {
        val pbb = defaultProgressBarBuilder(label, expectedCount)
        ProgressBar.wrap(this, pbb)
    } else {
        this
    }
}

@Suppress("MagicNumber")
fun defaultProgressBarBuilder(
    label: String,
    expectedCount: Long
): ProgressBarBuilder = ProgressBarBuilder()
    .setUpdateIntervalMillis(250)
    .setMaxRenderedLength(120)
    .setStyle(ProgressBarStyle.COLORFUL_UNICODE_BAR)
    .setTaskName(label)
    .setInitialMax(expectedCount)

fun ProgressBar.stepBy(n: Int): ProgressBar = stepBy(n.toLong())

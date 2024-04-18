package utils.collections

import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarBuilder
import me.tongfei.progressbar.ProgressBarStyle

interface ProgressBarFactory {

    fun <T> createProgressBar(it: Iterator<T>, taskName: String, expectedCount: Long): Iterator<T>
}

class NullProgressbar : ProgressBarFactory {
    override fun <T> createProgressBar(it: Iterator<T>, taskName: String, expectedCount: Long): Iterator<T> = it
}

@Suppress("MagicNumber")
class FancyProgressbar : ProgressBarFactory {

    private val pbb = ProgressBarBuilder()

    init {
        pbb.setUpdateIntervalMillis(250)
            .setMaxRenderedLength(120)
            .setStyle(ProgressBarStyle.COLORFUL_UNICODE_BAR)
    }
    override fun <T> createProgressBar(it: Iterator<T>, taskName: String, expectedCount: Long): Iterator<T> {
        pbb.setTaskName(taskName)
            .setInitialMax(expectedCount)
        return ProgressBar.wrap(it, pbb)
    }
}

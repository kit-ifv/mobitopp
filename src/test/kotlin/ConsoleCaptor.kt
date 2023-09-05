import java.io.ByteArrayOutputStream
import java.io.PrintStream

class ConsoleCaptor {

    private val baos: ByteArrayOutputStream = ByteArrayOutputStream()
    private val oldOutputStream: PrintStream = System.out
    private var recording = true

    init {
        System.setOut(PrintStream(baos))
    }

    fun getText(): String {
        require(recording){
            "ConsoleCaptor was already closed."
        }
        recording = false

        System.out.flush()
        System.setOut(oldOutputStream)
        return baos.toString()
    }

}

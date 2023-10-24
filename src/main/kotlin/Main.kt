val creator: String
    get() {
        return "Robin"
    }

fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
    val t = Test(1)
    print(t.wololo)
}
@Mutable
class Test(val wololo: Int)

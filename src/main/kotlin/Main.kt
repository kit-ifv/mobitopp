fun creator(): String {
    return "Robin"
}

fun main(args: Array<String>) {
    println("Hello World!")
    println(random(1, 2, 3))

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
}

fun random(param: Int, param2: Int, param3: Int, param4: Int = 2, param5: Int = 101): Int {
    if (param != param2) {
        for(i in 1..10) {
            val x = param3 + 1
            if (x == param4) {
                return 2 + i
            }

        }
    }
    else{
        return 4
    }


    return 0
}

fun e() {
    println("EEEg")
}

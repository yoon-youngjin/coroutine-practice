package sec08

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import printWithThread

fun main(): Unit = runBlocking {
    printWithThread("START")
    printWithThread(calculateResult())
    printWithThread("END")
}

suspend fun calculateResult(): Int = withContext(Dispatchers.Default) {
    printWithThread("coroutineScope")
    val num1 = async {
        printWithThread("async")
        delay(1_000L)
        10
    }
    val num2 = async {
        printWithThread("async")
        delay(1_000L)
        20
    }
    num1.await() + num2.await()
}

package basiccoroutine.sec08

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import basiccoroutine.printWithThread

fun main(): Unit = runBlocking {
    basiccoroutine.printWithThread("START")
    basiccoroutine.printWithThread(basiccoroutine.sec08.calculateResult())
    basiccoroutine.printWithThread("END")
}

suspend fun calculateResult(): Int = withContext(Dispatchers.Default) {
    basiccoroutine.printWithThread("coroutineScope")
    val num1 = async {
        basiccoroutine.printWithThread("async")
        delay(1_000L)
        10
    }
    val num2 = async {
        basiccoroutine.printWithThread("async")
        delay(1_000L)
        20
    }
    num1.await() + num2.await()
}

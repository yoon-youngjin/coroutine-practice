package basiccoroutine.sec08

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import basiccoroutine.printWithThread

fun main(): Unit = runBlocking {
    val result: Int? = withTimeoutOrNull(1000) {
        delay(1500)
        10 + 20
    }

    basiccoroutine.printWithThread(result)
}


package basiccoroutine.sec06

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import basiccoroutine.printWithThread

fun main(): Unit = runBlocking {
    launch {
        delay(600L)
        basiccoroutine.printWithThread("A")
    }
    launch {
        delay(500L)
        throw IllegalArgumentException("코루틴 실패!")
    }
}


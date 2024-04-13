package basiccoroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

suspend fun main(): Unit = runBlocking {
    launch {
        basiccoroutine.printWithThread("코루틴 1 실행1")
        yield()
        basiccoroutine.printWithThread("코루틴 2 실행2")
    }

    launch {
        yield()
        basiccoroutine.printWithThread("코루틴 2 실행1")
    }
}
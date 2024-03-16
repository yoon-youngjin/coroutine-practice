import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

suspend fun main(): Unit = runBlocking {
    launch {
        printWithThread("코루틴 1 실행1")
        yield()
        printWithThread("코루틴 2 실행2")
    }

    launch {
        yield()
        printWithThread("코루틴 2 실행1")
    }
}
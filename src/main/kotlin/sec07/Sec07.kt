package sec07

import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import printWithThread

fun example() {
    CoroutineScope(Dispatchers.Default).launch {
        printWithThread("Job 1")
    }

    Thread.sleep(500)
}

suspend fun example3() {
    val job = CoroutineScope(Dispatchers.Default).launch {
        printWithThread("Job 1")
    }

    job.join()
}

fun main() {
    val threadPool = Executors.newSingleThreadExecutor()
    CoroutineScope(threadPool.asCoroutineDispatcher()).launch {

    }
}


class AsyncLogic {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun doSomeThing() {
        scope.launch {
            // 작업
        }
    }

    fun destroy() {
        scope.cancel()
    }
}
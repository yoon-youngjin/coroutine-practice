package sec07

import kotlinx.coroutines.*
import java.util.concurrent.Executors
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

suspend fun example4() {
    val job = CoroutineScope(Dispatchers.Default).launch {
        printWithThread("Job 1")
        coroutineContext + CoroutineName("테스트") + Dispatchers.Main
        coroutineContext.minusKey(CoroutineName.Key)
        printWithThread(coroutineContext.isActive)
        printWithThread(coroutineContext.job)
    }
    job.join()
}

suspend fun main() {
    example4()
//    val threadPool = Executors.newSingleThreadExecutor()
//    CoroutineScope(threadPool.asCoroutineDispatcher()).launch {
//
//    }
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